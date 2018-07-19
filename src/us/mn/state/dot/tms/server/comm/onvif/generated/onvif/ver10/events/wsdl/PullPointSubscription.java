package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.events.wsdl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.2.5
 * 2018-07-12T14:41:14.690-05:00
 * Generated source version: 3.2.5
 *
 */
@WebService(targetNamespace = "http://www.onvif.org/ver10/events/wsdl", name = "PullPointSubscription")
@XmlSeeAlso({us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsrf.r_2.ObjectFactory.class, ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsrf.bf_2.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.ObjectFactory.class, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.t_1.ObjectFactory.class})
public interface PullPointSubscription {

    /**
     * This method readjusts the pull pointer into the past.
     * 				A device supporting persistent notification storage shall provide the
     * 				following Seek command for all SubscriptionManager endpoints returned by
     * 				the CreatePullPointSubscription command. The optional Reverse argument can
     * 				be used to reverse the pull direction of the PullMessages command.
     * 				The UtcTime argument will be matched against the UtcTime attribute on a
     * 				NotificationMessage.
     * 			
     */
    @WebMethod(operationName = "Seek", action = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/SeekRequest")
    @Action(input = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/SeekRequest", output = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/SeekResponse")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "SeekResponse", targetNamespace = "http://www.onvif.org/ver10/events/wsdl", partName = "parameters")
    SeekResponse seek(
            @WebParam(partName = "parameters", name = "Seek", targetNamespace = "http://www.onvif.org/ver10/events/wsdl")
                    Seek parameters
    );

    /**
     * Properties inform a client about property creation, changes and
     * 				deletion in a uniform way. When a client wants to synchronize its properties with the
     * 				properties of the device, it can request a synchronization point which repeats the current
     * 				status of all properties to which a client has subscribed. The PropertyOperation of all
     * 				produced notifications is set to “Initialized”. The Synchronization Point is
     * 				requested directly from the SubscriptionManager which was returned in either the
     * 				SubscriptionResponse or in the CreatePullPointSubscriptionResponse. The property update is
     * 				transmitted via the notification transportation of the notification interface. This method is mandatory.
     * 			
     */
    @WebMethod(operationName = "SetSynchronizationPoint", action = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/SetSynchronizationPointRequest")
    @Action(input = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/SetSynchronizationPointRequest", output = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/SetSynchronizationPointResponse")
    @RequestWrapper(localName = "SetSynchronizationPoint", targetNamespace = "http://www.onvif.org/ver10/events/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.events.wsdl.SetSynchronizationPoint")
    @ResponseWrapper(localName = "SetSynchronizationPointResponse", targetNamespace = "http://www.onvif.org/ver10/events/wsdl", className = "us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.events.wsdl.SetSynchronizationPointResponse")
    void setSynchronizationPoint();

    /**
     * The device shall provide the following Unsubscribe command for all SubscriptionManager endpoints returned by the CreatePullPointSubscription command.
     * 				This command shall terminate the lifetime of a pull point.
     * 			
     */
    @WebMethod(operationName = "Unsubscribe", action = "http://docs.oasis-open.org/wsn/bw-2/SubscriptionManager/UnsubscribeRequest")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "UnsubscribeResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "UnsubscribeResponse")
    us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.UnsubscribeResponse unsubscribe(
            @WebParam(partName = "UnsubscribeRequest", name = "Unsubscribe", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
                    us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.Unsubscribe unsubscribeRequest
    ) throws us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.bw_2.UnableToDestroySubscriptionFault, us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;

    /**
     * This method pulls one or more messages from a PullPoint.
     * 				The device shall provide the following PullMessages command for all SubscriptionManager
     * 				endpoints returned by the CreatePullPointSubscription command. This method shall not wait until
     * 				the requested number of messages is available but return as soon as at least one message is available.
     * 				The command shall at least support a Timeout of one minute. In case a device supports retrieval of less messages 
     * 				than requested it shall return these without generating a fault.
     */
    @WebMethod(operationName = "PullMessages", action = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/PullMessagesRequest")
    @Action(input = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/PullMessagesRequest", output = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/PullMessagesResponse", fault = {@FaultAction(className = PullMessagesFaultResponse_Exception.class, value = "http://www.onvif.org/ver10/events/wsdl/PullPointSubscription/PullMessages/Fault/PullMessagesFaultResponse")})
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "PullMessagesResponse", targetNamespace = "http://www.onvif.org/ver10/events/wsdl", partName = "parameters")
    PullMessagesResponse pullMessages(
            @WebParam(partName = "parameters", name = "PullMessages", targetNamespace = "http://www.onvif.org/ver10/events/wsdl")
                    PullMessages parameters
    ) throws PullMessagesFaultResponse_Exception;
}
