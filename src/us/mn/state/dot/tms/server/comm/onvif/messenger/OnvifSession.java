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

public class OnvifSession {

    private boolean initialized = false;
    private String ip;

    public WSUsernameToken getAuth() {
        return auth;
    }

    private WSUsernameToken auth;
    private Capabilities capabilities;
    private String defaultProfileTok;
    private PTZSpaces ptzSpaces;

    public HttpMessenger getMessenger() {
        return messenger;
    }

    private HttpMessenger messenger;

    public PTZSpaces getPtzSpaces() {
        return ptzSpaces;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public OnvifSession(String ip, HttpMessenger m) {
        this.ip = ip;
        this.messenger = m;
    }

    public void initialize(WSUsernameToken auth) throws Exception {
        this.auth = auth;
        capabilities = getCapabilities();
        if (!hasPTZCapability() || !hasMediaCapability())
            throw new IOException("onvif device does not have required functionality");
        // all devices are guaranteed to have at least one profile
        defaultProfileTok = getProfiles().get(0).getToken();
        ptzSpaces = getPTZSpaces();
        messenger.setUri(getCapabilities().getPTZ().getXAddr());
        initialized = true;
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
     * @return capabilities which include the specific service addresses for
     * individual commands.
     */
    public Capabilities getCapabilities() {
        if (capabilities == null) {
            String uri = "http://" + ip + "/onvif/device_service";
            GetCapabilities getCapabilities = new GetCapabilities();
            GetCapabilitiesResponse getCapabilitiesResponse =
                    new GetCapabilitiesResponse();
            try {
                SoapWrapper soapWrapper =
                        new SoapWrapper(getCapabilities, getCapabilitiesResponse, auth);
                getCapabilitiesResponse =
                        (GetCapabilitiesResponse) soapWrapper.callSoapWebService(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            capabilities = getCapabilitiesResponse.getCapabilities();
        }
        return capabilities;
    }

    /**
     * a profile is required to make ptzService requests
     * @return all media profiles for the device
     */
    private List<Profile> getProfiles() throws Exception {
        GetProfiles getProfiles = new GetProfiles();
        GetProfilesResponse getProfilesResponse = new GetProfilesResponse();
        String mediaUri = getCapabilities().getMedia().getXAddr();
        SoapWrapper soapWrapper =
                new SoapWrapper(getProfiles, getProfilesResponse, auth);
        getProfilesResponse =
                (GetProfilesResponse) soapWrapper.callSoapWebService(mediaUri);
        return getProfilesResponse.getProfiles();
    }

    /**
     * @return the ptzService ptzSpaces are the different devices actions
     */
    private PTZSpaces getPTZSpaces() throws Exception {
        String ptzUri = getCapabilities().getPTZ().getXAddr();

        GetConfigurations getConfigurations = new GetConfigurations();
        GetConfigurationsResponse getConfigurationsResponse =
                new GetConfigurationsResponse();
        GetConfigurationOptions getConfigurationOptions =
                new GetConfigurationOptions();
        GetConfigurationOptionsResponse getConfigurationOptionsResponse =
                new GetConfigurationOptionsResponse();

        SoapWrapper soapWrapper1 =
                new SoapWrapper(getConfigurations, getConfigurationsResponse);
        getConfigurationsResponse =
                (GetConfigurationsResponse) soapWrapper1.callSoapWebService(ptzUri);

        String token = getConfigurationsResponse.getPTZConfiguration().get(0).getToken();

        getConfigurationOptions.setConfigurationToken(token);
        SoapWrapper soapWrapper2 =
                new SoapWrapper(getConfigurationOptions, getConfigurationOptionsResponse);

        // the getConfigurationOptionsResponse has info about the Spaces of
        // movement and their range limits
        getConfigurationOptionsResponse =
                (GetConfigurationOptionsResponse) soapWrapper2.callSoapWebService(ptzUri);
        return getConfigurationOptionsResponse.getPTZConfigurationOptions().getSpaces();
    }

    public String getDefaultProfileTok() {
        return defaultProfileTok;
    }
}
