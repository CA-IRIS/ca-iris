package us.mn.state.dot.tms.server.comm.onvif.properties;


import org.onvif.ver10.schema.PTZSpaces;
import org.onvif.ver10.schema.PTZSpeed;
import org.onvif.ver10.schema.Vector1D;
import org.onvif.ver10.schema.Vector2D;
import org.onvif.ver20.ptz.wsdl.ContinuousMove;
import org.onvif.ver20.ptz.wsdl.ContinuousMoveResponse;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;

import java.io.IOException;


public class OnvifPTZProperty extends OnvifProperty {

	private Float pan;
	private Float tilt;
	private Float zoom;
	private PTZSpeed speed = new PTZSpeed();
	private ContinuousMove continuousMove = new ContinuousMove();
	private ContinuousMoveResponse continuousMoveResponse =
		new ContinuousMoveResponse();

	public OnvifPTZProperty(
		CameraImpl c, float p, float t, float z,
		OnvifSessionMessenger session)
	{
		super(session);
		// todo validate inputs (e.g. null checks)
		pan = p;
		tilt = t;
		zoom = z;
	}

	private void initPTZSpeed() {
		PTZSpaces spaces = session.getPtzSpaces();
		// todo map map values to onvif range (ie. min and max)
		Vector2D vector2D = new Vector2D();
		vector2D.setX(pan);
		vector2D.setY(tilt);
		vector2D.setSpace(
			spaces.getContinuousPanTiltVelocitySpace().get(0)
				.getURI());
		speed.setPanTilt(vector2D);
		Vector1D vector1D = new Vector1D();
		vector1D.setSpace(spaces.getContinuousZoomVelocitySpace().get(0)
			.getURI());
		vector1D.setX(zoom);
		speed.setZoom(vector1D);
	}

	private void continuousMove() throws Exception {
		continuousMove.setProfileToken(session.getDefaultProfileTok());
		continuousMove.setVelocity(speed);
		SoapWrapper soapWrapper =
			new SoapWrapper(continuousMove, session.getAuth());
		String ptzUri = session.getCapabilities().getPTZ().getXAddr();
		continuousMoveResponse = (ContinuousMoveResponse) soapWrapper
			.callSoapWebService(ptzUri, continuousMoveResponse);
	}

	@Override
	protected void encodeStore() throws IOException {
		try {
			initPTZSpeed();
			continuousMove();
		} catch (Exception e) {
			throw new IOException(
				"Cannot send ptz request to device: "
					+ e.getMessage());
		}
	}
}
