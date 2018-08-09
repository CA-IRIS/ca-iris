package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SetPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SetPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import java.io.IOException;
import java.util.List;

/**
 * All iris presets on the device will have names that begin with IRIS Preset
 * operation fails if the device is moving during the preset
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZPresetStoreProperty extends OnvifPTZPresetProperty {
	private Integer preset;
	private String presetToken;

	public OnvifPTZPresetStoreProperty(
		OnvifSessionMessenger session, int num)
	{
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
			List<PTZPreset> presets = null;
			try {
				presets = getPresets();
			} catch (Exception e) {
				logFailure("Could not retrieve current presets");
			}
			presetToken = findPresetToken(preset, presets);
			try {
				if (presetToken != null)
					setPreset(preset, presetToken);
				else if (hasRoomForAnotherPreset())
					setPreset(preset, null);
				else throw new IOException(
						"Device does not have room for" +
							" more presets");
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
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
			log("Tried to set overwrite existing " +
				"preset," +
				" " +
				"but response preset token did not " +
				"match found preset token");
			throw new IOException(
				"Unexpected response to store preset request");
		}
		log("Preset overwritten: " + preset + ", token: " + presetToken);

	}

	private boolean hasRoomForAnotherPreset()
		throws IOException, ServiceNotSupportedException
	{
		return getPresets().size()
			< session.getNodes().get(0).getMaximumNumberOfPresets();
	}

	/**
	 * @param preset the preset number
	 * @param presetToken if null, then a new preset will be created
	 */
	private void setPreset(Integer preset, String presetToken)
		throws IOException, ServiceNotSupportedException
	{
		SetPreset setPreset = new SetPreset();
		setPreset.setProfileToken(session.getMediaProfileTok());
		setPreset.setPresetName("IRIS" + preset);
		if (presetToken != null)
			setPreset.setPresetToken(presetToken);
		response = session.makeRequest(setPreset,
			SetPresetResponse.class);
	}
}
