/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2006-2016  Minnesota Department of Transportation
 * Copyright (C) 2017       California Department of Transportation
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

import java.util.HashMap;
import java.util.Iterator;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.StationHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.units.Distance;
import us.mn.state.dot.tms.units.Interval;

import static us.mn.state.dot.tms.SystemAttrEnum.TRAVEL_TIME_MAX_MILES;
import static us.mn.state.dot.tms.units.Interval.Units.MINUTES;
import us.mn.state.dot.tms.units.Speed;
import static us.mn.state.dot.tms.units.Speed.Units.MPH;
import us.mn.state.dot.tms.utils.MultiBuilder;
import us.mn.state.dot.tms.utils.MultiString;
import us.mn.state.dot.tms.utils.TravelTimeTag;

/**
 * Travel time estimator
 *
 * @author Douglas Lau
 * @author Jacob Barde
 */
public class TravelTimeEstimator {

	/** Travel time debug log */
	static private final DebugLog TRAVEL_LOG = new DebugLog("travel");

	/** Calculate the maximum trip minute to display on the sign */
	static private int maximumTripMinutes(Distance d) {
		Speed min_trip = new Speed(
			SystemAttrEnum.TRAVEL_TIME_MIN_MPH.getInt(), MPH);
		Interval e_trip = min_trip.elapsed(d);
		return e_trip.round(MINUTES);
	}

	/** Calculate the minimum trip minute to display on the sign */
	static private int minimumTripMinutes(Distance d) {
		Speed max_trip = new Speed(SystemAttrEnum.TRAVEL_TIME_MAX_MPH.getInt(), MPH);
		Interval e_trip = max_trip.elapsed(d);
		return e_trip.round(MINUTES);
	}

	/** Round up to the next 5 minutes */
	static private int roundUp5Min(int min) {
		return ((min - 1) / 5 + 1) * 5;
	}

	/** Name to use for debugging */
	private final String name;

	/** Origin location */
	private final GeoLoc origin;

	/** Mapping of station IDs to routes */
	private final HashMap<String, Route> s_routes =
		new HashMap<String, Route>();

	/** Create a new travel time estimator */
	public TravelTimeEstimator(String n, GeoLoc o) {
		name = n;
		origin = o;
	}

	/** Replace travel time tags in a MULTI string.
	 * @param trav MULTI string to parse.
	 * @return MULTI string with tags replaced; null on bad route. */
	public String replaceTravelTimes(String trav) {
		MultiString multi = new MultiString(trav);
		TravelCallback cb = new TravelCallback();
		multi.parse(cb);
		if (cb.isChanged()) {
			cb.clear();
			multi.parse(cb);
		}
		if (cb.valid)
			return cb.toString();
		else
			return null;
	}

	/** MultiBuilder for replacing travel time tags */
	private class TravelCallback extends MultiBuilder {

		/* If all routes are on the same corridor, when the
		 * "OVER X" form is used, it must be used for all
		 * destinations. So, first we must calculate the times
		 * for each destination. Then, determine if the "OVER"
		 * form should be used. After that, replace the travel
		 * time tags with the selected values. */
		private boolean any_over = false;
		private boolean all_over = false;
		private boolean any_under = false;
		private boolean all_under = false;

		private boolean valid = true;

		/** Add a travel time destination */
		@Override
		public void addTravelTime(TravelTimeTag tt)
		{
			String wp1 = null;
			String wp2 = null;
			Route r;
			valid = tt.isValid();

			if (tt.hasOrigin()) {
				Iterator<String> i = tt.getWayPointStations().iterator();
				do {
					wp1 = wp2;
					wp2 = i.next();
					if (wp1 == null || wp2 == null)
						continue;
					r = lookupRoute(wp2, wp1);
					testRouteLeg(r, tt);
					if (!valid)
						break;
					if ((tt.getDistance(Distance.Units.MILES) + r.getDistance().asFloat(
						Distance.Units.MILES) > TRAVEL_TIME_MAX_MILES.getFloat())) {
						valid = false;
						break;
					}
					tt.addRoute(r);
				} while (i.hasNext());

			} else if (tt.isValid()) {
				r = lookupRoute(tt.getDestinationStation());
				testRouteLeg(r, tt);
				if (valid)
					tt.addRoute(r);
			}

			if (!valid || !tt.isValid()) {
				logTravel("NO ROUTE TO " + tt.getDestinationStation());
				valid = false;
			} else {
				addTravelTimeOverUnder(tt);
			}
		}

		/** Add a travel time for a route */
		private void testRouteLeg(Route r, TravelTimeTag tt)
		{
			boolean final_dest = isFinalDest(r);
			try {
				tt.addCalculatedTime(calculateTravelTime(r, final_dest));
				tt.addSlowestTime(maximumTripMinutes(r.getDistance()));
				tt.addFastestTime(minimumTripMinutes(r.getDistance()));
			}
			catch (BadRouteException e) {
				logTravel("BAD ROUTE, " + e.getMessage());
				valid = false;
			}
		}

		/** Add a travel time */
		private void addTravelTimeOverUnder(TravelTimeTag tt)
		{
			boolean over = tt.getCalculatedTime(true) > tt.getSlowestTime(true);
			boolean under = tt.useUnderMode() && tt.getCalculatedTime(true) < tt.getFastestTime(true);

			if (over)
				any_over = true;
			else if (under)
				any_under = true;

			if (over || all_over)
				addOverLimit(tt);
			else if (under || all_under)
				addUnderLimit(tt);
			else
				addSpan(String.valueOf(tt.getCalculatedTime()));
		}

		/** Add over limit travel time */
		private void addOverLimit(TravelTimeTag tt)
		{
			String lim = String.valueOf(roundUp5Min(tt.getSlowestTime()));
			switch (tt.getOverMode()) {
			case prepend:
				addSpan(tt.getOverText() + lim);
				break;
			case append:
				addSpan(lim + tt.getOverText());
				break;
			default:
				valid = false;
				break;
			}
		}

		/** Add under limit travel time */
		private void addUnderLimit(TravelTimeTag tt) {
			if (!tt.useUnderMode())
				return;

			String lim = String.valueOf(tt.getFastestTime());
			switch (tt.getUnderMode()) {
			case prepend:
				addSpan(tt.getUnderText() + lim);
				break;
			case append:
				addSpan(lim + tt.getUnderText());
				break;
			default:
				valid = false;
				break;
			}
		}

		/** Check if the callback has changed formatting mode */
		private boolean isChanged() {
			all_over = any_over && isSingleCorridor();
			all_under = any_under && isSingleCorridor();
			return all_over || all_under;
		}
	}

	/** Lookup a route by station ID */
	private Route lookupRoute(String sid) {
		if (!s_routes.containsKey(sid)) {
			Route r = createRoute(sid);
			if (r != null)
				s_routes.put(sid, r);
		}
		return s_routes.get(sid);
	}

	/** Lookup a route by station ID */
	private Route lookupRoute(String d_sid, String o_sid) {
		if (!s_routes.containsKey(d_sid)) {
			Route r = createRoute(d_sid, o_sid);
			if (r != null)
				s_routes.put(d_sid, r);
		}
		return s_routes.get(d_sid);
	}

	/** Create one route to a travel time destination */
	private Route createRoute(String sid) {
		Station s = StationHelper.lookup(sid);
		return (s != null) ? createRoute(s) : null;
	}

	/** Create one route to a travel time destination from an origin */
	private Route createRoute(String sid, String o_sid) {
		Station s = StationHelper.lookup(sid);
		Station os = StationHelper.lookup(o_sid);
		if (s != null && os != null)
			return createRoute(s, os);
		else
			return null;
	}

	/** Create one route to a travel time destination */
	private Route createRoute(Station s) {
		GeoLoc dest = s.getR_Node().getGeoLoc();
		return createRoute(dest);
	}

	/** Create one route to a travel time destination from an origin */
	private Route createRoute(Station s, Station os) {
		GeoLoc dest = s.getR_Node().getGeoLoc();
		GeoLoc orig = os.getR_Node().getGeoLoc();
		return createRoute(dest, orig, os.getName());
	}

	/** Create one route to a travel time destination */
	private Route createRoute(GeoLoc dest) {
		return createRoute(dest, origin, name);
	}

	/** Create one route to a travel time destination from an origin */
	private Route createRoute(GeoLoc dest, GeoLoc orig, String n) {
		RouteBuilder builder = new RouteBuilder(TRAVEL_LOG, n,
			BaseObjectImpl.corridors);
		return builder.findBestRoute(orig, dest);
	}

	/** Log a travel time error */
	private void logTravel(String m) {
		logTravel(m, name);
	}

	/** Log a travel time error */
	private void logTravel(String m, String n) {
		if (TRAVEL_LOG.isOpen())
			TRAVEL_LOG.log(n + ": " + m);
	}

	/** Check if the given route is a final destination */
	private boolean isFinalDest(Route r) {
		for (Route ro: s_routes.values()) {
			if (ro != r && isSameCorridor(r, ro) &&
			   (r.getDistance().m() < ro.getDistance().m()))
			{
				return false;
			}
		}
		return true;
	}

	/** Are two routes confined to the same single corridor */
	private boolean isSameCorridor(Route r1, Route r2) {
		if (r1 != null && r2 != null) {
			Corridor c1 = r1.getOnlyCorridor();
			Corridor c2 = r2.getOnlyCorridor();
			if (c1 != null && c2 != null)
				return c1 == c2;
		}
		return false;
	}

	/** Calculate the travel time for the given route */
	private int calculateTravelTime(Route route, boolean final_dest)
		throws BadRouteException
	{
		int m = route.getTravelTime(final_dest).floor(MINUTES);
		return m + 1;
	}

	/** Are all the routes confined to the same single corridor */
	private boolean isSingleCorridor() {
		Corridor cor = null;
		for (Route r: s_routes.values()) {
			Corridor c = r.getOnlyCorridor();
			if (null == c)
				return false;
			if (null == cor)
				cor = c;
			else if (c != cor)
				return false;
		}
		return cor != null;
	}

	/** Clear the current routes */
	public void clear() {
		s_routes.clear();
	}
}
