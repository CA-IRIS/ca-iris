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

import us.mn.state.dot.tms.client.map.Layer;
import us.mn.state.dot.tms.client.map.LayerChange;
import us.mn.state.dot.tms.client.map.LayerState;
import us.mn.state.dot.tms.client.map.MapBean;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A layer specific to heatmap related objects.
 * Since traditional vector markers are not used (like most layers), most of
 * the functionality for this layer is handled by its corresponding LayerState
 * @see HeatMapLayerState
 *
 * @author Jacob Barde
 */
public class HeatMapLayer extends Layer {

	/** session */
	private final Session session;

	/** Get the weather sensor manager */
	public WeatherSensorManager getManager() {
		return manager;
	}

	/** weather sensor manager */
	private final WeatherSensorManager manager;

	private LayerState layerState;

	public LayerState getLayerState() {
		return layerState;
	}

	/**
	 * Constructor to create the heatmap layer
	 * @param s session
	 * @param m manager
	 */
	public HeatMapLayer(Session s, WeatherSensorManager m) {
		super(I18N.get("weather.heatmaps"));
		session = s;
		manager = m;
	}

	/** Create a new layer state */
	@Override
	public LayerState createState(MapBean mb) {
		if(layerState == null)
			layerState = new HeatMapLayerState(this, mb);
		return layerState;
	}

	/** Update the layer geometry */
	public void updateGeometry() {
		fireLayerChanged(LayerChange.geometry);
	}

	/** Update the layer status */
	public void updateStatus() {
		fireLayerChanged(LayerChange.status);
	}


}
