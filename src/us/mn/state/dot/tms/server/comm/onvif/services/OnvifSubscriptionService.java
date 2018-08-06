package us.mn.state.dot.tms.server.comm.onvif.services;

import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.bw_2.PullPoint;
import org.oasis_open.docs.wsn.bw_2.UnableToDestroyPullPointFault;
import org.oasis_open.docs.wsn.bw_2.UnableToGetMessagesFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifSubscriptionService implements PullPoint {
	@Override
	public void notify(Notify notify) {

	}

	@Override
	public DestroyPullPointResponse destroyPullPoint(
		DestroyPullPoint destroyPullPointRequest)
		throws UnableToDestroyPullPointFault, ResourceUnknownFault
	{
		return null;
	}

	@Override
	public GetMessagesResponse getMessages(
		GetMessages getMessagesRequest)
		throws UnableToGetMessagesFault, ResourceUnknownFault
	{
		return null;
	}
}
