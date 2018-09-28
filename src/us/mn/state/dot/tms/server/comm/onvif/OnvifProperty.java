package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

import java.io.IOException;
import java.io.InputStream;

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

	/**
	 * message to display after decodeStore()
	 * Concrete OnvifProperties may change this message if they do not want
	 * "Ready" to be displayed when they are done. For example,
	 * OnvifPTZMoveProperty should be followed by OnvifPTZStopProperty, so
	 * we change the doneMsg to "PTZMoving" between those two properties. 
	 */
	protected String doneMsg = "Ready";

	/**
	 * OnvifPhases have OnvifProperties that are stores by default
	 */
	protected boolean isQuery = false;

	protected OnvifProperty(OnvifSessionMessenger session) {
		this.session = session;
	}

	/**
	 * by default, responses to stores are ignored but logged
	 */
	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (response == null)
			log("No response received from device");
		else {
			log("Device responded: " +
				response.getClass().getSimpleName());
			decodeStore();
		}
	}

	public String getDoneMsg() {
		return doneMsg;
	}

	public boolean isQuery() {
		return isQuery;
	}

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
	protected static float resize(
		float val,
		float oldMin, float oldMax,
		float newMin, float newMax)
		throws AssertionError
	{
		assert oldMin <= val && val <= oldMax; // validate inputs
		assert oldMin < oldMax; // avoid division by zero
		assert newMin <= newMax; // might be okay to constrain to a single value
		float oldRange = oldMax - oldMin;
		float newRange = newMax - newMin;
		return newRange * (val - oldMin) / oldRange + newMin;
	}

	protected void log(String msg) {
		session.log(msg, this);
	}
}
