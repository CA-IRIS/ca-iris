package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetOptionsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingOptionsProperty extends OnvifProperty {
	public OnvifImagingOptionsProperty(
		OnvifSessionMessenger session)
	{
		super(session);
		isQuery = true;
	}

	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		GetOptions request = new GetOptions();
		request.setVideoSourceToken(session.getMediaProfileTok());
		response = session.makeRequest(request, GetOptionsResponse.class);
	}

	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		session.setImagingOptions(((GetOptionsResponse)
			response).getImagingOptions());
	}
}
