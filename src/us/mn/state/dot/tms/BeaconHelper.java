/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2014  Minnesota Department of Transportation
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

import java.util.Iterator;

/**
 * Beacon helper methods.
 *
 * @author Douglas Lau
 */
public class BeaconHelper extends BaseHelper {

	/** Disallow instantiation */
	protected BeaconHelper() {
		assert false;
	}

	/** Get a beacon iterator */
	static public Iterator<Beacon> iterator() {
		return new IteratorWrapper<Beacon>(namespace.iterator(
			Beacon.SONAR_TYPE));
	}
}
