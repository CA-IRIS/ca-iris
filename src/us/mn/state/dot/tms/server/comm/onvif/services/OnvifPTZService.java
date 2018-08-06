package us.mn.state.dot.tms.server.comm.onvif.services;

import org.onvif.ver10.schema.*;
import org.onvif.ver20.ptz.wsdl.Capabilities;
import org.onvif.ver20.ptz.wsdl.PTZ;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import javax.xml.datatype.Duration;
import javax.xml.ws.Holder;
import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZService implements PTZ {


	@Override
	public void setPreset(
		String profileToken, String presetName,
		Holder<String> presetToken)
	{

	}

	@Override
	public void continuousMove(
		String profileToken, PTZSpeed velocity, Duration timeout)
	{
	}

	@Override
	public void geoMove(
		String profileToken, GeoLocation target, PTZSpeed speed,
		Float areaHeight, Float areaWidth)
	{

	}

	@Override
	public PTZStatus getStatus(String profileToken) {
		return null;
	}

	@Override
	public void operatePresetTour(
		String profileToken, String presetTourToken,
		PTZPresetTourOperation operation)
	{

	}

	@Override
	public void setHomePosition(String profileToken) {

	}

	@Override
	public String createPresetTour(String profileToken) {
		return null;
	}

	@Override
	public void modifyPresetTour(
		String profileToken, PresetTour presetTour)
	{

	}

	@Override
	public void stop(String profileToken, Boolean panTilt, Boolean zoom) {

	}

	@Override
	public List<PTZConfiguration> getConfigurations() {
		return null;
	}

	@Override
	public List<PresetTour> getPresetTours(String profileToken) {
		return null;
	}

	@Override
	public void removePreset(String profileToken, String presetToken) {

	}

	@Override
	public PresetTour getPresetTour(
		String profileToken, String presetTourToken)
	{
		return null;
	}

	@Override
	public void relativeMove(
		String profileToken, PTZVector translation, PTZSpeed speed)
	{

	}

	@Override
	public void gotoHomePosition(
		String profileToken, PTZSpeed speed)
	{

	}

	@Override
	public Capabilities getServiceCapabilities() {
		return null;
	}

	@Override
	public PTZConfiguration getConfiguration(String ptzConfigurationToken) {
		return null;
	}

	@Override
	public String sendAuxiliaryCommand(
		String profileToken, String auxiliaryData)
	{
		return null;
	}

	@Override
	public void setConfiguration(
		PTZConfiguration ptzConfiguration, boolean forcePersistence)
	{

	}

	@Override
	public List<PTZPreset> getPresets(String profileToken) {
		return null;
	}

	@Override
	public PTZNode getNode(String nodeToken) {
		return null;
	}

	@Override
	public void gotoPreset(
		String profileToken, String presetToken, PTZSpeed speed)
	{

	}

	@Override
	public void removePresetTour(
		String profileToken, String presetTourToken)
	{

	}

	@Override
	public List<PTZNode> getNodes() {
		return null;
	}

	@Override
	public PTZPresetTourOptions getPresetTourOptions(
		String profileToken, String presetTourToken)
	{
		return null;
	}

	@Override
	public void absoluteMove(
		String profileToken, PTZVector position, PTZSpeed speed)
	{

	}

	@Override
	public List<PTZConfiguration> getCompatibleConfigurations(
		String profileToken)
	{
		return null;
	}

	@Override
	public PTZConfigurationOptions getConfigurationOptions(
		String configurationToken)
	{
		return null;
	}
}
