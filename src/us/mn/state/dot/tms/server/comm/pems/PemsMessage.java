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

import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * A PeMS message.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class PemsMessage implements CommMessage {

	/** Output stream */
	private final OutputStream out_stream;

	/** Input stream */
	private final InputStream inp_stream;

	/** Properties */
	private LinkedList<PemsProperty> pems_props =
		new LinkedList<PemsProperty>();

	/** Constructor. */
	public PemsMessage(OutputStream os, InputStream is) {
		PemsPoller.log("called");
		out_stream = os;
		inp_stream = is;
	}

	/** Add a controller property. */
	@Override
	public void add(ControllerProperty cp) {
		if (cp instanceof PemsProperty)
			pems_props.add((PemsProperty) cp);
	}

	/**
	 * Query the controller properties.
	 *
	 * @throws IOException If errors sending or receiving.
	 */
	@Override
	public void queryProps() throws IOException {
	}

	/**
	 * Store the controller properties, which writes all station
	 * samples to PeMS.
	 *
	 * @throws IOException If errors sending or receiving.
	 */
	@Override
	public void storeProps() throws IOException {
		PemsPoller.log("total number of props=" +
			pems_props.size());
		int nsent = 0;
		for (PemsProperty p : pems_props) {
			if (p == null)
				continue;
			int nb = p.doSetRequest(out_stream, inp_stream);
			if (nb > 0) {
				++nsent;
				PemsPoller.log("Prop=" + p +
					", wrote " + nb + " bytes to pems");
				out_stream.flush();
			}
		}
		int nignored = pems_props.size() - nsent;
		PemsPoller.log("total stats=" + pems_props.size() +
			", sent=" + nsent + ", ignored=" + nignored);
	}

	@Override
	public void logQuery(ControllerProperty prop) {
	}

	@Override
	public void logStore(ControllerProperty prop) {
	}

}
