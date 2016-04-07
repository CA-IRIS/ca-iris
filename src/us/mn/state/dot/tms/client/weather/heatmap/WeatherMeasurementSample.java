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
 * WeatherMeasurementSample is a container for data samples utilized by
 * WeatherMeasurementDataSet
 *
 * @see WeatherMeasurementDataSet
 *
 * @author Jacob Barde
 */
public class WeatherMeasurementSample {

	/** weather sensor */
	private WeatherSensor weatherSensor;

	/** threshold color for data value */
	private Color color;

	/** item style / data type of measurement */
	private ItemStyle styleType;

	/** Constructor */
	public WeatherMeasurementSample(WeatherSensor ws, Color c, ItemStyle s) {
		weatherSensor = ws;
		color = c;
		styleType = s;
	}

	/** Get weatherSensor of sample */
	public WeatherSensor getWeatherSensor() {
		return weatherSensor;
	}

	/** Get color of sample */
	public Color getColor() {
		return color;
	}

	/** Get item style of sample */
	public ItemStyle getStyleType() {
		return styleType;
	}

}
