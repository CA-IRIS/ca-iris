package us.mn.state.dot.tms.server.comm.onvif.services;

import org.onvif.ver10.schema.*;
import org.onvif.ver20.imaging.wsdl.Capabilities;
import org.onvif.ver20.imaging.wsdl.ImagingPort;
import org.onvif.ver20.imaging.wsdl.ImagingPreset;

import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingService implements ImagingPort {
	@Override
	public ImagingStatus20 getStatus(
		String videoSourceToken)
	{
		return null;
	}

	@Override
	public ImagingOptions20 getOptions(
		String videoSourceToken)
	{
		return null;
	}

	@Override
	public ImagingPreset getCurrentPreset(
		String videoSourceToken)
	{
		return null;
	}

	@Override
	public List<ImagingPreset> getPresets(
		String videoSourceToken)
	{
		return null;
	}

	@Override
	public ImagingSettings20 getImagingSettings(
		String videoSourceToken)
	{
		return null;
	}

	@Override
	public void setCurrentPreset(
		String videoSourceToken, String presetToken)
	{

	}

	@Override
	public void move(
		String videoSourceToken, FocusMove focus)
	{

	}

	@Override
	public void stop(String videoSourceToken) {

	}

	@Override
	public Capabilities getServiceCapabilities() {
		return null;
	}

	@Override
	public void setImagingSettings(
		String videoSourceToken, ImagingSettings20 imagingSettings,
		Boolean forcePersistence)
	{

	}

	@Override
	public MoveOptions20 getMoveOptions(
		String videoSourceToken)
	{
		return null;
	}
}
