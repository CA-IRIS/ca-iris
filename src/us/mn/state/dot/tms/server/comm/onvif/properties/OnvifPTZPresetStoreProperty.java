package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZNode;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SetPreset;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SetPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationFailedException;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * All iris presets on the device will have names that begin with "IRIS". Preset
 * operations fail if the device is moving during the preset.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZPresetStoreProperty extends OnvifPTZPresetProperty {
	private String presetToken;

	public OnvifPTZPresetStoreProperty(
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
				"StorePreset");
		else {
			List<PTZPreset> presets;
			presets = getPresets();
			presetToken = findPresetToken(preset, presets);
			if (presetToken != null)
				setPreset(preset, presetToken);
			else if (hasRoomForAnotherPreset())
				setPreset(preset, null);
			else {
				log("Device does not have room " +
					"for more presets. Try " +
					"using the device's " +
					"web app (if " +
					"available) to delete " +
					"non-IRIS presets, or " +
					"overwrite a lower-" +
					"numbered preset. ");
				throw new OperationFailedException(
					"NoMoreRoomForPresets");
			}
		}
	}

	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		// null check then:
		// if we are overwriting, we should get back the same token
		// if we are creating, we should get any new token back
		SetPresetResponse casted = (SetPresetResponse) response;
		if (casted.getPresetToken() == null
			|| (presetToken != null
			&& !casted.getPresetToken().equals(presetToken)))
		{
			log("Tried to overwrite existing " +
				"preset, but response preset token did not " +
				"match found preset token. ");
			throw new IOException(
				"Unexpected response to store preset request" +
					". ");
		}
		log("Set preset for number: " + preset);

	}

	private boolean hasRoomForAnotherPreset() throws IOException {
		return getPresets().size()
			< nodes.get(0).getMaximumNumberOfPresets();
	}

	/**
	 * @param preset the preset number
	 * @param presetToken if null, then a new preset will be created
	 */
	private void setPreset(Integer preset, String presetToken)
		throws IOException
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
