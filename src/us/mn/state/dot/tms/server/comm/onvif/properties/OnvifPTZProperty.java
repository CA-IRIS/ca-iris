package us.mn.state.dot.tms.server.comm.onvif.properties;


import org.onvif.ver10.schema.PTZSpaces;
import org.onvif.ver10.schema.PTZSpeed;
import org.onvif.ver10.schema.Vector1D;
import org.onvif.ver10.schema.Vector2D;
import org.onvif.ver20.ptz.wsdl.ContinuousMove;
import org.onvif.ver20.ptz.wsdl.ContinuousMoveResponse;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.session.SoapWrapper;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class OnvifPTZProperty extends OnvifProperty {

	private Float pan;
	private Float tilt;
	private Float zoom;

	public OnvifPTZProperty(
		CameraImpl c, float p, float t, float z,
		OnvifSessionMessenger session)
	{
		super(session);
		pan = p;
		tilt = t;
		zoom = z;
	}

	private void checkInputs(Float pan, Float tilt, Float zoom)
		throws IOException
	{
		if (pan == null || tilt == null || zoom == null) {
			OnvifPoller
				.log(this.getClass() + " recevied null input");
			throw new IOException(
				"cannot perform ptz move request");
		}

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
		try {
			SoapWrapper soapRequest =
				new SoapWrapper(continuousMove);
			String ptzUri =
				session.getCapabilities().getPTZ().getXAddr();
			response = soapRequest
				.callSoapWebService(ptzUri,
					ContinuousMoveResponse.class,
					session.getAuth());
		} catch (Exception e) {
			OnvifPoller.log(e);
			throw new IOException(
				"Unable to send continous PTZ move request. ");
		}
	}

	@Override
	protected void encodeStore() throws IOException {
		checkInputs(pan, tilt, zoom);
		continuousMove(initPTZSpeed());
	}

	@Override
	protected void decodeStore() throws IOException {
		try {
			ContinuousMoveResponse continuousMoveResponse =
				(ContinuousMoveResponse) response;
		} catch (Exception e) {
			OnvifPoller.log(e.getClass().toString());
			// todo parse errors
		}
	}
}
