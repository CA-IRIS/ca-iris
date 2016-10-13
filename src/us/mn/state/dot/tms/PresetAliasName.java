/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
 * Copyright (C) 2016       California Department of Transportation
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
package us.mn.state.dot.tms;

/**
 * Preset alias name enumeration.
 * The ordinal values correspond to the records in the
 * iris.preset_alias_name look-up table.
 * NOTE: more alias names are planned for future functionality.
 *
 * @author Travis Swanston
 * @author Jacob Barde
 */
public enum PresetAliasName {

	/** Home Preset (0) */
	HOME("Home"),

	/** Night-shift Home preset (1) */
	NIGHT_SHIFT("Night-shift Home");

	/** Create a new preset alias value */
	private PresetAliasName(String a) {
		alias = a;
	}

	/** Preset alias name */
	public final String alias;

	/** Get a preset alias from an ordinal value */
	static public PresetAliasName fromOrdinal(int o) {
		for (PresetAliasName an: PresetAliasName.values()) {
			if (an.ordinal() == o)
				return an;
		}
		return null;
	}

	static public final int size = PresetAliasName.values().length;

}
