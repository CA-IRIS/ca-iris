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

import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Theme;

/**
 * HeatmapLayerState is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayerState extends LayerState {

	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 * @param theme
	 */
	public HeatmapLayerState(Layer layer,
		MapBean mb, Theme theme) {
		super(layer, mb, theme);
		//add themes?
	}

	@Override
	public MapObject forEach(MapSearcher s) {
		return null;
	}
}
