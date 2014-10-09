/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.viconptz;

/**
 * Vicon property to recall or store a preset (1-15).  Note: for newer Vicon
 * receivers, this command has no effect.  The extended preset command must be
 * used instead.
 *
 * @author Douglas Lau
 */
public class PresetProperty extends ViconPTZProperty {

	/** Maximum preset number for this property */
	static public final int MAX_PRESET = 15;

	/** Store (or recall) */
	private final boolean store;

	/** Preset to store or recall */
	private final int preset;

	/** Create a new preset property */
	public PresetProperty(boolean s, int p) {
		store = s;
		preset = p;
	}

	/** Get a string representation of the property */
	@Override
	public String toString() {
		return "preset: " + preset + " store:" + store;
	}

	/** Get the preset bits */
	@Override
	protected byte presetBits() {
		return (byte)(recallOrStore() | (preset & 0x0f));
	}

	/** Get recall or store bit */
	private int recallOrStore() {
		return store ? 0x40 : 0x20;
	}
}
