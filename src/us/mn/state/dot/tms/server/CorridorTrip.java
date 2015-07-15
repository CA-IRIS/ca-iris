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

import java.io.PrintStream;
import java.util.TreeMap;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.units.Distance;
import us.mn.state.dot.tms.units.Interval;

/**
 * A CorridorTrip is one "leg" (on a single corridor) of a Route.
 *
 * @author Douglas Lau
 */
public class CorridorTrip {

	/** Calculate the travel time for one link */
	static private float link_time(float start, float end, float o,
		float d, float speed)
	{
		float link = Math.min(end, d) - Math.max(start, o);
		if (link > 0)
			return link / speed;
		else
			return 0;
	}

	/** Calculate the travel time between two stations */
	static private float station_time(float m0, float m1, float[] spd,
		float o, float d)
	{
		float h = 0;
		float t = (m1 - m0) / 3;
		h += link_time(m0, m0 + t, o, d, spd[0]);
		h += link_time(m0 + t, m1 - t, o, d, (spd[0] + spd[1]) / 2);
		h += link_time(m1 - t, m1, o, d, spd[1]);
		return h;
	}

	/** Distance to use low station speed at end of trip (miles) */
	static private final float LOW_SPEED_DISTANCE = 1.0f;

	/** Maximum allowed length of a travel time link (miles) */
	static private final float MAX_LINK_LENGTH = 0.6f;

	/** Debug log */
	private final DebugLog dlog;

	/** Name to use for debugging purposes */
	private final String name;

	/** Corridor for the trip */
	private final Corridor corridor;

	/** Get the corridor */
	public Corridor getCorridor() {
		return corridor;
	}

	/** Origin/destination pair */
	private final ODPair od_pair;

	/** Milepoint of the trip origin */
	private final float origin;

	/** Milepoint of the trip destination */
	private final float destination;

	/** Mapping from mile point to station */
	private final TreeMap<Float, StationImpl> stations;

	/** Create a new corridor trip.
	 * @param dl Debug log.
	 * @param n Name (for debugging).
	 * @param c Corridor.
	 * @param od Origin-destination pair. */
	public CorridorTrip(DebugLog dl, String n, Corridor c, ODPair od)
		throws BadRouteException
	{
		dlog = dl;
		name = n;
		corridor = c;
		od_pair = od;
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
		stations = c.createStationMap();
		if (stations.isEmpty())
			throwException("No stations");
	}

	/** Throw a BadRouteException with the specified message */
	private void throwException(String msg) throws BadRouteException {
		throw new BadRouteException(msg + " (" + toString() + ")");
	}

	/** Get the trip distance.
	 * @return Distance of the corridor trip. */
	public Distance getDistance() {
		float d = destination - origin;
		return new Distance(d, Distance.Units.MILES);
	}

	/** Trip timer calculates travel times between consecutive stations */
	private class TripTimer {

		float low_mile = destination;
		float[] low = new float[2];
		float[] avg = new float[2];
		float smile = 0;
		float hours = 0;

		TripTimer(boolean final_destin) {
			if (final_destin)
				low_mile -= LOW_SPEED_DISTANCE;
		}

		void firstStation(float mile, float _avg, float _low) {
			avg[1] = _avg;
			low[1] = _low;
			smile = mile;
		}

		void nextStation(float mile, float _avg, float _low) {
			avg[0] = avg[1];
			low[0] = low[1];
			avg[1] = _avg;
			low[1] = _low;
			hours += station_time(smile, mile, avg, origin,
				low_mile);
			hours += station_time(smile, mile, low, low_mile,
				destination);
			if (dlog.isOpen()) {
				dlog.log(name + " st: " + mile + ", " + _avg +
					", " + _low + ", h: " + hours);
			}
			smile = mile;
		}
	}

	/** Check the length of a link between two milepoints */
	static private boolean checkLinkLength(float start, float end) {
		return (end - start) > (3 * MAX_LINK_LENGTH);
	}

	/** Find the trip time */
	private Interval findTripTime(TripTimer tt) throws BadRouteException {
		float avg = 0;
		float low = 0;
		float pmile = 0;
		boolean first = true;

		for (Float mile: stations.keySet()) {
			if (checkLinkLength(mile, origin))
				continue;
			if (checkLinkLength(destination, mile))
				break;
			StationImpl s = stations.get(mile);
			float _avg = s.getSmoothedAverageSpeed();
			float _low = s.getSmoothedLowSpeed();
			if (_avg <= 0 || _low <= 0)
				continue;
			avg = _avg;
			low = _low;
			if (first) {
				float mm = mile - MAX_LINK_LENGTH;
				if (mm > origin)
					throwException("Start > origin");
				tt.firstStation(mm, avg, low);
				first = false;
			} else if (checkLinkLength(pmile, mile)) {
				float llen = mile - pmile;
				throwException("Link too long (" + llen +
					") " + s);
			} else
				tt.nextStation(mile, avg, low);
			pmile = mile;
		}
		if (first)
			throwException("No speed data");
		else if (avg <= 0 || low <= 0)
			throwException("Missing destin data");
		else if (pmile < destination) {
			float mm = pmile + MAX_LINK_LENGTH;
			if (mm < destination)
				throwException("End < destin");
			tt.nextStation(mm, avg, low);
		}
		return new Interval(tt.hours, Interval.Units.HOURS);
	}

	/** Get the current travel time.
	 * @param final_dest Flag to indicate final destination.
	 * @return Travel time interval. */
	public Interval getTravelTime(boolean final_dest)
		throws BadRouteException
	{
		return findTripTime(new TripTimer(final_dest));
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
		sb.append(", st: ");
		for (StationImpl s: stations.values()) {
			sb.append(s.getName());
			sb.append(' ');
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
