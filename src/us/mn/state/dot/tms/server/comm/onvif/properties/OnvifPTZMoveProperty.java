package us.mn.state.dot.tms.server.comm.onvif.properties;


import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZConfigurationOptions;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZSpaces;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.PTZSpeed;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Vector1D;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.Vector2D;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.ContinuousMove;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.ContinuousMoveResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifPTZMoveProperty extends OnvifProperty {

	private Float pan;
	private Float tilt;
	private Float zoom;
	private final PTZConfigurationOptions options;

	public OnvifPTZMoveProperty(
		float p, float t, float z,
		OnvifSessionMessenger session,
		PTZConfigurationOptions options)
	{
		super(session);
		pan = p;
		tilt = t;
		zoom = z;
		this.options = options;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		if (!supportsContinuousPTZMove())
			throw new OperationNotSupportedException(
				"ContinuousPTZMove");
		resizeInputs();
		continuousMove(createPTZSpeed());
		if (pan != 0 || tilt != 0 || zoom != 0)
			doneMsg = "PTZMoving";
	}

	private boolean supportsContinuousPTZMove() throws IOException {
		return options != null
			&& options.getSpaces() != null
			&& ((pan == 0 && zoom == 0)
				|| options.getSpaces().getContinuousPanTiltVelocitySpace() != null)
			&& ((zoom == 0)
				|| options.getSpaces().getContinuousZoomVelocitySpace() != null);
	}

	/**
	 * we expect pan, tilt, and zoom, to be in the range of -1 to 1 from the
	 * iris client, but we be certain that the range for our device
	 * (though 100% of the devices we have seen so far map from -1 to 1
	 * also), so this is just for safety
	 */
	private void resizeInputs() throws IOException {
		float sourceMin = -1;
		float sourceMax = 1;
		PTZSpaces spaces = options.getSpaces();
		float targetMin = spaces.getContinuousPanTiltVelocitySpace().get(0).getXRange().getMin();
		float targetMax = spaces.getContinuousPanTiltVelocitySpace().get(0).getXRange().getMax();
		pan = resize(pan, sourceMin, sourceMax, targetMin, targetMax);
		targetMin = spaces.getContinuousPanTiltVelocitySpace().get(0).getYRange().getMin();
		targetMax = spaces.getContinuousPanTiltVelocitySpace().get(0).getYRange().getMax();
		tilt = resize(tilt, sourceMin, sourceMax, targetMin, targetMax);
		targetMin = spaces.getContinuousZoomVelocitySpace().get(0).getXRange().getMin();
		targetMax = spaces.getContinuousZoomVelocitySpace().get(0).getXRange().getMax();
		zoom = resize(zoom, sourceMin, sourceMax, targetMin, targetMax);
	}

	private PTZSpeed createPTZSpeed() throws IOException {
		PTZSpeed speed = new PTZSpeed();
		PTZSpaces spaces = options.getSpaces();
		if (pan != 0 || tilt != 0) {
			Vector2D vector2D = new Vector2D();
			vector2D.setX(pan);
			vector2D.setY(tilt);
			vector2D.setSpace(spaces.getContinuousPanTiltVelocitySpace().get(0).getURI());
			speed.setPanTilt(vector2D);
		}
		if (zoom != 0) {
			Vector1D vector1D = new Vector1D();
			vector1D.setSpace(spaces.getContinuousZoomVelocitySpace().get(0).getURI());
			vector1D.setX(zoom);
			speed.setZoom(vector1D);
		}
		return speed;
	}

	private void continuousMove(PTZSpeed speed) throws IOException {
		ContinuousMove continuousMove = new ContinuousMove();
		continuousMove.setProfileToken(session.getMediaProfileTok());
		continuousMove.setVelocity(speed);
		response = session.makeRequest(continuousMove, ContinuousMoveResponse.class);
	}
}
