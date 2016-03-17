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

import us.mn.state.dot.tms.GeoLoc;

/**
 *
 * @author Jacob Barde
 */
public class WeatherSensorSample {

	public final String id;

	public final Integer airTemp;

	public final Integer windSpeed;

	public final Integer windDir;

	public final Integer gustSpeed;

	public final Integer gustDir;

	public final Integer precipRate;

	public final Integer visibility;

	public final GeoLoc location;

	public WeatherSensorSample(String id, Integer airTemp,
				   Integer windSpeed, Integer windDir,
				   Integer gustSpeed, Integer gustDir,
				   Integer precipRate, Integer visibility,
				   GeoLoc location) {

		this.id = id;
		this.airTemp = airTemp;
		this.windSpeed = windSpeed;
		this.windDir = windDir;
		this.gustSpeed = gustSpeed;
		this.gustDir = gustDir;
		this.precipRate = precipRate;
		this.visibility = visibility;
		this.location = location;
	}

	public String getId() {

		return id;
	}

	public Integer getAirTemp() {

		return airTemp;
	}

	public Integer getWindSpeed() {

		return windSpeed;
	}

	public Integer getWindDir() {

		return windDir;
	}

	public Integer getGustSpeed() {

		return gustSpeed;
	}

	public Integer getGustDir() {

		return gustDir;
	}

	public Integer getPrecipRate() {

		return precipRate;
	}

	public Integer getVisibility() {

		return visibility;
	}

	public GeoLoc getLocation() {
		return location;
	}
}
