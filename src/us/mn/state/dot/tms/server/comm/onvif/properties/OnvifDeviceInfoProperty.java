package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetDeviceInformation;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetDeviceInformationResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifDeviceInfoProperty extends OnvifProperty {
	public OnvifDeviceInfoProperty(OnvifSessionMessenger session) {
		super(session);
		isQuery = true;
	}

	@Override
	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		response = session.makeRequest(new GetDeviceInformation(),
					GetDeviceInformationResponse.class);
	}

	@Override
	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		GetDeviceInformationResponse casted =
			(GetDeviceInformationResponse) response;
		log("{\n" +
			"\tManufacturer: " + casted.getManufacturer() + "\n" +
			"\tModel: " + casted.getModel() + "\n" +
			"\tFirmware version: " + casted.getFirmwareVersion() + "\n" +
			"\tSerial number: " + casted.getSerialNumber() + "\n" +
			"}");
		c.setVersion(casted.getFirmwareVersion());
	}
}
