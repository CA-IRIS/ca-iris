/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
 * Copyright (C) 2008-2010  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.dmsxml;

import java.io.IOException;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.sonar.server.UserImpl;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.DMSPoller;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.StreamMessenger;
import us.mn.state.dot.tms.utils.Log;

/**
 * This class provides a DMS Poller that communicates with
 * a standalone server via XML that provides DMS functionality.
 * It was developed for the Caltrans IRIS implementation. It 
 * maybe use useful for any application that needs to interface
 * IRIS DMS functionality with an external application.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class DmsXmlPoller extends MessagePoller implements DMSPoller {

	/** valid address range (inclusive) */
	static public final int MAX_ADDRESS = 255;
	static public final int MIN_ADDRESS = 1;

	/** Create a new dmsxml poller */
	public DmsXmlPoller(String n, Messenger m) {
		super(n, m);
		assert m instanceof StreamMessenger;
	}

	/**
	 * Create a new message for the specified controller. Called
	 * by parent MessagePoller.doPoll().
	 *
	 * @param c Associated controller.
	 * @return A newly created Message.
	 * @throws IOException
	 */
	public CommMessage createMessage(ControllerImpl c) throws IOException {
		return new Message(messenger.getOutputStream(c),
				   messenger.getInputStream("", c));
	}

	/** Check if a drop address is valid */
	public boolean isAddressValid(int drop) {
		return ((drop >= MIN_ADDRESS) && (drop <= MAX_ADDRESS));
	}

	/** Send a new message to the sign. Called by DMSImpl.
	 *  @param dms May be null.
	 *  @param m Sign message to send, may be null.
	 *  @param o User sending message, may be null.
	 *  @throws InvalidMessageException
	 *  @see DMSImpl, DMS */
	public void sendMessage(DMSImpl dms, SignMessage m, User o)
		throws InvalidMessageException
	{
		Log.finest("DmsXmlPoller.sendMessage(" + dms + ", " + 
			m + ", " + o+ ") called.");
		if(dms == null || m == null)
			return;
		if(SignMessageHelper.isBlank(m))
			addOperation(new OpBlank(dms, m, o));
		else
			addOperation(new OpMessage(dms, m, o));
	}

	/** Send a device request message to the sign, no user specified */
	public void sendRequest(DMSImpl dms, DeviceRequest r) {
		// user assumed to be IRIS
		User u = null;
		sendRequest(dms, r, u);
	}

	/** Send a device request message to the sign from a specific user */
	public void sendRequest(DMSImpl dms, DeviceRequest r, User u) {
		initialPoll(dms, r, u);
		_sendRequest(dms, r, u, false);
	}

	/** Perform an initial get-configuration and a get-status 
	 *  operation for periodically queriable DMS. */
	private void initialPoll(DMSImpl d, DeviceRequest r, User u) {
		if(d == null)
			return;
		// SEND_SETTINGS is sent on startup
		if(r == DeviceRequest.SEND_SETTINGS)
			if(!d.getConfigure()) {
				_sendRequest(d, DeviceRequest.
					QUERY_CONFIGURATION, u, false);
				_sendRequest(d, DeviceRequest.
					QUERY_MESSAGE, u, true);
			}
	}

	/** Send a device request message to the sign from a specific user */
	private void _sendRequest(DMSImpl dms, DeviceRequest r, User u, 
		boolean startup) 
	{
		if(dms == null)
			return;
		// handle requests
		if(r == DeviceRequest.QUERY_CONFIGURATION) {
			addOperation(new OpQueryConfig(dms, u));
		} else if(r == DeviceRequest.QUERY_MESSAGE) {
			addOperation(new OpQueryMsg(dms, u, r, startup));
		} else if(r == DeviceRequest.RESET_DEVICE) {
			addOperation(new OpReset(dms, u));
		} else if(r == DeviceRequest.QUERY_STATUS) {
			// ignore
		} else if(r == DeviceRequest.SEND_SETTINGS) {
			// ignore
		} else if(r == DeviceRequest.RESET_MODEM) {
			// ignore
		} else if(r == DeviceRequest.QUERY_PIXEL_FAILURES) {
			// ignore
		} else {
			// ignore other requests
			Log.finest("DmsXmlPoller.sendRequest(" + 
				dms.getName() +	"): ignored request r=" + r);
		}
	}
}
