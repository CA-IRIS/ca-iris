package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifDeviceInfoProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifDeviceRebootProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * An OpOnvifDevice sends OnvifDevice*Properties to the Device Service.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifDevice extends OpOnvif<OnvifProperty> {
	private DeviceRequest request;

	public OpOnvifDevice(
		DeviceImpl d,
		OnvifSessionMessenger session,
		DeviceRequest r, ControllerImpl controller)
	{
		super(PriorityLevel.URGENT, d, session, OnvifService.DEVICE, controller);
		request = r;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return new Device();
	}

	protected class Device extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty rv;
			switch (request) {
			case CAMERA_PREPARE:
				rv = new OnvifDeviceInfoProperty(session);
				break;
			case RESET_DEVICE:
				 rv = new OnvifDeviceRebootProperty(session);
				 break;
			default:
				throw new IOException(
					"Unsupported: " + request);
			}
			return rv;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
