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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jacob Barde
 */
public class WeatherSampleDataSet {

	protected final Map<String, WeatherSensorSample> samples = new HashMap<String, WeatherSensorSample>();

	protected final Map<String, WeatherSensorSample> nextSamples = new HashMap<String, WeatherSensorSample>();

	public synchronized void updateSample(WeatherSensorSample s) {
		samples.put(s.getId(), s);
	}

	public synchronized void swapSamples() {
		samples.clear();
		samples.putAll(nextSamples);
		nextSamples.clear();
	}

	public synchronized  void clearSamples() {
		samples.clear();
		nextSamples.clear();
	}

	public synchronized WeatherSensorSample getWeatherSensorSample(String id) {
		return samples.get(id);
	}
}
