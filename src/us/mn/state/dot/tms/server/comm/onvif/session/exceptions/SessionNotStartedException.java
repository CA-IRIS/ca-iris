package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import java.io.IOException;

/**
 * Thrown when a session method is called that requires session initialization
 * before the session has been initialized.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class SessionNotStartedException extends IOException {
	private static String NOT_INITIALIZED = "Session failed to start. ";

	public SessionNotStartedException(String cause) {
		super(NOT_INITIALIZED + cause);
	}

	public SessionNotStartedException(Throwable cause) {
		super(NOT_INITIALIZED, cause);
	}
}
