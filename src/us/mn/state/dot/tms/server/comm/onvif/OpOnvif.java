package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotStartedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapTransmissionException;

import java.io.IOException;

/**
 * An OpOnvif contains the logic for interpreting a specific IRIS client UI
 * request and converting it into one or more sequential OnvifProperties.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OpOnvif<T extends OnvifProperty> extends OpDevice<T> {
	protected final OnvifSessionMessenger session;
	private final OnvifService service;

	protected OpOnvif(
		PriorityLevel p, DeviceImpl d, OnvifSessionMessenger session,
		OnvifService service)
	{
		super(p, d);
		this.session = session;
		this.service = service;
	}

	/**
	 * @return An OnvifPhase returned is a single part of the sequence. A
	 * 	single phase may set/send multiple properties; however, this is
	 * 	generally not the case for OnvifPhases.
	 */
	@Override
	protected abstract OnvifPhase phaseTwo();

	protected abstract class OnvifPhase extends Phase<T> {
		// OpOnvifs only ever have one OnvifProperty, so it doesn't
		// make sense to queue them in the CommMessageImpl.
		// Furthermore, bypassing the usual add() and storeProps() of
		// the CommMessage allows bypassing of the null
		// checking on the input and output streams of the Messenger,
		// because we don't use them directly for blocking soap calls.
		protected T prop;

		protected abstract OnvifPhase poll2(
			CommMessage<T> cm)
			throws IOException;

		protected OnvifPhase poll(CommMessage<T> mess)
			throws IOException
		{
			try {
				log("Preparing for operation... ");
				session.selectService(service);
				OnvifPhase op = poll2(mess);
				if (prop == null)
					log("Missing property");
				else {
					mess.logStore(prop);
					prop.encodeStore(null, null);
					prop.decodeStore(null, null);
				}
				return op;
			} catch (SoapTransmissionException
				| SessionNotStartedException
				| ServiceNotSupportedException e) {
				setFailed();
				log(e.getMessage());
				throw new IOException(e.getMessage());
			} finally {
				log("Operation " + (isSuccess() ? "succeeded" :
					"failed") + ". ");
			}
		}
	}

	protected void updateOpStatus(String stat) {
		String s = getOperationDescription() + ": " + stat;
		device.setOpStatus(s);
	}

	protected void log(String msg) {
		updateOpStatus(msg);
		session.log(getOpName() + ": " + msg);
	}
}
