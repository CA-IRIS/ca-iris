package us.mn.state.dot.tms.server.comm.onvif;

import org.onvif.ver10.schema.PTZPreset;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OnvifProperty extends ControllerProperty {
	protected OnvifSessionMessenger session;
	public Object response;

	protected OnvifProperty(OnvifSessionMessenger session) {
		this.session = session;
		OnvifPoller.log("Preparing operation properties");
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
				session.initialize(c.getUsername(),
					c.getPassword());
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
	}

	/**
	 * Encode a store request and send it to the device
	 */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		initSession(c);
		encodeStore();
	}

	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
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

	}

	/**
	 *
	 * @param preset the IRIS present number to find
	 * @param presets the presets to look in
	 * @return null if not found else contains the presetToken
	 */
	protected String findPresetToken(
		Integer preset, List<PTZPreset> presets)
	{
		for (PTZPreset p : presets) {
			if (p.getName().equals("IRIS" + preset))
				return p.getToken();
		}
		return null;
	}
}
