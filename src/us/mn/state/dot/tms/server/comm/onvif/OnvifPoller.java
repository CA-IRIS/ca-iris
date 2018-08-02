package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.TransientPoller;
import us.mn.state.dot.tms.server.comm.onvif.operations.OpOnvifPTZ;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifPoller extends TransientPoller<OnvifProperty>
	implements CameraPoller
{
	private static final DebugLog ONVIF_LOG = new DebugLog("onvif");

	/**
	 * this is just a more specific reference to our session for
	 * convenience
	 */
	private OnvifSessionMessenger session;

	public OnvifPoller(String name, OnvifSessionMessenger m) throws
		IOException
	{
		super(name, m);
		session = m;
		CommLink cl = CommLinkHelper.lookup(name);
		ControllerImpl c = null;

		if (cl == null)
			log("failed to find CommLink.");
		else
			c = getControllerImpl((CommLinkImpl) cl);
		if (c == null)
			log("could not find controller for: " + name);
		else {
			try {
				session.initialize(c.getUsername(),
					c.getPassword());
			} catch (Exception e) {
				log(e);
				throw new IOException(
					"Failed to start onvif session");
			}
		}
		log("onvif device instantiated: " + name);
	}

	private ControllerImpl getControllerImpl(CommLinkImpl cl) {
		ControllerImpl found = null;
		LinkedList<Controller> controllers = cl.getActiveControllers();
		for (Controller c : controllers)
			if (c.getCommLink().getName().equals(cl.getName()))
				found = (ControllerImpl) c;
		return found;

	}

	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		addOperation(new OpOnvifPTZ(c, p, t, z, session));
	}

	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		// todo implement (ptz: set preset)
		log("store preset not yet implemented");
	}

	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		// todo implement (ptz: go to preset)
		log("recall preset not yet implemented");
	}

	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {
		switch (r) {
		case CAMERA_PTZ_FULL_STOP:
			addOperation(
				new OpOnvifPTZ(c, 0, 0, 0, session));
			break;
		case CAMERA_WIPER_ONESHOT:
			// todo implement (ptz: auxillary command)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_FOCUS_STOP:
			// todo implement (imaging: continuous focus)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_FOCUS_NEAR:
			// todo implement (imaging: continuous focus)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_FOCUS_FAR:
			// todo implement (imaging: continuous focus)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_FOCUS_MANUAL:
			// todo implement (imaging: focus modes)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_FOCUS_AUTO:
			// todo implement (imaging: focus modes)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_IRIS_STOP:
			// todo implement (imaging: set imaging
			// settings)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_IRIS_CLOSE:
			// todo implement (imaging: setimaging
			// settings)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_IRIS_OPEN:
			// todo implement (imaging: set imaging
			// settings)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_IRIS_MANUAL:
			// todo implement (imaging: set imaging
			// settings)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case CAMERA_IRIS_AUTO:
			// todo implement (imaging: set imaging
			// settings)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
			break;
		case RESET_DEVICE:
			// todo implement (device: soft factory reset)
			log("ONVIF DeviceRequest not yet implemented: "
				+ r);
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
			log("DeviceRequest not supported: " + r);
			break;
		case NO_REQUEST:
			log("received: " + r);
			break;
		default:
			log("received: " + r);
			break;
		}
	}

	/**
	 * onvif devices don't use drops
	 */
	@Override
	public boolean isAddressValid(int drop) {
		log("Drop addresses not supported");
		return true;
	}

	public static void log(String message) {
		ONVIF_LOG.log("ONVIF: " + message);
	}

	public static void log(Exception e) {
		ONVIF_LOG.log("ONVIF: " + e.getMessage() + "\n"
			+ Arrays.toString(e.getStackTrace()));
	}
}
