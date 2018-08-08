package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingIrisProperty extends OnvifImagingProperty {

	/** a percentage jump towards the close or open limit */
	private float value;
	private boolean open;
	private static float CLOSE_LIMIT = 100f;
	private static float OPEN_LIMIT = 0f;

	/**
	 * @param adjustment a percentage value to adjust to fully open or closed
	 */
	public OnvifImagingIrisProperty(
		OnvifSessionMessenger session, float adjustment, boolean open)
	{
		super(session);
		this.value = value;
		this.open = open;
	}

	@Override
	protected void encodeStore() throws IOException {
		if (!supportsIris()) {
			logFailure("Device does not support Iris operations");
		}
		float newMax = session.getImagingOptions().getExposure().getMaxIris();
		float newMin = session.getImagingOptions().getExposure().getMinIris();
		float adjustnment = resize(value, OPEN_LIMIT, CLOSE_LIMIT, newMin, newMax);
		setIris(adjustnment);
	}

	@Override
	protected void decodeStore() throws IOException {
		log(response.toString() + " command sent");
	}

	private void setIris(float adjustment) throws IOException {
		ImagingSettings20 settings = getImagingSettings();
		settings.getExposure().setIris(adjustment);
		SetImagingSettings request = new SetImagingSettings();
		request.setVideoSourceToken(session.getDefaultProfileTok());
		request.setImagingSettings(settings);
		response = session.call(OnvifService.IMAGING, request,
			SetImagingSettingsResponse.class);
	}

	private boolean supportsIris() throws IOException {
		return session.getImagingOptions().getExposure() != null
			&& session.getImagingOptions().getExposure().getIris() != null;
	}
}
