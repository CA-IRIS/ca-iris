package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A OnvifPoperty knows how to get information required for an OpOnvif and how
 * to send the specific OpOnvif. Usually a ControllerProperty is the encoded
 * message.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OnvifProperty extends ControllerProperty {
	protected OnvifSessionMessenger session;

	/** any response to the call to the Service */
	protected Object response;

	protected OnvifProperty(
		OnvifSessionMessenger session)
	{
		this.session = session;
		log("Preparing operation properties. ");
	}

	private String readStream(InputStream is) {
		// todo determine correct delimiter
		java.util.Scanner s = new java.util.Scanner(
			is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * @return a float remapped to a new range
	 * @throws AssertionError if oldMin == oldMax
	 */
	protected float resize(
		float val, float oldMin, float oldMax, float newMin,
		float newMax) throws AssertionError
	{
		assert oldMin != oldMax; // avoid division by zero
		assert val < oldMax && val > oldMin; // check inputs
		assert newMax > newMin;
		float oldRange = oldMax - oldMin;
		float newRange = newMax - newMin;
		return (val - oldMin) / oldRange * newRange + newMin;
	}

	/**
	 * Encode a store request and send it to the device
	 */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		log("Sending operation properties. ");
		try {
			encodeStore(os);
		} catch (ServiceNotSupportedException e) {
			logFailure(e.getMessage());
		}
	}

	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (response == null)
			log("No response received from device. ");
		else
			decodeStore();
	}

	/**
	 * A way of requiring concrete implementations of this class to check
	 * that the session is initialized before sending a request
	 */
	protected abstract void encodeStore(OutputStream os)
		throws IOException;

	/**
	 * may be overridden by concrete implementations if errors may be
	 * produced by encodeStore()
	 */
	protected void decodeStore() throws IOException {
		log(response.getClass().getSimpleName());
	}

	protected void log(String msg) {
		OnvifPoller.log(this.getClass().getSimpleName() + ": " + msg);
	}

	protected void logFailure(String msg) throws IOException {
		String m = this.getClass().getSimpleName() + ": " + msg;
		OnvifPoller.log(m);
		throw new IOException(m);
	}
}
