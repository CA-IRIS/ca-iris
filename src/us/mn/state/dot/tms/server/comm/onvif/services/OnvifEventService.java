package us.mn.state.dot.tms.server.comm.onvif.services;

import org.oasis_open.docs.wsn.bw_2.*;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.onvif.ver10.events.wsdl.*;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifEventService implements EventPortType {

	@Override
	public GetEventPropertiesResponse getEventProperties(
		GetEventProperties parameters)
	{
		return null;
	}

	@Override
	public CreatePullPointSubscriptionResponse createPullPointSubscription(
		CreatePullPointSubscription parameters)
		throws TopicNotSupportedFault,
		TopicExpressionDialectUnknownFault,
		InvalidTopicExpressionFault,
		InvalidMessageContentExpressionFault,
		InvalidProducerPropertiesExpressionFault,
		UnacceptableInitialTerminationTimeFault,
		NotifyMessageNotSupportedFault, ResourceUnknownFault,
		UnsupportedPolicyRequestFault, InvalidFilterFault,
		SubscribeCreationFailedFault, UnrecognizedPolicyRequestFault
	{
		return null;
	}

	@Override
	public Capabilities getServiceCapabilities() {
		return null;
	}
}
