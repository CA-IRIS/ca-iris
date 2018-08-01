package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.onvif.messenger.OnvifSessionMessenger;

import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class OnvifProperty extends ControllerProperty {

    static protected DebugLog ONVIF_LOG;

    protected OnvifSessionMessenger session;
    SOAPMessage response;

    OnvifProperty(DebugLog onvifLog, OnvifSessionMessenger session) {
        ONVIF_LOG = onvifLog;
        this.session = session;
    }

    /** Read from is and decode a STORE response */
    @Override
    public void decodeStore(ControllerImpl c, InputStream is)
            throws IOException {
        // todo handle error responses
        if (response == null)
            ONVIF_LOG.log("Onvif device response not received");
        else
            ONVIF_LOG.log(response.getContentDescription());
    }

    private String readStream(InputStream is) {
        // todo determine correct delimiter
        java.util.Scanner s = new java.util.Scanner(
                is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * must be called by subclasses before attempting to use the auth credentials
     * @return true if the session was freshly initialized
     */
    private void initSession(ControllerImpl c) throws IOException {
        // this should be a one time session setup per device per client
        if (!session.isInitialized()) {
            try {
                session.initialize(c.getUsername(), c.getPassword());
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    /** Encode a store request and write it to the output stream */
    @Override
    public void encodeStore(ControllerImpl c, OutputStream os)
            throws IOException {
        initSession(c);
        encodeStore();
    }

    abstract protected void encodeStore() throws IOException;
}
