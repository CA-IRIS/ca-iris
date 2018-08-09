package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class SessionNotInitializedException extends IOException {
	private static String CAUSE = "Please check device username, password, and uri.";

	public SessionNotInitializedException() {
		super(CAUSE);
	}

	public SessionNotInitializedException(String message) {
		super(message + CAUSE);
	}
}
