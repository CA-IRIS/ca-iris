package us.mn.state.dot.tms.server.comm.onvif.messenger;

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

import java.io.IOException;
import java.util.List;

public class OnvifSessionMessenger extends HttpMessenger {

    private boolean initialized = false;
    private WSUsernameToken auth;
    private List<Profile> mediaProfiles;
    private Capabilities capabilities;
    private String defaultProfileTok;
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

    public void initialize(String username, String password) throws Exception {
        this.auth = new WSUsernameToken(username, password);
        initCapabilities();
        if (!hasPTZCapability() || !hasMediaCapability())
            throw new IOException("onvif device does not have required functionality");
        initMediaProfiles();
        // all devices are guaranteed to have at least one profile
        defaultProfileTok = mediaProfiles.get(0).getToken();
        initPtzSpaces();
        this.setUri(capabilities.getPTZ().getXAddr());
        initialized = true;
    }

    /**
     * @return the ptzService ptzSpaces are the different devices actions
     */
    private void initPtzSpaces() throws Exception {
        String ptzUri = getCapabilities().getPTZ().getXAddr();

        GetConfigurations getConfigurations = new GetConfigurations();
        GetConfigurationsResponse getConfigurationsResponse = new GetConfigurationsResponse();
        GetConfigurationOptions getConfigurationOptions = new GetConfigurationOptions();
        GetConfigurationOptionsResponse getConfigurationOptionsResponse = new GetConfigurationOptionsResponse();

        SoapWrapper soapWrapper1 = new SoapWrapper(getConfigurations);
        getConfigurationsResponse = (GetConfigurationsResponse) soapWrapper1.callSoapWebService(ptzUri, getConfigurationsResponse);

        String token = getConfigurationsResponse.getPTZConfiguration().get(0).getToken();

        getConfigurationOptions.setConfigurationToken(token);
        SoapWrapper soapWrapper2 = new SoapWrapper(getConfigurationOptions);

        // the getConfigurationOptionsResponse has info about the Spaces of
        // movement and their range limits
        getConfigurationOptionsResponse = (GetConfigurationOptionsResponse) soapWrapper2.callSoapWebService(ptzUri, getConfigurationOptionsResponse);
        ptzSpaces = getConfigurationOptionsResponse.getPTZConfigurationOptions().getSpaces();
    }

    private boolean hasPTZCapability() {
        return getCapabilities().getPTZ() != null
                && getCapabilities().getPTZ().getXAddr() != null;
    }

    private boolean hasMediaCapability() {
        return getCapabilities().getMedia() != null
                && getCapabilities().getMedia().getXAddr() != null;
    }

    /**
     * capabilities which include the specific service addresses for
     * individual commands.
     */
    private void initCapabilities() throws Exception {
        String uri = this.getUri();
        GetCapabilities getCapabilities = new GetCapabilities();
        GetCapabilitiesResponse getCapabilitiesResponse = new GetCapabilitiesResponse();
        SoapWrapper soapWrapper = new SoapWrapper(getCapabilities, auth);
        getCapabilitiesResponse = (GetCapabilitiesResponse) soapWrapper.callSoapWebService(uri, getCapabilitiesResponse);
        capabilities = getCapabilitiesResponse.getCapabilities();
    }

    /**
     * a profile is required to make ptzService requests
     * @return all media profiles for the device
     */
    private void initMediaProfiles() throws Exception {
        GetProfiles getProfiles = new GetProfiles();
        GetProfilesResponse getProfilesResponse = new GetProfilesResponse();
        String mediaUri = getCapabilities().getMedia().getXAddr();
        SoapWrapper soapWrapper =
                new SoapWrapper(getProfiles, auth);
        getProfilesResponse =
                (GetProfilesResponse) soapWrapper.callSoapWebService(mediaUri, getProfilesResponse);
        mediaProfiles = getProfilesResponse.getProfiles();
    }

    /** user input self defense */
    private void checkUri(String uri) throws IOException {
        if (uri == null || uri.isEmpty())
            throw new IOException("uri not set");
        if (!uri.startsWith("http://"))
            throw new IOException("onvif uri missing \"http://\"");
        String uriParts [] = uri.substring("http://".length()).split(":");
        if (uriParts.length != 1) {
            if (uriParts.length != 2)
                throw new IOException("onvif port incorrectly specified");
            if (!uriParts[1].equals("80"))
                throw new IOException("onvif restricted to port 80");
        }
        String ipParts [] = uriParts[0].split("\\.");
        if (ipParts.length != 4)
            throw new IOException("onvif ip does not have four parts");
        for (String p: ipParts)
            if (!p.matches("[0-9]+"))
                throw new IOException("onvif ip contains values that are not numbers");
    }
}
