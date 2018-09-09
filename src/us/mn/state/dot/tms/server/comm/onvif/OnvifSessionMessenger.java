package us.mn.state.dot.tms.server.comm.onvif;

import jdk.nashorn.internal.runtime.ParserException;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.ControllerException;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.ParsingException;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetCapabilities;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetCapabilitiesResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetSystemDateAndTime;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetSystemDateAndTimeResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl.GetProfiles;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl.GetProfilesResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Capabilities;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.DateTime;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.MoveOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZConfiguration;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZNode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZSpaces;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Profile;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.SystemDateTime;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;
import us.mn.state.dot.tms.server.comm.onvif.session.WSUsernameToken;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import javax.naming.ldap.Control;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * An OnvifSessionMessenger caches device authentication session, capabilities,
 * and some constant device information for as long as it is open.
 *
 * Typical use:
 * 	1. instantiate this
 *  	2. setCamera() // required to display messages to client
 * 	3. setAuth()
 * 	3. open() // requires setAuth()
 * 	4. selectService)() // requires open()
 * 	5. makeRequest() // requires selectService() if making call to different
 * 		service than was previously selected
 *	6. repeat 4 & 5 as needed
 *	7. close()
 *
 * There are many cached values in the session. These are 'live' values (Java
 * values by reference like all others). If you are going to send a request to
 * update one of the values to the device, then you should also update the
 * cached value.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifSessionMessenger extends Messenger {
	private static final DebugLog ONVIF_SESSION_LOG = new DebugLog(
		"onvif");
	private static final DebugLog SOAP_LOG = new DebugLog("soap");
	private static final String DEVICE_SERVICE_PATH =
		"/onvif/device_service";

	// messenger properties
	private WSUsernameToken auth;
	private int timeout = 5000; // in milliseconds
	private URL baseUri;
	private URL currentUri;
	private SOAPConnection soapConnection;

	// we hold onto the CameraImpl to centralize status reporting
	private CameraImpl camera;

	// cached values required for any service requests
	private Capabilities capabilities;
	private List<Profile> mediaProfiles;

	/**
	 * @param uri including protocol (always http), ip, and, optionally,
	 * 	the port (always 80) and DEVICE_SERVICE_PATH path.
	 * @throws IOException if the currentUri is invalid
	 */
	public OnvifSessionMessenger(String uri) throws IOException {
		baseUri = checkUri(uri);
		currentUri = getDeviceServiceUri();
	}

	/**
	 * @return true if the auth credentials have been set
	 */
	boolean isAuthSet() {
		return auth != null;
	}

	/** set the auth credentials */
	void setAuth(String username, String password) {
		auth = new WSUsernameToken(username, password);
	}

	public void setCamera(CameraImpl c) {
		this.camera = c;
	}

	@Override
	public void open() throws IOException {
		log("Starting session. ", this);
		try {
			soapConnection = SOAPConnectionFactory.newInstance()
					.createConnection();
			setAuthClockOffset();
			capabilities = initCapabilities();
			mediaProfiles = initMediaProfiles();
		} catch (IOException e) {
			openFailed();
			throw e;
		} catch (SOAPException e) {
			openFailed();
			throw new IOException(e);
		}
		log("Session started. ", this);
	}

	@Override
	public void close() {
		log("Closing session. ", this);
		auth = null;
		// clear cached values
		capabilities = null;
		mediaProfiles = null;
		ptzSpaces = null;
		nodes = null;
		imagingMoveOptions = null;
		imagingSettings = null;
		imagingOptions = null;
		soapConnection = null;
		setStatus("Closed");
		log("Session closed. ", this);
	}

	@Override
	public void setTimeout(int t) {
		timeout = t;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the session currentUri for subsequent requests.
	 *
	 * @throws ServiceNotSupportedException if s is not supported
	 */
	void selectService(OnvifService s)
		throws ControllerException, ServiceNotSupportedException,
		MalformedURLException
	{
		if (capabilities == null && !s.equals(OnvifService.DEVICE))
			throw new ControllerException("Missing capabilities");
		switch (s) {
		case DEVICE:
			currentUri = getDeviceServiceUri();
			break;
		case MEDIA:
			currentUri = getMediaServiceUri();
			break;
		case PTZ:
			currentUri = getPTZServiceUri();
			break;
		case IMAGING:
			currentUri = getImagingServiceUri();
			break;
		default:
			throw new ServiceNotSupportedException(s);
		}
	}

	/**
	 * @return The first media profile token (all devices are required to
	 * 	have at least one media profile. This is generally required
	 * 	for PTZ and Imaging Service requests.
	 */
	public String getMediaProfileTok() throws ControllerException {
		if (mediaProfiles == null)
			throw new ControllerException("Missing media profile");
		return mediaProfiles.get(0).getToken();
	}

	/**
	 * Places the request to the currentUri and returns a response Class on
	 * success
	 *
	 * @param request the request xml object
	 * @param responseClass the response Object format
	 * @return the response xml Object
	 */
	public Object makeRequest(Object request, Class<?> responseClass)
		throws SocketTimeoutException,
		ControllerException, ParsingException
	{
		return _makeRequest(request, responseClass, true);
	}

	private Object _makeRequest(Object request, Class<?> responseClass,
								boolean withAuth)
			throws SocketTimeoutException, ControllerException,
			ParsingException
	{
		setStatus(request.getClass().getSimpleName() + "Request");
		try {
			SOAPMessage soap = SoapWrapper.newMessage(request);
			if (withAuth)
				SoapWrapper.addAuthHeader(soap, auth);
			logSoap("Request SOAPMessage", request.getClass(),
					responseClass, soap);
			SOAPMessage response =
					soapConnection.call(soap, currentUri);
			if (response.getSOAPBody().hasFault()) {
				logSoap("SOAPFault", request.getClass(),
						responseClass, response);
				throw new ParsingException(
						response.getSOAPBody().getFault()
								.getFaultString());
			}
			logSoap("Response SOAPMessage", request.getClass(),
					responseClass,
					response);
			Object o;
			try {
				o = SoapWrapper.convertToObject(
						response, responseClass);
			} catch (JAXBException e) {
				e.printStackTrace();
				log(e.getMessage(), this);
				throw new ParserException(e.getMessage());
			}
			return o;
		} catch (ParserConfigurationException
				| NoSuchAlgorithmException
				| JAXBException e) {
			System.err.println(
					"Unable to make request: " + e.getMessage());
			e.printStackTrace();
			log(e.getMessage(), this);
			throw new ControllerException(e.getMessage());
		} catch (SOAPException e) {
			log(e.getMessage(), this);
			if (e.getCause() != null
					&& e.getCause().getCause() != null
					&& e.getCause().getCause() instanceof
					SocketTimeoutException) {
				log("Hint: check URI, timeout, " +
								"and network connection. ",
						this);
				// this exception is expected by caller (e.g.
				// MessagePoller), so we strip off the wrappers
				throw (SocketTimeoutException)
						e.getCause().getCause();
			}
			int err = parseSoapErrStatus(e);
			String msg = knownStatuses(err);
			log(msg, this);
			throw new ControllerException(msg);
		}
	}

	private String knownStatuses(int httpStatus) {
		String reason;
		String error;
		switch (httpStatus) {
		case 400:
			reason = "Bad Request";
			error = "Malformed Request";
			break;
		case 401:
			reason = "Unauthorized";
			error = "Requires Authorization";
			break;
		case 403:
			// not an official ONVIF code, but some devices use it
			reason = "Forbidden";
			error = "Bad Username/Password";
			break;
		case 405:
			reason = "HTTP Method is neither POST or GET";
			error = "Method Not Allowed";
			break;
		case 415:
			reason = "Unsupported message encapsulation method";
			error = "Unsupported Media";
			break;
		default:
			reason = "" + httpStatus;
			error = "Unrecognized";
		}
		return "Status: " + httpStatus + ", " +
				"Reason: " + reason + ", " +
				"Error: " + error;
	}

	private int parseSoapErrStatus(SOAPException e)
		throws ControllerException
	{
		String msg = e.getMessage();
		String strB4Stats = "Bad response: (";
		if (!msg.contains(strB4Stats))
			throw new ControllerException(e.getMessage());
		int startI = msg.indexOf(strB4Stats);
		if (msg.length() < strB4Stats.length() + 3)
			throw new ControllerException(e.getMessage());
		String statusStr = msg.substring(startI + strB4Stats.length(),
			startI + strB4Stats.length() + 3);
		int status;
		try {
			status = Integer.parseInt(statusStr);
		} catch (NumberFormatException e1) {
			e.printStackTrace();
			throw new ControllerException(e.getMessage());
		}
		return status;
	}

	/**
	 * user input self defense
	 *
	 * @param uri gets checked
	 * @return a normalized version of the currentUri (protocol, ip, and
	 * 	port)
	 * @throws IOException if the currentUri is malformed
	 */
	private URL checkUri(String uri) throws IOException {
		if (uri == null)
			throw new IOException("URI not set. ");
		URL url = new URL(uri);
		if (!url.getProtocol().equalsIgnoreCase("http"))
			throw new IOException(
				"ONVIF URI protocol is not \"http\". ");
		if (!url.getPath().equals(""))
			throw new IOException(
				"ONVIF URI path may only be empty. ");
		return url;
	}

	/**
	 * Get our current timestamp and then make a request for the device's
	 * date and time. Next format the response, and send off to the auth to
	 * set the clock offset. Caution! If you set a breakpoint in this
	 * method, it may cause inaccurate calculation of device clock which
	 * may
	 * cause the device to reject requests.
	 */
	private void setAuthClockOffset() throws IOException {
		// add one second for travel delay as the ONVIF programmer
		// guide shows
		ZonedDateTime ourDateTime =
			ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(1);
		SystemDateTime response =
			((GetSystemDateAndTimeResponse) makeInternalRequest(
				new GetSystemDateAndTime(),
				GetSystemDateAndTimeResponse.class,
				OnvifService.DEVICE, false))
				.getSystemDateAndTime();
		DateTime deviceDT = response.getUTCDateTime();
		ZoneOffset zoneID = ZoneOffset.UTC;
		// we will may only get one of the time formats (UTC only
		// explicitly required by ONVIF 2.0), so local time is a backup
		if (deviceDT == null) {
			deviceDT = response.getLocalDateTime();
			zoneID = ZoneOffset.of(response.getTimeZone().getTZ());
		}
		ZonedDateTime deviceDateTime = ZonedDateTime.of(
			deviceDT.getDate().getYear(),
			deviceDT.getDate().getMonth(),
			deviceDT.getDate().getDay(),
			deviceDT.getTime().getHour(),
			deviceDT.getTime().getMinute(),
			deviceDT.getTime().getSecond(),
			0,
			zoneID);
		auth.setClockOffset(ourDateTime, deviceDateTime);
	}

	private Capabilities initCapabilities() throws IOException {
		GetCapabilities getCapabilities = new GetCapabilities();
		return ((GetCapabilitiesResponse) makeInternalRequest(
			getCapabilities, GetCapabilitiesResponse.class,
			OnvifService.DEVICE, true))
			.getCapabilities();
	}

	private List<Profile> initMediaProfiles() throws IOException {
		List<Profile> profiles =
			((GetProfilesResponse) makeInternalRequest(
				new GetProfiles(), GetProfilesResponse.class,
				OnvifService.MEDIA, true)).getProfiles();
		if (profiles == null
			|| profiles.size() < 1
			|| profiles.get(0).getToken() == null
			|| profiles.get(0).getToken().isEmpty())
			throw new ControllerException("Missing required media profile");
		return profiles;
	}

	/**
	 * For internal requests that might otherwise overwrite the external
	 * selectService() call set by OpOnvifs
	 */
	private Object makeInternalRequest(Object request,
					   Class<?> responseClass,
					   OnvifService service, boolean withAuth)
		throws ServiceNotSupportedException, ParsingException,
		SocketTimeoutException, ControllerException,
		MalformedURLException
	{
		URL savedUri = currentUri;
		selectService(service);
		try {
			return _makeRequest(request, responseClass, withAuth);
		} finally {
			currentUri = savedUri;
		}
	}

	private URL getDeviceServiceUri() throws MalformedURLException {
		return new URL(baseUri, DEVICE_SERVICE_PATH, streamHdlr());
	}

	private URL getMediaServiceUri()
		throws ServiceNotSupportedException, MalformedURLException
	{
		if (capabilities.getMedia() == null
			|| capabilities.getMedia().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.MEDIA);
		return buildUri(capabilities.getMedia().getXAddr());
	}

	private URL getPTZServiceUri()
		throws ServiceNotSupportedException, MalformedURLException
	{
		if (capabilities.getPTZ() == null
			|| capabilities.getPTZ().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.PTZ);
		return buildUri(capabilities.getPTZ().getXAddr());
	}

	private URL getImagingServiceUri()
		throws ServiceNotSupportedException, MalformedURLException
	{
		if (capabilities.getImaging() == null
			|| capabilities.getImaging().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.IMAGING);
		return buildUri(capabilities.getImaging().getXAddr());
	}

	/**
	 * Handles case where device may be behind a NAT.
	 *
	 * @param xAddr the path from xAddr will be used
	 * @return baseUri with the path from xAddr
	 */
	private URL buildUri(String xAddr) throws MalformedURLException {
		return new URL(baseUri,
			(new URL(xAddr)).getPath(),
			streamHdlr());
	}

	private URLStreamHandler streamHdlr() {
		return new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u)
				throws IOException
			{
				URL copy = new URL(u.toString());
				URLConnection connection =
					copy.openConnection();
				connection.setConnectTimeout(timeout);
				connection.setReadTimeout(timeout);
				return connection;
			}
		};
	}

	private void openFailed() {
		close();
		setStatus("Failed");
		log("Failed to start session. ", this);
	}

	public void log(String msg, Object reporter) {
		ONVIF_SESSION_LOG
			.log("<" + baseUri.getHost() + "> "
			+ reporter.getClass().getSimpleName() +  ": " + msg);
	}

	public void setStatus(String msg) {
		if (camera != null)
			camera.setOpStatus(msg);
	}

	/**
	 * Formats context information and msg and writes to soap logSoap file.
	 */
	private void logSoap(
		String context, Class<?> requestClass, Class<?> responseClass,
		SOAPMessage msg)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			msg.writeTo(out);
		} catch (SOAPException
			| IOException e) {
			System.err.println(
				"Could not convert SOAP message to string for "
					+ context + " to service: " + currentUri
					+ "\n");
			e.printStackTrace();
			return;
		}
		SOAP_LOG.log(
			context + "\n"
				+ "\tService: " + currentUri + "\n"
				+ "\tRequest class: " + requestClass.getSimpleName() + "\n"
				+ "\tExpected response class: " + responseClass.getSimpleName() + "\n"
				+ "\tSoap: " + new String(out.toByteArray()));
	}

	/******** cached values required for some service requests ********/

	private List<PTZConfiguration> ptzConfigurations;

	public List<PTZConfiguration> getPtzConfigurations() {
		return ptzConfigurations;
	}

	public void setPtzConfigurations(
		List<PTZConfiguration> ptzConfigurations)
	{
		this.ptzConfigurations = ptzConfigurations;
	}

	private PTZSpaces ptzSpaces;

	public PTZSpaces getPtzSpaces() {
		return ptzSpaces;
	}

	public void setPtzSpaces(PTZSpaces ptzSpaces) {
		this.ptzSpaces = ptzSpaces;
	}

	private List<PTZNode> nodes;

	public List<PTZNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<PTZNode> nodes) {
		this.nodes = nodes;
	}

	private ImagingSettings20 imagingSettings;

	public ImagingSettings20 getImagingSettings(){
		return imagingSettings;
	}

	public void setImagingSettings(
		ImagingSettings20 imagingSettings)
	{
		this.imagingSettings = imagingSettings;
	}

	private ImagingOptions20 imagingOptions;

	public ImagingOptions20 getImagingOptions() {
		return imagingOptions;
	}

	public void setImagingOptions(
		ImagingOptions20 imagingOptions)
	{
		this.imagingOptions = imagingOptions;
	}

	private MoveOptions20 imagingMoveOptions;

	public MoveOptions20 getImagingMoveOptions() {
		return imagingMoveOptions;
	}

	public void setImagingMoveOptions(
		MoveOptions20 imagingMoveOptions)
	{
		this.imagingMoveOptions = imagingMoveOptions;
	}
}
