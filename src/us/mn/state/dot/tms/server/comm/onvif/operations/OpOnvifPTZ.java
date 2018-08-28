package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

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
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return (pan == 0 && tilt == 0 && zoom == 0) ?
				new OnvifPTZStopProperty(session)
				: new OnvifPTZMoveProperty(
				pan, tilt, zoom, session);
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
