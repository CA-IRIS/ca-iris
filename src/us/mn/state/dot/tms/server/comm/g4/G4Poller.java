/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012  Iteris Inc.
 * Copyright (C) 2012-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.g4;

import java.io.IOException;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.SamplePoller;

/**
 * G4Poller is a java implementation of the RTMS G4 VDS protocol.
 *
 * @author Michael Darter
 * @author Douglas Lau
 */
public class G4Poller extends MessagePoller<G4Property> implements SamplePoller{

	/** Debug log */
	static protected final DebugLog G4_LOG = new DebugLog("g4");

	/** Create a new G4 poller */
	public G4Poller(String n, Messenger m) {
		super(n, m);
	}

	/** Check if a sensor id is valid */
	@Override
	public boolean isAddressValid(int drop) {
		return drop >= 0 && drop < 65536;
	}

	/** Perform a controller reset */
	@Override
	public void resetController(ControllerImpl c) {
		addOperation(new OpSendSensorSettings(c, true));
	}

	/** Send sample settings to a controller. */
	@Override
	public void sendSettings(ControllerImpl c) {
		addOperation(new OpSendSensorSettings(c, false));
	}

	/** Query sample data.
 	 * @param c Controller to poll.
 	 * @param p Sample period in seconds. */
	@Override
	public void querySamples(ControllerImpl c, int p) {
		if (p == 30) {
			if (c.hasActiveDetector())
				addOperation(new OpQueryStats(c, p));
		}
	}

	/** Get the protocol debug log */
	@Override
	protected DebugLog protocolLog() {
		return G4_LOG;
	}
}
