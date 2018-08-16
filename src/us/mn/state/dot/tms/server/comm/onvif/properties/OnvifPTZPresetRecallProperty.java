package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GotoPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GotoPresetResponse;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZPresetRecallProperty extends OnvifPTZPresetProperty {
	public OnvifPTZPresetRecallProperty(
		OnvifSessionMessenger session, int num)
	{
		super(session, num);
	}

	@Override
	protected void encodeStore() throws IOException {
		if (!supportsPresets())
			logFailure("Presets not supported. ");
		else {
			String token;
			token = findPresetToken(preset, getPresets());
			if (token == null)
				throw new IOException(
					"Could not find preset. ");
			goToPreset(token);
		}
	}

	@Override
	public void decodeStore() throws IOException {
		super.decodeStore();
		GotoPresetResponse gotoPresetResponse = (GotoPresetResponse) response;
		log(gotoPresetResponse.getClass().getSimpleName() + ": Went to preset: " + preset);

	}

	/**
	 * @param token the token for ONVIF purposes
	 */
	private void goToPreset(String token) throws IOException {
		GotoPreset gotoPreset = new GotoPreset();
		gotoPreset.setProfileToken(session.getMediaProfileTok());
		gotoPreset.setPresetToken(token);
		response = session.makeRequest(gotoPreset,
			GotoPresetResponse.class);
	}
}
