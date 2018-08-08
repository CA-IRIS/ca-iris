package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OpOnvif extends OpDevice<OnvifProperty> {
	protected OnvifSessionMessenger session;

	protected OpOnvif(
		PriorityLevel p, DeviceImpl d, OnvifSessionMessenger session)
	{
		super(p, d);
		this.session = session;
		checkSessionCredentials();
		log("Preparing an operation");
	}

	private void checkSessionCredentials() {
		if (!session.isInitialized()) {
			ControllerImpl c = getController();
			if (c == null)
				log("Failed to find Controller");
			else if (c.getUsername() == null || c.getPassword() == null
				|| c.getUsername().isEmpty() || c.getPassword().isEmpty())
				log("Controller username or password not set: " + c.getName());
			else
				session.setAuth(c.getUsername(), c.getPassword());
		}
	}

	/**
	 * Update device op status. We bundle the operation description into
	 * the status because camera ops are generally so short that, as far as I
	 * can tell, by the time the client gets the SONAR "operation"
	 * notification and requests the op's description via SONAR, the device
	 * has already been released, and thus Device.getOperation() returns
	 * "None".
	 */
	protected void updateOpStatus(String stat) {
		String s = getOperationDescription() + ": " + stat;
		device.setOpStatus(s);
	}

	protected void log(String msg) {
		String m = device.getName() + ": " + getOpName() + ": " + msg;
		updateOpStatus(m);
		OnvifPoller.log(m);
	}
}
