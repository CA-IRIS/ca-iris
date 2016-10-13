/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       California Department of Transportation
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static us.mn.state.dot.tms.SystemAttrEnum.RWIS_OPACITY_PERCENTAGE;
import static us.mn.state.dot.tms.WeatherSensorHelper.*;
import static us.mn.state.dot.tms.client.weather.WeatherSensorTheme.HCOLOR;
import static us.mn.state.dot.tms.client.weather.WeatherSensorTheme.LCOLOR;
import static us.mn.state.dot.tms.client.weather.WeatherSensorTheme.MCOLOR;

/**
 * WeatherMeasurementDataSet is for holding weather data of particular type in
 * a means accessible via a threshold color.
 *
 * @author Jacob Barde
 */
public class WeatherMeasurementDataSet {

	/** opacity for colors */
	public static final int OPACITY = (int) (
		RWIS_OPACITY_PERCENTAGE.getInt() * (0xFF / 100));

	/** low-threshold color, with opacity applied */
	public static final Color LOCOLOR = new Color(LCOLOR.getRed(),
		LCOLOR.getGreen(), LCOLOR.getBlue(), OPACITY);

	/** medium-threshold color, with opacity applied */
	public static final Color MOCOLOR = new Color(MCOLOR.getRed(),
		MCOLOR.getGreen(), MCOLOR.getBlue(), OPACITY);

	/** high-threshold color, with opacity applied */
	public static final Color HOCOLOR = new Color(HCOLOR.getRed(),
		HCOLOR.getGreen(), HCOLOR.getBlue(), OPACITY);

	/** measurement type of the dataset */
	private ItemStyle measurementType = ItemStyle.WIND_SPEED;

	/** hashmap containing the data */
	private Map<Color, List<WeatherMeasurementSample>> color_data = new HashMap<>();

	/** Constructor */
	public WeatherMeasurementDataSet() {
		init();
	}

	/** change the data set's measurement type to desired measurement type */
	public void changeDataType(ItemStyle mtype) {
		measurementType = mtype;
		reset();
		init();
	}

	/** initialize the internal data set */
	public void init() {
		color_data.put(LOCOLOR,
			new ArrayList<WeatherMeasurementSample>());
		color_data.put(MOCOLOR,
			new ArrayList<WeatherMeasurementSample>());
		color_data.put(HOCOLOR,
			new ArrayList<WeatherMeasurementSample>());
	}

	/**
	 * add weather sensor's data to the data set.
	 * appropriate data will be read into the data set depending on the
	 * measurement type of this data set.
	 *
	 * @param ws weather sensor
	 */
	public void add(WeatherSensor ws) {
		addSample(ws);
	}

	/** add appropriate data to data set */
	private void addSample(WeatherSensor ws) {
		if (ws == null)
			return;

		Color c = getMeasurementColor(measurementType, ws);
		if (c != null)
			color_data.get(c)
				.add(new WeatherMeasurementSample(ws, c,
					measurementType));
	}

	/** get a list of data for a given threshold color */
	public List<WeatherMeasurementSample> getList(Color c) {
		return color_data.get(c);
	}

	/** reset the data set to an empty state */
	public void reset() {
		clearAll();
	}

	/** remove all data in the data set */
	private void clearAll() {
		color_data.clear();
	}

	/** get threshold color for the measurement type and measurement value */
	public static Color getMeasurementColor(ItemStyle mtype,
		WeatherSensor ws) {

		if (isSampleExpired(ws) || isCrazyState(ws))
			return null;

		Boolean lb;
		Boolean hb;
		Number n;

		switch (mtype) {
		case AIR_TEMP:
			hb = isHighAirTempCelsius(ws);
			lb = isLowAirTempCelsius(ws);
			n = getAirTempCelsius(ws);
			break;

		case PRECIPITATION:
			hb = isHighPrecipRate(ws);
			lb = isLowPrecipRate(ws);
			n = getPrecipRate(ws);
			break;

		case VISIBILITY:
			hb = isHighVisibility(ws);
			lb = isLowVisibility(ws);
			n = getVisibilityMeters(ws);
			break;

		case WIND_SPEED:
			hb = isHighWind(ws);
			lb = isLowWind(ws);
			n = getWindSpeedKph(ws);
			break;

		default:
			return null;
		}

		if (n == null)
			return null;
		if (lb)
			return LOCOLOR;
		if (hb)
			return HOCOLOR;

		return MOCOLOR;
	}
}
