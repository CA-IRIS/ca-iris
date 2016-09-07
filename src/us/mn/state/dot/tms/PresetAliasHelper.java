/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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

import java.lang.Integer;
import java.util.Iterator;

/**
 * Camera preset alias helper methods.
 *
 * @author Travis Swanston
 */
public class PresetAliasHelper extends BaseHelper {

	/** Disallow instantiation */
	protected PresetAliasHelper() {
		assert false;
	}

	/** Get a PresetAlias iterator */
	static public Iterator<PresetAlias> iterator() {
		return new IteratorWrapper<PresetAlias>(namespace.iterator(
			PresetAlias.SONAR_TYPE));
	}

	/**
	 * Get the current preset mapping for a given camera and preset alias
	 * name.
	 *
	 * @param c the Camera
	 * @param pan the PresetAliasName
	 * @return the mapped preset number, or null if none exists.
	 */
	static public Integer getPreset(Camera c, PresetAliasName pan) {
		int alias = pan.ordinal();
		Iterator<PresetAlias> it = iterator();
		while (it.hasNext()) {
			PresetAlias pa = it.next();
			if ((pa.getCamera() == c) &&
				(pa.getPresetAliasName() == alias)) {
				return Integer.valueOf(pa.getPresetNum());
			}
		}
		return null;
	}

	/**
	 * Determine if a camera has a preset enabled
	 * @param c the camera
	 * @return
	 */
	static public boolean hasShiftPreset(Camera c, PresetAliasName pan) {
		return getPreset(c, pan) != null ? true : false;
	}
}
