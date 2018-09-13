package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.ControllerImpl;
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
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingMoveOptionsProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingOptionsProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifImagingSettingsProperty;
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
		DeviceRequest r, ControllerImpl controller)
	{
		super(PriorityLevel.COMMAND, d, session, OnvifService.IMAGING, controller);
		request = r;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return new SettingsPhase();
	}

	/**
	 * Some camera manufacturers have applied rounding in the case
	 * of negative attenuation values, so we must anticipate this and
	 * ensure that we have the correct value in our session cache after the
	 * device response is retrieved.
	 */
	protected class SettingsPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty p = null;
			switch (request) {
				case CAMERA_FOCUS_MANUAL:
				case CAMERA_FOCUS_AUTO:
				case CAMERA_IRIS_CLOSE:
				case CAMERA_IRIS_OPEN:
				case CAMERA_IRIS_MANUAL:
				case CAMERA_IRIS_AUTO:
					p = new OnvifImagingSettingsProperty(session);
			}
			return p;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new OptionsPhase();
		}
	}

	protected class OptionsPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty p = null;
			if (session.getImagingOptions() == null) {
				switch (request) {
				case CAMERA_FOCUS_MANUAL:
				case CAMERA_FOCUS_AUTO:
				case CAMERA_IRIS_CLOSE:
				case CAMERA_IRIS_OPEN:
				case CAMERA_IRIS_MANUAL:
				case CAMERA_IRIS_AUTO:
					p = new OnvifImagingOptionsProperty(session);
				}
			}
			return p;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new MoveOptionsPhase();
		}
	}

	protected class MoveOptionsPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty p = null;
			if (session.getImagingMoveOptions() == null) {
				switch (request) {
				case CAMERA_FOCUS_NEAR:
				case CAMERA_FOCUS_FAR:
				case CAMERA_FOCUS_STOP:
					p = new OnvifImagingMoveOptionsProperty(session);
				}
			}
			return p;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			OnvifPhase op;
			switch (request) {
			case CAMERA_IRIS_CLOSE:
			case CAMERA_IRIS_OPEN:
				op = new IrisModeCheckPhase();
				break;
			default:
				op = new AdjustPhase();
			}
			return op;
		}
	}

	/**
	 * Some manufacturers will ignore manual iris adjustments if the device
	 * is currently in auto mode.
	 */
	protected class IrisModeCheckPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out = null;
			if (session.getImagingSettings().getExposure() != null
				&& session.getImagingSettings().getExposure().getMode() == ExposureMode.AUTO)
				out = new OnvifImagingIrisAutoProperty(
					session, false,
					session.getImagingSettings(),
					session.getImagingOptions());
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return new AdjustPhase();
		}
	}

	protected class AdjustPhase extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			OnvifProperty out = null;
			switch (request) {
			case CAMERA_FOCUS_NEAR:
				out = new OnvifImagingFocusMoveProperty(session,
					-0.1f,
					session.getImagingMoveOptions());
				break;
			case CAMERA_FOCUS_FAR:
				out = new OnvifImagingFocusMoveProperty(session,
					0.1f,
					session.getImagingMoveOptions());
				break;
			case CAMERA_FOCUS_STOP:
				out = new OnvifImagingFocusMoveProperty(session,
					0f,
					session.getImagingMoveOptions());
				break;
			case CAMERA_FOCUS_MANUAL:
				out = new OnvifImagingFocusAutoProperty(session,
					false,
					session.getImagingSettings(),
					session.getImagingOptions());
				break;
			case CAMERA_FOCUS_AUTO:
				out = new OnvifImagingFocusAutoProperty(session,
					true,
					session.getImagingSettings(),
					session.getImagingOptions());
				break;
			case CAMERA_IRIS_CLOSE:
			case CAMERA_IRIS_OPEN:
				out = new OnvifImagingIrisMoveProperty(session,
					request,
					session.getImagingSettings(),
					session.getImagingOptions());
				break;
			case CAMERA_IRIS_MANUAL:
				out = new OnvifImagingIrisAutoProperty(session,
					false,
					session.getImagingSettings(),
					session.getImagingOptions());
				break;
			case CAMERA_IRIS_AUTO:
				out = new OnvifImagingIrisAutoProperty(session,
					true,
					session.getImagingSettings(),
					session.getImagingOptions());
				break;
			case CAMERA_IRIS_STOP:
				// ignored
				break;
			default:
				throw new IOException(
					"Unrecognized: " + request);
			}
			return out;
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
