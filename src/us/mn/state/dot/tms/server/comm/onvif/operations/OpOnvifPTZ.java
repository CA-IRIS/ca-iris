package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OpOnvifPTZ extends OpOnvif {

	private OnvifProperty property;

	public OpOnvifPTZ(
		CameraImpl c, float p, float t, float z,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.COMMAND, c, session);
		if (p == 0 && t == 0 && z == 0)
			property = new OnvifPTZStopProperty(session);
		else
			property = new OnvifPTZMoveProperty(p, t, z, session);
	}

	@Override
	protected Phase<OnvifProperty> phaseTwo() {
		return new PTZ();
	}

	protected class PTZ extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(property);
			mess.storeProps();
			updateOpStatus("PTZ command sent");
			return null;
		}
	}
}
