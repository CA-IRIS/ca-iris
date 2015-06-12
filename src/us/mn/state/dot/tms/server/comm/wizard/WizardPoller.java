/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.wizard;

import java.io.IOException;
import java.util.LinkedList;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.SamplePoller;
import us.mn.state.dot.tms.utils.TimedGate;

/**
 * Implementation of the Infotek Wizard VDS protocol using UDP.
 * An object of this class is instantiated once, per defined
 * CommLink using this protocol. This driver supports multiple
 * field devices forwarding UDP packets to the same port. The
 * sensor id field in each VDS station sample is used to match
 * the drop value specified for each controller in the comm link.
 * The poller reads UDP datagrams from the input stream continuously.
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WizardPoller extends MessagePoller implements SamplePoller {

	/** Debug log */
	static protected final DebugLog WIZARD_LOG = new DebugLog("wizard");

	/** Log a msg */
	static protected void log(String msg) {
		if (WIZARD_LOG.isOpen())
			WIZARD_LOG.log(msg);
	}

	/** Minimum sensor ID */
	static protected final int SID_MIN = 1;

	/** Maximum sensor ID */
	static protected final int SID_MAX = 254;

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Start reading once */
	private boolean started_reading = false;

	/** Timer gate, determines how often failed state of
	 * controllers is updated */
	private final TimedGate timer_gate = new TimedGate(1000);

	/** Constructor */
	public WizardPoller(String n, Messenger m) {
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
		log("n=" + n + ", m=" + m + ", cl=" + comm_link);
	}

	/**
	 * Create a new message.
	 * @param o Ignored, because the operations are not associated
	 *          with controllers.
	 */
	@Override
	public CommMessage createCommMessageOp(Operation o) throws IOException {
		log("cl=" + comm_link);
		return new WizardMessage(messenger.getOutputStream(null),
			messenger.getInputStream(null), timer_gate);
	}

	/**
	 * Is the drop address valid?
	 * @param drop The controller drop address, which is used to match the
	 *             Wizard protocol sensor ID.
	 */
	@Override
	public boolean isAddressValid(int drop) {
		return ((drop >= SID_MIN) && (drop <= SID_MAX));
	}

	/** Reset controller */
	@Override
	public void resetController(ControllerImpl c) {
	}

	/** Send sample settings to a controller */
	@Override
	public void sendSettings(ControllerImpl c) {
	}

	/** Query sample data */
	@Override
	public void querySamples(ControllerImpl c, int i)  {
	}

	/** Query the sample poller */
	@Override
	public void queryPoller() {
		if(started_reading)
			return;
		started_reading = true;
		log("cl=" + comm_link + ": creating OpRead");
		addOperation(new OpRead(comm_link));
	}

	/** Sleep */
	static protected void sleepy() {
		sleepy(3797);			// FIXME: magic
	}

	/** Sleep */
	static protected void sleepy(int ms) {
		try {
			log("Sleeping " + ms);
			Thread.sleep(ms);
			log("Done sleeping");
		} catch(Exception e) {
			log("Sleep interupted ex=" + e);
		}
	}

	/** Get the protocol debug log */
	@Override
	protected DebugLog protocolLog() {
		return WIZARD_LOG;
	}

}
