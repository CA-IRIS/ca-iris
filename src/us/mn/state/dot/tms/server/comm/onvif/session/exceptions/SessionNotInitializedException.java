package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import java.io.IOException;

/**
 * Thrown when a session method is called that requires session initialization
 * before the session has been initialized.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class SessionNotInitializedException extends IOException {
	public SessionNotInitializedException() {
		super("Session failed to start. " +
			"Please check username, password, and uri. ");
	}

	public SessionNotInitializedException(String cause) {
		super("Session failed to start. "
			+ "Invalid " + cause + ". ");
	}

	public SessionNotInitializedException(Throwable cause) {
		super("Session failed to start. ", cause);
	}
}
