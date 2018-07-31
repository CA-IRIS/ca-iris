package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.messenger.OnvifSessionMessenger;

import java.io.IOException;

public class OpOnvifPTZ extends OpOnvif {

    private OnvifPTZProperty onvifPTZProperty;

    public OpOnvifPTZ(CameraImpl c, float p, float t, float z,
                      DebugLog debugLog, OnvifSessionMessenger session)
    {
        super(PriorityLevel.COMMAND, c, debugLog, session);
        onvifPTZProperty = new OnvifPTZProperty(c, p, t, z, debugLog, session);
    }

    @Override
    protected Phase<OnvifProperty> phaseTwo() {
        return new PhaseTwo();
    }

    protected class PhaseTwo extends Phase<OnvifProperty> {
        protected Phase<OnvifProperty> poll(
                CommMessage<OnvifProperty> mess) throws IOException
        {
            mess.add(onvifPTZProperty);
            mess.storeProps();
            updateOpStatus("ptz cmd sent");
            return null;
        }
    }
}
