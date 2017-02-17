/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015-2017  California Department of Transporation
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
 * TravelTimeOptions is for...
 *
 * @author Jacob Barde
 */
public class TravelTimeOptions {
	static public final String DEF_LOW_TEXT = "OVER ";
	static public final Multi.OverLimitMode DEF_LOW_MODE = Multi.OverLimitMode.prepend;

	/** origin station id */
	private String argOriginStationId;

	/** destination station id */
	private String argDestStationId;

	/** OverLimitMode for lower threshold */
	private Multi.OverLimitMode argLowerMode;

	/** lower threshold text */
	private String argLowerText;

	/** OverLimitMode for upper threshold */
	private Multi.OverLimitMode argUpperMode;

	/** upper threshold text */
	private String argUpperText;

	/** route */
	private Route route;

	/** slowest time allowed */
	private int slowestTime;

	/** fastest time allowed */
	private int fastestTime;

	/** calculated time */
	private int calculatedTime;

	/** Constructor */
	public TravelTimeOptions(String d_sid) {
		this.argDestStationId = d_sid;
		argLowerMode = Multi.OverLimitMode.prepend;
		argLowerText = DEF_LOW_TEXT;
	}

	/** Constructor */
	public TravelTimeOptions(String d_sid, Multi.OverLimitMode l_mode, String l_txt) {
		this(d_sid);
		argLowerMode = l_mode;
		argLowerText = l_txt;
	}

	/** Set the argOriginStationId field value. */
	public String getArgOriginStationId() {
		return argOriginStationId;
	}

	/** Set the argOriginStationId field value. */
	public void setArgOriginStationId(String argOriginStationId) {
		this.argOriginStationId = argOriginStationId;
	}

	/** Set the argDestStationId field value. */
	public String getArgDestStationId() {
		return argDestStationId;
	}

	/** Set the argDestStationId field value. */
	public void setArgDestStationId(String argDestStationId) {
		this.argDestStationId = argDestStationId;
	}

	/** Set the argLowerMode field value. */
	public Multi.OverLimitMode getArgLowerMode() {
		return argLowerMode;
	}

	/** Set the argLowerMode field value. */
	public void setArgLowerMode(Multi.OverLimitMode argLowerMode) {
		this.argLowerMode = argLowerMode;
	}

	/** Set the argLowerText field value. */
	public String getArgLowerText() {
		return argLowerText;
	}

	/** Set the argLowerText field value. */
	public void setArgLowerText(String argLowerText) {
		this.argLowerText = argLowerText;
	}

	/** Set the argUpperMode field value. */
	public Multi.OverLimitMode getArgUpperMode() {
		return argUpperMode;
	}

	/** Set the argUpperMode field value. */
	public void setArgUpperMode(Multi.OverLimitMode argUpperMode) {
		this.argUpperMode = argUpperMode;
	}

	/** Set the argUpperText field value. */
	public String getArgUpperText() {
		return argUpperText;
	}

	/** Set the argUpperText field value. */
	public void setArgUpperText(String argUpperText) {
		this.argUpperText = argUpperText;
	}

	/** Set the route field value. */
	public Route getRoute() {
		return route;
	}

	/** Set the route field value. */
	public void setRoute(Route route) {
		this.route = route;
	}

	/** Set the slowestTime field value. */
	public int getSlowestTime() {
		return slowestTime;
	}

	/** Set the slowestTime field value. */
	public void setSlowestTime(int slowestTime) {
		this.slowestTime = slowestTime;
	}

	/** Set the fastestTime field value. */
	public int getFastestTime() {
		return fastestTime;
	}

	/** Set the fastestTime field value. */
	public void setFastestTime(int fastestTime) {
		this.fastestTime = fastestTime;
	}

	/** Set the calculatedTime field value. */
	public int getCalculatedTime() {
		return calculatedTime;
	}

	/** Set the calculatedTime field value. */
	public void setCalculatedTime(int calculatedTime) {
		this.calculatedTime = calculatedTime;
	}

	/** is this object valid? */
	public boolean isValid() {
		if (isBlank(emptyBecomesNull(argDestStationId)))
			return false;
		if (null == argLowerMode)
			return false;
		if (null == argLowerText)
			return false;
		return true;
	}

	/** is extended tag arguments */
	public boolean isExtended() {
		if (hasOrigin() || (null != argUpperMode && null != argUpperText))
			return true;
		return false;
	}

	public boolean hasOrigin() {
		if (!isBlank(emptyBecomesNull(argOriginStationId)))
			return true;
		return false;
	}

	/** create a string for use as arguments in a tag */
	static public String mapToArgs(TravelTimeOptions tt) {
		if (!tt.isValid())
			return null;

		StringBuilder rv = new StringBuilder("");
		if (!tt.isExtended()) {
			rv.append(tt.getArgDestStationId());
			if (tt.getArgLowerMode() != DEF_LOW_MODE)
				rv.append(",").append(tt.getArgLowerMode());
			if (DEF_LOW_TEXT.equals(tt.getArgLowerText()))
				rv.append(",").append(tt.getArgLowerText());
		} else {
			rv.append("d_sid=").append(tt.getArgDestStationId());
			if (tt.getArgLowerMode() != DEF_LOW_MODE)
				rv.append(",l_mode=").append(tt.getArgLowerMode());
			if (DEF_LOW_TEXT.equals(tt.getArgLowerText()))
				rv.append(",l_txt=").append(tt.getArgLowerText());
			if (!isBlank(emptyBecomesNull(tt.getArgOriginStationId())))
				rv.append(",o_sid=").append(tt.getArgOriginStationId());
			if (tt.getArgUpperMode() != null)
				rv.append(",u_mode=").append(tt.getArgUpperMode());
			if (tt.getArgUpperText() != null)
				rv.append(",u_txt").append(tt.getArgUpperText());
		}

		return rv.toString();
	}

	/** map tag string arguments to a new TravelTimeOptions object */
	static public TravelTimeOptions mapTo(String[] args) {
		TravelTimeOptions rv;
		if (args.length <= 3) {
			rv = new TravelTimeOptions(
				((args.length > 0) ? args[0] : null));
			rv.setArgLowerMode(DEF_LOW_MODE);
			rv.setArgLowerText(DEF_LOW_TEXT);
			if (args.length > 1)
				rv.setArgLowerMode(parseOverMode(args[1]));
			if (args.length > 2)
				rv.setArgLowerText(args[2]);
		} else {
			rv = new TravelTimeOptions(null);
			for (String s : args) {
				String[] kv = s.split("=",2);
				if (null != kv[0] && !"".equals(kv[0])) {
					if ("d_sid".equals(kv[0]))
						rv.setArgDestStationId(kv[1]);
					else if ("o_sid".equals(kv[0]))
						rv.setArgOriginStationId(kv[1]);
					else if ("l_mode".equals(kv[0]))
						rv.setArgLowerMode(parseOverMode(kv[1]));
					else if ("l_txt".equals(kv[0]))
						rv.setArgLowerText(kv[1]);
					else if ("u_mode".equals(kv[0]))
						rv.setArgUpperMode(parseOverMode(kv[1]));
					else if ("u_txt".equals(kv[0]))
						rv.setArgUpperText(kv[1]);
				}
			}
		}
		return rv;
	}

	/** Parse a over limit mode value */
	static private Multi.OverLimitMode parseOverMode(String mode) {
		for (Multi.OverLimitMode m : Multi.OverLimitMode.values()) {
			if (mode.equals(m.toString()))
				return m;
		}
		return DEF_LOW_MODE;
	}
}
