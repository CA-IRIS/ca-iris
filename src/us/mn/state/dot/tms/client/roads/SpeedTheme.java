/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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

import us.mn.state.dot.map.Style;

/**
 * A theme for drawing segment objects based on speed thresholds.
 *
 * @author Douglas Lau
 */
public class SpeedTheme extends SegmentTheme {

	/** Speed styles */
	static protected final Style[] S_STYLES = new Style[] {
		new Style("0-24 mph", RED),
		new Style("25-39 mph", ORANGE),
		new Style("40-54 mph", YELLOW),
		new Style("55-90 mph", GREEN),
		new Style("Over 90 mph", VIOLET)
	};

	/** Create a new speed theme */
	public SpeedTheme() {
		super("Speed");
		for(Style s: S_STYLES)
			addStyle(s);
	}

	/** Get the style to draw a given segment */
	protected Style getStyle(MapSegment ms) {
		Integer spd = ms.getSpeed();
		if(spd == null)
			return DEFAULT_STYLE;
		if(spd < 25)
			return S_STYLES[0];
		if(spd < 40)
			return S_STYLES[1];
		if(spd < 55)
			return S_STYLES[2];
		if(spd < 90)
			return S_STYLES[3];
		return S_STYLES[4];
	}
}
