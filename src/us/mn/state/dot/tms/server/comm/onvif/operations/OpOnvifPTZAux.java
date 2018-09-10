package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZNodesProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZWiperProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * An OpOnvifPTZ sends OnvifPTZ*Properties to the PTZ Service that are
 * non-standard.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPTZAux extends OpOnvif<OnvifProperty> {
	public OpOnvifPTZAux(
		DeviceImpl d,
		OnvifSessionMessenger session, ControllerImpl controller)
	{
		super(PriorityLevel.COMMAND, d, session, OnvifService.PTZ, controller);
	}

	/**
	 * More Auxiliary commands may be supported in the future, but wiper is
	 * all that the UI supports for now.
	 */
	@Override
	protected OnvifPhase phaseTwo() {
		return session.getNodes() == null ? new Nodes() : new WiperOn();
	}


	protected class Nodes extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return new OnvifPTZNodesProperty(session);
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new WiperOn();
		}
	}

	/**
	 * Onvif does not have the idea of a wiper one shot; in fact, it barely
	 * has a wiper command at all. This the best attempt at a one shot
	 */
	protected class WiperOn extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return new OnvifPTZWiperProperty(session,
				session.getNodes(), true);
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			// Onvif does not have the idea of a wiper one shot;
			// in fact, it barely has a wiper command at all.
			// This one second delay is the best attempt at a one
			// shot.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log("Pause between wiper on " +
					"and wiper off was " +
					"interrupted. Wiper " +
					"operation may not " +
					"have completed correctly. ");
				e.printStackTrace();
			}
			return new WiperOff();
		}
	}

	protected class WiperOff extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			return new OnvifPTZWiperProperty(session,
				session.getNodes(), false);
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
