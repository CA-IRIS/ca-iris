package us.mn.state.dot.tms.server.comm.onvif.properties.exceptions;

import java.io.IOException;

/**
 * Thrown when the response from the Camera to an OnvifProperty's request
 * indicates that the Camera failed to successfully perform the request or when
 * the operation is supported, but some precondition was not met.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OperationFailedException extends IOException {
	public OperationFailedException(String m) {
		super("Failed" + m);
	}
}
