package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZWiperProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPTZAux extends OpOnvif<OnvifProperty> {
	public OpOnvifPTZAux(
		DeviceImpl d,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.COMMAND, d, session);
	}

	/**
	 * More Auxiliary commands may be supported in the future, but wiper is
	 * all that the UI supports for now.
	 *
	 * @return
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
		protected OnvifPhase poll2(
			CommMessage<OnvifProperty> p) throws IOException
		{
			p.add(new OnvifPTZWiperProperty(session, true));
			p.storeProps();
			log("Wiper on command sent");
			return new WiperOff();
		}
	}

	protected class WiperOff extends OnvifPhase {
		protected OnvifPhase poll2(
			CommMessage<OnvifProperty> p) throws IOException
		{
			p.add(new OnvifPTZWiperProperty(session, false));
			p.storeProps();
			log("Wiper off command sent");
			return null;
		}
	}
}
