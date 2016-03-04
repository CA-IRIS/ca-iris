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

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;

/**
 * Cache for weather sensor-related proxy objects.
 *
 * @author Jacob Barde
 *
 * FIXME: Is this necessary?  Seems redundant... investigate later
 */
public class WeatherHeatmapCache {

	/** Cache of weatherSensors */
	protected final TypeCache<WeatherSensor> weatherSensors;

	/** Get the camera cache */
	public TypeCache<WeatherSensor> getWeatherSensors() {
		return weatherSensors;
	}

	/** WeatherSensor proxy list model */
	protected final ProxyListModel<WeatherSensor> ws_model;

	/** Get the camera list model */
	public ProxyListModel<WeatherSensor> getWeatherSensorModel() {
		return ws_model;
	}

	/** Create a new camera cache */
	public WeatherHeatmapCache(SonarState client) throws IllegalAccessException, NoSuchFieldException {
		weatherSensors = new TypeCache<WeatherSensor>(WeatherSensor.class, client);
		ws_model = new ProxyListModel<WeatherSensor>(weatherSensors);
		ws_model.initialize();
	}

	/** Populate the type caches */
	public void populate(SonarState client) {
		client.populateReadable(weatherSensors);
		if(client.canRead(WeatherSensor.SONAR_TYPE)) {
			weatherSensors.ignoreAttribute("operation");
			weatherSensors.ignoreAttribute("stamp");
		}
	}
}
