package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZConfiguration;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurationOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetConfigurationOptionsResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZSpacesProperty extends OnvifProperty {
	private final List<PTZConfiguration> ptzConfigurations;

	public OnvifPTZSpacesProperty(
		OnvifSessionMessenger session,
		List<PTZConfiguration> ptzConfigurations)
	{
		super(session);
		this.ptzConfigurations = ptzConfigurations;
		isQuery = true;
	}

	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		GetConfigurationOptions request =
			new GetConfigurationOptions();
		request.setConfigurationToken(
			ptzConfigurations.get(0).getToken());
		response = session.makeRequest(request,
			GetConfigurationOptionsResponse.class);
	}

	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (!(response instanceof GetConfigurationOptionsResponse))
			throw new OperationFailedException("GetConfigurationOptionsResponse");
		session.setPTZConfigurationOptions(
			((GetConfigurationOptionsResponse) response).getPTZConfigurationOptions());
	}
}
