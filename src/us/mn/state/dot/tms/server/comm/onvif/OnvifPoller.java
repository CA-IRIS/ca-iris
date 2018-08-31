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
import us.mn.state.dot.tms.server.comm.onvif.operations.OpOnvifDevice;
import us.mn.state.dot.tms.server.comm.onvif.operations.OpOnvifImaging;
import us.mn.state.dot.tms.server.comm.onvif.operations.OpOnvifPTZ;
import us.mn.state.dot.tms.server.comm.onvif.operations.OpOnvifPTZAux;
import us.mn.state.dot.tms.server.comm.onvif.operations.OpOnvifPTZPreset;

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
	private OnvifSessionMessenger session =
		(OnvifSessionMessenger) messenger;
	private String name;

	public OnvifPoller(String name, OnvifSessionMessenger m) {
		super(name, m);
		this.name = name;
		log("Created " + this.name);
	}

	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		prepAndAddOp(new OpOnvifPTZ(c, p, t, z, session), c);
	}

	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		prepAndAddOp(new OpOnvifPTZPreset(c, preset, true, session), c);
	}

	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		prepAndAddOp(new OpOnvifPTZPreset(c, preset, false, session), c);
	}

	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {
		switch (r) {
		case CAMERA_PTZ_FULL_STOP:
			prepAndAddOp(
				new OpOnvifPTZ(c, 0, 0, 0, session), c);
			break;
		case CAMERA_WIPER_ONESHOT:
			prepAndAddOp(new OpOnvifPTZAux(c, session), c);
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
			prepAndAddOp(new OpOnvifImaging(c, session, r), c);
			break;
		case RESET_DEVICE:
		case NO_REQUEST:
			prepAndAddOp(new OpOnvifDevice(c, session, r), c);
			break;
		default:
			log("Unsupported: " + r + ". ");
		}
	}

	/**
	 * Onvif devices don't use drops.
	 */
	@Override
	public boolean isAddressValid(int drop) {
		log("Drop addresses are invalid for Onvif devices");
		return true;
	}

	/**
	 * Sets the timeout (see MessagePoller) and the auth credentials (see
	 * OnvifSessionMessenger).
	 */
	private void prepAndAddOp(OpOnvif<OnvifProperty> op, CameraImpl ci) {
		session.setCamera(ci);
		if (session.authNotSet())
			applyAuthCredentials();
		setIdleSecs(session.getTimeout());
		addOperation(op);
	}

	private void applyAuthCredentials() {
		CommLink cl = CommLinkHelper.lookup(name);
		ControllerImpl c = null;
		if (cl == null)
			log("Failed to find CommLink for" + name);
		else
			c = getControllerImpl((CommLinkImpl) cl);
		if (c == null)
			log("Failed to find Controller for " + name);
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
		session.log(message, this);
	}
}
