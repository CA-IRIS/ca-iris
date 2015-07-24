/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012  Iteris Inc.
 * Copyright (C) 2012-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.sensys;

import java.io.IOException;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import static us.mn.state.dot.tms.server.comm.MessagePoller.ConnMode;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.SamplePoller;

/**
 * Implementation of the Sensys VDS protocol. This driver assumes the
 * access point has been configured as follows:<p>
 *   Operating Mode = "Connection-based polls"<p>
 *   TCP Port Number = specify any port number<p>
 *   Units = Imperial<p>
 *   Individual Car Reports = disabled<p>
 *   Report Interval = 30 seconds<p>
 *   Average Speed = True<p>
 *   Speed Histogram = enable for TTI<p>
 *   Length Histogram = enable for TTI<p>
 *   Timestamp option = middle of interval<p>
 *   Use diagnostic to correct averages = disable<p>
 *   display diagnostics = enable<p>
 *
 * The connection-based polling means the access point will keep track
 * of the last record that was sent to the driver. This eliminates the
 * possibility of receiving a duplicate. After the socket is opened to
 * the access point, it blocks until the latest sample is sent. This
 * driver assumes a 30 second polling period and will wait up to ~25
 * seconds for the sample to arrive. If the sample is available in the
 * last 5 seconds, it will be read in the subsequent read. See the
 * SensysRec comment for the record format.<p>
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class SensysPoller extends MessagePoller implements SamplePoller {

	/** Debug log */
	static protected final DebugLog SENSYS_LOG = new DebugLog("sensys");

	/** Log a msg */
	static protected void log(String msg) {
		if (SENSYS_LOG.isOpen())
			SENSYS_LOG.log(msg);
	}

	/**
	 * Constructor
	 * @param n Name of commlink
	 * @param m Associated messenger
	 */
	public SensysPoller(String n, Messenger m) {
		super(n, m, ConnMode.PER_OP, 0);
		log("n=" + n + ", m=" + m);
	}

	/** Create a new message. */
	@Override
	public CommMessage createCommMessageOp(Operation o) throws IOException {
		return new SensysMessage(messenger);
	}

	/** Is the drop address valid? */
	@Override
	public boolean isAddressValid(int drop) {
		return true;
	}

	/** Sample poller interface: reset controller. */
	@Override
	public void resetController(ControllerImpl c) {
	}

	/** Sample poller interface: send sample settings. */
	@Override
	public void sendSettings(ControllerImpl c) {
	}

	/**
	 * Sample poller interface: query poller.
	 * Called on timer thread
	 */
	@Override
	public void queryPoller() {
	}

	/** Sample poller interface: query sample data. */
	public void querySamples(ControllerImpl ci, int in) {
		if ((in == 30) && ci.hasActiveDetector())
			addOperation(new OpQuerySamples(ci));
	}

	/** Sleep. */
	static protected void sleepy(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch(Exception e) {
			log("Sleep interupted ex=" + e);
		}
	}

}

