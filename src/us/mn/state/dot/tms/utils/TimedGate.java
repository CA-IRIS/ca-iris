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
package us.mn.state.dot.tms.utils;

import us.mn.state.dot.sched.TimeSteward;

/**
 * Timed gate, to control how frequently an operation is performed.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class TimedGate {

	/** Last time gate opened. */
	private long last_open_time_ms;

	/** Minimum time the gate is locked in MS. */
	private final long min_locked_time_ms;

	/**
	 * Constructor.
	 * @param mlt Minimum time the gate is locked in MS.
	 */
	public TimedGate(long mlt) {
		min_locked_time_ms = mlt;
		updateOpenTime();
	}

	/** Update last open time. */
	private void updateOpenTime() {
		last_open_time_ms = TimeSteward.currentTimeMillis();
	}

	/**
	 * Is the gate locked? The gate is locked for a minimum 
	 * time period after last open.
	 */
	public boolean getGateLocked() {
		long lt = TimeSteward.currentTimeMillis() - last_open_time_ms;
		return lt < min_locked_time_ms;
	}

	/** Open the gate. */
	public void openGate() {
		updateOpenTime();
	}

}
