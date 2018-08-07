package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SetPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SetPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;
import java.util.List;

/**
 * All iris presets on the device will have names that begin with IRIS Preset
 * operation fails if the device is moving during the preset
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPresetStoreProperty extends OnvifPresetProperty {
	private Integer preset;
	private String presetToken;

	public OnvifPresetStoreProperty(
		OnvifSessionMessenger session, int num)
	{
		super(session);
		preset = num;
	}

	@Override
	protected void encodeStore() throws IOException {
		List<PTZPreset> presets;
		try {
			presets = getPresets();
		} catch (Exception e) {
			OnvifPoller.log(e.getMessage());
			throw new IOException(
				"Could not retrieve current presets");
		}
		presetToken = findPresetToken(preset, presets);
		try {
			if (presetToken != null)
				setPreset(preset, presetToken);
			else if (hasRoomForAnotherPreset())
				setPreset(preset, null);
			else throw new IOException(
					"Device does not have room for more " +
						"presets");
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private boolean hasRoomForAnotherPreset() throws IOException {
		return getPresets().size()
			< session.getNodes().get(0).getMaximumNumberOfPresets();
	}

	/**
	 * @param preset the preset number
	 * @param presetToken if null, then a new preset will be created
	 */
	private void setPreset(Integer preset, String presetToken)
		throws IOException
	{
		SetPreset setPreset = new SetPreset();
		setPreset.setProfileToken(session.getDefaultProfileTok());
		setPreset.setPresetName("IRIS" + preset);
		if (presetToken != null)
			setPreset.setPresetToken(presetToken);
		response = session.call(OnvifService.PTZ, setPreset,
			SetPresetResponse.class);
	}

	@Override
	public void decodeStore() throws IOException {
		// null check then:
		// if we are overwriting, we should get back the same token
		// if we are creating, we should get any token back
		SetPresetResponse setPresetResponse =
			(SetPresetResponse) response;
		if (setPresetResponse.getPresetToken() == null
			|| (presetToken != null
			&& !setPresetResponse.getPresetToken()
			.equals(presetToken))
			|| setPresetResponse.getPresetToken().isEmpty())
		{
			OnvifPoller
				.log("Tried to set overwrite existing " +
					"preset," +
					" " +
					"but response preset token did not " +
					"match found preset token");
			throw new IOException(
				"Unexpected response to store preset request");
		}
		OnvifPoller
			.log("Preset overwritten: " + preset + ", token: " + presetToken);

	}
}
