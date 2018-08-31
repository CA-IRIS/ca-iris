package us.mn.state.dot.tms.server.comm.onvif.properties.exceptions;

import java.io.IOException;

/**
 * Thrown when an operation fails.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OperationFailedException extends IOException {
	public OperationFailedException(String m) {
		super(m);
	}
}
