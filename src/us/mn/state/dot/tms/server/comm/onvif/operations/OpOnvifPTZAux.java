package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZWiperProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPTZAux extends OpOnvif<OnvifProperty> {
	public OpOnvifPTZAux(
		DeviceImpl d,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.COMMAND, d, session, OnvifService.PTZ);
	}

	/**
	 * More Auxiliary commands may be supported in the future, but wiper is
	 * all that the UI supports for now.
	 *
	 */
	@Override
	protected OnvifPhase phaseTwo() {
		return new WiperOn();
	}

	/**
	 * Onvif does not have the idea of a wiper one shot; in fact, it barely
	 * has a wiper command at all. This the best attempt at a one shot
	 */
	protected class WiperOn extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm) {
			prop = new OnvifPTZWiperProperty(session, true);
			return new WiperOff();
		}
	}

	protected class WiperOff extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm) {
			prop = new OnvifPTZWiperProperty(session, false);
			return null;
		}
	}
}
