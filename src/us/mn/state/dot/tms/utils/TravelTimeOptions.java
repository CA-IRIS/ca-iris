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
	private String originStationId;

	/** destination station id */
	private String destinationStationId;

	/** OverLimitMode for lower threshold */
	private Multi.OverLimitMode lowerMode;

	/** lower threshold text */
	private String lowerText;

	/** OverLimitMode for upper threshold */
	private Multi.OverLimitMode upperMode;

	/** upper threshold text */
	private String upperText;

	/** Constructor */
	public TravelTimeOptions(String d_sid) {
		this.destinationStationId = d_sid;
		lowerMode = Multi.OverLimitMode.prepend;
		lowerText = DEF_LOW_TEXT;
	}

	/** Constructor */
	public TravelTimeOptions(String d_sid, Multi.OverLimitMode l_mode, String l_txt) {
		this(d_sid);
		lowerMode = l_mode;
		lowerText = l_txt;
	}

	/** Set the originStationId field value. */
	public String getOriginStationId() {
		return originStationId;
	}

	/** Set the originStationId field value. */
	public void setOriginStationId(String originStationId) {
		this.originStationId = originStationId;
	}

	/** Set the destinationStationId field value. */
	public String getDestinationStationId() {
		return destinationStationId;
	}

	/** Set the destinationStationId field value. */
	public void setDestinationStationId(String destinationStationId) {
		this.destinationStationId = destinationStationId;
	}

	/** Set the lowerMode field value. */
	public Multi.OverLimitMode getLowerMode() {
		return lowerMode;
	}

	/** Set the lowerMode field value. */
	public void setLowerMode(Multi.OverLimitMode lowerMode) {
		this.lowerMode = lowerMode;
	}

	/** Set the lowerText field value. */
	public String getLowerText() {
		return lowerText;
	}

	/** Set the lowerText field value. */
	public void setLowerText(String lowerText) {
		this.lowerText = lowerText;
	}

	/** Set the upperMode field value. */
	public Multi.OverLimitMode getUpperMode() {
		return upperMode;
	}

	/** Set the upperMode field value. */
	public void setUpperMode(Multi.OverLimitMode upperMode) {
		this.upperMode = upperMode;
	}

	/** Set the upperText field value. */
	public String getUpperText() {
		return upperText;
	}

	/** Set the upperText field value. */
	public void setUpperText(String upperText) {
		this.upperText = upperText;
	}

	/** is this object valid? */
	public boolean isValid() {
		if (isBlank(emptyBecomesNull(destinationStationId)))
			return false;
		if (null == lowerMode)
			return false;
		if (null == lowerText)
			return false;
		return true;
	}

	/** is extended tag arguments */
	public boolean isExtended() {
		if (hasOrigin() || (null != upperMode && null != upperText))
			return true;
		return false;
	}

	public boolean hasOrigin() {
		if (!isBlank(emptyBecomesNull(originStationId)))
			return true;
		return false;
	}

	/** create a string for use as arguments in a tag */
	static public String mapToArgs(TravelTimeOptions tt) {
		if (!tt.isValid())
			return null;

		StringBuilder rv = new StringBuilder("");
		if (!tt.isExtended()) {
			rv.append(tt.getDestinationStationId());
			if (tt.getLowerMode() != DEF_LOW_MODE)
				rv.append(",").append(tt.getLowerMode());
			if (DEF_LOW_TEXT.equals(tt.getLowerText()))
				rv.append(",").append(tt.getLowerText());
		} else {
			rv.append("d_sid=").append(tt.getDestinationStationId());
			if (tt.getLowerMode() != DEF_LOW_MODE)
				rv.append(",l_mode=").append(tt.getLowerMode());
			if (DEF_LOW_TEXT.equals(tt.getLowerText()))
				rv.append(",l_txt=").append(tt.getLowerText());
			if (!isBlank(emptyBecomesNull(tt.getOriginStationId())))
				rv.append(",o_sid=").append(tt.getOriginStationId());
			if (tt.getUpperMode() != null)
				rv.append(",u_mode=").append(tt.getUpperMode());
			if (tt.getUpperText() != null)
				rv.append(",u_txt").append(tt.getUpperText());
		}

		return rv.toString();
	}

	/** map tag string arguments to a new TravelTimeOptions object */
	static public TravelTimeOptions mapTo(String[] args) {
		TravelTimeOptions rv;
		if (args.length <= 3) {
			rv = new TravelTimeOptions(
				((args.length > 0) ? args[0] : null));
			rv.setLowerMode(DEF_LOW_MODE);
			rv.setLowerText(DEF_LOW_TEXT);
			if (args.length > 1)
				rv.setLowerMode(parseOverMode(args[1]));
			if (args.length > 2)
				rv.setLowerText(args[2]);
		} else {
			rv = new TravelTimeOptions(null);
			for (String s : args) {
				String[] kv = s.split("=",2);
				if (null != kv[0] && !"".equals(kv[0])) {
					if ("d_sid".equals(kv[0]))
						rv.setDestinationStationId(kv[1]);
					else if ("o_sid".equals(kv[0]))
						rv.setOriginStationId(kv[1]);
					else if ("l_mode".equals(kv[0]))
						rv.setLowerMode(parseOverMode(kv[1]));
					else if ("l_txt".equals(kv[0]))
						rv.setLowerText(kv[1]);
					else if ("u_mode".equals(kv[0]))
						rv.setUpperMode(parseOverMode(kv[1]));
					else if ("u_txt".equals(kv[0]))
						rv.setUpperText(kv[1]);
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
