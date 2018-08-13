package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import java.io.IOException;

/**
 * Thrown when the Onvif device does something we don't expect.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class SoapWrapperException extends IOException {
	private Integer httpErr = null;

	public Integer getHttpErr() {
		return httpErr;
	}

	public void setHttpErr(int httpErr) {
		this.httpErr = httpErr;
	}

	public SoapWrapperException(int status) {
		this.httpErr = status;
	}

	public SoapWrapperException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return "Device responded with HTTP error status: "
			+ httpErr + ". ";
	}
}
