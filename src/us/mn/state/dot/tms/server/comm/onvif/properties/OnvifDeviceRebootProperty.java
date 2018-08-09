package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.SystemReboot;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.SystemRebootResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifDeviceRebootProperty extends OnvifDeviceProperty {
	public OnvifDeviceRebootProperty(
		OnvifSessionMessenger session)
	{
		super(session);
	}

	@Override
	protected void encodeStore() throws IOException {
		response = session.makeRequest(new SystemReboot(),
			SystemRebootResponse.class);
	}

	@Override
	public void decodeStore() throws IOException {
		SystemRebootResponse status = (SystemRebootResponse) response;
		log("Onvif device responded to reboot request: " + status
			.getMessage());
	}
}
