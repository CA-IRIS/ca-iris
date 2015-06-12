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
package us.mn.state.dot.tms.server.comm.wizard;

/**
 * Sample data for one lane.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class LaneSample {

	/** Maximum number of scans in 30 seconds */
	static public final int MAX_SCANS = 1800;

	/** Lane number, [1,max] */
	public final int lanenum;

	/** Vehicle count in sample period */
	public int volume;

	/** Speed in MPH */
	public int speed;

	/** Occupancy, ranges 0 - 100 */
	public float occ;

	/** Volumes per class, read by not stored */
	public int vol_c1;
	public int vol_c2;
	public int vol_c3;
	public int vol_c4;

	/** Constructor */
	public LaneSample(int ln) {
		assert ln > 0;
		lanenum = ln;
	}

	/** Get scans, which is an integer 0 - 1800. */
	public int getScans() {
		float o = occ / 100f;
		return Math.round(o * MAX_SCANS);
	}

	/** To string */
	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append(", lane=").append(lanenum);
		r.append(", vol=").append(volume);
		r.append(", spd=").append(speed);
		r.append(", occ=").append(occ);
		r.append(", vol_c1=").append(vol_c1);
		r.append(", vol_c2=").append(vol_c2);
		r.append(", vol_c3=").append(vol_c3);
		r.append(", vol_c4=").append(vol_c4);
		return r.toString();
	}

}
