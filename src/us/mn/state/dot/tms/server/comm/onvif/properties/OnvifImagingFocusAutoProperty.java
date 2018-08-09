package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.AutoFocusMode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingFocusAutoProperty extends OnvifImagingProperty {
	private boolean enable;

	public OnvifImagingFocusAutoProperty(
		OnvifSessionMessenger session, boolean enable)
	{
		super(session);
		this.enable = enable;
	}

	@Override
	protected void encodeStore()
		throws IOException, ServiceNotSupportedException
	{
		ImagingSettings20 settings = getImagingSettings();
		if (!supportsAutoFocusMode(settings))
			logFailure(
				"Device does not support auto focus mode " +
					"change");
		setAutoFocusMode(settings);
	}

	private boolean supportsAutoFocusMode(ImagingSettings20 settings) {
		return settings.getFocus() != null && settings.getFocus()
			.getAutoFocusMode() != null;
	}

	private void setAutoFocusMode(ImagingSettings20 currentSettings)
		throws IOException, ServiceNotSupportedException
	{
		SetImagingSettings request = new SetImagingSettings();
		request.setVideoSourceToken(session.getMediaProfileTok());
		currentSettings.getFocus().setAutoFocusMode(
			enable ? AutoFocusMode.AUTO : AutoFocusMode.MANUAL);
		request.setImagingSettings(currentSettings);
		response = session.makeRequest(request,
			SetImagingSettingsResponse.class);
	}

	private ImagingSettings20 getImagingSettings()
		throws IOException, ServiceNotSupportedException
	{
		GetImagingSettings request = new GetImagingSettings();
		request.setVideoSourceToken(session.getMediaProfileTok());
		return ((GetImagingSettingsResponse) session.makeRequest(request,
				GetImagingSettingsResponse.class))
			.getImagingSettings();
	}
}
