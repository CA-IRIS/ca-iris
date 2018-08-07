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
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * keeps track of device authentication session, verifies capabilities, and
 * interface to some web service logic
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

	public List<Profile> getMediaProfiles() {
		return mediaProfiles;
	}

	public List<PTZNode> getNodes() {
		return nodes;
	}

	public String getDefaultProfileTok() {
		return defaultProfileTok;
	}

	public PTZSpaces getPtzSpaces() {
		return ptzSpaces;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public OnvifSessionMessenger(String uri) throws IOException {
		super(uri + "/onvif/device_service");
		checkUri(uri);
	}

	/**
	 * establish a session with the device and determine the device's
	 * capabilities
	 *
	 * @param username may not be null
	 * @param password may not be null
	 */
	public void initialize(String username, String password)
		throws IOException
	{
		OnvifPoller.log("Attempting to start session. ");
		try {
			auth = new WSUsernameToken(username, password);
			setAuthClockOffset();

		} catch (IOException e) {
			OnvifPoller.log("Check username, password, and uri");
			throw e;
		}
		GetCapabilities getCapabilities =
			new GetCapabilities();
		capabilities = ((GetCapabilitiesResponse)
			call(getCapabilities,
				GetCapabilitiesResponse.class))
			.getCapabilities();
		if (!hasPTZCapability())
			throw new IOException(
				"Onvif device does not support ptz");

		GetProfiles getProfiles = new GetProfiles();
		mediaProfiles = ((GetProfilesResponse) (call(
			OnvifService.MEDIA, getProfiles,
			GetProfilesResponse.class))).getProfiles();
		// all devices are guaranteed to have at least one profile
		defaultProfileTok = mediaProfiles.get(0).getToken();

		ptzSpaces = initPtzSpaces();
		if (ptzSpaces.getContinuousPanTiltVelocitySpace() == null ||
			ptzSpaces.getContinuousZoomVelocitySpace() == null)
			throw new IOException(
				"Device does not support continuous move");

		GetNodes getNodes = new GetNodes();
		nodes = ((GetNodesResponse) call(OnvifService.PTZ,
			getNodes, GetNodesResponse.class)).getPTZNode();

		initialized = true;
		OnvifPoller.log("Session started. ");
	}

	private void setAuthClockOffset() throws IOException {
		GetSystemDateAndTime getSystemDateAndTime =
			new GetSystemDateAndTime();
		SystemDateTime response;
		// add one second for travel delay as the ONVIF programmer
		// guide does
		ZonedDateTime ourDateTime =
			ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(1);
		try {
			response = ((GetSystemDateAndTimeResponse) call(
				getSystemDateAndTime,
				GetSystemDateAndTimeResponse.class))
				.getSystemDateAndTime();
		} catch (IOException e) {
			throw new IOException(
				"Could not get device time; check device uri",
				e);
		}
		DateTime deviceDT = response.getUTCDateTime();
		ZoneOffset zoneID = ZoneOffset.UTC;
		// we will may only get one of the time formats (UTC only
		// required by ONVIF 2.0), so local time is a backup
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

	/**
	 * Adds authentication and calls the specified service with the request
	 * object
	 */
	public Object call(
		OnvifService s, Object request, Class<?> responseClass)
		throws IOException
	{
		switch (s) {
		case DEVICE:
			setUri(capabilities.getDevice().getXAddr());
			break;
		case MEDIA:
			setUri(capabilities.getMedia().getXAddr());
			break;
		case PTZ:
			setUri(capabilities.getPTZ().getXAddr());
			break;
		case IMAGING:
			setUri(capabilities.getImaging().getXAddr());
			break;
		}
		return call(request, responseClass);
	}

	/**
	 * @return the ptzSpaces are akin to different devices actions
	 */
	private PTZSpaces initPtzSpaces() throws IOException {
		GetConfigurations getConfigurations = new GetConfigurations();
		GetConfigurationOptions getConfigurationOptions =
			new GetConfigurationOptions();
		GetConfigurationsResponse getConfigurationsResponse =
			(GetConfigurationsResponse) call(
				OnvifService.PTZ, getConfigurations,
				GetConfigurationsResponse.class);

		String token =
			getConfigurationsResponse.getPTZConfiguration().get(0)
				.getToken();

		getConfigurationOptions.setConfigurationToken(token);

		// the getConfigurationOptionsResponse has info about the
		// Spaces of movement and their range limits
		GetConfigurationOptionsResponse
			getConfigurationOptionsResponse =
			(GetConfigurationOptionsResponse) call(
				OnvifService.PTZ, getConfigurationOptions,
				GetConfigurationOptionsResponse.class);
		return getConfigurationOptionsResponse
			.getPTZConfigurationOptions().getSpaces();
	}

	/**
	 * @return true if the device has ptz capabilities (also checks for
	 * 	media capabilities)
	 */
	private boolean hasPTZCapability() {
		return hasMediaCapability()
			&& capabilities != null
			&& capabilities.getPTZ() != null
			&& capabilities.getPTZ().getXAddr() != null;
	}

	/**
	 * @return true if the device has media capabilities (required for ptz)
	 */
	private boolean hasMediaCapability() {
		return capabilities != null
			&& capabilities.getMedia() != null
			&& capabilities.getMedia().getXAddr() != null;
	}

	/**
	 * user input self defense
	 *
	 * @param uri gets checked
	 * @throws IOException if the uri is malformed
	 */
	private void checkUri(String uri) throws IOException {
		try {
			// basic null, protocol, and form check
			new URL(uri);
		} catch (MalformedURLException e) {
			throw new IOException(e.getMessage());
		}
		if (!uri.startsWith("http://"))
			throw new IOException("onvif uri missing \"http://\"");
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
						"not" +
						" numbers");
	}
}
