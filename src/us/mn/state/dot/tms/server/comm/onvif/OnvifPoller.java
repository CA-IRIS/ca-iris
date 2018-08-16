package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.TransientPoller;
import us.mn.state.dot.tms.server.comm.onvif.operations.*;

import java.util.LinkedList;

/**
 * An OnvifPoller represents a single Onvif device. It dispatches OpOnvifs in
 * response to client UI requests.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifPoller extends TransientPoller<OnvifProperty>
	implements CameraPoller
{
	/**
	 * This is just a more specific reference to our messenger (which
	 * happens to be a session) for convenience
	 */
	private OnvifSessionMessenger session = (OnvifSessionMessenger) messenger;
	private String name;

	public OnvifPoller(String name, OnvifSessionMessenger m) {
		super(name, m);
		this.name = name;
		log("Created " + this.name + ". ");
	}

	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		setAuthAddOp(new OpOnvifPTZ(c, p, t, z, session));
	}

	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		setAuthAddOp(new OpOnvifPTZPreset(c, preset, true, session));
	}

	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		setAuthAddOp(new OpOnvifPTZPreset(c, preset, false, session));
	}

	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {
		switch (r) {
		case CAMERA_PTZ_FULL_STOP:
			setAuthAddOp(
				new OpOnvifPTZ(c, 0, 0, 0, session));
			break;
		case CAMERA_WIPER_ONESHOT:
			setAuthAddOp(new OpOnvifPTZAux(c, session));
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
		case BRIGHTNESS_GOOD:
		case BRIGHTNESS_TOO_DIM:
		case BRIGHTNESS_TOO_BRIGHT:
			setAuthAddOp(new OpOnvifImaging(c, session, r));
			break;
		case RESET_DEVICE:
		case QUERY_CONFIGURATION:
		case QUERY_MESSAGE:
		case QUERY_STATUS:
		case QUERY_PIXEL_FAILURES:
		case TEST_PIXELS:
		case TEST_FANS:
		case TEST_LAMPS:
		case RESET_MODEM:
		case SEND_SETTINGS:
		case SEND_LEDSTAR_SETTINGS:
		case QUERY_LEDSTAR_SETTINGS:
		case DISABLE_SYSTEM:
		case NO_REQUEST:
			setAuthAddOp(new OpOnvifDevice(c, session, r));
			break;
		}
	}

	/**
	 * Onvif devices don't use drops.
	 */
	@Override
	public boolean isAddressValid(int drop) {
		log("Drop addresses are not valid for Onvif devices. ");
		return true;
	}

	private void setAuthAddOp(OpOnvif<OnvifProperty> op) {
		if (session.authNotSet())
			applySessionCredentials();
		addOperation(op);
	}

	private void applySessionCredentials() {
		CommLink cl = CommLinkHelper.lookup(name);
		ControllerImpl c = null;
		if (cl == null)
			log("Failed to find CommLink for" + name + ". ");
		else
			c = getControllerImpl((CommLinkImpl) cl);
		if (c == null)
			log("Failed to find Controller for " + name + ". ");
		else
			session.setAuth(c.getUsername(), c.getPassword());
	}

	private ControllerImpl getControllerImpl(CommLinkImpl cl) {
		ControllerImpl found = null;
		LinkedList<Controller> controllers = cl.getActiveControllers();
		for (Controller c : controllers)
			if (c.getCommLink().getName().equals(cl.getName()))
				found = (ControllerImpl) c;
		return found;
	}

	private void log(String message) {
		session.log(getClass().getSimpleName() + ": " + message);
	}
}
