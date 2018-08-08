package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.SystemReboot;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.SystemRebootResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifDeviceRebootProperty extends OnvifProperty {
	public OnvifDeviceRebootProperty(
		OnvifSessionMessenger session)
	{
		super(session);
	}

	@Override
	protected void encodeStore() throws IOException {
		response = session.call(OnvifService.DEVICE, new SystemReboot(),
			SystemRebootResponse.class);
	}

	protected void decodeStore() throws IOException {
		SystemRebootResponse status = (SystemRebootResponse) response;
		logFailure("Onvif device responded to reboot request: "
			+ status.getMessage());
	}
}
