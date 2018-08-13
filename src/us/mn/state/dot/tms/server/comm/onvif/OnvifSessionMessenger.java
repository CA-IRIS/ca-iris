package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetCapabilities;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetCapabilitiesResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetSystemDateAndTime;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetSystemDateAndTimeResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl.GetProfiles;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl.GetProfilesResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Capabilities;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.*;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptionsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.*;
import us.mn.state.dot.tms.server.comm.onvif.session.HttpMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;
import us.mn.state.dot.tms.server.comm.onvif.session.WSUsernameToken;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotInitializedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapWrapperException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Keeps track of device authentication session, capabilities, and some constant
 * device information.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifSessionMessenger extends HttpMessenger {

	private static final String DEVICE_SERVICE_PATH =
		"/onvif/device_service";
	private String baseUri;
	private boolean initialized = false;
	private WSUsernameToken auth;
	private Capabilities capabilities;
	private List<Profile> mediaProfiles;
	private PTZSpaces ptzSpaces;
	private List<PTZNode> nodes;
	private MoveOptions20 imagingMoveOptions;

	/**
	 * @param uri including protocol (always http), ip, and, optionally,
	 * 	the port (always 80)
	 * @throws IOException if the uri is invalid
	 */
	public OnvifSessionMessenger(String uri) throws IOException {
		super();
		try {
			baseUri = normalizeUri(uri);
		} catch (IllegalArgumentException e) {
			throw new IOException("Bad ONVIF URI: " + uri, e);
		}
		setUri(getDeviceServiceUri());
	}

	/** @return true iff the device is ready for service requests */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets this device's authentication. Must be set before the device can
	 * be initialized. Neither username nor password may be null!
	 */
	void setAuth(String username, String password) {
		close();
		auth = new WSUsernameToken(username, password);
	}

	/**
	 * Initializes the session with device
	 *
	 * @throws IOException if there are problems opening the connection
	 * @throws SessionNotInitializedException if this was not able to init
	 * @throws ServiceNotSupportedException if there was an error during
	 * 	init
	 * @throws SoapWrapperException if there was an error placing an init
	 * 	request
	 */
	@Override
	public void open() throws SessionNotInitializedException,
		ServiceNotSupportedException, SoapWrapperException,
		IOException
	{
		initialize();
		super.open();
	}

	/**
	 * Get this ready to re-initialize.
	 */
	@Override
	public void close() {
		initialized = false;
		super.close();
	}

	/**
	 * Sets the session uri for subsequent requests.
	 *
	 * @throws SessionNotInitializedException if this is not initialized
	 * @throws ServiceNotSupportedException if s is not supported
	 */
	public void setUri(OnvifService s)
		throws SessionNotInitializedException,
		ServiceNotSupportedException
	{
		if (capabilities == null)
			throw new SessionNotInitializedException();
		switch (s) {
		case DEVICE:
			setUri(getDeviceServiceUri());
			break;
		case MEDIA:
			setUri(getMediaServiceUri());
			break;
		case PTZ:
			setUri(getPTZServiceUri());
			break;
		case IMAGING:
			setUri(getImagingServiceUri());
			break;
		}
	}

	/**
	 * Places the request to the selected service uri and returns a
	 * response
	 * Class
	 *
	 * @param request the request xml object
	 * @param responseClass the response Object format
	 * @return the response xml Object
	 * @throws SoapWrapperException if there is a problem during
	 * 	communication or soap formatting
	 */
	public Object makeRequest(Object request, Class<?> responseClass)
		throws SoapWrapperException
	{
		return SoapWrapper
			.callWebService(request, getUri(), responseClass,
				auth);
	}

	/**
	 * @return all the PTZ Nodes (provides ranges for PTZ request values)
	 */
	public List<PTZNode> getNodes()
		throws ServiceNotSupportedException,
		SessionNotInitializedException, SoapWrapperException
	{
		if (nodes == null) {
			setUri(OnvifService.PTZ);
			GetNodes getNodes = new GetNodes();
			nodes = ((GetNodesResponse) makeRequest(getNodes,
				GetNodesResponse.class)).getPTZNode();
		}
		return nodes;
	}

	/**
	 * @return The first media profile token (all devices are required to
	 * 	have at least one media profile. This is frequently required
	 * 	for PTZ
	 * 	and Imaging Service requests.
	 * @throws ServiceNotSupportedException if the Media Service is not
	 * 	supported (unexpected)
	 * @throws SessionNotInitializedException if this is not initialized
	 * @throws SoapWrapperException if the request fails (unexpected)
	 */
	public String getMediaProfileTok()
		throws ServiceNotSupportedException,
		SessionNotInitializedException, SoapWrapperException
	{
		if (mediaProfiles == null)
			mediaProfiles = initMediaProfiles();
		// all devices are required to have at least one media
		// profile
		return mediaProfiles.get(0).getToken();
	}

	/**
	 * @return PTZ spaces correspond to the different types of PTZ requests
	 * @throws ServiceNotSupportedException if the PTZ Service is not
	 * 	supported
	 * @throws SessionNotInitializedException if this is not initialized
	 * @throws SoapWrapperException if the request fails (unexpected)
	 */
	public PTZSpaces getPtzSpaces()
		throws ServiceNotSupportedException, SoapWrapperException,
		SessionNotInitializedException
	{
		if (ptzSpaces == null)
			ptzSpaces = initPTZSpaces();
		return ptzSpaces;
	}

	/**
	 * @return supported focus move requests and ranges
	 * @throws ServiceNotSupportedException if the Imaging Service is not
	 * 	supported
	 * @throws SessionNotInitializedException if this is not initialized
	 * @throws SoapWrapperException if the request fails (unexpected)
	 */
	public MoveOptions20 getImagingMoveOptions()
		throws ServiceNotSupportedException, SoapWrapperException,
		SessionNotInitializedException
	{
		if (imagingMoveOptions == null)
			imagingMoveOptions = initImagingMoveOptions();
		return imagingMoveOptions;
	}

	/**
	 * user input self defense
	 *
	 * @param uri gets checked
	 * @return a normalized version of the uri (protocol, ip, and port)
	 * @throws IllegalArgumentException if the uri is malformed
	 */
	private String normalizeUri(String uri)
		throws IllegalArgumentException
	{
		// basic null, protocol, and form check
		URL url;
		try {
			url = new URL(uri);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
		// protocol check
		if (!url.getProtocol().equalsIgnoreCase("http"))
			throw new IllegalArgumentException(
				"ONVIF URI protocol is not http. ");
		// path normalization and check
		String tmp = uri;
		if (tmp.endsWith("/"))
			tmp = uri.substring(0, uri.length() - 2);
		if (tmp.endsWith(DEVICE_SERVICE_PATH))
			tmp = uri.substring(0, tmp.length()
				- DEVICE_SERVICE_PATH.length() - 1);
		// strip off the protocol and port
		String uriParts[] =
			tmp.substring("http://".length()).split(":");
		// check port
		if (uriParts.length != 1) {
			if (uriParts.length != 2)
				throw new IllegalArgumentException(
					"ONVIF port is malformed. ");
			if (!uriParts[1].equals("80"))
				throw new IllegalArgumentException(
					"ONVIF port is not 80. ");
		}
		// check the ip
		String ipParts[] = uriParts[0].split("\\.");
		if (ipParts.length != 4)
			throw new IllegalArgumentException(
				"ONVIF IP does not have four parts. ");
		for (String p : ipParts)
			if (!p.matches("[0-9]+"))
				throw new IllegalArgumentException(
					"ONVIF IP contains values that are " +
						"not numbers. ");
		return tmp;
	}

	/**
	 * Establish a session with the device, determine the device's
	 * capabilities, and get the media profiles (which sets the default
	 * profile token).
	 *
	 * @throws SessionNotInitializedException if the session could not be
	 * 	initialized
	 * @throws SoapWrapperException if there was a soap error (unexpected)
	 * @throws ServiceNotSupportedException if there was an issue with the
	 * 	media service (unexpected)
	 */
	private void initialize()
		throws SessionNotInitializedException, SoapWrapperException,
		ServiceNotSupportedException
	{
		log("Attempting to start session... ");
		if (auth == null)
			throw new SessionNotInitializedException();
		// for safety we will set uri again (ignored if already set to
		// the correct Device Service URI).
		setUri(baseUri + "/onvif/device_service");
		try {
			setAuthClockOffset();
		} catch (SoapWrapperException e) {
			if (e.getHttpErr() != null)
				switch (e.getHttpErr()) {
				case 400:
				case 401:
				case 402:
				case 403:
					throw new SessionNotInitializedException(
						"username or password");
				case 404:
					throw new SessionNotInitializedException(
						"URI");
				}
			throw new SessionNotInitializedException(e);
		}
		GetCapabilities getCapabilities = new GetCapabilities();
		capabilities =
			((GetCapabilitiesResponse) makeRequest(getCapabilities,
				GetCapabilitiesResponse.class))
				.getCapabilities();
		mediaProfiles = initMediaProfiles();
		initialized = true;
		log("Session started. ");
	}

	/**
	 * Get our current timestamp and then make a request for the device's
	 * date and time. Next format the response, and send off to the auth to
	 * set the clock offset
	 */
	private void setAuthClockOffset() throws SoapWrapperException {
		GetSystemDateAndTime request = new GetSystemDateAndTime();
		SystemDateTime response;
		// add one second for travel delay as the ONVIF programmer
		// guide does
		ZonedDateTime ourDateTime = ZonedDateTime
			.now(ZoneOffset.UTC).plusSeconds(1);
		response = ((GetSystemDateAndTimeResponse) makeRequest(
			request,
			GetSystemDateAndTimeResponse.class))
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

	private List<Profile> initMediaProfiles()
		throws SessionNotInitializedException,
		ServiceNotSupportedException, SoapWrapperException
	{
		setUri(OnvifService.MEDIA);
		List<Profile> profiles;
		GetProfiles getProfiles = new GetProfiles();
		profiles = ((GetProfilesResponse) (makeRequest(getProfiles,
			GetProfilesResponse.class))).getProfiles();
		if (profiles.size() < 1
			|| profiles.get(0).getToken() == null
			|| profiles.get(0).getToken().isEmpty())
			throw new ServiceNotSupportedException(
				OnvifService.MEDIA,
				"Missing required Media Profile. ");
		return profiles;
	}

	private PTZSpaces initPTZSpaces() throws SessionNotInitializedException,
		ServiceNotSupportedException, SoapWrapperException
	{
		setUri(OnvifService.PTZ);
		GetConfigurations getConfigurations = new GetConfigurations();
		GetConfigurationOptions getConfigurationOptions =
			new GetConfigurationOptions();
		GetConfigurationsResponse getConfigurationsResponse =
			(GetConfigurationsResponse) makeRequest(
				getConfigurations,
				GetConfigurationsResponse.class);
		String token =
			getConfigurationsResponse.getPTZConfiguration().get(0)
				.getToken();
		getConfigurationOptions.setConfigurationToken(token);
		// the getConfigurationOptionsResponse has info about the
		// Spaces of movement and their range limits
		GetConfigurationOptionsResponse
			getConfigurationOptionsResponse =
			(GetConfigurationOptionsResponse) makeRequest(
				getConfigurationOptions,
				GetConfigurationOptionsResponse.class);
		return getConfigurationOptionsResponse
			.getPTZConfigurationOptions().getSpaces();
	}

	private MoveOptions20 initImagingMoveOptions()
		throws ServiceNotSupportedException,
		SessionNotInitializedException, SoapWrapperException
	{
		setUri(OnvifService.IMAGING);
		GetMoveOptions getMoveOptions = new GetMoveOptions();
		getMoveOptions.setVideoSourceToken(getMediaProfileTok());
		return ((GetMoveOptionsResponse) makeRequest(getMoveOptions,
			GetMoveOptionsResponse.class)).getMoveOptions();
	}

	private String getDeviceServiceUri() {
		String uri;
		if (capabilities.getDevice() == null
			|| capabilities.getDevice().getXAddr() == null)
			uri = baseUri + DEVICE_SERVICE_PATH;
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

	private void log(String msg) {
		OnvifPoller.log("Session (" + baseUri + "): " + msg);
	}
}
