package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.messenger.OnvifSession;

abstract public class OpOnvif extends OpDevice<OnvifProperty> {

    static protected DebugLog ONVIF_LOG;

    protected OnvifSession session;

    protected OpOnvif(PriorityLevel p, DeviceImpl d, DebugLog debugLog,
                      OnvifSession session)
    {
        // so far all implemented onvif operations are exclusive
        super(p, d, true);
        ONVIF_LOG = debugLog;
        this.session = session;
        device.setOpStatus("sending cmd");
    }

    protected OpOnvif(PriorityLevel p, CameraImpl d) {
        super(p, d);
    }

    /**
     * Update device op status.
     * We bundle the operation description into the status because camera
     * ops are generally so short that, as far as I can tell, by the time
     * the client gets the SONAR "operation" notification and requests the
     * op's description via SONAR, the device has already been released,
     * and thus Device.getOperation() returns "None".
     */
    protected void updateOpStatus(String stat) {
        String s = getOperationDescription() + ": " + stat;
        device.setOpStatus(s);
    }
}
