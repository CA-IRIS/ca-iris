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
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingIrisAutoProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

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
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm) {
			prop = selectProperty(request);
			return null;
		}
	}

	private OnvifProperty selectProperty(DeviceRequest r) {
		OnvifProperty out = null;
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
		case CAMERA_IRIS_MANUAL:
			out = new OnvifImagingIrisAutoProperty(session, false);
			break;
		case CAMERA_IRIS_AUTO:
			out = new OnvifImagingIrisAutoProperty(session, true);
			break;
		case CAMERA_IRIS_CLOSE:
			// todo
			break;
		case CAMERA_IRIS_OPEN:
			// todo
			break;
		case CAMERA_IRIS_STOP:
			log(r.toString());
			break;
		default:
			log("Unrecognized: " + r);
		}
		return out;
	}
}
