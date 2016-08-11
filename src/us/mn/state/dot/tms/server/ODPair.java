/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;

/**
 * An O/D pair is a tuple containing an origin and destination location.
 *
 * @author Douglas Lau
 */
public class ODPair {

	/** Origin location */
	private final GeoLoc origin;

	/** Get the origin location */
	public GeoLoc getOrigin() {
		return origin;
	}

	/** Destination location */
	private final GeoLoc destination;

	/** Get the destination location */
	public GeoLoc getDestination() {
		return destination;
	}

	/** Is the destination a "turn" */
	private final boolean turn;

	/** Check if the destination is a "turn" */
	public boolean hasTurn() {
		return turn;
	}

	/** Create a new O/D pair */
	public ODPair(GeoLoc o, GeoLoc d, boolean t) {
		origin = o;
		destination = d;
		turn = t;
	}

	/** Get a string representation */
	@Override
	public String toString() {
		return "o: " + GeoLocHelper.getDescription(origin) + ", d: " +
			GeoLocHelper.getDescription(destination);
	}

	/** Get the corridor name (if O/D on same corridor) */
	public String getCorridorName() {
		if (GeoLocHelper.isSameCorridor(origin, destination))
			return GeoLocHelper.getCorridorName(origin);
		else
			return null;
	}
}
