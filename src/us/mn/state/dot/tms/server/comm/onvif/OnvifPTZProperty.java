package us.mn.state.dot.tms.server.comm.onvif;


import org.onvif.ver10.schema.PTZSpaces;
import org.onvif.ver10.schema.PTZVector;
import org.onvif.ver10.schema.Vector1D;
import org.onvif.ver10.schema.Vector2D;
import org.onvif.ver20.ptz.wsdl.RelativeMove;
import org.onvif.ver20.ptz.wsdl.RelativeMoveResponse;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.onvif.messenger.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.messenger.SoapWrapper;

import java.io.IOException;


class OnvifPTZProperty extends OnvifProperty {

    private Float pan;
    private Float tilt;
    private Float zoom;
    private PTZVector translation = new PTZVector();
    private RelativeMove relativeMove = new RelativeMove();
    private RelativeMoveResponse relativeMoveResponse = new RelativeMoveResponse();

    public OnvifPTZProperty(CameraImpl c, float p, float t, float z, DebugLog d,
                            OnvifSessionMessenger session)
    {
        super(d, session);
        // todo validate inputs (e.g. null checks)
        pan = p;
        tilt = t;
        zoom = z;
    }

    private void initPTZVector() {
        PTZSpaces spaces = session.getPtzSpaces();
        // todo map map values to onvif range (ie. min and max)
        Vector2D vector2DStop = new Vector2D();
        vector2DStop.setX(pan);
        vector2DStop.setY(tilt);
        vector2DStop.setSpace(spaces.getRelativePanTiltTranslationSpace().get(0).getURI());
        translation.setPanTilt(vector2DStop);
        Vector1D vector1DStop = new Vector1D();
        vector1DStop.setSpace(spaces.getRelativeZoomTranslationSpace().get(0).getURI());
        vector1DStop.setX(zoom);
        translation.setZoom(vector1DStop);
    }

    private void relativeMove() throws Exception {
        relativeMove.setProfileToken(session.getDefaultProfileTok());
        relativeMove.setTranslation(translation);
        SoapWrapper soapWrapper = new SoapWrapper(relativeMove, session.getAuth());
        String mediaUri = session.getCapabilities().getMedia().getXAddr();
        relativeMoveResponse = (RelativeMoveResponse) soapWrapper.callSoapWebService(mediaUri, relativeMoveResponse);
    }

    @Override
    protected void encodeStore() throws IOException {
        try {
            initPTZVector();
            relativeMove();
        } catch (Exception e) {
            throw new IOException("cannot send ptz request to device: "
                    +  e.getMessage());
        }
    }
}
