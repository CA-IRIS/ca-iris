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

import us.mn.state.dot.tms.server.Constants;

/**
 * All samples for one lane.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class LaneSample {

	/** Maximum number of scans in 30 seconds */
	static public final int MAX_SCANS = 1800;

	/** Maximum number of lanes */
	static public final int MAX_NUM_LANES = 8;

	/** Lane number (1-based) */
	public final int lane_num;

	/** Volume, the number of vehicles in the sample period */
	public int volume = Constants.MISSING_DATA;

	/** Speed in either KPH or MPH */
	public double speed = Constants.MISSING_DATA;

	/** Occupancy (double, 0 - 100) */
	public double occupancy = Constants.MISSING_DATA;

	/**
	 * Constructor.
	 * @param ln Lane number (1-based)
	 */
	protected LaneSample(int ln) {
		assert (ln >= 1) : "error: ln < 1";
		assert (ln <= MAX_NUM_LANES) : "error: ln > max_num_lanes";
		lane_num = ln;
	}

	/** Get scans (integer, 0 - 1800). */
	public int getScans() {
		if (occupancy == Constants.MISSING_DATA)
			return Constants.MISSING_DATA;
		double o = occupancy / 100.0D;
		return (int)Math.round(o * MAX_SCANS);
	}

	/**
	 * Get the speed.
	 * @param si Convert speed from SI to MPH before returning
	 * @return The speed in MPH
	 */
	public double getSpeed(boolean si) {
		if (speed == Constants.MISSING_DATA)
			return Constants.MISSING_DATA;
		return (si ? kphToMph(speed) : speed);
	}

	/**
	 * Convert KPH to MPH.
	 * FIXME: use us.mn.state.dot.tms.units.Distance instead
	 * @param kph The speed in KPH
	 * @return The speed in MPH
	 */
	static private double kphToMph(double kph) {
		return Math.round(0.621371192D * kph);
	}

	/** Return a representative string. */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("(LaneSample: lane=").append(lane_num);
		s.append(", v=").append(volume);
		s.append(", s=").append(speed);
		s.append(", o=").append(occupancy);
		s.append(")");
		return s.toString();
	}

}
