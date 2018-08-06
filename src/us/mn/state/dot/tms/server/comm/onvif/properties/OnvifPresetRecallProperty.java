package us.mn.state.dot.tms.server.comm.onvif.properties;

import org.onvif.ver20.ptz.wsdl.GotoPreset;
import org.onvif.ver20.ptz.wsdl.GotoPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPresetRecallProperty extends OnvifProperty {
	private Integer preset;

	public OnvifPresetRecallProperty(
		OnvifSessionMessenger session, int num)
	{
		super(session);
		preset = num;
	}

	@Override
	protected void encodeStore() throws IOException {
		String token;
		try {
			token = findPresetToken(preset, session.getPresets());
		} catch (Exception e) {
			OnvifPoller.log(e.getMessage());
			throw new IOException("Could not retrieve presets");
		}
		if (token == null)
			throw new IOException("Could not find preset");
		goToPreset(token);
	}

	/**
	 * @param token the token for ONVIF purposes
	 */
	private void goToPreset(String token) throws IOException {
		GotoPreset gotoPreset = new GotoPreset();
		gotoPreset.setProfileToken(session.getDefaultProfileTok());
		gotoPreset.setPresetToken(token);
		try {
			SoapWrapper soap = new SoapWrapper(gotoPreset);
			response = soap.callSoapWebService(session.getUri(),
				GotoPresetResponse.class, session.getAuth());
		} catch (Exception e) {
			throw new IOException("failed to go to preset " +
				"position",
				e);
		}

	}
}
