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
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.Messenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class OnvifSessionMessenger extends Messenger {

    private boolean initialized = false;
    private String uri;
    private String ptzUri;

    public WSUsernameToken getAuth() {
        return auth;
    }

    private WSUsernameToken auth;
    private Capabilities capabilities;
    private String defaultProfileTok;
    private PTZSpaces ptzSpaces;

    public PTZSpaces getPtzSpaces() {
        return ptzSpaces;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public OnvifSessionMessenger(String uri) {
        this.uri = uri;
    }

    public void initialize(WSUsernameToken auth) throws Exception {
        this.auth = auth;
        capabilities = getCapabilities();
        if (!hasPTZCapability() || !hasMediaCapability())
            throw new IOException("onvif device does not have required functionality");
        // all devices are guaranteed to have at least one profile
        defaultProfileTok = getProfiles().get(0).getToken();
        ptzSpaces = getPTZSpaces();
        ptzUri = getCapabilities().getPTZ().getXAddr();
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
            // todo assuming that "http://" is already part of uri, but need to verify this
            String uri = this.uri + "/onvif/device_service";
            GetCapabilities getCapabilities = new GetCapabilities();
            GetCapabilitiesResponse getCapabilitiesResponse =
                    new GetCapabilitiesResponse();
            try {
                SoapWrapper soapWrapper =
                        new SoapWrapper(getCapabilities, auth);
                getCapabilitiesResponse =
                        (GetCapabilitiesResponse) soapWrapper.callSoapWebService(uri, getCapabilitiesResponse);
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
                new SoapWrapper(getProfiles, auth);
        getProfilesResponse =
                (GetProfilesResponse) soapWrapper.callSoapWebService(mediaUri, getProfilesResponse);
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
                new SoapWrapper(getConfigurations);
        getConfigurationsResponse =
                (GetConfigurationsResponse) soapWrapper1.callSoapWebService(ptzUri, getConfigurationsResponse);

        String token = getConfigurationsResponse.getPTZConfiguration().get(0).getToken();

        getConfigurationOptions.setConfigurationToken(token);
        SoapWrapper soapWrapper2 =
                new SoapWrapper(getConfigurationOptions);

        // the getConfigurationOptionsResponse has info about the Spaces of
        // movement and their range limits
        getConfigurationOptionsResponse =
                (GetConfigurationOptionsResponse) soapWrapper2.callSoapWebService(ptzUri, getConfigurationOptionsResponse);
        return getConfigurationOptionsResponse.getPTZConfigurationOptions().getSpaces();
    }

    public String getDefaultProfileTok() {
        return defaultProfileTok;
    }

    @Override
    public void open() throws IOException {

    }

    @Override
    public void close() {

    }

    @Override
    public void setTimeout(int t) throws IOException {

    }

    @Override
    public int getTimeout() {
        return 5000;
    }

    @Override
    public InputStream getInputStream(String path, ControllerImpl c)
            throws IOException {
        return input;
    }

    @Override
    public OutputStream getOutputStream(ControllerImpl c)
            throws IOException {
        return output;
    }
}
