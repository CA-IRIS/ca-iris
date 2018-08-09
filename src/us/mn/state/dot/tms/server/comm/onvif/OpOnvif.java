package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OpOnvif<T extends OnvifProperty> extends OpDevice<T> {
	protected OnvifSessionMessenger session;

	protected OpOnvif(
		PriorityLevel p, DeviceImpl d, OnvifSessionMessenger session)
	{
		super(p, d);
		this.session = session;
		if (!session.isInitialized())
			applySessionCredentials();
		log("Preparing operation");
	}

	private void applySessionCredentials() {
		ControllerImpl c = getController();
		if (c == null)
			log("Failed to find Controller");
		else if (c.getUsername() == null || c.getPassword() == null
			|| c.getUsername().isEmpty() || c.getPassword()
			.isEmpty())
			log("Controller username or password not set: " +
				c.getName());
		else
			session.setAuth(c.getUsername(), c.getPassword());
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

	@Override
	protected abstract OnvifPhase phaseTwo();

	protected abstract class OnvifPhase extends Phase<T>
	{
		protected abstract OnvifPhase poll2(CommMessage<T> p)
			throws IOException;

		/**
		 * Forces some error handling for Onvif devices
		 * @throws IOException if the property failed
		 */
		protected OnvifPhase poll(CommMessage<T> mess) throws IOException
		{
			try {
				return poll2(mess);
			} catch (IOException e) {
				log(e.getMessage());
				setFailed();
				throw e;
			}
		}
	}

	protected void log(String msg) {
		updateOpStatus(msg);
		OnvifPoller.log(getOpName() + ": " + msg);
	}
}
