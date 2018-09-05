package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetMoveOptionsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingMoveOptionsProperty extends OnvifProperty {
	public OnvifImagingMoveOptionsProperty(
		OnvifSessionMessenger session)
	{
		super(session);
		isQuery = true;
	}

	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		GetMoveOptions request = new GetMoveOptions();
		request.setVideoSourceToken(session.getMediaProfileTok());
		response = session.makeRequest(request,
			GetMoveOptionsResponse.class);
	}

	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		session.setImagingMoveOptions(((GetMoveOptionsResponse)
			response).getMoveOptions());
	}
}
