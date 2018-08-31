package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FloatRange;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotStartedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapTransmissionException;

import java.io.IOException;

/**
 * A class that makes continuous iris movements from the absolute iris movement
 * interface provided by ONVIF.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingIrisMoveProperty extends OnvifProperty {
	/**
	 * The granularity of individual requests. A larger number means the
	 * stepping of iris movements will be more smooth. A smaller number
	 * will
	 * mean larger jumps and a stepped feel to iris movements. Since we
	 * must
	 * receive a response from the device before we can proceed to the next
	 * request, this does not represent a linear relationship. A larger
	 * number will mean more requests which are effectively slower.
	 */
	private static final float GRANULARITY_OF_MOVEMENT = 25;
	private final DeviceRequest req;

	public OnvifImagingIrisMoveProperty(
		OnvifSessionMessenger session, DeviceRequest r)
	{
		super(session);
		this.req = r;
	}

	@Override
	protected void encodeStore() throws IOException {
		if (!supportsIrisMove())
			throw new OperationNotSupportedException("IrisMove");
		switch (req) {
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
			doneMsg = "IrisMoving";
			stepIris();
			break;
		case CAMERA_IRIS_STOP:
			log(req + " ignored. " +
				"ONVIF only supports absolute iris movements." +
				" ");
			break;
		default:
			throw new IllegalArgumentException(
				"Unexpected: " + req);
		}
	}

	/**
	 * Some camera manufacturers have applied ceiling rounding in the case
	 * of negative attenuation values, so we must anticipate this and
	 * ensure
	 * that we have the correct value in our session cache after the device
	 * response is retrieved.
	 */
	@Override
	protected void decodeStore() throws IOException {
		if (!(response instanceof SetImagingSettingsResponse))
			throw new IOException("Unexpected response to Iris" +
				"Move request. ");
		GetImagingSettings getImagingSettings =
			new GetImagingSettings();
		getImagingSettings.setVideoSourceToken(
			session.getMediaProfileTok());
		GetImagingSettingsResponse getImagingSettingsResponse
			= (GetImagingSettingsResponse)
			session.makeRequest(getImagingSettings,
				GetImagingSettingsResponse.class);
		session.getImagingSettings().getExposure().setIris(
			getImagingSettingsResponse.getImagingSettings()
				.getExposure().getIris());
	}

	private boolean supportsIrisMove()
		throws SoapTransmissionException, ServiceNotSupportedException,
		SessionNotStartedException
	{
		boolean supported = true;
		if (session.getImagingOptions() == null
			|| session.getImagingOptions().getExposure() == null
			|| session.getImagingOptions().getExposure()
			.getIris() == null
			// ONVIF states that min == max is indicative of
			// unsupported iris move
			|| session.getImagingOptions().getExposure().getIris()
			.getMin()
			!= session.getImagingOptions().getExposure().getIris()
			.getMax()
			|| session.getImagingSettings() == null
			|| session.getImagingSettings().getExposure() == null
			|| session.getImagingSettings().getExposure()
			.getIris() == null)
			supported = false;
		return supported;
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
	private void stepIris()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		FloatRange range = session.getImagingOptions()
			.getExposure().getIris();
		final float min = range.getMin();
		final float max = range.getMax();
		final float incr = (max - min) / GRANULARITY_OF_MOVEMENT;
		float val =
			session.getImagingSettings().getExposure().getIris();
		val += (req == DeviceRequest.CAMERA_IRIS_OPEN ? 1 : -1) * incr;
		if (max == 0f)
			negativeAttenuation(val, min, max);
		else if (min == 0f)
			absValAttenuation(val, min, max);
	}


	private void negativeAttenuation(float val, float min, float max)
		throws SoapTransmissionException, ServiceNotSupportedException,
		SessionNotStartedException
	{
		if (req == DeviceRequest.CAMERA_IRIS_OPEN && val > max)
			updateVal(max);
		else if (req == DeviceRequest.CAMERA_IRIS_CLOSE && val < min)
			updateVal(min);
		else
			updateVal(val);
	}

	private void absValAttenuation(float val, float min, float max)
		throws SoapTransmissionException, ServiceNotSupportedException,
		SessionNotStartedException
	{
		if (req == DeviceRequest.CAMERA_IRIS_OPEN && val < min)
			updateVal(min);
		else if (req == DeviceRequest.CAMERA_IRIS_CLOSE && val > max)
			updateVal(max);
		else
			updateVal(val);
	}

	private void updateVal(float val)
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		session.getImagingSettings().getExposure().setIris(val);
		SetImagingSettings setReq = new SetImagingSettings();
		setReq.setVideoSourceToken(session.getMediaProfileTok());
		setReq.setImagingSettings(session.getImagingSettings());
		response = session.makeRequest(setReq,
			SetImagingSettingsResponse.class);
	}
}
