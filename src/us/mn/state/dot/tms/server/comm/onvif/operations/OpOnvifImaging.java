package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ExposureMode;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingFocusAutoProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingFocusMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingIrisAutoProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingIrisMoveProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * An OpOnvifImaging sends OnvifImaging*Properties to the Imaging Service.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifImaging extends OpOnvif<OnvifProperty> {
	private final DeviceRequest request;

	public OpOnvifImaging(
		DeviceImpl d,
		OnvifSessionMessenger session,
		DeviceRequest r)
	{
		super(PriorityLevel.COMMAND, d, session, OnvifService.IMAGING);
		request = r;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		OnvifPhase op;
		switch (request) {
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
			op = new IrisModeCheck();
			break;
		default:
			op = new Adjust();
		}
		return op;
	}

	/**
	 * Some manufacturers will ignore manual iris adjustments if the device
	 * is currently in auto mode.
	 */
	protected class IrisModeCheck extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out = null;
			if (session.getImagingSettings().getExposure()
				.getMode() == ExposureMode.AUTO)
				out = new OnvifImagingIrisAutoProperty(
					session, false);
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new Adjust();
		}
	}

	protected class Adjust extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out = null;
			switch (request) {
			case CAMERA_FOCUS_NEAR:
				out =
					new OnvifImagingFocusMoveProperty(session,
					-0.1f);
				break;
			case CAMERA_FOCUS_FAR:
				out =
					new OnvifImagingFocusMoveProperty(session,
					0.1f);
				break;
			case CAMERA_FOCUS_STOP:
				out =
					new OnvifImagingFocusMoveProperty(session,
					0f);
				break;
			case CAMERA_FOCUS_MANUAL:
				out =
					new OnvifImagingFocusAutoProperty(session,
					false);
				break;
			case CAMERA_FOCUS_AUTO:
				out =
					new OnvifImagingFocusAutoProperty(session,
					true);
				break;
			case CAMERA_IRIS_CLOSE:
			case CAMERA_IRIS_OPEN:
			case CAMERA_IRIS_STOP:
				out = new OnvifImagingIrisMoveProperty(session,
					request);
				break;
			case CAMERA_IRIS_MANUAL:
				out = new OnvifImagingIrisAutoProperty(session,
					false);
				break;
			case CAMERA_IRIS_AUTO:
				out = new OnvifImagingIrisAutoProperty(session,
					true);
				break;
			default:
				log("Unrecognized: " + request);
			}
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}


}
