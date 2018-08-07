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
public class OnvifStopProperty extends OnvifProperty {

	public OnvifStopProperty(OnvifSessionMessenger session) {
		super(session);
	}

	@Override
	protected void encodeStore() throws IOException {
		Stop stop = new Stop();
		stop.setProfileToken(session.getDefaultProfileTok());
		response = session.call(OnvifService.PTZ, stop, StopResponse.class);
	}
}
