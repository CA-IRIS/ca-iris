package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SendAuxiliaryCommand;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SendAuxiliaryCommandResponse;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZWiperProperty extends OnvifProperty {
	/** true if we should swith the wiper on, else false */
	private boolean switchOn;
	/**
	 * based on the onvif spec, these are the only foreseeable free form
	 * versions of these auxiliary commands
	 */
	private static String WIPER_OFF[] = {"tt:Wiper|Off", "wiperon"};
	private static String WIPER_ON[] = {"tt:Wiper|On", "wiperoff"};

	public OnvifPTZWiperProperty(
		OnvifSessionMessenger session, boolean switchOn)
	{
		super(session);
		this.switchOn = switchOn;
	}

	@Override
	protected void encodeStore() throws IOException {
		// ensure that the device supports wiper auxiliary command
		SendAuxiliaryCommand supportedCmd = findSupportedCmd();
		if (supportedCmd == null)
			logFailure("Wiper command not supported. ");
		else
			doWiper(supportedCmd);
	}

	private void doWiper(SendAuxiliaryCommand cmd) throws IOException {
		// Onvif does not have the idea of a wiper one shot;
		// in fact, it barely has a wiper command at all.
		// This one second delay is the best attempt at a one
		// shot.
		if (!switchOn) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log("Pause between wiper on " +
					"and wiper off was " +
					"interrupted. Wiper " +
					"operation may not " +
					"have completed correctly. ");
			}
		}
		response = session.makeRequest(cmd,
			SendAuxiliaryCommandResponse.class);
	}

	private SendAuxiliaryCommand findSupportedCmd() throws IOException {
		return switchOn ?
			initCmd(matchAny(
				(String[]) session.getNodes().get(0)
					.getAuxiliaryCommands().toArray(),
				WIPER_ON))
			: initCmd(matchAny(
			(String[]) session.getNodes().get(0)
				.getAuxiliaryCommands().toArray(),
			WIPER_OFF));
	}


	/**
	 * @return a initialized AuxiliaryCommand if the param is not null
	 */
	private SendAuxiliaryCommand initCmd(String param) throws IOException {
		SendAuxiliaryCommand cmd = null;
		if (param != null) {
			cmd = new SendAuxiliaryCommand();
			cmd.setProfileToken(session.getMediaProfileTok());
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
