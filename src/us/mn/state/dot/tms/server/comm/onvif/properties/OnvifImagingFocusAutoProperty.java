package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.AutoFocusMode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingFocusAutoProperty extends OnvifProperty {
	private boolean enable;
	private ImagingSettings20 settings;

	public OnvifImagingFocusAutoProperty(
		OnvifSessionMessenger session, boolean enable,
		ImagingSettings20 settings)
	{
		super(session);
		this.enable = enable;
		this.settings = settings;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		ImagingSettings20 settings = this.settings;
		if (!supportsAutoFocusMode(settings))
			throw new OperationNotSupportedException(
				(enable ? "Auto" : "Manual") + "Focus");
		setAutoFocusMode(settings);
	}

	private boolean supportsAutoFocusMode(ImagingSettings20 settings) {
		return settings.getFocus() != null && settings.getFocus()
			.getAutoFocusMode() != null;
	}

	private void setAutoFocusMode(ImagingSettings20 currentSettings)
		throws IOException
	{
		SetImagingSettings request = new SetImagingSettings();
		request.setVideoSourceToken(session.getMediaProfileTok());
		currentSettings.getFocus().setAutoFocusMode(
			enable ? AutoFocusMode.AUTO : AutoFocusMode.MANUAL);
		request.setImagingSettings(currentSettings);
		response = session.makeRequest(request,
			SetImagingSettingsResponse.class);
	}
}
