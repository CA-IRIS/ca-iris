package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OnvifProperty extends ControllerProperty {
	protected OnvifSessionMessenger session;
	protected Object response;

	protected OnvifProperty(OnvifSessionMessenger session) {
		this.session = session;
		log("Preparing operation properties");
	}

	private String readStream(InputStream is) {
		// todo determine correct delimiter
		java.util.Scanner s = new java.util.Scanner(
			is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * must be called by subclasses before attempting to use the auth
	 * credentials
	 */
	private void initSession(ControllerImpl c) throws IOException {
		// this should be a one time session setup per device per
		// client
		if (!session.isInitialized()) {
			try {
				session.setAuth(c.getUsername(),
					c.getPassword());
				session.open();
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
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
		log("Sending operation properties");
		initSession(c);
		encodeStore();
	}

	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		log("Reading operation response properties");
		if (response == null)
			throw new IOException(
				"No response received from device");
		decodeStore();
	}

	/**
	 * A way of requiring concrete implementations of this class to check
	 * that the session is initialized before sending a request
	 *
	 * @throws IOException the session could not be initialized
	 */
	protected abstract void encodeStore() throws IOException;

	/**
	 * may be overridden by concrete implementations if errors may be
	 * produced by encodeStore()
	 *
	 * @throws IOException
	 */
	protected void decodeStore() throws IOException {
		log("Device responded: " + response.toString());
	}

	protected void log(String msg) {
		OnvifPoller.log("Property: " + msg);
	}

	protected void logFailure (String msg) throws IOException {
		String m = this.getClass().getSimpleName() + ": " + msg;
		log(m);
		throw new IOException(m);
	}
}
