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
	/** colors */
	static private final Color UNUSED = new Color(0, 0, 0, 0.20f);
	static private final Color LOW = new Color(2, 0, 185);
	static private final Color LOWMED = new Color(16, 0, 255);
	static private final Color MED = new Color(0, 125, 255);
	static private final Color MEDHIGH = new Color(0, 187, 209);
	static private final Color HIGH = new Color(0, 255, 243);
	/** Speed styles */
	static private final Style[] S_STYLES = new Style[] {
		// segment not used in Travel-Time
		new Style(I18N.get("units.travel.unused"), OUTLINE, UNUSED),
		new Style(I18N.get("units.travel.low"), OUTLINE, LOW),
		new Style(I18N.get("units.travel.low.med"), OUTLINE, LOWMED),
		new Style(I18N.get("units.travel.medium"), OUTLINE, MED),
		new Style(I18N.get("units.travel.med.high"), OUTLINE, MEDHIGH),
		new Style(I18N.get("units.travel.high"), OUTLINE, HIGH)
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
		Integer cnt = ms.getSpeed(); // FIXME TT route count
		cnt = (cnt == null) ? 0 : cnt;

		if (cnt == 0)
			return S_STYLES[0];

		//FIXME TESTING
		cnt = (int) (cnt * 0.1);
		if (cnt < 3)
			return S_STYLES[1];
		if (cnt < 5)
			return S_STYLES[2];
		if (cnt < 7)
			return S_STYLES[3];
		if (cnt < 10)
			return S_STYLES[4];
		return S_STYLES[5];
	}
}
