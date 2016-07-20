/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.server.comm.axisptz;

import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

import java.io.IOException;

/**
 * Axis VAPIX PTZ operation.
 *
 * @author Travis Swanston
 */
public class OpAxisPTZ extends OpDevice<AxisPTZProperty> {

	/** Minimum time interval (ms) to enforce between Axis commands */
	static protected final int MIN_CMD_INTERVAL_MS = 30;

	private final AxisPTZPoller poller;

	/** Op property */
	private final AxisPTZProperty prop;

	/** Operation description */
	private final String op_desc;

	/**
	 * Create a new operation.
	 *
	 * @param c the CameraImpl instance
	 */
	protected OpAxisPTZ(CameraImpl c, AxisPTZProperty p,
		AxisPTZPoller poll) {

		super(PriorityLevel.COMMAND, c);
		prop = p;
		op_desc = p.getDesc();
		poller = poll;
		device.setOpStatus("sending cmd");
	}

	/** Create the second phase of the operation */
	protected Phase<AxisPTZProperty> phaseTwo() {
		return new PhaseTwo();
	}

	protected class PhaseTwo extends Phase<AxisPTZProperty> {

		protected Phase<AxisPTZProperty> poll(
			CommMessage<AxisPTZProperty> mess) throws IOException {

			mess.add(prop);
			pauseIfNeeded();
			mess.storeProps();
			poller.setLastCmdTime(TimeSteward.currentTimeMillis());
			updateOpStatus("cmd sent");

			return null;
		}
	}

	/** Return operation description */
	@Override
	public String getOperationDescription() {
		return (op_desc == null ? "Unnamed operation" : op_desc);
	}

	/**
	 * Update device op status.
	 * We bundle the operation description into the status because camera
	 * ops are generally so short that, as far as I can tell, by the time
	 * the client gets the SONAR "operation" notification and requests the
	 * op's description via SONAR, the device has already been released,
	 * and thus Device.getOperation() returns "None".
	*/
	protected void updateOpStatus(String stat) {
		String s = getOperationDescription() + ": " + stat;
		device.setOpStatus(s);
	}

	public AxisPTZProperty getProp() {
		return prop;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OpAxisPTZ)) return false;
		if (!super.equals(o)) return false;

		OpAxisPTZ opAxisPTZ = (OpAxisPTZ) o;

		return prop.equals(opAxisPTZ.prop);

	}

	@Override
	public int hashCode() {
		return prop.hashCode();
	}

	/**
	 * If CohuPTZPoller.MIN_CMD_INTERVAL_MS milliseconds have not passed
	 * since the previous device transaction, sleep until they have.
	 */
	protected void pauseIfNeeded() {
		long lastCmdTime = poller.getLastCmdTime();
		long curTime = System.currentTimeMillis();
		long delta = curTime - lastCmdTime;
		if (delta < MIN_CMD_INTERVAL_MS)
			TimeSteward.sleep_well(MIN_CMD_INTERVAL_MS - delta);
	}
}

