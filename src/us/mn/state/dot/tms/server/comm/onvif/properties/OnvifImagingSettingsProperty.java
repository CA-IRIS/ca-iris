package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingSettingsProperty extends OnvifProperty {
	public OnvifImagingSettingsProperty(
		OnvifSessionMessenger session)
	{
		super(session);
		isQuery = true;
	}

	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		GetImagingSettings request = new GetImagingSettings();
		request.setVideoSourceToken(session.getVideoSoureTok());
		response = session.makeRequest(request, GetImagingSettingsResponse.class);
	}

	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (!(response instanceof GetImagingSettingsResponse))
			throw new OperationFailedException("GetImagingSettingsResponse");
		session.setImagingSettings(
			((GetImagingSettingsResponse) response).getImagingSettings());
	}
}
