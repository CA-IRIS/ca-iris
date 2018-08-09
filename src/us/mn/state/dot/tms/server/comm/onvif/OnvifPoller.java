package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.TransientPoller;
import us.mn.state.dot.tms.server.comm.onvif.operations.*;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifPoller extends TransientPoller<OnvifProperty>
	implements CameraPoller
{
	private static final DebugLog ONVIF_LOG = new DebugLog("onvif");
	/**
	 * this is just a more specific reference to our messenger (which
	 * happens to be a session) for convenience
	 */
	private OnvifSessionMessenger session;

	public OnvifPoller(String name, OnvifSessionMessenger m) {
		super(name, m);
		session = m;
		log("Onvif device created: " + name);
	}

	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		addOperation(new OpOnvifPTZ(c, p, t, z, session));
	}

	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		addOperation(new OpOnvifPTZPreset(c, preset, true, session));
	}

	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		addOperation(new OpOnvifPTZPreset(c, preset, false, session));
	}

	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {
		switch (r) {
		case CAMERA_PTZ_FULL_STOP:
			addOperation(
				new OpOnvifPTZ(c, 0, 0, 0, session));
			break;
		case CAMERA_WIPER_ONESHOT:
			addOperation(new OpOnvifPTZAux(c, session));
			break;
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
			addOperation(new OpOnvifImaging(c, session, r));
			break;
		case RESET_DEVICE:
			addOperation(new OpOnvifDevice(c, session));
			break;
		case QUERY_CONFIGURATION:
		case QUERY_MESSAGE:
		case QUERY_STATUS:
		case QUERY_PIXEL_FAILURES:
		case TEST_PIXELS:
		case TEST_FANS:
		case TEST_LAMPS:
		case BRIGHTNESS_GOOD:
		case BRIGHTNESS_TOO_DIM:
		case BRIGHTNESS_TOO_BRIGHT:
		case RESET_MODEM:
		case SEND_SETTINGS:
		case SEND_LEDSTAR_SETTINGS:
		case QUERY_LEDSTAR_SETTINGS:
		case DISABLE_SYSTEM:
			log("Device request not supported: " + r);
			break;
		case NO_REQUEST:
			log("Received: " + r);
			break;
		default:
			log("Unrecognized device request: " + r);
			break;
		}
	}

	/**
	 * onvif devices don't use drops
	 */
	@Override
	public boolean isAddressValid(int drop) {
		log("Drop addresses not valid for Onvif devices");
		return true;
	}

	public static void log(String message) {
		ONVIF_LOG.log("ONVIF: " + message);
	}
}
