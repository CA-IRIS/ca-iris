package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.TransientPoller;
import us.mn.state.dot.tms.server.comm.onvif.messenger.OnvifSessionMessenger;

import java.io.IOException;

public class OnvifPoller extends TransientPoller<OnvifProperty>
        implements CameraPoller
{
    static private final DebugLog ONVIF_LOG = new DebugLog("onvif");

    private OnvifSessionMessenger session = null;

    public OnvifPoller(String name, OnvifSessionMessenger m) {
        super(name, m);
        session = m;
        ONVIF_LOG.log("ONVIF device instantiated: " + name);
        CommLink commLink = CommLinkHelper.lookup(name);

        if (commLink == null) {
            ONVIF_LOG.log("Failed to find CommLink.");
            return;
        }

        int to = commLink.getTimeout();
        try {
            m.setTimeout(to);
            ONVIF_LOG.log("Set Messenger timeout to " + to + ".");
        } catch (IOException e) {
            ONVIF_LOG.log("Failed to set Messenger timeout.");
        }
    }

    @Override
    public void sendPTZ(CameraImpl c, float p, float t, float z) {
        addOperation(new OpOnvifPTZ(c, p, t, z, ONVIF_LOG, session));
    }

    @Override
    public void sendStorePreset(CameraImpl c, int preset) {
        ONVIF_LOG.log("store preset not yet implemented");
    }

    @Override
    public void sendRecallPreset(CameraImpl c, int preset) {
        ONVIF_LOG.log("recall preset not yet implemented");
    }

    @Override
    public void sendRequest(CameraImpl c, DeviceRequest r) {
        switch (r) {
            case CAMERA_PTZ_FULL_STOP:
                addOperation(new OpOnvifPTZ(
                        c, 0, 0, 0, ONVIF_LOG, session));
                break;
            case RESET_DEVICE:
                // todo implement
                ONVIF_LOG.log("ONVIF DeviceRequest not implemented: " + r);
                break;
            case CAMERA_FOCUS_STOP:
            case CAMERA_FOCUS_NEAR:
            case CAMERA_FOCUS_FAR:
            case CAMERA_FOCUS_MANUAL:
            case CAMERA_FOCUS_AUTO:
            case CAMERA_IRIS_STOP:
            case CAMERA_IRIS_CLOSE:
            case CAMERA_IRIS_OPEN:
            case CAMERA_IRIS_MANUAL:
            case CAMERA_IRIS_AUTO:
            case CAMERA_WIPER_ONESHOT:
            case QUERY_CONFIGURATION:
            case SEND_SETTINGS:
            case QUERY_MESSAGE:
            case QUERY_STATUS:
            case QUERY_PIXEL_FAILURES:
            case TEST_PIXELS:
            case TEST_FANS:
            case TEST_LAMPS:
            case BRIGHTNESS_GOOD:
            case BRIGHTNESS_TOO_DIM:
            case BRIGHTNESS_TOO_BRIGHT:
            case RESET_MODEM:
            case SEND_LEDSTAR_SETTINGS:
            case QUERY_LEDSTAR_SETTINGS:
            case DISABLE_SYSTEM:
                ONVIF_LOG.log("ONVIF DeviceRequest not supported: " + r);
                break;
            case NO_REQUEST:
                ONVIF_LOG.log("ONVIF DeviceRequest received no request: " + r);
                break;
            default:
                ONVIF_LOG.log("ONVIF DeviceRequest unrecognized: " + r);
                break;
        }
    }

    /**
     * onvif devices don't use drops
     */
    @Override
    public boolean isAddressValid(int drop) {
        ONVIF_LOG.log("ONVIF device was asked to validate drop address but" +
                " does not use drop addresses");
        return true;
    }
}
