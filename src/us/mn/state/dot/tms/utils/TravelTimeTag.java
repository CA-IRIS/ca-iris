/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2017  California Department of Transportation
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
package us.mn.state.dot.tms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import us.mn.state.dot.tms.server.Route;
import us.mn.state.dot.tms.units.Distance;

import static us.mn.state.dot.tms.SystemAttrEnum.TRAVEL_TIME_UNIT_TEXT;
import static us.mn.state.dot.tms.utils.SString.emptyBecomesNull;
import static us.mn.state.dot.tms.utils.SString.isBlank;

/**
 * TravelTimeTag is for holding values pertaining to Travel Times that are
 * frequently passed around.
 *
 * @author Jacob Barde
 */
public class TravelTimeTag {
	/** default over mode */
	static public final Multi.OverLimitMode DEF_OVER_MODE = Multi.OverLimitMode.prepend;

	/** default over text */
	static public final String DEF_OVER_TEXT = "OVER ";

	/** default under mode */
	static public final Multi.OverLimitMode DEF_UNDER_MODE = Multi.OverLimitMode.prepend;

	/** default under text */
	static public final String DEF_UNDER_TEXT = "UNDER ";

	/** argument delimiter */
	static public final String ARG_DELIM = ",";

	/** key-value delimiter */
	static public final String KV_DELIM = "=";

	/** way-point delimiter */
	static public final String WP_DELIM = "\\s+";

	/** dest sid key */
	static public final String WAY_POINTS = "wp";

	/** over mode key */
	static public final String OVER_MODE = "om";

	/** over text key */
	static public final String OVER_TEXT = "ot";

	/** under mode key */
	static public final String UNDER_MODE = "um";

	/** under text key */
	static public final String UNDER_TEXT = "ut";

	/** unit text key */
	static public final String UNIT_TEXT = "unit";

	/** way-point station ids.
	 * last station id is always the destination.
	 * if 2 or more way points, first is considered origin station */
	private List<String> wayPointStations;

	/** OverLimitMode for over threshold */
	private Multi.OverLimitMode overMode;

	/** over threshold text */
	private String overText;

	/** OverLimitMode for under threshold */
	private Multi.OverLimitMode underMode;

	/** under threshold text */
	private String underText;

	/** unit text */
	private String unitText;

	/** routes */
	private List<Route> routes;

	/** slowest time allowed */
	private int slowestTime;

	/** accumulated slowest time allowed */
	private List<Integer> accSlow = new ArrayList<>();

	/** fastest time allowed */
	private int fastestTime;

	/** accumulated fastest time allowed */
	private List<Integer> accFast = new ArrayList<>();

	/** calculated time */
	private int calculatedTime;

	/** accumulated calculated time */
	private List<Integer> accCalc = new ArrayList<>();

	/** Constructor */
	private TravelTimeTag() {
		wayPointStations = new ArrayList<>();
		overMode = DEF_OVER_MODE;
		overText = DEF_OVER_TEXT;
		underMode = null;
		underText = null;
		routes = new ArrayList<>();
		slowestTime = 0;
		fastestTime = 0;
		calculatedTime = 0;
		unitText = TRAVEL_TIME_UNIT_TEXT.getString();
	}

	/** Constructor */
	public TravelTimeTag(String d_sid) {
		this();
		assert d_sid != null;
		assert !"".equals(d_sid.trim());
		setWayPointStations(d_sid);
	}

	/** Constructor */
	public TravelTimeTag(String d_sid, Multi.OverLimitMode o_mode, String o_txt) {
		this(d_sid);
		overMode = o_mode;
		overText = o_txt;
	}

	/** Set the station. */
	public String getDestinationStation() {
		return wayPointStations.get((wayPointStations.size()-1));
	}

	/** get way-points as a string */
	private String getWayPointStationsStr() {
		StringBuilder rv = new StringBuilder();
		for (String s : getWayPointStations()) {
			rv.append(s).append(" ");
		}
		return rv.toString().trim();
	}

	/** get a list of way-point stations. */
	public List<String> getWayPointStations() {
		return wayPointStations;
	}

	/** set a list of way-point stations from string. */
	private void setWayPointStations(String wps) throws IllegalArgumentException {
		if (isBlank(emptyBecomesNull(wps)))
			throw new IllegalArgumentException("Nulls or empty way-point strings not allowed");
		String w = wps.trim();
		wayPointStations = Arrays.asList(w.split(WP_DELIM));
	}

	/** do we have an destination sid */
	public boolean hasDest() {
		return !wayPointStations.isEmpty();
	}

	/** Set the overMode field value. */
	public Multi.OverLimitMode getOverMode() {
		return overMode;
	}

	/** Set the overMode field value. */
	private void setOverMode(Multi.OverLimitMode om) {
		overMode = om;
	}

	/** Set the overText field value. */
	public String getOverText() {
		return overText;
	}

	/** Set the overText field value. */
	private void setOverText(String ot) {
		overText = ot;
	}

	/** Set the origin station. */
	public String getOriginStation() {
		return wayPointStations.get(0);
	}

	/** do we have an origin sid */
	public boolean hasOrigin() {
		return (wayPointStations.size() > 1);
	}

	/** Set the underMode field value. */
	public Multi.OverLimitMode getUnderMode() {
		return underMode;
	}

	/** Set the underMode field value. */
	private void setUnderMode(Multi.OverLimitMode um) {
		underMode = um;
		if (um != null && underText == null)
			underText = DEF_UNDER_TEXT;
	}

	/** use under mode? */
	public boolean useUnderMode() {
		return underMode != null;
	}

	/** Set the underText field value. */
	public String getUnderText() {
		return underText;
	}

	/** Set the underText field value. */
	private void setUnderText(String ut) {
		underText = ut;
		if (ut != null && underMode == null)
			underMode = DEF_UNDER_MODE;
	}

	/** has under text */
	public boolean hasUnderText() {
		return underText != null;
	}

	/** Set the unitText field value. */
	public String getUnitText() {
		return (unitText != null) ? unitText : "";
	}

	/** Set the unitText field value. */
	public void setUnitText(String unit) {
		unitText = unit;
	}

	/** get route(s) combined distance */
	public float getDistance(Distance.Units units) {
		float rv = 0.0F;
		for (Route r : routes) {
			rv += r.getDistance().asFloat(units);
		}
		return rv;
	}
	/** Set the routes field value. */
	public List<Route> getRoutes() {
		return routes;
	}

	/** Set the routes field value. */
	public void setRoutes(List<Route> r) {
		routes = r;
	}

	public void addRoute(Route r) {
		routes.add(r);
	}

	/** Set the slowestTime field value. */
	public int getSlowestTime() {
		return slowestTime;
	}

	/** get the slowest time
	 * @param accumulated boolean.
	 *                    true to get accumulated time,
	 *                    false to get the regular [latest] value.
	 * @return integer value
	 */
	public int getSlowestTime(boolean accumulated) {
		if (!accumulated)
			return getSlowestTime();

		Integer rv = 0;
		for (Integer i : accSlow)
			if (i != null)
				rv += i;
		return rv;
	}

	/** Set the slowestTime field value. */
	private void setSlowestTime(int st) {
		slowestTime = st;
	}

	/** add and set slowest time */
	public void addSlowestTime(int ft) {
		accSlow.add(ft);
		setSlowestTime(ft);
	}

	/** Set the fastestTime field value. */
	public int getFastestTime() {
		return fastestTime;
	}

	/** get the fastest time
	 * @param accumulated boolean.
	 *                    true to get accumulated time,
	 *                    false to get the regular [latest] value.
	 * @return integer value
	 */
	public int getFastestTime(boolean accumulated) {
		if (!accumulated)
			return getFastestTime();

		Integer rv = 0;
		for (Integer i : accFast)
			if (i != null)
				rv += i;
		return rv;
	}

	/** Set the fastestTime field value. */
	private void setFastestTime(int ft) {
		fastestTime = ft;
	}

	/** add and set the fastest time */
	public void addFastestTime(int ft) {
		accFast.add(ft);
		setFastestTime(ft);
	}

	/** Set the calculatedTime field value. */
	public int getCalculatedTime() {
		return calculatedTime;
	}

	/** get the calculated time time
	 * @param accumulated boolean.
	 *                    true to get accumulated time,
	 *                    false to get the regular [latest] value.
	 * @return integer value
	 */
	public int getCalculatedTime(boolean accumulated) {
		if (!accumulated)
			return getCalculatedTime();

		Integer rv = 0;
		for (Integer i : accCalc)
			if (i != null)
				rv += i;
		return rv;
	}


	/** Set the calculatedTime field value. */
	private void setCalculatedTime(int ct) {
		calculatedTime = ct;
	}

	/** add and set calculated time */
	public void addCalculatedTime(int ct) {
		accCalc.add(ct);
		setCalculatedTime(ct);
	}

	/** is this object valid? */
	public boolean isValid() {
		if (!hasDest())
			return false;
		if (null == overMode || null == overText)
			return false;
		if ((underMode != null && underText == null) || (underText != null && underMode == null))
			return false;
		return true;
	}

	/** is extended tag arguments */
	public boolean isExtended() {
		return (isValid() && (hasOrigin() || (useUnderMode() && hasUnderText())));
	}

	/** create a string for use as arguments in a tag */
	static public String mapToArgs(TravelTimeTag tt) {
		if (!tt.isValid())
			return null;

		StringBuilder rv = new StringBuilder("");
		if (tt.isExtended()) {
			rv.append(WAY_POINTS).append(KV_DELIM).append(tt.getWayPointStationsStr());
			if (!DEF_OVER_MODE.equals(tt.getOverMode()))
				rv.append(ARG_DELIM).append(OVER_MODE).append(KV_DELIM).append(tt.getOverMode());
			if (!DEF_OVER_TEXT.equals(tt.getOverText()))
				rv.append(ARG_DELIM).append(OVER_TEXT).append(KV_DELIM).append(tt.getOverText());
			if (tt.useUnderMode()) {
				rv.append(ARG_DELIM).append(UNDER_MODE).append(KV_DELIM).append(tt.getUnderMode());
				if (tt.hasUnderText() && !DEF_UNDER_TEXT.equals(tt.getUnderText()))
					rv.append(ARG_DELIM).append(UNDER_TEXT).append(KV_DELIM).append(tt.getUnderText());
			}
			if (!isBlank(emptyBecomesNull(tt.getUnitText()))
				&& !TRAVEL_TIME_UNIT_TEXT.getString().equals(tt.getUnitText()))
				rv.append(ARG_DELIM).append(UNIT_TEXT).append(KV_DELIM).append(tt.getUnitText());
		} else {
			rv.append(tt.getWayPointStationsStr());
			if (!DEF_OVER_MODE.equals(tt.getOverMode()))
				rv.append(ARG_DELIM).append(tt.getOverMode());
			if (!DEF_OVER_TEXT.equals(tt.getOverText()))
				rv.append(ARG_DELIM).append(tt.getOverText());
		}

		return rv.toString();
	}

	/** map tag string arguments to a new TravelTimeTag object */
	static public TravelTimeTag mapTo(String v) {
		String[] args = v.split(ARG_DELIM, 6);
		TravelTimeTag rv;
		if (!v.contains("=") && args.length > 0 && args.length <= 3) {
			rv = new TravelTimeTag(args[0]);
			rv.setOverMode(DEF_OVER_MODE);
			rv.setOverText(DEF_OVER_TEXT);
			if (args.length > 1)
				rv.setOverMode(parseMode(args[1], DEF_OVER_MODE));
			if (args.length > 2)
				rv.setOverText(args[2]);
		} else {
			rv = new TravelTimeTag();
			for (String s : args) {
				String[] kv = s.split(KV_DELIM,2);
				if (null != kv[0] && !"".equals(kv[0].trim())) {
					if (WAY_POINTS.equals(kv[0]))
						rv.setWayPointStations(kv[1].trim());
					else if (OVER_MODE.equals(kv[0]))
						rv.setOverMode(parseMode(kv[1].trim(), DEF_OVER_MODE));
					else if (OVER_TEXT.equals(kv[0]))
						rv.setOverText(kv[1]);
					else if (UNDER_MODE.equals(kv[0]))
						rv.setUnderMode(parseMode(kv[1].trim(), DEF_UNDER_MODE));
					else if (UNDER_TEXT.equals(kv[0]))
						rv.setUnderText(kv[1]);
					else if (UNIT_TEXT.equals(kv[0]))
						rv.setUnitText(kv[1]);
				}
			}
		}
		return rv;
	}

	/** Parse a over limit mode value */
	static private Multi.OverLimitMode parseMode(String mode, Multi.OverLimitMode def) {
		for (Multi.OverLimitMode m : Multi.OverLimitMode.values()) {
			if (mode.equals(m.toString()))
				return m;
		}
		return def;
	}
}
