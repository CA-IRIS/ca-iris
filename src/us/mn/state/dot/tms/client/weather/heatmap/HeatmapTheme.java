/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       Southwest Research Institute
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
package us.mn.state.dot.tms.client.weather.heatmap;

import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Style;
import us.mn.state.dot.map.StyledTheme;

import java.awt.*;

/**
 * Heatmap theme
 *
 * @author Jacob Barde
 */
public class HeatmapTheme extends StyledTheme {

	/**
	 * Create a new styled theme
	 *
	 * @param n
	 * @param ls
	 */
	public HeatmapTheme(String n, Shape ls) {

		super(n, ls);
	}

	/**
	 * Create a new styled theme
	 *
	 * @param n
	 * @param ls
	 * @param sz
	 */
	public HeatmapTheme(String n, Shape ls, int sz) {

		super(n, ls, sz);
	}

	/**
	 * Get the style to draw a given map object
	 *
	 * @param mo
	 */
	@Override
	public Style getStyle(MapObject mo) {

		return null;
	}
}
