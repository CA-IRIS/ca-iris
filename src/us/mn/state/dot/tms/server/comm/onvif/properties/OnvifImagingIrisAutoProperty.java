package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ExposureMode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.SetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingIrisAutoProperty extends OnvifProperty {
	private final boolean enable;
	private ImagingSettings20 settings;
	private ImagingOptions20 options;

	public OnvifImagingIrisAutoProperty(
		OnvifSessionMessenger session, boolean enable,
		ImagingSettings20 settings,
		ImagingOptions20 options)
	{
		super(session);
		this.enable = enable;
		this.settings = settings;
		this.options = options;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		if (!supportsAutoIris())
			throw new OperationNotSupportedException(
				(enable ? "Auto" : "Manual") + "Iris");
		setAutoIrisMode();
	}

	private boolean supportsAutoIris() {
		return options != null
			&& options.getExposure() != null
			&& options.getExposure().getMode() != null
			&& options.getExposure().getMode().contains(enable ?
			ExposureMode.AUTO : ExposureMode.MANUAL);
	}

	private void setAutoIrisMode() throws IOException {
		settings.getExposure().setMode(enable ?
			ExposureMode.AUTO : ExposureMode.MANUAL);
		SetImagingSettings request = new SetImagingSettings();
		request.setVideoSourceToken(session.getVideoSoureTok());
		request.setImagingSettings(settings);
		response = session.makeRequest(request,
			SetImagingSettingsResponse.class);
	}
}
