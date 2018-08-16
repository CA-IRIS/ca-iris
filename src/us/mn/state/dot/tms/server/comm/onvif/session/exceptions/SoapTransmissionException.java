package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import java.io.IOException;

/**
 * Thrown when the Onvif device does something we don't expect.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class SoapTransmissionException extends IOException {
	public SoapTransmissionException(String message) {
		super(message);
	}

	public SoapTransmissionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoapTransmissionException(Throwable cause) {
		super(cause);
	}
}
