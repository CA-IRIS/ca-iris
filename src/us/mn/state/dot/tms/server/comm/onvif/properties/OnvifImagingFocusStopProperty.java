package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.MoveOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.Stop;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.StopResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;

public class OnvifImagingFocusStopProperty extends OnvifProperty {
	private final MoveOptions20 options;

	public OnvifImagingFocusStopProperty(OnvifSessionMessenger session,
					     MoveOptions20 options) {
		super(session);
		this.options = options;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		if (!supportsFocusStopMode())
			throw new OperationNotSupportedException("FocusStop");
		stop();
	}

	private boolean supportsFocusStopMode() {
		return options != null;
	}

	private void stop() throws IOException {
		Stop request = new Stop();
		request.setVideoSourceToken(session.getVideoSoureTok());
		response = session.makeRequest(request, StopResponse.class);
	}
}
