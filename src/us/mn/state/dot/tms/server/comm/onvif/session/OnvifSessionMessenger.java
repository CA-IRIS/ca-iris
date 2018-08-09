package us.mn.state.dot.tms.server.comm.onvif.session;

import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
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
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotInitializedException;

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

	private boolean initialized = false;
	private WSUsernameToken auth;
	private Capabilities capabilities;
	private List<Profile> mediaProfiles;
	private PTZSpaces ptzSpaces;
	private List<PTZNode> nodes;
	private MoveOptions20 imagingMoveOptions;

	/** @return true iff the device is ready for service requests */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets this device's authentication. Must be set before the device can
	 * be initialized.
	 */
	public void setAuth(String username, String password) {
		close();
		auth = new WSUsernameToken(username, password);
	}

	/**
	 * @return all the PTZ Nodes (provides ranges for PTZ request values)
	 * @throws IOException if the device does not support PTZ or is not
	 * 	initialized
	 */
	public List<PTZNode> getNodes()
		throws IOException, ServiceNotSupportedException
	{
		if (!initialized)
			initialize();
		if (nodes == null) {
			selectService(OnvifService.PTZ);
			GetNodes getNodes = new GetNodes();
			nodes = ((GetNodesResponse) makeRequest(getNodes, GetNodesResponse.class)).getPTZNode();
		}
		return nodes;
	}

	/**
	 * @return The first media profile token (all devices required to have
	 * 	at least one media profile. This is frequently required for
	 * 	PTZ and
	 * 	Imaging service requests.
	 */
	public String getMediaProfileTok()
		throws IOException, ServiceNotSupportedException
	{
		if (!initialized)
			initialize();
		if (mediaProfiles == null)
			mediaProfiles = initMediaProfiles();
		// all devices are required to have at least one media
		// profile
		return mediaProfiles.get(0).getToken();
	}

	/**
	 * @return PTZ spaces correspond to the different types of PTZ requests
	 * @throws IOException if the device does not support the PTZ service
	 * 	or the the device is not initialized.
	 */
	public PTZSpaces getPtzSpaces()
		throws IOException, ServiceNotSupportedException
	{
		if (!initialized)
			initialize();
		if (ptzSpaces == null)
			ptzSpaces = initPTZSpaces();
		return ptzSpaces;
	}

	/**
	 * @return supported focus move requests and ranges
	 * @throws IOException if the device does not support Imaging Service *
	 * 	requests or the device is not initialized
	 */
	public MoveOptions20 getImagingMoveOptions()
		throws IOException, ServiceNotSupportedException
	{
		if (!initialized)
			initialize();
		if (imagingMoveOptions == null)
			imagingMoveOptions = initImagingService();
		return imagingMoveOptions;
	}

	/**
	 * @param uri including protocol (always http), ip, and, optionally,
	 * 	the port (always 80)
	 * @throws IllegalArgumentException if the uri is malformed
	 */
	public OnvifSessionMessenger(String uri)
		throws IllegalArgumentException
	{
		// the /onvif/device_service is the Device Service URI
		// path for all ONVIF devices
		super(uri + "/onvif/device_service");
		checkUri(uri);
	}

	/**
	 * Initializes the session with device
	 *
	 * @throws IOException if there are problems opening the connection
	 */
	@Override
	public void open() throws IOException {
		initialize();
		super.open();
	}

	@Override
	public void close() {
		initialized = false;
		super.close();
	}

	/**
	 * user input self defense
	 *
	 * @param uri gets checked
	 * @throws IllegalArgumentException if the uri is malformed
	 */
	private void checkUri(String uri) throws IllegalArgumentException {
		URL url;
		try {
			// basic null, protocol, and form check
			url = new URL(uri);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		if (!url.getProtocol().equalsIgnoreCase("http"))
			throw new IllegalArgumentException(
				"onvif uri missing http protocol");
		// strip off the protocol and port
		String uriParts[] =
			uri.substring("http://".length()).split(":");
		// check port
		if (uriParts.length != 1) {
			if (uriParts.length != 2)
				throw new IllegalArgumentException(
					"onvif port incorrectly specified");
			if (!uriParts[1].equals("80"))
				throw new IllegalArgumentException(
					"onvif restricted to port 80");
		}
		// check the ip
		String ipParts[] = uriParts[0].split("\\.");
		if (ipParts.length != 4)
			throw new IllegalArgumentException(
				"onvif ip does not have four parts");
		for (String p : ipParts)
			if (!p.matches("[0-9]+"))
				throw new IllegalArgumentException(
					"onvif ip contains values that are " +
						"not numbers");
	}

	/**
	 * Establish a session with the device and determine the device's
	 * capabilities.
	 */
	public void initialize() throws IOException
	{
		log("Attempting to start session for " + getUri());
		if (auth == null)
			throw new IOException("Auth must be set to " +
				"initialize session");
		setAuthClockOffset();
		try {
			GetCapabilities getCapabilities =
				new GetCapabilities();
			capabilities =
				((GetCapabilitiesResponse) makeRequest(getCapabilities,
					GetCapabilitiesResponse.class))
					.getCapabilities();
		} catch (IOException e) {
			throw new SessionNotInitializedException();
		}
		try {
			mediaProfiles = initMediaProfiles();
		} catch (ServiceNotSupportedException e) {
			throw new IOException("Media Service is required. ", e);
		}
		initialized = true;
		log("Session started");
	}

	/**
	 * Get our current timestamp and then device's date and time, format,
	 * and send off to the auth to set the clock offset
	 *
	 * @throws IOException communication error with device
	 */
	private void setAuthClockOffset() throws IOException {
		GetSystemDateAndTime request = new GetSystemDateAndTime();
		SystemDateTime response;
		// add one second for travel delay as the ONVIF programmer
		// guide does
		ZonedDateTime ourDateTime = ZonedDateTime
			.now(ZoneOffset.UTC).plusSeconds(1);
		try {
			response = ((GetSystemDateAndTimeResponse) makeRequest(
				request,
				GetSystemDateAndTimeResponse.class))
				.getSystemDateAndTime();
		} catch (IOException e) {
			throw new IOException(
				"Could not get device time; Check device: " +
					getUri(), e);
		}
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

	/**
	 * Initializes all static Media Service fields for this session.
	 *
	 * @throws IOException Device error (all devices must support the Media
	 * 	Service and at least one profile)
	 */
	private List<Profile> initMediaProfiles()
		throws IOException, ServiceNotSupportedException
	{
		selectService(OnvifService.MEDIA);
		List<Profile> profiles;
		GetProfiles getProfiles = new GetProfiles();
		profiles = ((GetProfilesResponse) (makeRequest(getProfiles,
			GetProfilesResponse.class))).getProfiles();
		if (profiles.size() < 1
			|| profiles.get(0).getToken() == null
			|| profiles.get(0).getToken().isEmpty())
			throw new IOException(
				"Device error: missing required Media " +
					"Profile" +
					" token");
		return profiles;
	}

	private PTZSpaces initPTZSpaces()
		throws IOException, ServiceNotSupportedException
	{
		selectService(OnvifService.PTZ);
		GetConfigurations getConfigurations = new GetConfigurations();
		GetConfigurationOptions getConfigurationOptions =
			new GetConfigurationOptions();
		GetConfigurationsResponse getConfigurationsResponse =
			(GetConfigurationsResponse) makeRequest(getConfigurations,
				GetConfigurationsResponse.class);
		String token =
			getConfigurationsResponse.getPTZConfiguration().get(0)
				.getToken();
		getConfigurationOptions.setConfigurationToken(token);
		// the getConfigurationOptionsResponse has info about the
		// Spaces of movement and their range limits
		GetConfigurationOptionsResponse
			getConfigurationOptionsResponse =
			(GetConfigurationOptionsResponse) makeRequest(getConfigurationOptions,
				GetConfigurationOptionsResponse.class);
		return getConfigurationOptionsResponse
			.getPTZConfigurationOptions().getSpaces();
	}

	/**
	 * Initializes all static Imaging Service fields for this session.
	 */
	private MoveOptions20 initImagingService()
		throws IOException, ServiceNotSupportedException
	{
		selectService(OnvifService.IMAGING);
		GetMoveOptions getMoveOptions = new GetMoveOptions();
		getMoveOptions.setVideoSourceToken(getMediaProfileTok());
		return ((GetMoveOptionsResponse) makeRequest(getMoveOptions,
			GetMoveOptionsResponse.class)).getMoveOptions();
	}

// todo implement or remove
//	public void callNoWait(Object request, OutputStream os)
//		throws IOException
//	{
//		try {
//			SoapWrapper soap = new SoapWrapper(request);
//			soap.callSoapWebService(getUri(), auth, os);
//		} catch (Exception e) {
//			throw new IOException(
//				"Failed to send request to device", e);
//		}
//	}
//
//	/**
//	 * Adds authentication and calls the specified service with the request
//	 * object
//	 */
//	public Object makeRequest(
//		OnvifService s, Object request, Class<?> responseClass)
//		throws IOException
//	{
//		try {
//			selectService(s);
//		} catch (ServiceNotSupportedException e) {
//			throw new IOException(e);
//		}
//		return makeRequest(request, responseClass);
//	}

	/**
	 * service must be selected before makeRequest
	 */
	public Object makeRequest(Object request, Class<?> responseClass)
		throws IOException
	{
		try {
			SoapWrapper soap = new SoapWrapper(request);
			return soap.callSoapWebService(getUri(),
				responseClass, auth);
		} catch (Exception e) {
			throw new IOException("Could not make Onvif request",
				e);
		}
	}

	public void selectService(OnvifService s)
		throws SessionNotInitializedException,
		ServiceNotSupportedException
	{
		if (capabilities == null)
			throw new SessionNotInitializedException(
				"Device not yet initialized. ");
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
	 * @throws ServiceNotSupportedException if the device has Device
	 * 	Service capabilities (should never be thrown if device is
	 * 	initialized)
	 */
	private String getDeviceServiceUri()
		throws ServiceNotSupportedException
	{
		if (capabilities.getDevice() == null
			|| capabilities.getDevice().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.DEVICE);
		return capabilities.getDevice().getXAddr();
	}

	/**
	 * @throws ServiceNotSupportedException if the device has Media Service
	 * 	capabilities (should never be thrown if device is initialized)
	 */
	private String getMediaServiceUri()
		throws ServiceNotSupportedException
	{
		if (capabilities.getMedia() == null
			|| capabilities.getMedia().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.MEDIA);
		return capabilities.getMedia().getXAddr();
	}

	/**
	 * @throws ServiceNotSupportedException if the device does not have PTZ
	 * 	Service capabilities
	 */
	private String getPTZServiceUri() throws ServiceNotSupportedException {
		if (capabilities.getPTZ() == null
			|| capabilities.getPTZ().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.PTZ);
		return capabilities.getPTZ().getXAddr();
	}

	/**
	 * @throws ServiceNotSupportedException if the device does not have
	 * 	Imaging Service capabilities
	 */
	private String getImagingServiceUri()
		throws ServiceNotSupportedException
	{
		if (capabilities.getImaging() == null
			|| capabilities.getImaging().getXAddr() == null)
			throw new ServiceNotSupportedException(
				OnvifService.IMAGING);
		return capabilities.getImaging().getXAddr();
	}

	private static void log(String msg) {
		OnvifPoller.log("Session: " + msg);
	}
}
