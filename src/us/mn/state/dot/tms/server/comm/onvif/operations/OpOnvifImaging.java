package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingFocusAutoProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingFocusMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingIrisProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
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
		try {
			session.selectService(OnvifService.IMAGING);
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	private OnvifImagingProperty selectProperty(DeviceRequest r) {
		OnvifImagingProperty out = null;
		switch (r) {
		case CAMERA_FOCUS_NEAR:
			out = new OnvifImagingFocusMoveProperty(session, 0.2f, true);
			break;
		case CAMERA_FOCUS_FAR:
			out = new OnvifImagingFocusMoveProperty(session, 0.2f, false);
			break;
		case CAMERA_FOCUS_STOP:
			out = new OnvifImagingFocusMoveProperty(session, 0f, true);
			break;
		case CAMERA_FOCUS_MANUAL:
			out = new OnvifImagingFocusAutoProperty(session, false);
			break;
		case CAMERA_FOCUS_AUTO:
			out = new OnvifImagingFocusAutoProperty(session, true);
			break;
		case CAMERA_IRIS_CLOSE:
			out = new OnvifImagingIrisProperty(session, 10f, false);
			break;
		case CAMERA_IRIS_OPEN:
			out = new OnvifImagingIrisProperty(session, 10f, true);
			break;
		case CAMERA_IRIS_STOP:
			out = new OnvifImagingIrisProperty(session, 0f, false);
			break;
		default:
			log("Imaging Service request not recognized: " + r);
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
			log("Onvif device reboot command sent");
			return null;
		}
	}
}
