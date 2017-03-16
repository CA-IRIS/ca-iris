/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015-2017  California Department of Transportation
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
package us.mn.state.dot.tms.client.roads;

import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.utils.I18N;

/**
 * TravelTimeSegment is for displaying travel time routes within IRIS
 *
 * @author Jacob Barde
 */
public class TravelTimeSegment extends MapSegment {

	/**
	 * Create a new travel time segment
	 *
	 * @param s
	 * @param scale
	 */
	public TravelTimeSegment(Segment s, float scale) {
		super(s, scale);
	}

	/**
	 * Create a new travel time segment
	 *
	 * @param s
	 * @param sh
	 * @param scale
	 */
	public TravelTimeSegment(Segment s, int sh, float scale) {
		super(s, sh, scale);
	}

	/** Get the map segment tool tip */
	@Override
	public String getTip() {
		StringBuilder sb = new StringBuilder();
		R_Node r = getSegment().getModel().r_node;
		GeoLoc g = (r != null) ? r.getGeoLoc() : null;
		boolean git = true; //FIXME find out if GeoLoc is in a Travel Time Route
		if (git) {
			String label = getSegment().getLabel(getLane());
			if (label != null)
				sb.append(label);
			// FIXME list QuickMessage names using tt tag that fall in here.
			Integer speed = getSpeed();
			if (speed != null)
				sb.append("\n ").append(I18N.get("units.speed")).append(" = ").append(speed);
		}
		return (sb.length() > 0) ? sb.toString() : super.getTip();
	}
}
