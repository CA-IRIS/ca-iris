package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ExposureMode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FloatRange;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A class that makes continuous iris movements from the absolute iris movement
 * interface provided by ONVIF.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingIrisMoveProperty extends OnvifProperty {
	/**
	 * The granularity of individual requests. A larger number means the
	 * stepping of iris movements will be more smooth. A smaller number will
	 * mean larger jumps and a stepped feel to iris movements. Since we must
	 * receive a response from the device before we can proceed to the next
	 * request, this does not represent a linear relationship. A larger
	 * number will mean more requests which are effectively slower.
	 */
	private static final float GRANULARITY_OF_MOVEMENT = 10;
	private final DeviceRequest req;
	private ImagingSettings20 settings;
	private ImagingOptions20 options;

	public OnvifImagingIrisMoveProperty(
		OnvifSessionMessenger session, DeviceRequest r,
		ImagingSettings20 settings,
		ImagingOptions20 options) {
		super(session);
		this.req = r;
		this.settings = settings;
		this.options = options;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		if (!supportsIrisMove())
			throw new OperationNotSupportedException("IrisMove");
		switch (req) {
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
			setVal(getNewVal(options.getExposure().getIris(),
				settings.getExposure().getIris()));
			break;
		default:
			throw new IllegalArgumentException("Unexpected: " + req);
		}
	}

	private boolean supportsIrisMove() {
		return options != null
			&& options.getExposure() != null
			&& options.getExposure().getMode() != null
			&& options.getExposure().getMode().contains(ExposureMode.MANUAL)
			&& options.getExposure().getIris() != null
			&& options.getExposure().getIris().getMin()
				< options.getExposure().getIris().getMax()
			&& settings != null
			&& settings.getExposure() != null
			&& settings.getExposure().getMode() != null
			&& settings.getExposure().getMode() == ExposureMode.MANUAL;
	}

	/**
	 * Note that ONVIF iris commands are specified in decibels of light
	 * attenuation. A 0 dB iris value means that the iris is full opened.
	 * Experience dictates that as the value becomes more negative, the
	 * iris
	 * closes more. However, some manufacturers may encode this as an
	 * absolute value of attenuation (the spec is unclear). Therefore, we
	 * must write the logic for either case.
	 */
	Float getNewVal(FloatRange range, float oldVal) {
		Float rv = null;
		final float min = range.getMin();
		final float max = range.getMax();
		final float incr = (max - min) / GRANULARITY_OF_MOVEMENT;
		if (max == 0f)
			rv = negativeAttenuation(oldVal, incr, min, max);
		else if (min == 0f)
			rv = absValAttenuation(oldVal, incr, min, max);
		return rv;
	}
	
	float negativeAttenuation(float oldVal, float incr,
					    float min, float max)
	{
		float rv;
		float val = oldVal + (req == DeviceRequest.CAMERA_IRIS_OPEN ? 1 : -1) * incr;
		if (req == DeviceRequest.CAMERA_IRIS_OPEN && val > max)
			rv = max;
		else if (req == DeviceRequest.CAMERA_IRIS_CLOSE && val < min)
			rv = min;
		else
			rv = val;
		return rv;
	}

	float absValAttenuation(float oldVal, float incr,
					  float min, float max)
	{
		float rv;
		float val = oldVal +
			(req == DeviceRequest.CAMERA_IRIS_OPEN ? -1 : 1) * incr;
		if (req == DeviceRequest.CAMERA_IRIS_OPEN && val < min)
			rv = min;
		else if (req == DeviceRequest.CAMERA_IRIS_CLOSE && val > max)
			rv = max;
		else
			rv = val;
		return rv;
	}

	private void setVal(Float val) throws IOException {
		if (val != null) {
			settings.getExposure().setIris(val);
			SetImagingSettings setReq = new SetImagingSettings();
			setReq.setVideoSourceToken(session.getVideoSoureTok());
			setReq.setImagingSettings(settings);
			response = session.makeRequest(setReq,
				SetImagingSettingsResponse.class);
		}
	}
}
