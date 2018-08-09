package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifDeviceRebootProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifDevice extends OpOnvif<OnvifProperty> {
	public OpOnvifDevice(
		DeviceImpl d,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.URGENT, d, session);
	}

	/**
	 * More Device commands may be supported in the future, but Reboot is
	 * all the UI supports for now.
	 */
	@Override
	protected OnvifPhase phaseTwo() {
		return new Reboot();
	}

	protected class Reboot extends OnvifPhase {
		@Override
		protected OnvifPhase poll2(CommMessage<OnvifProperty> p)
			throws IOException {
			p.add(new OnvifDeviceRebootProperty(session));
			p.storeProps();
			log("Onvif device reboot command sent");
			return null;
		}
	}
}
