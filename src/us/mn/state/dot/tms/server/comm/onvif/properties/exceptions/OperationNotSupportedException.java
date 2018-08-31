package us.mn.state.dot.tms.server.comm.onvif.properties.exceptions;

import java.io.IOException;

/**
 * Thrown when an web service operation is requested that is not supported by
 * the device.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OperationNotSupportedException extends IOException {
	public OperationNotSupportedException(String op) {
		super("Unsupported" + op);
	}
}
