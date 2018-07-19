package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.device.wsdl.GetCapabilities;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.device.wsdl.GetCapabilitiesResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.Capabilities;
import us.mn.state.dot.tms.server.comm.onvif.soap.SoapMessenger;
import us.mn.state.dot.tms.server.comm.onvif.soap.WSUsernameToken;

public class OnvifDevice extends WSUsernameToken {

    private String ip;
    private Capabilities capabilities = null;

    public OnvifDevice(String username, String password, String ip) {
        super(username, password);
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean hasPTZCapability() {
        if (capabilities == null) {
            getCapabilities();
        }
        return capabilities.getPTZ() != null
                && capabilities.getPTZ().getXAddr() != null;
    }

    public boolean hasMediaCapability() {
        if (capabilities == null) {
            getCapabilities();
        }
        return capabilities.getMedia() != null
                && capabilities.getMedia().getXAddr() != null;
    }

    private void getCapabilities() {
        String uri = "http://" + ip + "/onvif/device_service";

        GetCapabilities getCapabilities = new GetCapabilities();
        GetCapabilitiesResponse getCapabilitiesResponse = new GetCapabilitiesResponse();

        try {
            SoapMessenger soapMessenger = new SoapMessenger(getCapabilities, getCapabilitiesResponse, this);
            getCapabilitiesResponse = (GetCapabilitiesResponse) soapMessenger.callSoapWebService(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        capabilities = getCapabilitiesResponse.getCapabilities();
    }
}
