package us.mn.state.dot.tms.server.comm.onvif.session.exceptions;

import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

/**
 * Thrown when a service is requested that is not supported by the device
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class ServiceNotSupportedException extends Exception {
	public ServiceNotSupportedException(OnvifService type) {
		super("Device does not support " + type + " Service requests.");
	}
}
