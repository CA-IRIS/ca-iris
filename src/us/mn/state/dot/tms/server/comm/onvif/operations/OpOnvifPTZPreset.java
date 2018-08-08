package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZStopProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZPresetRecallProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZPresetStoreProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPTZPreset extends OpOnvif {
	private boolean store;
	private int preset;

	public OpOnvifPTZPreset(CameraImpl c, int preset, boolean store,
				OnvifSessionMessenger session) {
		super(PriorityLevel.COMMAND, c, session);
		this.preset = preset;
		this.store = store;
		try {
			session.selectService(OnvifService.PTZ);
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	@Override
	protected Phase<OnvifProperty> phaseTwo() {
		return store ? new StoreStop() : new Recall();
	}

	/**
	 * Onvif devices must be in a stopped state in order to save position
	 */
	protected class StoreStop extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPTZStopProperty(session));
			mess.storeProps();
			log("Stop command sent before preset store");
			return new StoreMove();
		}
	}

	protected class StoreMove extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPTZPresetStoreProperty(session, preset));
			mess.storeProps();
			log("Store preset command sent");
			return null;
		}
	}

	protected class Recall extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPTZPresetRecallProperty(session, preset));
			mess.storeProps();
			log("Recall command sent");
			return null;
		}
	}
}
