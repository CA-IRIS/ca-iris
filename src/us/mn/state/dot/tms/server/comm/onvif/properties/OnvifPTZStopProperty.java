package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.Stop;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.StopResponse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZStopProperty extends OnvifProperty {

	public OnvifPTZStopProperty(OnvifSessionMessenger session) {
		super(session);
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		// All Onvif PTZ devices must implement stop.
		// Therefore, if we support the PTZ Service,
		// we can assume the feature will be present
		Stop stop = new Stop();
		stop.setProfileToken(session.getMediaProfileTok());
		// setting these values to true should be optional by spec,
		// but some cameras to honor the spec.
		stop.setPanTilt(true);
		stop.setZoom(true);
		response = session.makeRequest(stop, StopResponse.class);
	}
}
