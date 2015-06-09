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

/**
 * RTMS protocol record type. The combination of a qualifier value and
 * qualifier length identifies a unique record type.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public enum RecType {
	VOLLONG("VOLUME LONG", 0x1b, 9),
	VOLUME("VOLUME", 0x10, 9),
	OCCUPANCY("OCCUPANCY", 0x11, 9),
	OCCUPANCYL("OCCUPANCYL", 0x11, 17),
	SPEED("SPEED", 0x12, 11),
	MIDSIZE("MID-SIZE", 0x20, 9),
	XLONG("EXTRA LONG VOL", 0x36, 9),
	RTCLOCK("CLOCK", 0x16, 7),
	CLOCKTICK("CLOCK TICK", 0x35, 4),
	MSGINFO("MSG INFO", 0x39, 5);

	/** Qualifier */
	public final int t_qual;

	/** Message payload length */
	public final int t_length;

	/** Description */
	public final String t_desc;

	/**
	 * Constructor.
	 * @param d Type description
	 * @param q Type qualifier
	 * @param l Type length
	 */
	RecType(String d, int q, int l) {
		t_desc = d;
		t_qual = q;
		t_length = l;
	}

	/** Valid record qualifier? */
	static public boolean validQualifier(int q) {
		for(RecType t : RecType.values())
			if(t.t_qual == q)
				return true;
		return false;
	}

	/**
	 * Lookup a type using the length and qualifier. The same qualifier
	 * can have multiple lengths.
	 * @param q Type qualifier
	 * @param l Type length
	 * @return The type, or null if match not found.
	 */
	static public RecType lookupType(int q, int l) {
		for(RecType t : RecType.values())
			if(q == t.t_qual && l == t.t_length)
				return t;
		return null;
	}

}
