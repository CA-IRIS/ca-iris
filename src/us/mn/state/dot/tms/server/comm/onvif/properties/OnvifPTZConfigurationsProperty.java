package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurations;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurationsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZConfigurationsProperty extends OnvifProperty {
	public OnvifPTZConfigurationsProperty(
		OnvifSessionMessenger session)
	{
		super(session);
		isQuery = true;
	}

	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		response = session.makeRequest(
			new GetConfigurations(),
			GetConfigurationsResponse.class);
	}

	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		session.setPtzConfigurations(
			((GetConfigurationsResponse) response)
			.getPTZConfiguration());
	}
}
