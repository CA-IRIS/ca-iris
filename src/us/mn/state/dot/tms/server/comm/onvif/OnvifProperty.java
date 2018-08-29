package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An OnvifPoperty knows how to get the information required for an OpOnvif and
 * how to send the specific OpOnvif. Usually a ControllerProperty is the encoded
 * message and the sending logic.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OnvifProperty extends ControllerProperty {
	protected final OnvifSessionMessenger session;

	/** any response to the call to the Service */
	protected Object response;

	protected OnvifProperty(OnvifSessionMessenger session) {
		this.session = session;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		log("Preparing operation properties... ");
		encodeStore();
		log("Operation properties sent. ");
	}

	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (response == null)
			log("No response received from device. ");
		else {
			log("Device responded: " +
				response.getClass().getSimpleName());
			decodeStore();
		}
	}

	/**
	 * Send this property to the device. Should set response.
	 * @throws IOException
	 */
	protected abstract void encodeStore() throws IOException;

	/**
	 * Handle errors or additional checking as needed from response to
	 * encodeStore().
	 */
	protected void decodeStore() throws IOException {}

	/**
	 * @return val remapped to a new range
	 * @throws AssertionError if inputs are invalid (e.g. a min is not less
	 * 	than a max or val is not between oldMin and oldMax inclusive).
	 */
	protected float resize(
		float val,
		float oldMin, float oldMax,
		float newMin, float newMax)
		throws AssertionError
	{
		assert oldMin <= val && val <= oldMax; // validate inputs
		assert oldMin != oldMax; // avoid division by zero
		assert newMin < newMax; // also assures no division by zero
		float oldRange = oldMax - oldMin;
		float newRange = newMax - newMin;
		return newRange * (val - oldMin) / oldRange + newMin;
	}

	protected void log(String msg) {
		session.log(getClass().getSimpleName() + ": " + msg);
	}

	protected void logFailure(String msg) throws IOException {
		String m = getClass().getSimpleName() + ": " + msg;
		session.log(m);
		throw new IOException(m);
	}
}
