package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SendAuxiliaryCommand;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SendAuxiliaryCommandResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifWiperProperty extends OnvifProperty {
	/** true if we should swith the wiper on, else false */
	private boolean switchOn;
	/**
	 * based on the onvif spec, these are the only foreseeable free form
	 * versions of these auxiliary commands
	 */
	private static String WIPER_OFF[] = {"tt:Wiper|Off", "wiperon"};
	private static String WIPER_ON[] = {"tt:Wiper|On", "wiperoff"};

	public OnvifWiperProperty(
		OnvifSessionMessenger session, boolean switchOn)
	{
		super(session);
		this.switchOn = switchOn;
	}

	@Override
	protected void encodeStore() throws IOException {
		SendAuxiliaryCommand cmd;
		if (switchOn) {
			cmd = initCmd(matchAny(
				(String[]) session.getNodes().get(0)
					.getAuxiliaryCommands().toArray(),
				WIPER_ON));
		} else {
			cmd = initCmd(matchAny(
				(String[]) session.getNodes().get(0)
					.getAuxiliaryCommands().toArray(),
				WIPER_OFF));
		}
		if (cmd != null)
			response = session.call(OnvifService.PTZ, cmd,
				SendAuxiliaryCommandResponse.class);
		else
			OnvifPoller.log("Wiper command not supported.");
	}

	/**
	 * @return a initialized AuxiliaryCommand if the param is not null
	 */
	private SendAuxiliaryCommand initCmd(String param) {
		SendAuxiliaryCommand cmd = null;
		if (param != null) {
			cmd = new SendAuxiliaryCommand();
			cmd.setProfileToken(session.getDefaultProfileTok());
			cmd.setAuxiliaryData(param);
		}
		return cmd;
	}

	/**
	 * @return the last String in reference that matches any String in
	 * 	findAny (null if none found)
	 */
	private String matchAny(String[] reference, String[] findAny) {
		String match = null;
		if (reference != null && findAny != null) {
			for (String s : reference) {
				for (String find : findAny) {
					if (s.equalsIgnoreCase(find))
						match = s;
				}
			}
		}
		return match;
	}
}
