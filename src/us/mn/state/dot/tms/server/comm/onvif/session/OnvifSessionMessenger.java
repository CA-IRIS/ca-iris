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
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.*;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.*;

import java.io.IOException;
import java.io.OutputStream;
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

	/** true iff the device is ready for commands */
	private boolean initialized = false;
	private WSUsernameToken auth;

	/**
	 * correspond to media stream types a profile is required to make
	 * ptzService requests each profile corresponds to a media stream type
	 */
	private List<Profile> mediaProfiles;
	private Capabilities capabilities;
	/**
	 * the first media profile token (all devices required to have at least
	 * one
	 */
	private String defaultProfileTok;
	/** different types of ptz commands */
	private PTZSpaces ptzSpaces;

	private List<PTZNode> nodes;
	/** supported imaging options including iris operations and ranges */
	private ImagingSettings20 imagingOptions;
	/** supported focus move operations and ranges */
	private MoveOptions20 imagingMoveOptions;

	public boolean isInitialized() {
		return initialized;
	}

	public void setAuth(String username, String password) {
		initialized = false;
		auth = new WSUsernameToken(username, password);
	}

	public List<Profile> getMediaProfiles() throws IOException {
		if (mediaProfiles == null)
			throw new IOException("Media Profiles not found");
		return mediaProfiles;
	}

	public List<PTZNode> getNodes() throws IOException {
		if (nodes == null)
			throw new IOException("PTZ Nodes not found");
		return nodes;
	}

	public String getDefaultProfileTok() throws IOException {
		if (defaultProfileTok == null)
			throw new IOException("Media Profile Token not found");
		return defaultProfileTok;
	}

	public PTZSpaces getPtzSpaces() throws IOException {
		if (ptzSpaces == null)
			throw new IOException("PTZ Spaces not found");
		return ptzSpaces;
	}

	public ImagingSettings20 getImagingOptions() throws IOException {
		if (imagingOptions == null)
			throw new IOException("Imaging Options not found");
		return imagingOptions;
	}

	public MoveOptions20 getImagingMoveOptions() throws IOException {
		if (imagingMoveOptions == null)
			throw new IOException("Imaging Move Options not " +
				"found");
		return imagingMoveOptions;
	}

	public OnvifSessionMessenger(String uri) throws IOException {
		super(uri + "/onvif/device_service");
		checkUri(uri);
	}

	@Override
	public void open() throws IOException {
		initServices();
		super.open();
	}

	/**
	 * user input self defense
	 *
	 * @param uri gets checked
	 * @throws IOException if the uri is malformed
	 */
	private void checkUri(String uri) throws IOException {
		URL url;
		try {
			// basic null, protocol, and form check
			url = new URL(uri);
		} catch (MalformedURLException e) {
			throw new IOException(e.getMessage());
		}
		if (!url.getProtocol().equalsIgnoreCase("http"))
			throw new IOException(
				"onvif uri missing http protocol");
		// check the ip
		String uriParts[] =
			uri.substring("http://".length()).split(":");
		if (uriParts.length != 1) {
			if (uriParts.length != 2)
				throw new IOException(
					"onvif port incorrectly specified");
			if (!uriParts[1].equals("80"))
				throw new IOException(
					"onvif restricted to port 80");
		}
		String ipParts[] = uriParts[0].split("\\.");
		if (ipParts.length != 4)
			throw new IOException(
				"onvif ip does not have four parts");
		for (String p : ipParts)
			if (!p.matches("[0-9]+"))
				throw new IOException(
					"onvif ip contains values that are " +
						"not numbers");
	}

	/**
	 * Establish a session with the device and determine the device's
	 * capabilities.
	 */
	private void initServices() throws IOException
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
				((GetCapabilitiesResponse) call(getCapabilities,
					GetCapabilitiesResponse.class))
					.getCapabilities();
		} catch (IOException e) {
			throw new IOException(
				"Failed to initialize device. Check username," +
					" password, and uri.");
		}
		initMediaService();
		initPTZService();
		initImagingService();
		initialized = true;
		log("Session started");
	}

	private void setAuthClockOffset() throws IOException {
		GetSystemDateAndTime request = new GetSystemDateAndTime();
		SystemDateTime response;
		// add one second for travel delay as the ONVIF programmer
		// guide does
		ZonedDateTime ourDateTime = ZonedDateTime
			.now(ZoneOffset.UTC).plusSeconds(1);
		try {
			response = ((GetSystemDateAndTimeResponse) call(
				request,
				GetSystemDateAndTimeResponse.class))
				.getSystemDateAndTime();
		} catch (IOException e) {
			throw new IOException(
				"Could not get device time; Check device " +
					"getUri()",
				e);
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

	private void initMediaService() throws IOException {
		try {
			GetProfiles getProfiles = new GetProfiles();
			mediaProfiles = ((GetProfilesResponse) (call(
				OnvifService.MEDIA, getProfiles,
				GetProfilesResponse.class))).getProfiles();
			defaultProfileTok = mediaProfiles.get(0).getToken();
		} catch (IOException e) {
			log("Device does not support Media Service");
			throw e;
		}
	}

	private void initPTZService() {
		try {
			GetConfigurations getConfigurations =
				new GetConfigurations();
			GetConfigurationOptions getConfigurationOptions =
				new GetConfigurationOptions();
			GetConfigurationsResponse getConfigurationsResponse =
				(GetConfigurationsResponse) call(
					OnvifService.PTZ, getConfigurations,
					GetConfigurationsResponse.class);

			String token =
				getConfigurationsResponse.getPTZConfiguration()
					.get(0).getToken();
			getConfigurationOptions.setConfigurationToken(token);

			// the getConfigurationOptionsResponse has info about
			// the
			// Spaces of movement and their range limits
			GetConfigurationOptionsResponse
				getConfigurationOptionsResponse =
				(GetConfigurationOptionsResponse) call(
					OnvifService.PTZ,
					getConfigurationOptions,
					GetConfigurationOptionsResponse.class);
			ptzSpaces = getConfigurationOptionsResponse
				.getPTZConfigurationOptions().getSpaces();
			GetNodes getNodes = new GetNodes();
			nodes = ((GetNodesResponse) call(OnvifService.PTZ,
				getNodes, GetNodesResponse.class)).getPTZNode();
		} catch (IOException e) {
			log("Device does not support PTZ Service");
		}
	}

	private void initImagingService() {
		try {
			GetOptions getOptions = new GetOptions();
			imagingOptions = ((GetImagingSettingsResponse) call(
				OnvifService.IMAGING, getOptions,
				GetOptionsResponse.class)).getImagingSettings();
			GetMoveOptions getMoveOptions = new GetMoveOptions();
			getMoveOptions.setVideoSourceToken(defaultProfileTok);
			imagingMoveOptions = ((GetMoveOptionsResponse) call(
				OnvifService.IMAGING, getMoveOptions,
				GetMoveOptionsResponse.class)).getMoveOptions();
		} catch (IOException e) {
			log("Device does not support Imaging Service");
		}
	}

	public void callNoWait(Object request, OutputStream os)
		throws IOException
	{
		try {
			SoapWrapper soap = new SoapWrapper(request);
			soap.callSoapWebService(getUri(), auth, os);
		} catch (Exception e) {
			throw new IOException(
				"Failed to send request to device", e);
		}
	}

	/**
	 * Adds authentication and calls the specified service with the request
	 * object
	 */
	public Object call(
		OnvifService s, Object request, Class<?> responseClass)
		throws IOException
	{
		selectService(s);
		return call(request, responseClass);
	}

	private Object call(Object request, Class<?> responseClass)
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

	public void selectService(OnvifService s) throws IOException {
		if (capabilities == null)
			throw new IOException("Device not yet initialized");
		switch (s) {
		case DEVICE:
			if (!supportsDeviceService())
				throw new IOException(
					"Device does not support Device " +
						"Service requests");
			setUri(capabilities.getDevice().getXAddr());
			break;
		case MEDIA:
			if (!supportsMediaService())
				throw new IOException(
					"Device does not support the Media " +
						"Service requests");
			setUri(capabilities.getMedia().getXAddr());
			break;
		case PTZ:
			if (!supportsPTZService())
				throw new IOException(
					"Device does not support the PTZ " +
						"Service requests");
			setUri(capabilities.getPTZ().getXAddr());
			break;
		case IMAGING:
			if (!supportsImagingService())
				throw new IOException(
					"Device does not support the Imaging" +
						" " +
						"Service requests");
			setUri(capabilities.getImaging().getXAddr());
			break;
		default:
			throw new IOException(
				"Attempt to call unreognized Service");
		}
	}

	/**
	 * @return true if the device has media capabilities (required for ptz)
	 */
	private boolean supportsDeviceService() throws IOException {
		return capabilities.getDevice() != null
			&& capabilities.getDevice().getXAddr() != null;
	}

	/**
	 * @return true if the device has media capabilities (required for ptz)
	 */
	private boolean supportsMediaService() throws IOException {
		return capabilities.getMedia() != null
			&& capabilities.getMedia().getXAddr() != null;
	}

	/**
	 * @return true if the device has ptz capabilities (also checks for
	 * 	media capabilities)
	 */
	private boolean supportsPTZService() throws IOException {
		return capabilities.getPTZ() != null
			&& capabilities.getPTZ().getXAddr() != null;
	}

	/**
	 * @return true if the device has ptz capabilities (also checks for
	 * 	media capabilities)
	 */
	private boolean supportsImagingService() throws IOException {
		return capabilities.getImaging() != null
			&& capabilities.getImaging().getXAddr() != null;
	}

	private static void log(String msg) {
		OnvifPoller.log(msg);
	}
}
