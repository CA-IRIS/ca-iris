package us.mn.state.dot.tms.server.comm.onvif.properties;

import org.onvif.ver10.schema.PTZPreset;
import org.onvif.ver20.ptz.wsdl.SetPreset;
import org.onvif.ver20.ptz.wsdl.SetPresetResponse;
import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * All iris presets on the device will have names that begin with IRIS Preset
 * operation fails if the device is moving during the preset
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPresetStoreProperty extends OnvifProperty {
	private Integer preset;

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
			presets = session.getPresets();
		} catch (Exception e) {
			OnvifPoller.log(e.getMessage());
			throw new IOException(
				"Could not retrieve current presets");
		}
		String presetToken = findPresetToken(preset, presets);
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

	private boolean hasRoomForAnotherPreset()
		throws NoSuchAlgorithmException, ParserConfigurationException,
		IOException, SOAPException, JAXBException
	{
		return session.getPresets().size()
			< session.getNodes().get(0).getMaximumNumberOfPresets();
	}

	/**
	 * @param preset the preset number
	 * @param presetToken if null, then a new preset will be created
	 */
	private void setPreset(Integer preset, String presetToken)
		throws JAXBException, NoSuchAlgorithmException, SOAPException,
		IOException, ParserConfigurationException
	{
		SetPreset setPreset = new SetPreset();
		setPreset.setProfileToken(session.getDefaultProfileTok());
		setPreset.setPresetName("IRIS" + preset);
		if (presetToken != null)
			setPreset.setPresetToken(presetToken);
		SoapWrapper soap = new SoapWrapper(setPreset);
		SetPresetResponse setPresetResponse = (SetPresetResponse) soap
			.callSoapWebService(session.getUri(),
				SetPresetResponse.class, session.getAuth());
		// null check then:
		// if we are overwriting, we should get back the same token
		// if we are creating, we should get any token back
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
