
package us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.bw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.2.5
 * 2018-07-12T14:41:14.679-05:00
 * Generated source version: 3.2.5
 */

@WebFault(name = "ResumeFailedFault", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
public class ResumeFailedFault extends Exception {

    private us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.ResumeFailedFaultType resumeFailedFault;

    public ResumeFailedFault() {
        super();
    }

    public ResumeFailedFault(String message) {
        super(message);
    }

    public ResumeFailedFault(String message, Throwable cause) {
        super(message, cause);
    }

    public ResumeFailedFault(String message, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.ResumeFailedFaultType resumeFailedFault) {
        super(message);
        this.resumeFailedFault = resumeFailedFault;
    }

    public ResumeFailedFault(String message, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.ResumeFailedFaultType resumeFailedFault, Throwable cause) {
        super(message, cause);
        this.resumeFailedFault = resumeFailedFault;
    }

    public us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.ResumeFailedFaultType getFaultInfo() {
        return this.resumeFailedFault;
    }
}
