package us.mn.state.dot.tms.server.comm.onvif.session;

import org.onvif.ver10.device.wsdl.GetCapabilities;
import org.onvif.ver10.device.wsdl.GetCapabilitiesResponse;
import org.onvif.ver10.media.wsdl.GetProfiles;
import org.onvif.ver10.media.wsdl.GetProfilesResponse;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.PTZSpaces;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver20.ptz.wsdl.GetConfigurationOptions;
import org.onvif.ver20.ptz.wsdl.GetConfigurationOptionsResponse;
import org.onvif.ver20.ptz.wsdl.GetConfigurations;
import org.onvif.ver20.ptz.wsdl.GetConfigurationsResponse;
import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
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
	/** correspond to media stream types */
	private List<Profile> mediaProfiles;
	private Capabilities capabilities;
	/** the first media profile token (all devices required to have at least one */
	private String defaultProfileTok;
	/** different types of ptz commands */
	private PTZSpaces ptzSpaces;

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public String getDefaultProfileTok() {
		return defaultProfileTok;
	}

	public WSUsernameToken getAuth() {
		return auth;
	}

	public List<Profile> getMediaProfiles() {
		return mediaProfiles;
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
	 * establish a session with the device and determine the device's capabilities
	 * @param username may not be null
	 * @param password may not be null
	 * @throws ParserConfigurationException soap error
	 * @throws NoSuchAlgorithmException cannot create required password digest
	 * @throws SOAPException soap formatting error
	 * @throws IOException soap transmission error
	 * @throws JAXBException soap formatting error
	 */
	public void initialize(String username, String password)
		throws IOException, ParserConfigurationException,
		NoSuchAlgorithmException, SOAPException, JAXBException
	{
		OnvifPoller.log("Attempting to start session. ");
		try {
			auth = new WSUsernameToken(username, password);
			capabilities = initCapabilities(this.getUri());
		} catch (IOException e) {
			OnvifPoller.log("Check username, password, and uri");
		}
		if (!hasPTZCapability())
			throw new IOException(
				"Onvif device does not have required " +
					"functionality");
		setUri(capabilities.getMedia().getXAddr());
		mediaProfiles = initMediaProfiles(this.getUri());
		// all devices are guaranteed to have at least one profile
		defaultProfileTok = mediaProfiles.get(0).getToken();
		setUri(capabilities.getPTZ().getXAddr());
		ptzSpaces = initPtzSpaces(this.getUri());
		initialized = true;
		OnvifPoller.log("Session started. ");
	}

	/**
	 * @return capabilities which include the specific service addresses
	 * for individual commands.
	 */
	private Capabilities initCapabilities(String deviceUri)
		throws ParserConfigurationException, NoSuchAlgorithmException,
		SOAPException, JAXBException, IOException
	{
		GetCapabilities getCapabilities = new GetCapabilities();
		SoapWrapper soapWrapper =
			new SoapWrapper(getCapabilities);
		GetCapabilitiesResponse getCapabilitiesResponse = (GetCapabilitiesResponse) soapWrapper
			.callSoapWebService(deviceUri,
				GetCapabilitiesResponse.class, auth);
		return getCapabilitiesResponse.getCapabilities();
	}

	/**
	 * a profile is required to make ptzService requests
	 * each profile corresponds to a media stream type
	 *
	 * @return all media profiles for the device
	 */
	private List<Profile> initMediaProfiles(String mediaUri)
		throws SOAPException, JAXBException, IOException,
		ParserConfigurationException, NoSuchAlgorithmException
	{
		GetProfiles getProfiles = new GetProfiles();
		SoapWrapper soapWrapper = new SoapWrapper(getProfiles);
		GetProfilesResponse getProfilesResponse = (GetProfilesResponse) soapWrapper
			.callSoapWebService(mediaUri, GetProfilesResponse.class,
				auth);
		return getProfilesResponse.getProfiles();
	}

	/**
	 * @return the ptzSpaces are akin to different devices actions
	 */
	private PTZSpaces initPtzSpaces(String ptzUri)
		throws SOAPException, JAXBException, IOException,
		ParserConfigurationException, NoSuchAlgorithmException
	{
		GetConfigurations getConfigurations = new GetConfigurations();
		GetConfigurationOptions getConfigurationOptions =
			new GetConfigurationOptions();
		SoapWrapper soapWrapper1 =
			new SoapWrapper(getConfigurations);
		GetConfigurationsResponse getConfigurationsResponse =
			(GetConfigurationsResponse) soapWrapper1
				.callSoapWebService(ptzUri,
					GetConfigurationsResponse.class, auth);

		String token =
			getConfigurationsResponse.getPTZConfiguration().get(0)
				.getToken();

		getConfigurationOptions.setConfigurationToken(token);
		SoapWrapper soapWrapper2 =
			new SoapWrapper(getConfigurationOptions);

		// the getConfigurationOptionsResponse has info about the
		// Spaces of
		// movement and their range limits
		GetConfigurationOptionsResponse getConfigurationOptionsResponse =
			(GetConfigurationOptionsResponse) soapWrapper2
				.callSoapWebService(ptzUri,
					GetConfigurationOptionsResponse.class, auth);
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
			URL url = new URL(uri);
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
