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
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
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
		return new ConfigPhase();
	}

	private class ConfigPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out = null;
			if (session.getPtzConfigurations() == null)
				out = new OnvifPTZConfigurationsProperty(session);
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new ConfigOptionsPhase();
		}
	}

	private class ConfigOptionsPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out = null;
			if (session.getPTZConfigurationOptions() == null)
				out = new OnvifPTZSpacesProperty(session,
					session.getPtzConfigurations());
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new PTZPhase();
		}
	}

	protected class PTZPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out;
			// we might use the PTZ stop property, but
			// Caltrans' Pelco cameras do not seem to support it.
			if (pan == 0 && tilt == 0 && zoom == 0)
				out = new OnvifPTZStopProperty(session);
			else
				out = new OnvifPTZMoveProperty(
					pan, tilt, zoom, session,
					session.getPTZConfigurationOptions());
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
