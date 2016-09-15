/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.client.map.Marker;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;

/**
 * HeatMapTheme is defined purely to satisfy the Layer-Theme relationship
 * As a heatmap does not need any traditional shapes, this serves placate
 * the existing framework.
 *
 * @author Jacob Barde
 */
public class HeatMapTheme extends ProxyTheme {

	/** Constructor */
	public HeatMapTheme(ProxyManager m) {
		this(m, new DummyShape(4));
	}

	/** Constructor */
	public HeatMapTheme(ProxyManager m, Marker s) {
		super(m, s);
	}

	/** required shape of a rectangle of zero width and height */
	private static class DummyShape extends Marker {
		public DummyShape(int c) {

			super(c);
		}
	}
}
