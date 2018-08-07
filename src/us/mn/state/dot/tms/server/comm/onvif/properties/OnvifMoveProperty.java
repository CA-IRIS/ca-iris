package us.mn.state.dot.tms.server.comm.onvif.properties;


import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZSpaces;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZSpeed;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Vector1D;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Vector2D;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.ContinuousMove;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.ContinuousMoveResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifMoveProperty extends OnvifProperty {

	private Float pan;
	private Float tilt;
	private Float zoom;

	public OnvifMoveProperty(
		float p, float t, float z,
		OnvifSessionMessenger session)
	{
		super(session);
		pan = p;
		tilt = t;
		zoom = z;
	}
//
//	/**
//	 * we expect pan, tilt, and zoom, to be in the range of -1 to 1
//	 * (hence our input range is 2)
//	 * @throws IOException
//	 */
//	private void resizeInputs() throws IOException
//	{
//		if (pan == null || tilt == null || zoom == null) {
//			OnvifPoller
//				.log(this.getClass() + " recevied null input");
//			throw new IOException(
//				"cannot perform ptz move request");
//		}
//		float inputRange = 2, inputMin = -1;
//		if (pan != 0) {
//			float targetMin = session.getPtzSpaces()
// .getContinuousPanTiltVelocitySpace().get(0).getXRange().getMin();
//			float targetMax = session.getPtzSpaces()
// .getContinuousPanTiltVelocitySpace().get(0).getXRange().getMax();
//			float targetRange = targetMax - targetMin;
//			float temp = pan + 1;
//			temp = temp / inputRange;
//
//		}
//	}

	private PTZSpeed initPTZSpeed() {
		PTZSpeed speed = new PTZSpeed();
		PTZSpaces spaces = session.getPtzSpaces();
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
		return speed;
	}

	private void continuousMove(PTZSpeed speed) throws IOException {
		ContinuousMove continuousMove = new ContinuousMove();
		continuousMove.setProfileToken(session.getDefaultProfileTok());
		continuousMove.setVelocity(speed);
		response = session.call(OnvifService.PTZ,
			continuousMove,
			ContinuousMoveResponse.class);
	}

	@Override
	protected void encodeStore() throws IOException {
//		resizeInputs();
		// todo validate inputs
		continuousMove(initPTZSpeed());
	}
}
