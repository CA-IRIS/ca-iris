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
package us.mn.state.dot.tms.server.comm.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.ProtocolException;
import us.mn.state.dot.tms.utils.TimedGate;

/**
 * Wizard message
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WizardMessage implements CommMessage {

	/** Output stream */
	protected final OutputStream out_stream;

	/** Input stream */
	protected final InputStream inp_stream;

	/** Device property */
	protected WizardProperty dev_prop;

	/** Timer gate, passed via constructor */
	private final TimedGate timer_gate;

	/** Create a new message */
	public WizardMessage(OutputStream o, InputStream i, TimedGate tg) {
		WizardPoller.log("called");
		out_stream = o;
		inp_stream = i;
		timer_gate = tg;
	}

	/** Add a controller property. */
	@Override
	public void add(ControllerProperty cp) {
		WizardPoller.log("adding property");
		if(cp instanceof WizardProperty)
			dev_prop = (WizardProperty)cp;
	}

	/**
	 * Read data from traffic controller and update controller statuses.
	 * @throws IOException On IO errors, except SocketTimeoutException.
	 */
	@Override
	public void queryProps() throws IOException {
		WizardPoller.log("called");
		if(dev_prop == null)
			throw new ProtocolException("No property");
		_queryProps();
		if(!timer_gate.getGateLocked()) {
			timer_gate.openGate();
			dev_prop.updateControllers();
		}
	}

	/**
	 * Read a datagram. Timeouts (SocketTimeoutException) are silently
	 * caught and handled.
	 * @return true on timeout else false.
	 * @throws IOException On any IO errors.
	 */
	private boolean _queryProps() throws IOException {
		try {
			dev_prop.doGetRequest(out_stream, inp_stream);
			return false;
		} catch(SocketTimeoutException ex) {
			WizardPoller.log("timeout ex=" + ex);
			return true;
		}
	}

	/** Store the controller properties.
	 * @throws IOException On any errors sending request or receiving
	 *                     response
	 */
	@Override
	public void storeProps() throws IOException {
	}

}
