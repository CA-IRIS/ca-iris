package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotInitializedException;

import java.io.IOException;

/**
 * An OpOnvif contains the logic for interpreting a specific IRIS client UI
 * request and converting it into one or more sequential OnvifProperties.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OpOnvif<T extends OnvifProperty> extends OpDevice<T> {
	protected OnvifSessionMessenger session;
	private IOException setupException;

	protected OpOnvif(
		PriorityLevel p, DeviceImpl d, OnvifSessionMessenger session,
		OnvifService service)
	{
		super(p, d);
		log("Preparing for operation. ");
		this.session = session;
		if (!session.isInitialized())
			applySessionCredentials();
		if (setupException == null)
			selectService(service);
		if (setupException != null) {
			log(setupException.getMessage());
			setFailed();
		}
	}

	private void applySessionCredentials() {
		ControllerImpl c = getController();
		if (c == null)
			setupException =
				new IOException("Failed to find Controller. ");
		else if (c.getUsername() == null || c.getUsername().isEmpty()
			|| c.getPassword() == null || c.getPassword().isEmpty())
			setupException = new IOException(
				"Controller username or password not set: " + c
					.getName());
		else
			session.setAuth(c.getUsername(), c.getPassword());
	}

	/**
	 * We have to make the service selection before encodeStore, because
	 * the
	 * open command will be called for us before poll/encodeStore().
	 * However, we cannot throw an exception in the constructor, so we save
	 * it until we can throw it at an appropriate time. We also cannot do
	 * this in the property, because the
	 */
	private void selectService(OnvifService service) {
		try {
			session.setUri(service);
		} catch (SessionNotInitializedException e) {
			try {
				session.open();
				session.setUri(service);
			} catch (IOException e1) {
				setupException = e1;
			}
		} catch (ServiceNotSupportedException e) {
			setupException = e;
		}
	}

	protected void updateOpStatus(String stat) {
		String s = getOperationDescription() + ": " + stat;
		device.setOpStatus(s);
	}

	@Override
	protected abstract OnvifPhase phaseTwo();

	protected abstract class OnvifPhase extends Phase<T> {
		protected abstract OnvifPhase poll2(CommMessage<T> cm)
			throws IOException;

		/**
		 * Forces some error handling for Onvif devices
		 *
		 * @throws IOException if the setup or property fail
		 */
		protected OnvifPhase poll(CommMessage<T> mess)
			throws IOException
		{
			try {
				if (setupException != null)
					throw setupException;
				return poll2(mess);
			} catch (IOException e) {
				log(e.getMessage());
				setFailed();
				throw new IOException(e.getMessage());
			}
		}
	}

	protected void log(String msg) {
		updateOpStatus(msg);
		OnvifPoller.log(getOpName() + ": " + msg);
	}

	protected void logSent(OnvifProperty p) {
		log(p.getClass().getSimpleName() + " sent. ");
	}
}
