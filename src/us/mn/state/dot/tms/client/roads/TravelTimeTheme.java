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

import java.awt.Color;
import us.mn.state.dot.tms.client.map.Style;
import us.mn.state.dot.tms.utils.I18N;

/**
 * TravelTimeTheme is for Travel Time map theme
 *
 * @author Jacob Barde
 */
public class TravelTimeTheme extends SegmentTheme {

	/** Speed styles */
	static private final Style[] S_STYLES = new Style[] {
		new Style(I18N.get("units.travel.unused"), OUTLINE, new Color(0, 0, 0, 0.3f)),
		new Style(I18N.get("units.speed.low"), OUTLINE, RED),
		new Style(I18N.get("units.speed.low.med"), OUTLINE, ORANGE),
		new Style(I18N.get("units.speed.medium"), OUTLINE, YELLOW),
		new Style(I18N.get("units.speed.med.high"), OUTLINE, GREEN),
		new Style(I18N.get("units.speed.high"), OUTLINE, VIOLET)
	};

	/** Create a new speed theme */
	public TravelTimeTheme() {
		super(I18N.get("units.travel"));
		for (Style s: S_STYLES)
			addStyle(s);
	}

	/** Get the style to draw a given segment */
	@Override
	protected Style getSegmentStyle(MapSegment ms) {
		Integer spd = ms.getSpeed();
		boolean segmentInTTRoute = true; //FIXME need to determine this
		if(spd != null && segmentInTTRoute) {
			if (spd < 25)
				return S_STYLES[1];
			if (spd < 40)
				return S_STYLES[2];
			if (spd < 55)
				return S_STYLES[3];
			if (spd < 90)
				return S_STYLES[4];
			return S_STYLES[5];
		}
		return S_STYLES[0];
	}
}
