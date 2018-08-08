package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetPresets;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetPresetsResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;
import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public abstract class OnvifPTZPresetProperty extends OnvifProperty {
	OnvifPTZPresetProperty(
		OnvifSessionMessenger session)
	{
		super(session);
	}

	/**
	 * @param preset the IRIS present number to find
	 * @param presets the presets to look in
	 * @return null if not found else contains the presetToken
	 */
	String findPresetToken(
		Integer preset, List<PTZPreset> presets)
	{
		for (PTZPreset p : presets) {
			if (p.getName().equals("IRIS" + preset))
				return p.getToken();
		}
		return null;
	}

	protected List<PTZPreset> getPresets() throws IOException {
		GetPresets getPresets = new GetPresets();
		getPresets.setProfileToken(session.getDefaultProfileTok());
		return ((GetPresetsResponse) session
			.call(OnvifService.PTZ, getPresets, GetPresetsResponse.class))
			.getPreset();
	}

	/**
	 *
	 * @return any device that has at least 1 preset must support the preset commands
	 */
	boolean supportsPresets() throws IOException {
		return session.getNodes().get(0).getMaximumNumberOfPresets() > 0;
	}
}
