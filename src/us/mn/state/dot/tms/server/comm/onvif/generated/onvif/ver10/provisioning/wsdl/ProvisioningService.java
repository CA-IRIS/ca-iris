package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Functionality for all provisioning service operations.
 *
 * This class was generated by Apache CXF 3.2.5
 * 2018-07-12T14:44:35.219-05:00
 * Generated source version: 3.2.5
 *
 */
@WebService(targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", name = "ProvisioningService")
@XmlSeeAlso({us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsrf.bf_2.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.w3._2004._08.xop.include.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.t_1.ObjectFactory.class, ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.w3._2003._05.soap_envelope.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.w3._2005._05.xmlmime.ObjectFactory.class})
public interface ProvisioningService {

    /**
     * Moves device on the zoom axis.
     */
    @WebMethod(operationName = "ZoomMove", action = "http://www.onvif.org/ver10/provisioning/wsdl/ZoomMove")
    @RequestWrapper(localName = "ZoomMove", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.ZoomMove")
    @ResponseWrapper(localName = "ZoomMoveResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.ZoomMoveResponse")
    void zoomMove(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource,
            @WebParam(name = "Direction", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    ZoomDirection direction,
            @WebParam(name = "Timeout", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    javax.xml.datatype.Duration timeout
    );

    /**
     * Moves device on the roll axis.
     */
    @WebMethod(operationName = "RollMove", action = "http://www.onvif.org/ver10/provisioning/wsdl/RollMove")
    @RequestWrapper(localName = "RollMove", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.RollMove")
    @ResponseWrapper(localName = "RollMoveResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.RollMoveResponse")
    void rollMove(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource,
            @WebParam(name = "Direction", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    RollDirection direction,
            @WebParam(name = "Timeout", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    javax.xml.datatype.Duration timeout
    );

    /**
     * Moves device on the focus axis.
     */
    @WebMethod(operationName = "FocusMove", action = "http://www.onvif.org/ver10/provisioning/wsdl/FocusMove")
    @RequestWrapper(localName = "FocusMove", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.FocusMove")
    @ResponseWrapper(localName = "FocusMoveResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.FocusMoveResponse")
    void focusMove(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource,
            @WebParam(name = "Direction", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    FocusDirection direction,
            @WebParam(name = "Timeout", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    javax.xml.datatype.Duration timeout
    );

    /**
     * Moves device on the tilt axis.
     */
    @WebMethod(operationName = "TiltMove", action = "http://www.onvif.org/ver10/provisioning/wsdl/TiltMove")
    @RequestWrapper(localName = "TiltMove", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.TiltMove")
    @ResponseWrapper(localName = "TiltMoveResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.TiltMoveResponse")
    void tiltMove(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource,
            @WebParam(name = "Direction", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    TiltDirection direction,
            @WebParam(name = "Timeout", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    javax.xml.datatype.Duration timeout
    );

    /**
     * Returns the capabilities of the provisioning service.
     */
    @WebMethod(operationName = "GetServiceCapabilities", action = "http://www.onvif.org/ver10/provisioning/wsdl/GetServiceCapabilities")
    @RequestWrapper(localName = "GetServiceCapabilities", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.GetServiceCapabilities")
    @ResponseWrapper(localName = "GetServiceCapabilitiesResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.GetServiceCapabilitiesResponse")
    @WebResult(name = "Capabilities", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
    Capabilities getServiceCapabilities();

    /**
     * Moves device on the pan axis.
     */
    @WebMethod(operationName = "PanMove", action = "http://www.onvif.org/ver10/provisioning/wsdl/PanMove")
    @RequestWrapper(localName = "PanMove", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.PanMove")
    @ResponseWrapper(localName = "PanMoveResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.PanMoveResponse")
    void panMove(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource,
            @WebParam(name = "Direction", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    PanDirection direction,
            @WebParam(name = "Timeout", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    javax.xml.datatype.Duration timeout
    );

    /**
     * Stops device motion on all axes.
     */
    @WebMethod(operationName = "Stop", action = "http://www.onvif.org/ver10/provisioning/wsdl/Stop")
    @RequestWrapper(localName = "Stop", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.Stop")
    @ResponseWrapper(localName = "StopResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.StopResponse")
    void stop(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource
    );

    /**
     * Returns the lifetime move counts.
     */
    @WebMethod(operationName = "GetUsage", action = "http://www.onvif.org/ver10/provisioning/wsdl/Usage")
    @RequestWrapper(localName = "GetUsage", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.GetUsage")
    @ResponseWrapper(localName = "GetUsageResponse", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl.GetUsageResponse")
    @WebResult(name = "Usage", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
    Usage getUsage(
            @WebParam(name = "VideoSource", targetNamespace = "http://www.onvif.org/ver10/provisioning/wsdl")
                    String videoSource
    );
}
