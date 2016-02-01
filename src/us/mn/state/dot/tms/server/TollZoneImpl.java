/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015-2016  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.LaneType;
import us.mn.state.dot.tms.StationHelper;
import us.mn.state.dot.tms.TollZone;
import us.mn.state.dot.tms.TMSException;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;

/**
 * A toll zone is a roadway segment which is tolled by usage.
 *
 * @author Douglas Lau
 */
public class TollZoneImpl extends BaseObjectImpl implements TollZone {

	/** Toll zone debug log */
	static private final DebugLog TOLL_LOG = new DebugLog("toll");

	/** Maximum number of time steps needed for sample history */
	static private final int MAX_STEPS = 12;

	/** Magic constant to convert density to price dollars */
	static private final double ALPHA = 0.045;

	/** Magic exponent to convert density to price dollars */
	static private final double BETA = 1.10;

	/** Load all the toll zones */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, TollZoneImpl.class);
		store.query("SELECT name, start_id, end_id, tollway " +
			"FROM iris." + SONAR_TYPE + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new TollZoneImpl(
					row.getString(1),	// name
					row.getString(2),	// start_id
					row.getString(3),	// end_id
					row.getString(4)	// tollway
				));
			}
		});
	}

	/** Get a mapping of the columns */
	@Override
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("start_id", start_id);
		map.put("end_id", end_id);
		map.put("tollway", tollway);
		return map;
	}

	/** Get the database table name */
	@Override
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	@Override
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new toll zone */
	public TollZoneImpl(String n) {
		super(n);
	}

	/** Create a new toll zone */
	protected TollZoneImpl(String n, String sid, String eid, String tw) {
		this(n);
		start_id = sid;
		end_id = eid;
		tollway = tw;
	}

	/** Starting station ID */
	private String start_id;

	/** Set the starting station ID */
	@Override
	public void setStartID(String sid) {
		start_id = sid;
	}

	/** Set the starting station ID */
	public void doSetStartID(String sid) throws TMSException {
		if (!stringEquals(sid, start_id)) {
			store.update(this, "start_id", sid);
			setStartID(sid);
		}
	}

	/** Get the starting station ID */
	@Override
	public String getStartID() {
		return start_id;
	}

	/** Ending station ID */
	private String end_id;

	/** Set the ending station ID */
	@Override
	public void setEndID(String eid) {
		end_id = eid;
	}

	/** Set the ending station ID */
	public void doSetEndID(String eid) throws TMSException {
		if (!stringEquals(eid, end_id)) {
			store.update(this, "end_id", eid);
			setEndID(eid);
		}
	}

	/** Get the ending station ID */
	@Override
	public String getEndID() {
		return end_id;
	}

	/** Tollway ID */
	private String tollway;

	/** Set the tollway ID */
	@Override
	public void setTollway(String tw) {
		tollway = tw;
	}

	/** Set the tollway ID */
	public void doSetTollway(String tw) throws TMSException {
		if (!stringEquals(tw, tollway)) {
			store.update(this, "tollway", tw);
			setTollway(tw);
		}
	}

	/** Get the tollway ID */
	@Override
	public String getTollway() {
		return tollway;
	}

	/** Density history for one detector */
	static private class DensityHist {

		/** Density history for 6 minutes */
		private final BoundedSampleHistory hist =
			new BoundedSampleHistory(MAX_STEPS);

		/** Current density */
		private Double density;

		/** Update the density history.
		 * @param np New period.
		 * @param k Current density. */
		void updateDensity(boolean np, double k) {
			hist.push((k >= 0) ? k : null);
			if (np)
				density = hist.average();
		}
	}

	/** Mapping of density history for all detectors */
	private transient final HashMap<DetectorImpl, DensityHist> k_hist =
		new HashMap<DetectorImpl, DensityHist>();

	/** Lookup all HOT detectors in a route.
	 * @param r The route.
	 * @return Set of all HOT detectors in the route. */
	private SamplerSet lookupDetectors(Route r) {
		if (r != null)
			return r.getSamplerSet(LaneType.HOT);
		else {
			if (isLogging()) {
				log("No route from " + start_id + " to " +
				    end_id);
			}
		      	return new SamplerSet();
		}
	}

	/** Build the route for the whole toll zone */
	private Route buildRoute() {
		GeoLoc o = StationHelper.lookupGeoLoc(start_id);
		if (o != null)
			return buildRoute(o);
		else {
			if (isLogging())
				log("Invalid zone start: " + start_id);
			return null;
		}
	}

	/** Build the route from an origin.
	 * @param o Origin geo location.
	 * @return Route from origin to end of zone, or null */
	private Route buildRoute(GeoLoc o) {
		GeoLoc d = StationHelper.lookupGeoLoc(end_id);
		if (d != null)
			return buildRoute(o, d);
		else {
			if (isLogging())
				log("Invalid zone end: " + end_id);
			return null;
		}
	}

	/** Build a route from an origin to a destination.
	 * @param o Origin geo location.
	 * @param d Destination geo location.
	 * @return Route from origin to destination, or null */
	private Route buildRoute(GeoLoc o, GeoLoc d) {
		RouteBuilder builder = new RouteBuilder(TOLL_LOG, name,
			BaseObjectImpl.corridors);
		return builder.findBestRoute(o, d);
	}

	/** Update density.
	 * @param np New pricing period (if true). */
	public synchronized void updateDensity(boolean np) {
		for (Map.Entry<DetectorImpl, DensityHist> e: k_hist.entrySet()){
			double k = e.getKey().getDensity();
			e.getValue().updateDensity(np, k);
		}
	}

	/** Get the current toll zone price.
	 * @param lbl Sign label for logging.
	 * @param o Origin (location of DMS).
	 * @return Price (dollars). */
	public float getPrice(String lbl, GeoLoc o) {
		updateDensityHistory(lbl);
		SamplerSet ss = lookupDetectors(buildRoute(o));
		if (isLogging())
			log(lbl + " use detectors: " + ss);
		Double k_hot = findMaxDensity(ss);
		float price = calculatePricing(k_hot);
		if (isLogging())
			log(lbl + " k_hot: " + k_hot + ", price: $" + price);
		return price;
	}

	/** Update density history for all detectors in the toll zone */
	private void updateDensityHistory(String lbl) {
		SamplerSet ss = lookupDetectors(buildRoute());
		if (isLogging())
			log(lbl + " all detectors: " + ss);
		synchronized (this) {
			removeHistoryMappings(ss);
			addHistoryMappings(ss);
		}
	}

	/** Remove mappings from k_hist if not in sampler set */
	private void removeHistoryMappings(SamplerSet ss) {
		Iterator<DetectorImpl> it = k_hist.keySet().iterator();
		while (it.hasNext()) {
			if (!ss.hasDetector(it.next()))
				it.remove();
		}
	}

	/** Add mappings from sampler set if they don't exist */
	private void addHistoryMappings(SamplerSet ss) {
		for (DetectorImpl det: ss.getAll()) {
			if (!k_hist.containsKey(det))
				k_hist.put(det, new DensityHist());
		}
	}

	/** Find the max density within a sampler set */
	private synchronized Double findMaxDensity(SamplerSet ss) {
		Double k_hot = null;
		for (Map.Entry<DetectorImpl, DensityHist> e: k_hist.entrySet()){
			if (ss.hasDetector(e.getKey())) {
				Double k = e.getValue().density;
				if (k_hot == null || (k != null && k > k_hot))
					k_hot = k;
			}
		}
		return k_hot;
	}

	/** Calculate the pricing */
	private float calculatePricing(Double k_hot) {
		if (k_hot != null) {
			/* This was arrived at by using a least squares fit */
			double price = ALPHA * Math.pow(k_hot, BETA);
			int quarters = (int) Math.round(price * 4);
			return quarters / 4.0f;
		} else
			return 0;
	}

	/** Check if we're logging */
	private boolean isLogging() {
		return TOLL_LOG.isOpen();
	}

	/** Log a toll zone message */
	private void log(String m) {
		TOLL_LOG.log(name + ": " + m);
	}
}
