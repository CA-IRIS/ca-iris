package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifSessionMessenger;

/**
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OpOnvif extends OpDevice<OnvifProperty> {

	protected OnvifSessionMessenger session;

	protected OpOnvif(
		PriorityLevel p, DeviceImpl d, OnvifSessionMessenger session)
	{
		super(p, d);
		this.session = session;
		updateOpStatus("Preparing to send operation");
		OnvifPoller.log("Preparing to send operation");
	}

	/**
	 * Update device op status. We bundle the operation description into
	 * the status because camera ops are generally so short that, as far as I
	 * can tell, by the time the client gets the SONAR "operation"
	 * notification and requests the op's description via SONAR, the device
	 * has already been released, and thus Device.getOperation() returns
	 * "None".
	 */
	protected void updateOpStatus(String stat) {
		String s = getOperationDescription() + ": " + stat;
		device.setOpStatus(s);
	}
}
