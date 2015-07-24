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
 * Samples for multiple lanes.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class LaneSamples {

	/** A missing sample for all possible lanes */
	static protected final int[] MISSING_SAMPLE = {
		Constants.MISSING_DATA, Constants.MISSING_DATA,
		Constants.MISSING_DATA, Constants.MISSING_DATA,
		Constants.MISSING_DATA, Constants.MISSING_DATA,
		Constants.MISSING_DATA, Constants.MISSING_DATA
	};

	/** Units */
	private final boolean si_unit;

	/** Lane samples */
	private final LaneSample[] lane_samples;


	/** Constructor for unknown number of lanes. */
	protected LaneSamples() {
		this(false, LaneSample.MAX_NUM_LANES);
	}

	/**
	 * Constructor for known number of lanes.
	 * Lanes are numbered from 1 to the number specified.
	 * @param u True for SI units, false for imperial
	 * @param nlanes Number of lanes
	 */
	protected LaneSamples(boolean u, int nlanes) {
		assert nlanes >= 0;
		si_unit = u;
		lane_samples = new LaneSample[nlanes];
		for (int i = 0; i < nlanes; ++i)
			lane_samples[i] = new LaneSample(i + 1);
	}

	/** Get the number of lanes. */
	protected int getNumLanes() {
		return lane_samples.length;
	}

	/**
	 * Set the volume for a specific lane.
	 * @param i Lane index
	 * @param val Value to set
	 */
	protected void setVolume(int i, int val) {
		if (i >= 0 && i < lane_samples.length)
			lane_samples[i].volume = val;
		else
			assert false;
	}

	/**
	 * Set occupancy for a specific lane.
	 * @param i Lane index
	 * @param val Value to set
	 */
	protected void setOccupancy(int i, double val) {
		if (i >= 0 && i < lane_samples.length)
			lane_samples[i].occupancy = val;
		else
			assert false;
	}

	/**
	 * Set speed for a specific lane.
	 * @param i Lane index
	 * @param val Value to set
	 */
	protected void setSpeed(int i, double val) {
		if (i >= 0 && i < lane_samples.length)
			lane_samples[i].speed = val;
		else
			assert false;
	}

	/** Get the volume array, in lane order. */
	protected int[] getVolumes() {
		int nl = lane_samples.length;
		if (nl <= 0)
			return LaneSamples.MISSING_SAMPLE;
		int[] vol = new int[nl];
		for (LaneSample ls: lane_samples)
			vol[ls.lane_num - 1] = ls.volume;
		return vol;
	}

	/** Get the scan count as an array, in lane order. */
	protected int[] getScans() {
		int nl = lane_samples.length;
		if (nl <= 0)
			return LaneSamples.MISSING_SAMPLE;
		int[] scans = new int[nl];
		for (LaneSample ls: lane_samples)
			scans[ls.lane_num - 1] = ls.getScans();
		return scans;
	}

	/** Get the speeds as an array, in lane order, in MPH. */
	protected int[] getSpeeds() {
		int nl = lane_samples.length;
		if (nl <= 0)
			return LaneSamples.MISSING_SAMPLE;
		int[] speed = new int[nl];
		for (LaneSample ls: lane_samples) {
			speed[ls.lane_num - 1] = 
				(int)Math.round(ls.getSpeed(si_unit));
		}
		return speed;
	}

	/** Return a representative string. */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("(LaneSamples: #lanes=").append(getNumLanes());
		s.append(", lane_sample=");
		for (int l = 0; l < getNumLanes(); ++l)
			s.append(lane_samples[l].toString());
		s.append(")");
		return s.toString();
	}

}
