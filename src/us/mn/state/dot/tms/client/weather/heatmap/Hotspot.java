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

import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.weather.WeatherSensorModel;

/**
 *
 * @author Jacob Barde
 */
public class Hotspot {

	/** weather sensor model */
	private WeatherSensorModel model;

	/** Location of station */
	private final MapGeoLoc loc;

	private final WeatherSensor weatherSensor;

	public Hotspot(WeatherSensorModel model,
		       MapGeoLoc loc, WeatherSensor ws) {

		assert model != null;
		assert loc != null;
		assert ws != null;
		this.loc = loc;
		this.model = model;
		this.weatherSensor = ws;
	}

	public MapGeoLoc getLoc() {

		return loc;
	}

	public WeatherSensorModel getModel() {

		return model;
	}

	public WeatherSensor getWeatherSensor() {

		return weatherSensor;
	}
}
