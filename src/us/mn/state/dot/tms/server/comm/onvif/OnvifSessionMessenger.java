package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.comm.Messenger;
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
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZNode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZSpaces;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Profile;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.SystemDateTime;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptionsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetOptionsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurationOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurationOptionsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurations;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurationsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetNodes;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetNodesResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;
import us.mn.state.dot.tms.server.comm.onvif.session.WSUsernameToken;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotStartedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapTransmissionException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * An OnvifSessionMessenger caches device authentication session, capabilities,
 * and some constant device information for as long as the it is open.
 *
 * Typical use:
 * 	1. instantiate this
 * 	2. setAuth()
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

	// onvif session properties
	private URL deviceServiceUri;
	private WSUsernameToken auth;
	/**
	 * Our proxy for an open session are the capabilities. If they are set,
	 * we are open.
	 */
	private Capabilities capabilities;

	// messenger properties
	private String currentUri;
	private int timeout = 5000; // in milliseconds

	// cached onvif device values
	private List<Profile> mediaProfiles;
	private PTZSpaces ptzSpaces;
	private List<PTZNode> nodes;
	private ImagingOptions20 imagingOptions;
	private ImagingSettings20 imagingSettings;
	private MoveOptions20 imagingMoveOptions;

	/**
	 * @param uri including protocol (always http), ip, and, optionally,
	 * 	the port (always 80) and DEVICE_SERVICE_PATH path.
	 * @throws IOException if the currentUri is invalid
	 */
	public OnvifSessionMessenger(String uri) throws IOException {
		deviceServiceUri = normalizeDevServUri(uri);
	}

	/**
	 * @return true if the auth credentials have been set
	 */
	boolean authNotSet() {
		return auth == null;
	}

	/** set the auth credentials */
	void setAuth(String username, String password) {
		auth = new WSUsernameToken(username, password);
	}

	@Override
	public void open() throws IOException {
		log("Starting session... ");
		try {
			setAuthClockOffset();
			capabilities = initCapabilities();
			mediaProfiles = initMediaProfiles();
		} catch (SoapTransmissionException e) {
			log("Failed to start session. " + e.getMessage());
			close();
			throw e;
		}
		log("Session started. ");
	}

	@Override
	public void close() {
		log("Closing session... ");
		auth = null;
		capabilities = null;
		mediaProfiles = null;
		ptzSpaces = null;
		nodes = null;
		imagingMoveOptions = null;
		imagingSettings = null;
		imagingOptions = null;
		log("Session closed. ");
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
	 * @throws SessionNotStartedException if this is not initialized
	 * @throws ServiceNotSupportedException if s is not supported
	 */
	void selectService(OnvifService s)
		throws SessionNotStartedException,
		ServiceNotSupportedException
	{
		if (capabilities == null && !s.equals(OnvifService.DEVICE))
			throw new SessionNotStartedException(
				"Capabilities not found. ");
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
	 * 	have at least one media profile. This is frequently required
	 * 	for PTZ and Imaging Service requests.
	 */
	public String getMediaProfileTok() throws SessionNotStartedException {
		if (mediaProfiles == null)
			throw new SessionNotStartedException(
				"No media token found. ");
		return mediaProfiles.get(0).getToken();
	}

	/**
	 * @return PTZ spaces correspond to the different types of PTZ requests
	 * @throws ServiceNotSupportedException if the PTZ Service is not
	 * 	supported
	 * @throws SessionNotStartedException if this is not initialized
	 * @throws SoapTransmissionException if the request fails (unexpected)
	 */
	public PTZSpaces getPtzSpaces()
		throws ServiceNotSupportedException, SoapTransmissionException,
		SessionNotStartedException
	{
		if (ptzSpaces == null)
			ptzSpaces = initPTZSpaces();
		return ptzSpaces;
	}

	/**
	 * @return all the PTZ Nodes (provides ranges for PTZ request values)
	 * @throws ServiceNotSupportedException if the PTZ Service is not
	 * 	supported
	 * @throws SessionNotStartedException if this is not initialized
	 * @throws SoapTransmissionException if the request fails
	 */
	public List<PTZNode> getNodes()
		throws ServiceNotSupportedException,
		SessionNotStartedException, SoapTransmissionException
	{
		if (nodes == null)
			nodes = initNodes();
		return nodes;
	}

	public ImagingSettings20 getImagingSettings()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		if (imagingSettings == null)
			imagingSettings = initImagingSettings();
		return imagingSettings;
	}

	public ImagingOptions20 getImagingOptions()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		if (imagingOptions == null)
			imagingOptions = initImagingOptions();
		return imagingOptions;
	}

	/**
	 * @return supported focus move requests and ranges
	 * @throws ServiceNotSupportedException if the Imaging Service is not
	 * 	supported
	 * @throws SessionNotStartedException if this is not initialized
	 * @throws SoapTransmissionException if the request fails
	 */
	public MoveOptions20 getImagingMoveOptions()
		throws ServiceNotSupportedException, SoapTransmissionException,
		SessionNotStartedException
	{
		if (imagingMoveOptions == null)
			imagingMoveOptions = initImagingMoveOptions();
		return imagingMoveOptions;
	}

	/**
	 * Places the request to the currentUri and returns a response Class on
	 * success
	 *
	 * @param request the request xml object
	 * @param responseClass the response Object format
	 * @return the response xml Object
	 * @throws SoapTransmissionException if there is a problem during
	 * 	communication or soap formatting or transmission
	 */
	public Object makeRequest(Object request, Class<?> responseClass)
		throws SoapTransmissionException
	{
		try {
			SOAPMessage soap = SoapWrapper.newMessage(request);
			SoapWrapper.addAuthHeader(soap, auth);
			SOAPConnection soapConnection =
				SOAPConnectionFactory.newInstance()
					.createConnection();
			logSoap("Request SOAPMessage", request.getClass(),
				responseClass, soap);
			SOAPMessage response =
				soapConnection.call(soap, currentUri);
			if (response.getSOAPBody().hasFault()) {
				logSoap("SOAPFault", request.getClass(),
					responseClass, response);
				throw new SoapTransmissionException(
					response.getSOAPBody().getFault()
						.getFaultString());
			}
			logSoap("Response SOAPMessage", request.getClass(),
				responseClass,
				response);
			return SoapWrapper
				.convertToObject(response, responseClass);
		} catch (JAXBException
			| ParserConfigurationException
			| NoSuchAlgorithmException e) {
			System.err.println(
				"Unable to make request: " + e.getMessage());
			throw new SoapTransmissionException(e);
		} catch (SOAPException e) {
			int err = parseSoapErrStatus(e);
			if (err >= 400 && err <= 403)
				throw new SoapTransmissionException(
					"Bad username or password. ", e);
			throw new SoapTransmissionException(e);
		}
	}

	private int parseSoapErrStatus(SOAPException e)
		throws SoapTransmissionException
	{
		String msg = e.getMessage();
		String strB4Stats = "Bad response: (";
		if (!msg.contains(strB4Stats))
			throw new SoapTransmissionException(e);
		int startI = msg.indexOf(strB4Stats);
		if (msg.length() < strB4Stats.length() + 3)
			throw new SoapTransmissionException(e);
		String statusStr = msg.substring(startI + strB4Stats.length(),
			startI + strB4Stats.length() + 3);
		int status;
		try {
			status = Integer.parseInt(statusStr);
		} catch (NumberFormatException e1) {
			throw new SoapTransmissionException(e);
		}
		return status;
	}

	/**
	 * user input self defense
	 *
	 * @param uri gets checked
	 * @return a normalized version of the currentUri (protocol, ip, and
	 * 	port)
	 * @throws IllegalArgumentException if the currentUri is malformed
	 */
	private URL normalizeDevServUri(String uri) throws IOException {
		URL url = new URL(uri);
		if (url.getPath().equals(""))
			url = new URL(url.getProtocol()
				+ "://"
				+ url.getAuthority()
				+ DEVICE_SERVICE_PATH);
		else
			throw new IOException(
				"ONVIF URI path may only be \""
					+ DEVICE_SERVICE_PATH
					+ "\" or empty. ");
		if (!url.getProtocol().equalsIgnoreCase("http"))
			throw new IOException(
				"ONVIF URI protocol is not \"http\". ");
		if (url.getPort() != 80)
			throw new IOException(
				"ONVIF URI port is not \"80\". ");
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
	private void setAuthClockOffset()
		throws SoapTransmissionException, SessionNotStartedException,
		ServiceNotSupportedException
	{
		// add one second for travel delay as the ONVIF programmer
		// guide shows
		ZonedDateTime ourDateTime =
			ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(1);
		SystemDateTime response =
			((GetSystemDateAndTimeResponse) makeInternalRequest(
				new GetSystemDateAndTime(),
				GetSystemDateAndTimeResponse.class,
				OnvifService.DEVICE))
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

	private Capabilities initCapabilities()
		throws SoapTransmissionException, SessionNotStartedException,
		ServiceNotSupportedException
	{
		GetCapabilities getCapabilities = new GetCapabilities();
		return ((GetCapabilitiesResponse) makeInternalRequest(
			getCapabilities, GetCapabilitiesResponse.class,
			OnvifService.DEVICE))
			.getCapabilities();
	}

	private List<Profile> initMediaProfiles()
		throws SessionNotStartedException,
		ServiceNotSupportedException,
		SoapTransmissionException
	{
		List<Profile> profiles =
			((GetProfilesResponse) makeInternalRequest(
				new GetProfiles(), GetProfilesResponse.class,
				OnvifService.MEDIA)).getProfiles();
		if (profiles == null
			|| profiles.size() < 1
			|| profiles.get(0).getToken() == null
			|| profiles.get(0).getToken().isEmpty())
			throw new SessionNotStartedException(
				"Missing required Media Profile. ");
		return profiles;
	}

	private PTZSpaces initPTZSpaces()
		throws SessionNotStartedException,
		ServiceNotSupportedException,
		SoapTransmissionException
	{
		GetConfigurationsResponse getConfigurationsResponse =
			(GetConfigurationsResponse) makeInternalRequest(
				new GetConfigurations(),
				GetConfigurationsResponse.class,
				OnvifService.PTZ);
		GetConfigurationOptions getConfigurationOptions =
			new GetConfigurationOptions();
		String token =
			getConfigurationsResponse.getPTZConfiguration().get(0)
				.getToken();
		getConfigurationOptions.setConfigurationToken(token);
		GetConfigurationOptionsResponse
			getConfigurationOptionsResponse =
			(GetConfigurationOptionsResponse) makeInternalRequest(
				getConfigurationOptions,
				GetConfigurationOptionsResponse.class,
				OnvifService.PTZ);
		return getConfigurationOptionsResponse
			.getPTZConfigurationOptions().getSpaces();
	}

	private List<PTZNode> initNodes()
		throws SessionNotStartedException,
		ServiceNotSupportedException,
		SoapTransmissionException
	{
		GetNodes getNodes =
			new GetNodes();
		return ((GetNodesResponse) makeInternalRequest(getNodes,
			GetNodesResponse.class, OnvifService.PTZ)).getPTZNode();
	}

	private ImagingOptions20 initImagingOptions()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		GetOptions getOptions = new GetOptions();
		getOptions.setVideoSourceToken(getMediaProfileTok());
		GetOptionsResponse getOptionsResponse =
			(GetOptionsResponse) makeInternalRequest(
				getOptions, GetOptionsResponse.class,
				OnvifService.IMAGING);
		return getOptionsResponse.getImagingOptions();
	}

	private ImagingSettings20 initImagingSettings()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		GetImagingSettings request = new GetImagingSettings();
		request.setVideoSourceToken(getMediaProfileTok());
		GetImagingSettingsResponse response =
			(GetImagingSettingsResponse) makeInternalRequest(
				request, GetImagingSettingsResponse.class,
				OnvifService.IMAGING);
		return response.getImagingSettings();
	}

	private MoveOptions20 initImagingMoveOptions()
		throws ServiceNotSupportedException,
		SessionNotStartedException, SoapTransmissionException
	{
		GetMoveOptions getMoveOptions = new GetMoveOptions();
		getMoveOptions.setVideoSourceToken(getMediaProfileTok());
		return ((GetMoveOptionsResponse) makeInternalRequest(
			getMoveOptions, GetMoveOptionsResponse.class,
			OnvifService.IMAGING)).getMoveOptions();
	}

	/**
	 * For internal requests that might overwrite the external
	 * selectService() call.
	 */
	private Object makeInternalRequest(Object request, Class<?> responseClass,
					   OnvifService service)
		throws SessionNotStartedException, ServiceNotSupportedException,
		SoapTransmissionException
	{
		String savedUri = currentUri;
		selectService(service);
		try {
			return makeRequest(request, responseClass);
		} finally {
			currentUri = savedUri;
		}
	}

	private String getDeviceServiceUri() {
		String uri;
		if (capabilities == null
			|| capabilities.getDevice() == null
			|| capabilities.getDevice().getXAddr() == null)
			uri = deviceServiceUri.toString();
		else
			uri = capabilities.getDevice().getXAddr();
		return uri;
	}

	private String getMediaServiceUri()
		throws ServiceNotSupportedException
	{
		if (capabilities.getMedia() == null
			|| capabilities.getMedia().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.MEDIA);
		return capabilities.getMedia().getXAddr();
	}

	private String getPTZServiceUri() throws ServiceNotSupportedException {
		if (capabilities.getPTZ() == null
			|| capabilities.getPTZ().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.PTZ);
		return capabilities.getPTZ().getXAddr();
	}

	private String getImagingServiceUri()
		throws ServiceNotSupportedException
	{
		if (capabilities.getImaging() == null
			|| capabilities.getImaging().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.IMAGING);
		return capabilities.getImaging().getXAddr();
	}

	public void log(String msg) {
		ONVIF_SESSION_LOG
			.log("<" + deviceServiceUri.getHost() + "> " + msg);
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
		} catch (Exception e) {
			System.err.println(
				"Could not convert SOAP message to string for "
					+ context + " to service: " + currentUri
					+ "\n");
			e.printStackTrace();
			return;
		}
		SOAP_LOG.log(
			context + "\n"
				+ "Service: " + currentUri +
				"\n"
				+ "Request class: " + requestClass
				.getSimpleName() + "\n"
				+ "Expected response class: " + responseClass
				.getSimpleName() + "\n"
				+ "Soap: " + new String(out.toByteArray()));
	}
}
