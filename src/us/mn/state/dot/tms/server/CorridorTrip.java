/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2016  Minnesota Department of Transportation
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

import java.util.ArrayList;
import us.mn.state.dot.tms.LaneType;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.units.Distance;

/**
 * A CorridorTrip is one "leg" (on a single corridor) of a Route.
 *
 * @author Douglas Lau
 */
public class CorridorTrip {

	/** Corridor for the trip */
	public final Corridor corridor;

	/** Milepoint of the trip origin */
	public final float origin;

	/** Milepoint of the trip destination */
	public final float destination;

	/** Create a new corridor trip.
	 * @param c Corridor.
	 * @param od Origin-destination pair. */
	public CorridorTrip(Corridor c, ODPair od) throws BadRouteException {
		corridor = c;
		if (!c.getName().equals(od.getCorridorName()))
			throwException("Bad trip");
		Float o = c.calculateMilePoint(od.getOrigin());
		Float d = c.calculateMilePoint(od.getDestination());
		if (o == null || d == null)
			throwException("No nodes on corridor");
		origin = o;
		destination = d;
		if (origin > destination)
			throwException("Origin > destination");
	}

	/** Throw a BadRouteException with the specified message */
	public void throwException(String msg) throws BadRouteException {
		throw new BadRouteException(msg + " (" + toString() + ")");
	}

	/** Get the trip distance.
	 * @return Distance of the corridor trip. */
	public Distance getDistance() {
		float d = destination - origin;
		return new Distance(d, Distance.Units.MILES);
	}

	/** Lookup detectors on a corridor trip */
	public ArrayList<DetectorImpl> lookupDetectors(final LaneType lt) {
		final ArrayList<DetectorImpl> dets =
			new ArrayList<DetectorImpl>();
		corridor.findStation(new Corridor.StationFinder() {
			public boolean check(Float m, StationImpl s) {
				if (isWithinTrip(m))
					dets.addAll(lookupDets(s, lt));
				return false;
			}
		});
		return dets;
	}

	/** Check if a milepoint is within the trip */
	private boolean isWithinTrip(float m) {
		return m > origin && m <= destination;
	}

	/** Lookup the detectors for one station and lane type */
	private ArrayList<DetectorImpl> lookupDets(StationImpl s,
		final LaneType lt)
	{
		DetectorSet ds = lookupDetectors(s);
		return ds.filter(new DetectorSet.Filter() {
			public boolean check(DetectorImpl d) {
				return lt.ordinal() == d.getLaneType();
			}
		});
	}

	/** Lookup the detectors for one station */
	private DetectorSet lookupDetectors(StationImpl s) {
		R_Node n = s.getR_Node();
		if (n instanceof R_NodeImpl) {
			R_NodeImpl rn = (R_NodeImpl)n;
			return rn.getDetectorSet();
		} else
			return new DetectorSet();
	}

	/** Get a string representation of the trip */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("trip: ");
		sb.append(corridor.getName());
		sb.append(", o: ");
		sb.append(origin);
		sb.append(", d: ");
		sb.append(destination);
		return sb.toString();
	}
}
