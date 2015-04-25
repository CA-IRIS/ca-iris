/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.pems;

import java.io.IOException;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.Operation;

/**
 * A PeMS poller forwards real-time traffic data to a PeMS server.
 * Each station observation is delivered to the PeMS server via
 * a single UDP datagram that contains a single line of comma
 * separated values:<p>
 * <p>
 * Example line:<p>
 * <p>
 *   <Blockquote>
 *     1018510,3,15,60,3,15,70,3,15,80,3,2010-12-10 09:06:43<p>
 *   </Blockquote>
 * Fields:<p>
 *   <Blockquote>
 *     <code>station_id, number_of_lanes, lane1_data,
 *     lane2_data, ..., timestamp</code><p>
 *   </Blockquote>
 * <p>
 * Where<p>
 * <ul>
 *   <li><code>station_id</code>: PeMS station id,
 *   <li><code>number_of_lanes</code>: integer (>= 1),
 *   <li><code>lane_data</code>: repeat the following triple for each lane:
 *     <ul>
 *       <li><code>flow</code>: vehicles counted in 30-seconds
 *                              (>= 0) or empty,
 *       <li><code>speed</code>: integer, mph (>= 0), or empty,
 *       <li><code>occupancy</code>: integer (>=0 and <= 1000),
 *                                   or empty.
 *     </ul>
 *   <li><code>timestamp</code>: local time as yyyy-MM-dd HH:mm:ss
 * </ul>
 * Also,<p>
 * <ul>
 *   <li>Station observations are reported every 30 seconds.
 *   <li>There are no special line terminator requirements.
 *   <li>If a value for flow, speed, or occupancy is missing, it should
 *       be reported as an empty string.
 *   <li>If an entire observation is missing (e.g. comm problems) then
 *       an observation for that 30 second interval should not be sent.
 *   <li>There should be no spaces except in the <code>timestamp</code>
 *       field.
 * </ul>
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class PemsPoller extends MessagePoller implements Timer.TimerEvent {


	/** Debug log */
	static protected final DebugLog PEMS_LOG = new DebugLog("pems");

	/** Log a msg */
	static protected void log(String msg) {
		if (PEMS_LOG.isOpen())
			PEMS_LOG.log(msg);
	}

	/** Timer for periodic writes */
	private Timer write_timer = null;

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Constructor. */
	public PemsPoller(String n, Messenger m) {
		super(n, m);
		comm_link = (CommLinkImpl) CommLinkHelper.lookup(n);
		if (comm_link == null) {
			log("Failed to find CommLink.");
			return;
		}
		int to = comm_link.getTimeout();
		try {
			m.setTimeout(to);
			log("Set Messenger timeout to " + to + ".");
		}
		catch (IOException e) {
			log("Failed to set Messenger timeout.");
		}
		PemsPoller.log("n=" + n + ", m=" + m + ", cl=" + comm_link);
		write_timer = new Timer("pems", 30, 15, this);
	}

	/** Create a new message. */
	@Override
	public CommMessage createCommMessageOp(Operation o) throws IOException {
		PemsPoller.log("creating message");
		return new PemsMessage(messenger.getOutputStream(null),
			messenger.getInputStream(null));
	}

	/** Consider all drops valid. */
	public boolean isAddressValid(int drop) {
		return true;
	}

	/** Timer event: write to PeMS. */
	@Override
	public void readEvent() {
		addOperation(new OpWrite(comm_link));
	}

}
