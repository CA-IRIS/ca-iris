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

	@Override
	protected void encodeStore() throws IOException {
		resizeInputs();
		continuousMove(initPTZSpeed());
	}

	/**
	 * we expect pan, tilt, and zoom, to be in the range of -1 to 1 from
	 * the
	 * iris client, but we cannot say for certain the range for our device
	 * (though 100% of the devices we have seen so far map from -1 to 1
	 * also), so this is just for safety
	 */
	private void resizeInputs() throws IOException
	{
		float sourceMin = -1;
		float sourceMax = 1;
		float targetMin = session.getPtzSpaces()
			.getContinuousPanTiltVelocitySpace().get(0).getXRange()
			.getMin();
		float targetMax = session.getPtzSpaces()
			.getContinuousPanTiltVelocitySpace().get(0).getXRange()
			.getMax();
		pan = resize(pan, sourceMin, sourceMax, targetMin, targetMax);
		targetMin = session.getPtzSpaces()
			.getContinuousPanTiltVelocitySpace().get(0).getYRange()
			.getMin();
		targetMax = session.getPtzSpaces()
			.getContinuousPanTiltVelocitySpace().get(0).getYRange()
			.getMax();
		tilt = resize(tilt, sourceMin, sourceMax, targetMin,
			targetMax);
		targetMin =
			session.getPtzSpaces().getContinuousZoomVelocitySpace()
				.get(0).getXRange().getMin();
		targetMax =
			session.getPtzSpaces().getContinuousZoomVelocitySpace()
				.get(0).getXRange().getMax();
		zoom = resize(zoom, sourceMin, sourceMax, targetMin,
			targetMax);
	}

	/**
	 * @return a float remapped to a new range
	 * @throws AssertionError if oldMin == oldMax
	 */
	private float resize(
		float val, float oldMin, float oldMax, float newMin,
		float newMax) throws AssertionError
	{
		assert oldMin != oldMax; // avoid division by zero
		float oldRange = oldMax - oldMin;
		float newRange = newMax - newMin;
		return (val - oldMin) / oldRange * newRange + newMin;
	}

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
}
