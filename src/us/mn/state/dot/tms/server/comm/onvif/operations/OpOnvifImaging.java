package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingFocusAutoProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingFocusMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifImaging extends OpOnvif<OnvifProperty> {

	private final DeviceRequest request;

	public OpOnvifImaging(
		DeviceImpl d,
		OnvifSessionMessenger session,
		DeviceRequest r)
	{
		super(PriorityLevel.COMMAND, d, session, OnvifService.IMAGING);
		request = r;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return new Adjust();
	}

	protected class Adjust extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm)
			throws IOException
		{
			OnvifProperty p = selectProperty(request);
			cm.add(p);
			cm.storeProps();
			logSent(p);
			return null;
		}
	}

	private OnvifProperty selectProperty(DeviceRequest r)
		throws IOException
	{
		OnvifProperty out;
		switch (r) {
		case CAMERA_FOCUS_NEAR:
			out = new OnvifImagingFocusMoveProperty(session,
				-0.2f);
			break;
		case CAMERA_FOCUS_FAR:
			out = new OnvifImagingFocusMoveProperty(session, 0.2f);
			break;
		case CAMERA_FOCUS_STOP:
			out = new OnvifImagingFocusMoveProperty(session, 0f);
			break;
		case CAMERA_FOCUS_MANUAL:
			out = new OnvifImagingFocusAutoProperty(session,
				false);
			break;
		case CAMERA_FOCUS_AUTO:
			out = new OnvifImagingFocusAutoProperty(session, true);
			break;
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
		case CAMERA_IRIS_STOP:
		case CAMERA_IRIS_MANUAL:
		case CAMERA_IRIS_AUTO:
			log("Onvif iris commands not implemented. Onvif only " +
				"supports absolute iris adjustments, but Iris " +
				"only supports continuous iris adjustments. ");
		default:
			throw new IOException("Unsupported: " + r);
		}
		return out;
	}
}
