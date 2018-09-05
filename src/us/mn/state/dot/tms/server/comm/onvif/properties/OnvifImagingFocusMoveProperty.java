package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ContinuousFocus;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FocusMove;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.MoveOptions20;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.Move;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.MoveResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingFocusMoveProperty extends OnvifProperty {
	private float speed;
	private static final float NEAR_MIN = -1;
	private static final float FAR_MAX = 1;
	private MoveOptions20 imagingMoveOptions;

	/**
	 * @param speed negative value focuses near and positive value focuses
	 * 	far (expected range is from NEAR_MIN to FAR_MAX).
	 */
	public OnvifImagingFocusMoveProperty(
		OnvifSessionMessenger session, float speed,
		MoveOptions20 moveOptions)
	{
		super(session);
		this.speed = speed;
		this.imagingMoveOptions = moveOptions;
	}

	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		if (!supportsFocusMove())
			throw new OperationNotSupportedException("FocusMove");
		float newMin = imagingMoveOptions.getContinuous()
			.getSpeed().getMin();
		float newMax = imagingMoveOptions.getContinuous()
			.getSpeed().getMax();
		continuousFocus(
			resize(speed, NEAR_MIN, FAR_MAX, newMin, newMax));
		if (speed != 0)
			doneMsg = "FocusMoving";
	}

	private boolean supportsFocusMove() throws IOException {
		return imagingMoveOptions.getContinuous() != null
			&& imagingMoveOptions.getContinuous()
			.getSpeed() != null;
	}

	private void continuousFocus(float speed) throws IOException {
		Move request = new Move();
		request.setVideoSourceToken(session.getMediaProfileTok());
		ContinuousFocus val = new ContinuousFocus();
		FocusMove focusMove = new FocusMove();
		val.setSpeed(speed);
		focusMove.setContinuous(val);
		request.setFocus(focusMove);
		response = session.makeRequest(request, MoveResponse.class);
	}
}
