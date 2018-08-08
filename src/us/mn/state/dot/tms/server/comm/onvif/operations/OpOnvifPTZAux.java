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
public class OpOnvifPTZAux extends OpOnvif {
	public OpOnvifPTZAux(
		DeviceImpl d,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.COMMAND, d, session);
		try {
			session.selectService(OnvifService.PTZ);
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	/**
	 * More Auxiliary commands may be supported in the future, but wiper is
	 * all that the UI supports for now.
	 *
	 * @return
	 */
	@Override
	protected Phase<OnvifProperty> phaseTwo() {
		return new WiperOn();
	}

	/**
	 * Onvif does not have the idea of a wiper one shot; in fact, it barely
	 * has a wiper command at all. This the best attempt at a one shot
	 */
	protected class WiperOn extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPTZWiperProperty(session, true));
			mess.storeProps();
			log("Wiper on command sent");
			return new WiperOff();
		}
	}

	protected class WiperOff extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPTZWiperProperty(session, false));
			mess.storeProps();
			log("Wiper off command sent");
			return null;
		}
	}
}
