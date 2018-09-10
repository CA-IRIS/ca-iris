package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZConfigurationsProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZSpacesProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * An OpOnvifPTZ sends OnvifPTZ*Properties to the PTZ Service.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OpOnvifPTZ extends OpOnvif<OnvifProperty> {
	private final float pan;
	private final float tilt;
	private final float zoom;

	public OpOnvifPTZ(
		CameraImpl c, float p, float t, float z,
		OnvifSessionMessenger session, ControllerImpl controller)
	{
		super(PriorityLevel.COMMAND, c, session, OnvifService.PTZ, controller);
		pan = p;
		tilt = t;
		zoom = z;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return new PTZ();
	}

	protected class PTZ extends OnvifPhase {
		private boolean done = false;

		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out;
			if (session.getPtzConfigurations() == null)
				out = new OnvifPTZConfigurationsProperty(session);
			else if (session.getPtzSpaces() == null)
				out = new OnvifPTZSpacesProperty(session,
					session.getPtzConfigurations());
			else {
				out = new OnvifPTZMoveProperty(
					pan, tilt, zoom,
				session, session.getPtzSpaces());
				done = true;
			}
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return done ? null : new PTZ();
		}
	}
}
