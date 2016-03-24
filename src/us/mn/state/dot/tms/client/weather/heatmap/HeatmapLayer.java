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

import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyLayer;
import us.mn.state.dot.tms.utils.I18N;

/**
 * HeatmapLayer is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayer extends ProxyLayer /*implements Iterable<Hotspot>*/ {

	public HeatmapLayer(Session s, WeatherHeatmapManager m) {
		super(I18N.get("weather.heatmaps"), m);
	}

	@Override
	public LayerState createState(MapBean mb) {
		return new HeatmapLayerState(this, mb);
	}
}
