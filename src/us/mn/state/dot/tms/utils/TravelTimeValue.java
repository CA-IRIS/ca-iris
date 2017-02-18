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

import us.mn.state.dot.tms.server.Route;

import static us.mn.state.dot.tms.utils.SString.emptyBecomesNull;
import static us.mn.state.dot.tms.utils.SString.isBlank;

/**
 * TravelTimeValue is for holding values pertaining to Travel Times that are
 * frequently passed around.
 *
 * @author Jacob Barde
 */
public class TravelTimeValue {
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

	/** dest sid key */
	static public final String DEST_SID = "d_sid";
	/** origin sid key */
	static public final String ORIG_SID = "o_sid";
	/** over mode key */
	static public final String OVER_MODE = "o_mode";
	/** over text key */
	static public final String OVER_TEXT = "o_txt";
	/** under mode key */
	static public final String UNDER_MODE = "u_mode";
	/** under text key */
	static public final String UNDER_TEXT = "u_txt";

	/** origin station id */
	private String argOriginStationId;

	/** destination station id */
	private String argDestStationId;

	/** OverLimitMode for over threshold */
	private Multi.OverLimitMode argOverMode;

	/** over threshold text */
	private String argOverText;

	/** OverLimitMode for under threshold */
	private Multi.OverLimitMode argUnderMode;

	/** under threshold text */
	private String argUnderText;

	/** route */
	private Route route;

	/** slowest time allowed */
	private int slowestTime;

	/** fastest time allowed */
	private int fastestTime;

	/** calculated time */
	private int calculatedTime;

	/** Constructor */
	public TravelTimeValue(String d_sid) {
		argDestStationId = d_sid;
		argOverMode = DEF_OVER_MODE;
		argOverText = DEF_OVER_TEXT;
		argUnderMode = null;
		argUnderText = null;
		argOriginStationId = null;
	}

	/** Constructor */
	public TravelTimeValue(String d_sid, Multi.OverLimitMode o_mode, String o_txt) {
		this(d_sid);
		argOverMode = o_mode;
		argOverText = o_txt;
	}

	/** Set the argDestStationId field value. */
	public String getArgDestStationId() {
		return argDestStationId;
	}

	/** Set the argDestStationId field value. */
	public void setArgDestStationId(String dsid) {
		argDestStationId = dsid;
	}

	/** do we have an destination sid */
	public boolean hasDest() {
		if (!isBlank(emptyBecomesNull(argDestStationId)))
			return true;
		return false;
	}

	/** Set the argOverMode field value. */
	public Multi.OverLimitMode getArgOverMode() {
		return argOverMode;
	}

	/** Set the argOverMode field value. */
	public void setArgOverMode(Multi.OverLimitMode om) {
		argOverMode = om;
	}

	/** Set the argOverText field value. */
	public String getArgOverText() {
		return argOverText;
	}

	/** Set the argOverText field value. */
	public void setArgOverText(String ot) {
		argOverText = ot;
	}

	/** Set the argOriginStationId field value. */
	public String getArgOriginStationId() {
		return argOriginStationId;
	}

	/** Set the argOriginStationId field value. */
	public void setArgOriginStationId(String osid) {
		argOriginStationId = osid;
	}

	/** do we have an origin sid */
	public boolean hasOrigin() {
		if (!isBlank(emptyBecomesNull(argOriginStationId)))
			return true;
		return false;
	}

	/** Set the argUnderMode field value. */
	public Multi.OverLimitMode getArgUnderMode() {
		return argUnderMode;
	}

	/** Set the argUnderMode field value. */
	public void setArgUnderMode(Multi.OverLimitMode um) {
		argUnderMode = um;
		if (um != null && argUnderText == null)
			argUnderText = DEF_UNDER_TEXT;
	}

	/** use under mode? */
	public boolean useUnderMode() {
		return argUnderMode != null;
	}

	/** Set the argUnderText field value. */
	public String getArgUnderText() {
		return argUnderText;
	}

	/** Set the argUnderText field value. */
	public void setArgUnderText(String ut) {
		argUnderText = ut;
	}

	/** has under text */
	public boolean hasUnderText() {
		return argUnderText != null;
	}

	/** Set the route field value. */
	public Route getRoute() {
		return route;
	}

	/** Set the route field value. */
	public void setRoute(Route r) {
		route = r;
	}

	/** Set the slowestTime field value. */
	public int getSlowestTime() {
		return slowestTime;
	}

	/** Set the slowestTime field value. */
	public void setSlowestTime(int st) {
		slowestTime = st;
	}

	/** Set the fastestTime field value. */
	public int getFastestTime() {
		return fastestTime;
	}

	/** Set the fastestTime field value. */
	public void setFastestTime(int ft) {
		fastestTime = ft;
	}

	/** Set the calculatedTime field value. */
	public int getCalculatedTime() {
		return calculatedTime;
	}

	/** Set the calculatedTime field value. */
	public void setCalculatedTime(int ct) {
		calculatedTime = ct;
	}

	/** is this object valid? */
	public boolean isValid() {
		if (!hasDest())
			return false;
		if (null == argOverMode)
			return false;
		if (null == argOverText)
			return false;
		return true;
	}

	/** is extended tag arguments */
	public boolean isExtended() {
		if (hasOrigin() || (useUnderMode() && hasUnderText()))
			return true;
		return false;
	}

	/** create a string for use as arguments in a tag */
	static public String mapToArgs(TravelTimeValue tt) {
		if (!tt.isValid())
			return null;

		StringBuilder rv = new StringBuilder("");
		if (tt.isExtended()) {
			rv.append(DEST_SID).append(KV_DELIM).append(tt.getArgDestStationId());
			if (tt.getArgOverMode() != DEF_OVER_MODE)
				rv.append(ARG_DELIM).append(OVER_MODE).append(KV_DELIM).append(tt.getArgOverMode());
			if (DEF_OVER_TEXT.equals(tt.getArgOverText()))
				rv.append(ARG_DELIM).append(OVER_TEXT).append(KV_DELIM).append(tt.getArgOverText());
			if (!isBlank(emptyBecomesNull(tt.getArgOriginStationId())))
				rv.append(ARG_DELIM).append(ORIG_SID).append(KV_DELIM).append(tt.getArgOriginStationId());
			if (tt.useUnderMode()) {
				rv.append(ARG_DELIM).append(UNDER_MODE).append(KV_DELIM).append(tt.getArgUnderMode());
				if (tt.hasUnderText() && !DEF_UNDER_TEXT.equals(tt.getArgUnderText()))
					rv.append(ARG_DELIM).append(UNDER_TEXT).append(KV_DELIM).append(tt.getArgUnderText());
			}
		} else {
			rv.append(tt.getArgDestStationId());
			if (tt.getArgOverMode() != DEF_OVER_MODE)
				rv.append(ARG_DELIM).append(tt.getArgOverMode());
			if (DEF_OVER_TEXT.equals(tt.getArgOverText()))
				rv.append(ARG_DELIM).append(tt.getArgOverText());
		}

		return rv.toString();
	}

	/** map tag string arguments to a new TravelTimeValue object */
	static public TravelTimeValue mapTo(String[] args) {
		TravelTimeValue rv;
		if (args.length <= 3) {
			rv = new TravelTimeValue(
				((args.length > 0) ? args[0] : null));
			rv.setArgOverMode(DEF_OVER_MODE);
			rv.setArgOverText(DEF_OVER_TEXT);
			if (args.length > 1)
				rv.setArgOverMode(parseMode(args[1], DEF_OVER_MODE));
			if (args.length > 2)
				rv.setArgOverText(args[2]);
		} else {
			rv = new TravelTimeValue(null);
			for (String s : args) {
				String[] kv = s.split(KV_DELIM,2);
				if (null != kv[0] && !"".equals(kv[0].trim())) {
					if (DEST_SID.equals(kv[0]))
						rv.setArgDestStationId(kv[1]);
					else if (ORIG_SID.equals(kv[0]))
						rv.setArgOriginStationId(kv[1]);
					else if (OVER_MODE.equals(kv[0]))
						rv.setArgOverMode(parseMode(kv[1], DEF_OVER_MODE));
					else if (OVER_TEXT.equals(kv[0]))
						rv.setArgOverText(kv[1]);
					else if (UNDER_MODE.equals(kv[0]))
						rv.setArgUnderMode(parseMode(kv[1], DEF_UNDER_MODE));
					else if (UNDER_TEXT.equals(kv[0]))
						rv.setArgUnderText(kv[1]);
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
