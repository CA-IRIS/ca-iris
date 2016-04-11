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
package us.mn.state.dot.tms.client.weather;

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;

/**
 * WeatherSensorCache is maintained as a separate cache for future expansion and
 * customization now that weather-related matters are a top-tier item.
 *
 * @author Jacob Barde
 */
public class WeatherSensorCache {

	/** cache of weather sensors */
	protected final TypeCache<WeatherSensor> weatherSensors;

	/** get the cache of weather sensors */
	public TypeCache<WeatherSensor> getWeatherSensors() {
		return weatherSensors;
	}

	/** proxy list model */
	protected final ProxyListModel<WeatherSensor> weatherSensorModel;

	/** get proxy list model */
	public ProxyListModel<WeatherSensor> getWeatherSensorModel() {
		return weatherSensorModel;
	}

	/** create new weather sensor cache */
	public WeatherSensorCache(SonarState client)
		throws IllegalAccessException, NoSuchFieldException {

		weatherSensors = new TypeCache<WeatherSensor>(WeatherSensor.class, client);
		weatherSensorModel = new ProxyListModel<WeatherSensor>(weatherSensors);
		weatherSensorModel.initialize();
	}

	/** populate type cache(s) */
	public void populate(SonarState client) {
		client.populateReadable(weatherSensors);
		if(client.canRead(WeatherSensor.SONAR_TYPE)) {
			weatherSensors.ignoreAttribute("operation");
			weatherSensors.ignoreAttribute("stamp");

			// attributes not used at the moment
			weatherSensors.ignoreAttribute("surfaceTemps");
			weatherSensors.ignoreAttribute("subsurfaceTemps");
		}
	}
}
