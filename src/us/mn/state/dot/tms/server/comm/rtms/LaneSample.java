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
package us.mn.state.dot.tms.server.comm.rtms;

/**
 * All sample data for one lane.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class LaneSample {

	static private final int MAX_PERCENT = 1024;

	/** Maximum number of scans in 30 seconds */
	static public final int MAX_SCANS = 1800;

	public final int det;	// >= 1
	public int volume;	// # vehicles in sample period
	public int speed;	// miles per hour
	private int occupancy;	// ranges 0-1024, to calc a percentage

	/** Constructor */
	public LaneSample(int d) {
		assert d > 0;
		det = d;
	}

	/** Get scans */
	public int getScans() {
		float o = occupancy / (float)MAX_PERCENT;
		return Math.round(o * MAX_SCANS);
	}

	/** Calculate a percentage */
	static public float percent(int i) {
		return 100 * i / (float)MAX_PERCENT;
	}

	/** Given a percentage P (0 - 100), return P/100 * MAX_PERCENT. */
	static private float percToNumerator(float f) {
		float r = f / 100f * (float)MAX_PERCENT;
		r = (r < 0 ? 0 : r);
		r = (r > MAX_PERCENT ? MAX_PERCENT : r);
		return r;
	}

	/** To string */
	public String toString() {
		return det + ": v=" + volume + ", s=" + speed + ", o=" +
			percent(occupancy);
	}

	/** Set occupancy.
	 * @param o Occupancy as a percentage, 0 - 100 */
	public void setOccupancy(float o) {
		occupancy = Math.round(percToNumerator(o));
	}
}
