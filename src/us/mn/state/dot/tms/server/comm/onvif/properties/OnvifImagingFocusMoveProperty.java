package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.ContinuousFocus;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FocusMove;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.Move;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl.MoveResponse;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifImagingFocusMoveProperty extends OnvifImagingProperty {
	private float speed;
	private static float NEAR_MIN = -1;
	private static float FAR_MAX = 1;


	public OnvifImagingFocusMoveProperty(
		OnvifSessionMessenger session,
		float speed,
		boolean near)
	{
		super(session);
		this.speed = speed * (near ? NEAR_MIN : FAR_MAX);
	}

	@Override
	protected void encodeStore() throws IOException {
		if (!supportsFocusMove())
			logFailure("Device does not support Focus Move");
		float newMin = session.getImagingMoveOptions().getContinuous().getSpeed().getMin();
		float newMax = session.getImagingMoveOptions().getContinuous().getSpeed().getMin();
		speed = resize(speed, NEAR_MIN, FAR_MAX, newMin, newMax);
		continuousFocus();
	}

	private boolean supportsFocusMove() throws IOException {
		return session.getImagingMoveOptions().getContinuous() != null
			&& session.getImagingMoveOptions().getContinuous().getSpeed() != null;
	}

	private void continuousFocus() throws IOException {
		Move request = new Move();
		request.setVideoSourceToken(session.getDefaultProfileTok());
		FocusMove focusMove = new FocusMove();
		ContinuousFocus val = new ContinuousFocus();
		val.setSpeed(speed);
		focusMove.setContinuous(val);
		request.setFocus(focusMove);
		response = session.call(OnvifService.IMAGING, request,
			MoveResponse.class);
	}
}
