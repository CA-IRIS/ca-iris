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

import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.WeatherSensor;

import java.awt.Color;

/**
 * WeatherMeasurementSample is for...
 *
 * @author Jacob Barde
 */
public class WeatherMeasurementSample {

	private WeatherSensor weatherSensor;
	private Integer value;
	private Color color;
	private ItemStyle styleType;

	public WeatherMeasurementSample(WeatherSensor ws, Integer v, Color c, ItemStyle s) {
		weatherSensor = ws;
		value = v;
		color = c;
		styleType = s;
	}

	public WeatherSensor getWeatherSensor() {
		return weatherSensor;
	}

	public Color getColor() {
		return color;
	}

	public ItemStyle getStyleType() {
		return styleType;
	}

	public Integer getValue() {
		return value;
	}
}
