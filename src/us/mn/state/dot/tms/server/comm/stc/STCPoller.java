/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.stc;

import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.GateArmState;
import us.mn.state.dot.tms.server.GateArmImpl;
import us.mn.state.dot.tms.server.comm.GateArmPoller;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;

/**
 * STCPoller is a java implementation of the Hysecurity STC protocol.
 *
 * @author Douglas Lau
 */
public class STCPoller extends MessagePoller<STCProperty>
	implements GateArmPoller
{
	/** Create a new STC poller */
	public STCPoller(String n, Messenger m) {
		super(n, m);
	}

	/** Check if a drop address is valid */
	public boolean isAddressValid(int drop) {
		// Drop address 254 is reserved for broadcast
		return drop >= 1 && drop <= 99;
	}

	/** Send a device request */
	public void sendRequest(GateArmImpl ga, DeviceRequest r) {
		switch(r) {
		case SEND_SETTINGS:
			addOperation(new OpControlGate(ga));
			break;
		case RESET_DEVICE:
			addOperation(new OpResetGate(ga));
			break;
		case QUERY_STATUS:
			addOperation(new OpQueryGateStatus(ga));
			break;
		default:
			// Ignore other requests
			break;
		}
	}

	/** Open the gate arm */
	public void openGate(GateArmImpl ga, User o) {
		addOperation(new OpControlGate(ga, o, GateArmState.OPENING));
	}

	/** Close the gate arm */
	public void closeGate(GateArmImpl ga, User o) {
		addOperation(new OpControlGate(ga, o, GateArmState.CLOSING));
	}
}
