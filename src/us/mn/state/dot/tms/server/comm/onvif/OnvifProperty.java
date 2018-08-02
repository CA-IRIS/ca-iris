package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class OnvifProperty extends ControllerProperty {
	protected OnvifSessionMessenger session;
	protected SOAPMessage response;

	protected OnvifProperty(OnvifSessionMessenger session) {
		this.session = session;
		OnvifPoller.log("Preparing operation properties.");
	}

	/**
	 * Read from is and decode a STORE response
	 */
	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		// todo handle error responses
		if (response != null)
			OnvifPoller
				.log("Unexpected ONVIF device response " +
					"received");
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
	 *
	 * @return true if the session was freshly initialized
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
	 * Encode a store request and write it to the output stream
	 */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		initSession(c);
		encodeStore();
	}

	protected abstract void encodeStore() throws IOException;
}
