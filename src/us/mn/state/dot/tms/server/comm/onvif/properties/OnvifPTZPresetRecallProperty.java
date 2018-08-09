package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GotoPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GotoPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZPresetRecallProperty extends OnvifPTZPresetProperty {
	private Integer preset;

	public OnvifPTZPresetRecallProperty(OnvifSessionMessenger session, int num) {
		super(session);
		preset = num;
	}

	@Override
	protected void encodeStore()
		throws IOException, ServiceNotSupportedException
	{
		if (!supportsPresets())
			logFailure("Presets not supported");
		else {
			String token;
			try {
				token = findPresetToken(preset, getPresets());
			} catch (Exception e) {
				log("Could not retrieve presets");
				throw e;
			}
			if (token == null)
				throw new IOException("Could not find preset");
			goToPreset(token);
		}
	}

	/**
	 * @param token the token for ONVIF purposes
	 */
	private void goToPreset(String token)
		throws IOException, ServiceNotSupportedException
	{
		GotoPreset gotoPreset = new GotoPreset();
		gotoPreset.setProfileToken(session.getMediaProfileTok());
		gotoPreset.setPresetToken(token);
		response = session.makeRequest(gotoPreset,
			GotoPresetResponse.class);
	}
}
