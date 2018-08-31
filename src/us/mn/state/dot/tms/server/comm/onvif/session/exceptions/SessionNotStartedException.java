package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import java.io.IOException;

/**
 * Thrown when a session method is called that requires a session to be open
 * before the session has been successfully opened.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class SessionNotStartedException extends IOException {
	private static String NOT_INITIALIZED = "FailedToConnect";

	public SessionNotStartedException(String cause) {
		super(NOT_INITIALIZED + ": " + cause);
	}
}
