package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * Thrown when a service is requested that is not supported by the device.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class ServiceNotSupportedException extends IOException {
	public ServiceNotSupportedException(OnvifService type) {
		super("Device does not support " + type + " Service requests. ");
	}

	public ServiceNotSupportedException(OnvifService type, String reason) {
		super("Device does not support " + type + " Service requests. Cause: " + reason);
	}
}
