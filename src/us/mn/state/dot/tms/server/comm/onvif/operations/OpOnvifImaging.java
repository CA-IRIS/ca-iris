package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifFocusProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifImaging extends OpOnvif {
	private OnvifImagingProperty property;

	public OpOnvifImaging(
		DeviceImpl d,
		OnvifSessionMessenger session,
		DeviceRequest r)
	{
		super(PriorityLevel.COMMAND, d, session);
		property = selectProperty(r);
	}

	private OnvifImagingProperty selectProperty(DeviceRequest r) {
		OnvifFocusProperty out = null;
		switch (r) {
		case CAMERA_FOCUS_NEAR:
		case CAMERA_FOCUS_FAR:
		case CAMERA_FOCUS_STOP:
		case CAMERA_FOCUS_MANUAL:
		case CAMERA_FOCUS_AUTO:
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
		case CAMERA_IRIS_STOP:
		case CAMERA_IRIS_MANUAL:
		case CAMERA_IRIS_AUTO:
			out = new OnvifFocusProperty(session);
			break;
		default:
			OnvifPoller.log("OpOnvifImaging does not recognize request: " + r);
		}
		return out;
	}

	/**
	 * More Device commands may be supported in the future, but Reboot is
	 * all the UI supports for now.
	 */
	@Override
	protected Phase<OnvifProperty> phaseTwo() {
		return new Adjust();
	}

	protected class Adjust extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(property);
			mess.storeProps();
			updateOpStatus("Onvif device reboot command sent");
			return null;
		}
	}
}
