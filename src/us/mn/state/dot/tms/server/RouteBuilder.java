/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2013  Minnesota Department of Transportation
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

import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.units.Distance;

/**
 * A route builder builds a set of routes.
 *
 * @author Douglas Lau
 */
public class RouteBuilder {

	/** Check if we're logging */
	static private boolean isLogging() {
		return TravelTime.isLogging();
	}

	/** Log a message to the travel debug log */
	static private void log(String msg) {
		TravelTime.log(msg);
	}

	/** Maximum distance from origin to a corridor node (in meters) */
	static private final float MAX_ORIGIN_M = 1000;

	/** Maximum number of R_Nodes to follow on a corridor */
	static private final int MAX_R_NODE_LIMIT = 100;

	/** Name to use for debugging purposes */
	private final String name;

	/** Corridor manager */
	private final CorridorManager corridors;

	/** Maximum number of corridor legs */
	private final int legs = SystemAttrEnum.TRAVEL_TIME_MAX_LEGS.getInt();

	/** Maximum route distance (miles) */
	private float max_mi = SystemAttrEnum.TRAVEL_TIME_MAX_MILES.getInt();

	/** Working path */
	private final LinkedList<ODPair> path = new LinkedList<ODPair>();

	/** Set of all routes built */
	private final TreeSet<Route> routes = new TreeSet<Route>();

	/** Create a new route builder.
	 * @param n Name (for debugging).
	 * @param c Corridor manager. */
	public RouteBuilder(String n, CorridorManager c) {
		name = n;
		corridors = c;
	}

	/** Search a corridor for branching paths to a destination.
	 * @param distance Distance.
	 * @param origin Route origin.
	 * @param destination Route destination.
	 * @throws BadRouteException if route cannot be found. */
	private void searchCorridor(float distance, GeoLoc origin,
		GeoLoc destination) throws BadRouteException
	{
		String cid = GeoLocHelper.getCorridorName(origin);
		if(cid == null) {
			log(name + ": BAD ORIGIN: " + origin.getName());
			return;
		}
		Corridor c = corridors.getCorridor(cid);
		if(c == null) {
			log(name + ": MISSING CORRIDOR: " + cid);
			return;
		}
		R_NodeImpl r_node = c.findDownstreamNode(origin);
		Distance m = Corridor.nodeDistance(r_node, origin);
		if(m == null || m.m() > MAX_ORIGIN_M) {
			throw new BadRouteException("ORIGIN OFF MAINLINE: " +
				GeoLocHelper.getDescription(origin));
		}
		int i = 0;
		while(r_node != null) {
			GeoLoc dest = r_node.getGeoLoc();
			ODPair od = new ODPair(origin, dest, false);
			float dist = distance + c.calculateDistance(od);
			if(isLogging()) {
				log(name + ": SEARCH FOR " +
					GeoLocHelper.getDescription(destination)
					+ " (" + i + ", " + dist + " miles) " +
					od);
			}
			if(dist > max_mi) {
				if(isLogging()) {
					log(name + ": MAX DISTANCE (" + max_mi +
						") EXCEEDED");
				}
				break;
			}
			i++;
			if(i > MAX_R_NODE_LIMIT) {
				if(isLogging()) {
					log(name + ": BREAKING R_NODE LOOP AT "+
						r_node.getName());
				}
				break;
			}
			r_node = findNextNode(c, r_node, dist, origin,
				destination);
		}
	}

	/** Find the next node on the corridor.
	 * @param c Corridor.
	 * @param r_node Roadway node.
	 * @param distance Distance.
	 * @param origin Route origin.
	 * @param destination Route destination.
	 * @return Next roadway node.
	 * @throws BadRouteException on route error. */
	private R_NodeImpl findNextNode(Corridor c, R_NodeImpl r_node,
		float distance, GeoLoc origin, GeoLoc destination)
		throws BadRouteException
	{
		GeoLoc dest = r_node.getGeoLoc();
		R_NodeImpl next = null;
		for(R_NodeImpl n: r_node.getDownstream()) {
			if(!r_node.isCorridorType())
				continue;
			GeoLoc down = n.getGeoLoc();
			if(GeoLocHelper.isSameCorridor(down, origin))
				next = n;
			else {
				boolean turn = r_node.hasTurnPenalty()
					&& n.hasTurnPenalty();
				path.add(new ODPair(origin, dest, turn));
				findPaths(distance, down, destination);
				path.removeLast();
			}
		}
		return next;
	}

	/** Debug a route exception.
	 * @param e Bad route exception. */
	private void debugRouteException(BadRouteException e) {
		if(isLogging())
			log(name + ": BAD ROUTE: " + e.getMessage());
	}

	/** Find all paths from an origin to a destination.
	 * @param distance Distance.
	 * @param origin Route origin.
	 * @param destination Route destination. */
	private void findPaths(float distance, final GeoLoc origin,
		final GeoLoc destination)
	{
		ODPair od = new ODPair(origin, destination, false);
		Corridor c = corridors.getCorridor(od);
		if(c != null) {
			try {
				float d = c.calculateDistance(od);
				if(distance + d < max_mi)
					buildRoute(od);
			}
			catch(BadRouteException e) {
				debugRouteException(e);
			}
		}
		if(path.size() < legs) {
			try {
				searchCorridor(distance, origin, destination);
			}
			catch(BadRouteException e) {
				debugRouteException(e);
			}
		}
	}

	/** Build a route from the current path.
	 * @param odf Origin / destination pair.
	 * @throws BadRouteException on route error. */
	private void buildRoute(ODPair odf) throws BadRouteException {
		Route r = new Route(name);
		int turns = 0;
		for(ODPair od: path) {
			Corridor c = corridors.getCorridor(od);
			r.addTrip(new CorridorTrip(name, c, od));
			if(od.hasTurn())
				turns++;
		}
		r.setTurns(turns);
		Corridor c = corridors.getCorridor(odf);
		r.addTrip(new CorridorTrip(name, c, odf));
		routes.add(r);
		// NOTE: this optimisation will prevent us from finding some
		// secondary routes; we're only interested in the best route.
		max_mi = Math.min(max_mi, r.getGoodness());
		if(isLogging()) {
			GeoLoc dest = odf.getDestination();
			log(name + ": FOUND ROUTE TO " +
				GeoLocHelper.getDescription(dest) + ", " + r);
			if(max_mi == r.getGoodness())
				log(name + ": LOWERED MAX DIST TO " + max_mi);
		}
	}

	/** Find all the routes from an origin to a destination.
	 * @param o Route origin.
	 * @param d Route destination.
	 * @return Sorted set of routes. */
	public SortedSet<Route> findRoutes(GeoLoc o, GeoLoc d) {
		routes.clear();
		path.clear();
		findPaths(0, o, d);
		return routes;
	}
}
