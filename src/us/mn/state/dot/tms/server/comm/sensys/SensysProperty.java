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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.utils.ByteBlob;

/**
 * Protocol property, which reads a traffic sample from a field
 * sensor and stores the sample.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class SensysProperty extends ControllerProperty {

	/** Traffic sample period */
	static protected final long SAMPLE_PERIOD_MS = 30 * 1000;

	/** Time margin used to decide if VDS timed out */
	static protected final long READ_MARGIN_MS = 5 * 1000;

	/** Controller associated with property */
	private final ControllerImpl sensys_controller;

	/** Controller read timeout in ms.  See the SensysPoller comment. */
	static private final int TIMEOUT_MS = 25 * 1000;

	static private final int REREAD_SLEEP_TIME_MS = 250;

	/** Record */
	private final SensysRec ss_rec;

	/**
	 * Constructor.
	 * @param c Associated controller, never null
	 * @param r Record, never null
	 */
	public SensysProperty(ControllerImpl c, SensysRec r) {
		sensys_controller = c;
		ss_rec = r;
	}

	/** Perform a set request. */
	public void doSetRequest(OutputStream os, InputStream is, String h)
		throws IOException
	{
	}

	/**
	 * Perform a get request, which consists of reading sensor data,
	 * parsing into a record and storing.
	 * Called by SensysMessage.
	 * @throws IOException, e.g. on timeout or parse exception
	 */
	public void doGetRequest(OutputStream os, InputStream is)
		throws IOException
	{
		SensysPoller.log("Reading data from sensor...");
		SensysBlob sb = read(is, TIMEOUT_MS);		// throws STE
		SensysPoller.log("Returned from read(), " +
			"#bytes=" + sb.size());
		ss_rec.parse(sensys_controller.getName(),	// throws PE
			sb);
	}

	/**
	 * Read bytes from input stream, blocking until a timeout, a complete
	 * record is read, or an exception is thrown.
	 * @param is Input stream to read from.
	 * @param to Timeout in ms.
	 * @return Bytes read from the field device, excluding terminator.
	 * @throws IOException, e.g. on timeout
	 */
	private SensysBlob read(InputStream is, int to)
		throws IOException
	{
		SensysPoller.log("reading with timeout ms=" + to);
		SensysBlob blob = new SensysBlob();
		while(dataAvailable(is, to)) {
			int b = is.read();	// throws IOE
			if (b < 0)
				return blob;
			else if (b >= 0 && b <= 255) {
				if (SensysBlob.readComplete(b))
					return blob;
				blob.add(b);
			}
		}
		// didn't read a record terminator
		String m = "timeout after " + blob.size() + " bytes";
		SensysPoller.log(m);
		throw new SocketTimeoutException(m);
	}

	/**
	 * Block until data is available on the specified input stream or
	 * the specified timeout expires.
	 * @param is Input stream to read from.
	 * @param timeout Timeout in milliseconds.
	 * @throws IOException
	 * @returns True if data is available else false on timeout.
	 */
	static private boolean dataAvailable(InputStream is, int timeout)
		throws IOException
	{
		final long st = TimeSteward.currentTimeMillis();
		while(true) {
			if (is.available() > 0)
				return true;
			if (TimeSteward.currentTimeMillis() - st >= timeout)
				return (is.available() > 0);
			SensysPoller.sleepy(REREAD_SLEEP_TIME_MS);
		}
	}

}
