package us.mn.state.dot.tms.server.comm.onvif.properties;

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
	private boolean switchOn;

	public OnvifWiperProperty(
		OnvifSessionMessenger session, boolean switchOn)
	{
		super(session);
		this.switchOn = switchOn;
	}

	@Override
	protected void encodeStore() throws IOException {
		boolean supportsOn = false;
		boolean supportsOff = false;
		String OFF = "wiperoff";
		String ON = "wiperon";
		for (String cmd : session.getNodes().get(0)
			.getAuxiliaryCommands()) {
			if (cmd.equals(ON))
				supportsOn = true;
			if (cmd.equals(OFF))
				supportsOff = true;
		}
		SendAuxiliaryCommand cmd = null;
		if (supportsOn && switchOn) {
			cmd = new SendAuxiliaryCommand();
			cmd.setProfileToken(session.getDefaultProfileTok());
			cmd.setAuxiliaryData(ON);
		}
		if (supportsOff && !switchOn) {
			cmd = new SendAuxiliaryCommand();
			cmd.setProfileToken(session.getDefaultProfileTok());
			cmd.setAuxiliaryData(OFF);
		}
		if (cmd != null)
			response = session.call(OnvifService.PTZ, cmd,
				SendAuxiliaryCommandResponse.class);
	}
}
