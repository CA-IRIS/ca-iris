package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.Stop;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.StopResponse;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZStopProperty extends OnvifProperty {

	public OnvifPTZStopProperty(OnvifSessionMessenger session) {
		super(session);
	}

	@Override
	protected void encodeStore() throws IOException {
		// All Onvif PTZ devices must implement stop.
		// Therefore, if we support the PTZ Service,
		// we can assume the feature will be present
		Stop stop = new Stop();
		stop.setProfileToken(session.getMediaProfileTok());
		response = session.makeRequest(stop, StopResponse.class);
	}
}
