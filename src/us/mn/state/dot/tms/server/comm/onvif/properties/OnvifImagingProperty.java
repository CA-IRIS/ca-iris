package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ImagingSettings20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettings;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.GetImagingSettingsResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public abstract class OnvifImagingProperty extends OnvifProperty {
	OnvifImagingProperty(OnvifSessionMessenger session) {
		super(session);
	}

	ImagingSettings20 getImagingSettings() throws IOException {
		GetImagingSettings request =
			new GetImagingSettings();
		request.setVideoSourceToken(session.getDefaultProfileTok());
		return ((GetImagingSettingsResponse) session
			.call(OnvifService.IMAGING, request,
				GetImagingSettingsResponse.class))
			.getImagingSettings();
	}
}
