package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifFocusProperty extends OnvifImagingProperty {
	public OnvifFocusProperty(
		OnvifSessionMessenger session)
	{
		super(session);
	}

	@Override
	protected void encodeStore() throws IOException {

	}
}
