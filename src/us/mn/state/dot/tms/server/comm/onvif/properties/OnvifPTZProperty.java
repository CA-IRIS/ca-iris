package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public abstract class OnvifPTZProperty extends OnvifProperty {
	protected OnvifPTZProperty(OnvifSessionMessenger session) {
		super(session, OnvifService.PTZ);
	}
}
