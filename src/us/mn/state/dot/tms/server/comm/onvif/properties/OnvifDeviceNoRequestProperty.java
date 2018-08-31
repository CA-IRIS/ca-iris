package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifDeviceNoRequestProperty extends OnvifProperty
{
	public OnvifDeviceNoRequestProperty(OnvifSessionMessenger session) {
		super(session);
	}

	@Override
	protected void encodeStore() throws IOException {
		// ensure device is ready for requests
		session.getMediaProfileTok();
	}
}
