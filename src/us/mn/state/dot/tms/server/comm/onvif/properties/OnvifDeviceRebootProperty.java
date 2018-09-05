package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.HangUpException;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.SystemReboot;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.SystemRebootResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		response = session.makeRequest(new SystemReboot(),
			SystemRebootResponse.class);
	}

	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		SystemRebootResponse casted = (SystemRebootResponse) response;
		log(casted.getMessage());
		doneMsg = casted.getMessage();
		throw new HangUpException();
	}
}
