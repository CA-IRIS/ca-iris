package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPTZProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPresetRecallProperty;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifPresetStoreProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

import java.io.IOException;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifPreset extends OpOnvif {
	private boolean store;
	private int preset;

	public OpOnvifPreset(CameraImpl c, int preset, boolean store,
			     OnvifSessionMessenger session) {
		super(PriorityLevel.COMMAND, c, session);
		this.preset = preset;
		this.store = store;
	}

	@Override
	protected Phase<OnvifProperty> phaseTwo() {
		if (store) {
			return new StoreStop();
		}
		else {
			return new Recall();
		}
	}

	/**
	 * Onvif devices must be in a stopped state inorder to save position
	 */
	protected class StoreStop extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPTZProperty(0, 0, 0, session));
			mess.storeProps();
			updateOpStatus("Store command sent");
			return new StoreSave();
		}
	}

	protected class StoreSave extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPresetStoreProperty(session, preset));
			mess.storeProps();
			updateOpStatus("Store command sent");
			return null;
		}
	}

	protected class Recall extends Phase<OnvifProperty> {
		protected Phase<OnvifProperty> poll(
			CommMessage<OnvifProperty> mess) throws IOException
		{
			mess.add(new OnvifPresetRecallProperty(session, preset));
			mess.storeProps();
			updateOpStatus("Recall command sent");
			return null;
		}
	}
}
