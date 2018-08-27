package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ExposureMode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotStartedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapTransmissionException;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingIrisAutoProperty extends OnvifProperty {
	private final boolean enable;

	public OnvifImagingIrisAutoProperty(
		OnvifSessionMessenger session, boolean enable)
	{
		super(session);
		this.enable = enable;
	}

	@Override
	protected void encodeStore() throws IOException {
		if (!supportsAutoIris())
			logFailure("Camera does not support " + (enable ?
				"auto" : "manual") + "requests. ");
		setAutoIrisMode();
	}

	private boolean supportsAutoIris()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException
	{
		boolean supported = true;
		ImagingOptions20 options = session.getImagingOptions();
		if (options == null
			|| options.getExposure() == null
			|| options.getExposure().getMode() == null)
			supported = false;
		return supported;
	}

	private void setAutoIrisMode()
		throws SessionNotStartedException, SoapTransmissionException
	{
		ImagingSettings20 settings = session.getImagingSettings();
		settings.getExposure().setMode(enable ?
			ExposureMode.AUTO : ExposureMode.MANUAL);
		SetImagingSettings request = new SetImagingSettings();
		request.setVideoSourceToken(session.getMediaProfileTok());
		request.setImagingSettings(settings);
		response = session.makeRequest(request,
			SetImagingSettingsResponse.class);
	}
}
