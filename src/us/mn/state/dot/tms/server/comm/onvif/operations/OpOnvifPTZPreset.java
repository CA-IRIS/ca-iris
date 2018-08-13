package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZPresetRecallProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZPresetStoreProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPTZPreset extends OpOnvif<OnvifProperty> {
	private boolean store;
	private int preset;

	public OpOnvifPTZPreset(
		CameraImpl c, int preset, boolean store,
		OnvifSessionMessenger session)
	{
		super(PriorityLevel.COMMAND, c, session, OnvifService.PTZ);
		this.preset = preset;
		this.store = store;
	}

	@Override
	protected OnvifPhase phaseTwo() {
		return store ? new StoreStop() : new Recall();
	}

	/**
	 * Onvif devices must be in a stopped state in order to save position
	 */
	protected class StoreStop extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm)
			throws IOException
		{
			OnvifPTZStopProperty p =
				new OnvifPTZStopProperty(session);
			cm.add(p);
			cm.storeProps();
			logSent(p);
			return new StoreMove();
		}
	}

	protected class StoreMove extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm)
			throws IOException
		{
			OnvifPTZPresetStoreProperty p =
				new OnvifPTZPresetStoreProperty(session,
					preset);
			cm.add(p);
			cm.storeProps();
			logSent(p);
			return null;
		}
	}

	protected class Recall extends OnvifPhase {
		protected OnvifPhase poll2(CommMessage<OnvifProperty> cm)
			throws IOException
		{
			OnvifPTZPresetRecallProperty p =
				new OnvifPTZPresetRecallProperty(session,
					preset);
			cm.add(p);
			cm.storeProps();
			logSent(p);
			return null;
		}
	}
}
