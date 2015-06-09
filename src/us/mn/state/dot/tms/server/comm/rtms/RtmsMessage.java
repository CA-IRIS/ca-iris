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
package us.mn.state.dot.tms.server.comm.rtms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.ProtocolException;
import us.mn.state.dot.tms.utils.TimedGate;

/**
 * Rtms message
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class RtmsMessage implements CommMessage {

	/** Output stream */
	protected final OutputStream out_stream;

	/** Input stream */
	protected final InputStream inp_stream;

	/** Device property */
	protected RtmsProperty dev_prop;

	/** Timer gate, passed via constructor */
	private final TimedGate timer_gate;

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Create a new message */
	public RtmsMessage(CommLinkImpl cl, OutputStream o, InputStream i,
		TimedGate tg)
	{
		comm_link = cl;
		out_stream = o;
		inp_stream = i;
		timer_gate = tg;
		RtmsPoller.log("cl=" + comm_link);
	}

	/** Add a controller property */
	@Override
	public void add(ControllerProperty cp) {
		RtmsPoller.log("cl=" + comm_link +": adding prop");
		if(cp instanceof RtmsProperty)
			dev_prop = (RtmsProperty)cp;
	}

	/**
	 * Read data from traffic controller and update controller statuses.
	 * @throws IOException On IO errors, except SocketTimeoutException.
	 */
	@Override
	public void queryProps() throws IOException {
		RtmsPoller.log("cl=" + comm_link);
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
			RtmsPoller.log("cl=" + comm_link +": timeout ex");
			return true;
		}
	}

	/**
	 * Store the controller properties.
	 * @throws IOException On any errors sending a request or receiving
	 *         response
	 */
	@Override
	public void storeProps() throws IOException {
	}

}
