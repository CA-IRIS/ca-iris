package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZNode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GotoPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GotoPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationFailedException;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZPresetRecallProperty extends OnvifPTZPresetProperty {
	public OnvifPTZPresetRecallProperty(
		OnvifSessionMessenger session, int num, List<PTZNode> nodes)
	{
		super(session, num, nodes);
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		if (!supportsPresets())
			throw new OperationNotSupportedException(
				"RecallPreset");
		else {
			String token;
			token = findPresetToken(preset, getPresets());
			if (token == null)
				throw new OperationFailedException(
					"PresetNotSet");
			goToPreset(token);
		}
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
