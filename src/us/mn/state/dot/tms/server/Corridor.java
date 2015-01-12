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

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import us.mn.state.dot.tms.CorridorBase;
import us.mn.state.dot.tms.Direction;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.R_NodeHelper;

/**
 * A corridor is a collection of all R_Node objects for one roadway corridor.
 *
 * @author Douglas Lau
 */
public class Corridor extends CorridorBase {

	/** Create a new corridor */
	public Corridor(GeoLoc loc) {
		super(loc);
	}

	/** Arrange the nodes in the corridor */
	@Override
	public void arrangeNodes() {
		super.arrangeNodes();
		linkDownstream();
	}

	/** Link each node with the next downstream node in the corridor */
	protected void linkDownstream() {
		Iterator<R_Node> down = r_nodes.iterator();
		// Throw away first r_node in downstream iterator
		if (down.hasNext())
			down.next();
		for (R_Node n: r_nodes) {
			R_NodeImpl r_node = (R_NodeImpl)n;
			if (down.hasNext()) {
				R_NodeImpl d = (R_NodeImpl)down.next();
				if (r_node.hasDownstreamLink())
					r_node.addDownstream(d);
			}
		}
	}

	/** Interface to find a node on the corridor */
	static public interface NodeFinder {
		public boolean check(R_NodeImpl r_node);
	}

	/** Find an active node using a node finder callback interface */
	public R_NodeImpl findActiveNode(NodeFinder finder) {
		for (R_Node n: n_points.values()) {
			R_NodeImpl r_node = (R_NodeImpl)n;
			if (r_node.getActive() && finder.check(r_node))
				return r_node;
		}
		return null;
	}

	/** Interface to find a station on the corridor */
	static public interface StationFinder {
		public boolean check(Float m, StationImpl s);
	}

	/** Find a station using a station finder callback interface */
	protected StationImpl findStation(StationFinder finder) {
		for (Float m: n_points.keySet()) {
			assert m != null;
			R_NodeImpl n = (R_NodeImpl)n_points.get(m);
			if (n.getActive() && R_NodeHelper.isStation(n)) {
				StationImpl s = n.getStation();
				if (s != null && finder.check(m, s))
					return s;
			}
		}
		return null;
	}

	/** Create a mapping from mile points to stations */
	public TreeMap<Float, StationImpl> createStationMap() {
		final TreeMap<Float, StationImpl> stations =
			new TreeMap<Float, StationImpl>();
		findStation(new StationFinder() {
			public boolean check(Float m, StationImpl s) {
				stations.put(m, s);
				return false;
			}
		});
		return stations;
	}

	/** Calculate the distance for the given O/D pair (miles) */
	public float calculateDistance(ODPair od) throws BadRouteException {
		Float origin = calculateMilePoint(od.getOrigin());
		Float destination = calculateMilePoint(od.getDestination());
		if (origin == null || destination == null)
			throw new BadRouteException("No nodes on corridor");
		if (origin > destination) {
			throw new BadRouteException("Origin (" + origin +
				") > destin (" + destination + "), " + od);
		}
		return destination - origin;
	}

	/** Find the nearest node downstream from the given location */
	public R_NodeImpl findDownstreamNode(GeoLoc loc)
		throws BadRouteException
	{
		Float m = calculateMilePoint(loc);
		if (m == null)
			throw new BadRouteException("No nodes on corridor");
		for (Float mile: n_points.keySet()) {
			if (mile > m)
				return (R_NodeImpl)n_points.get(mile);
		}
		throw new BadRouteException("No downstream nodes");
	}

	/** Find an active node using a node finder callback (reverse order) */
	public R_NodeImpl findActiveNodeReverse(NodeFinder finder) {
		for (R_Node n: n_points.descendingMap().values()) {
			R_NodeImpl r_node = (R_NodeImpl)n;
			if (r_node.getActive() && finder.check(r_node))
				return r_node;
		}
		return null;
	}

	/** Get the IDs of all linked CD roads */
	public Iterator<String> getLinkedCDRoads() {
		HashSet<String> cds = new HashSet<String>();
		for (R_Node r_node: n_points.values()) {
			if (R_NodeHelper.isCD(r_node)) {
				GeoLoc l = r_node.getGeoLoc();
				String c = GeoLocHelper.getLinkedCorridor(l);
				if (c != null)
					cds.add(c);
			}
		}
		return cds.iterator();
	}

	/** Write out the corridor to an XML file */
	public void writeXml(Writer w, Map<String, RampMeterImpl> m_nodes)
		throws IOException
	{
		w.write("<corridor route='" + roadway + "' dir='" +
			Direction.fromOrdinal(road_dir).abbrev + "'>\n");
		for (R_Node n: r_nodes) {
			R_NodeImpl r_node = (R_NodeImpl)n;
			r_node.writeXml(w, m_nodes);
		}
		w.write("</corridor>\n");
	}

	/** Find the current bottlenecks on the corridor */
	public void findBottlenecks() {
		final TreeMap<Float, StationImpl> upstream =
			new TreeMap<Float, StationImpl>();
		findStation(new StationFinder() {
			public boolean check(Float m, StationImpl s) {
				if (s.getRollingAverageSpeed() > 0) {
					s.calculateBottleneck(m, upstream);
					upstream.put(m, s);
				} else
					s.clearBottleneck();
				s.debug();
				return false;
			}
		});
	}
}
