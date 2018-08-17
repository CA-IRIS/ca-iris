package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OpOnvifPTZ extends OpOnvif<OnvifProperty> {

	private final float pan;
	private final float tilt;
	private final float zoom;

	public OpOnvifPTZ(
		CameraImpl c, float p, float t, float z,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.COMMAND, c, session, OnvifService.PTZ);
		pan = p;
		tilt = t;
		zoom = z;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return new PTZ();
	}

	protected class PTZ extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm) {
			prop = (pan == 0 && tilt == 0 && zoom == 0) ?
					new OnvifPTZStopProperty(session)
					: new OnvifPTZMoveProperty(
						pan, tilt, zoom, session);
			return null;
		}
	}
}
