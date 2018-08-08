package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.Stop;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.StopResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

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
		// all onvif ptz devices must implement stop
		// there, if we support the ptz service,
		// we can assume the feature will be present
		Stop stop = new Stop();
		stop.setProfileToken(session.getDefaultProfileTok());
		response = session.call(OnvifService.PTZ, stop, StopResponse.class);
	}
}
