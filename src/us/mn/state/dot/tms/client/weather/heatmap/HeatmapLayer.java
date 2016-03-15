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
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.utils.I18N;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * HeatmapLayer is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayer extends Layer /*implements Iterable<Hotspot>*/ {

	private WeatherHeatmapManager manager;
	private final Session session;
	private WeatherSensorReader reader;

	public HeatmapLayer(Session s, WeatherHeatmapManager m) {
		super(I18N.get("weather.sensors"));
		manager = m;
		session = s;
	}

	@Override
	public LayerState createState(MapBean mb) {
		return new HeatmapLayerState(this, mb);
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	/*@Override
	public Iterator<Hotspot> iterator() {

		return null;
	}*/

	public WeatherHeatmapManager getManager() {

		return manager;
	}

	public void dispose() {
		reader = null;
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	/*@Override
	public Iterator<Hotspot> iterator() {

		return null;
	}*/

	public WeatherHeatmapManager getManager() {

		return manager;
	}

	public void dispose() {
		reader = null;
	}
}
