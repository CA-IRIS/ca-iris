package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.Stop;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.StopResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZStopProperty extends OnvifPTZProperty {

	public OnvifPTZStopProperty(OnvifSessionMessenger session) {
		super(session);
	}

	@Override
	protected void encodeStore()
		throws IOException, ServiceNotSupportedException
	{
		// All onvif ptz devices must implement stop.
		// Therefore, if we support the ptz service,
		// we can assume the feature will be present
		Stop stop = new Stop();
		stop.setProfileToken(session.getMediaProfileTok());
		response = session.makeRequest(stop, StopResponse.class);
	}
}
