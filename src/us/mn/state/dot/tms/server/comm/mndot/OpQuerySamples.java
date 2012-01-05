/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.mndot;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import us.mn.state.dot.sched.Completer;
import us.mn.state.dot.tms.Constants;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Query sample operations are for collecting 30-second or 5-minute binned
 * sample data.
 *
 * @author Douglas Lau
 */
abstract public class OpQuerySamples extends Op170 {

	/** Sample interval completer */
	protected final Completer completer;

	/** Volume data for all detectors on a controller */
	protected final int[] volume = new int[DETECTOR_INPUTS];

	/** Scan data for all detectors on a controller */
	protected final int[] scans = new int[DETECTOR_INPUTS];

	/** Create a new OpQuerySamples poll */
	protected OpQuerySamples(PriorityLevel p, ControllerImpl c,
		Completer comp)
	{
		super(p, c);
		completer = comp;
		for(int i = 0; i < DETECTOR_INPUTS; i++) {
			volume[i] = Constants.MISSING_DATA;
			scans[i] = Constants.MISSING_DATA;
		}
	}

	/** Begin the operation */
	public boolean begin() {
		return completer.beginTask(getKey()) && super.begin();
	}

	/** Cleanup the operation */
	public void cleanup() {
		completer.completeTask(getKey());
		super.cleanup();
	}

	/** Process sample data from the controller */
	protected void processData(byte[] record) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(record);
		DataInputStream dis = new DataInputStream(is);
		for(int i = 0; i < DETECTOR_INPUTS; i++)
			volume[i] = dis.readUnsignedByte();
		for(int i = 0; i < DETECTOR_INPUTS; i++)
			scans[i] = dis.readUnsignedShort();
	}
}
