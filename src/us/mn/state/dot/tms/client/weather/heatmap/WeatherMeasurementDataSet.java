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
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static us.mn.state.dot.tms.SystemAttrEnum.RWIS_OPACITY_PERCENTAGE;
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
	public static final int OPACITY = (int) (RWIS_OPACITY_PERCENTAGE.getInt() * (0xFF/100));

	/** low-threshold color, with opacity applied */
	public static final Color LOCOLOR = new Color(LCOLOR.getRed(), LCOLOR.getGreen(), LCOLOR.getBlue(), OPACITY);

	/** medium-threshold color, with opacity applied */
	public static final Color MOCOLOR = new Color(MCOLOR.getRed(), MCOLOR.getGreen(), MCOLOR.getBlue(), OPACITY);

	/** high-threshold color, with opacity applied */
	public static final Color HOCOLOR = new Color(HCOLOR.getRed(), HCOLOR.getGreen(), HCOLOR.getBlue(), OPACITY);

	/** data-type of the dataset */
	private ItemStyle datatype = ItemStyle.WIND_SPEED;

	/** hashmap containing the data */
	private Map<Color, List<WeatherMeasurementSample>> color_data = new HashMap<Color, List<WeatherMeasurementSample>>();

	/** Constructor */
	public WeatherMeasurementDataSet() {
		init();
	}

	/** change the data set's data-type to desired measurement type */
	public void changeDataType(ItemStyle mtype) {
		datatype = mtype;
		reset();
		init();
	}

	/** initialize the internal data set */
	public void init() {
		color_data.put(LOCOLOR, new ArrayList<WeatherMeasurementSample>());
		color_data.put(MOCOLOR, new ArrayList<WeatherMeasurementSample>());
		color_data.put(HOCOLOR, new ArrayList<WeatherMeasurementSample>());
	}

	/**
	 * add weather sensor's data to the data set.
	 * appropriate data will be read into the data set depending on the
	 * data-type of this data set.
	 *
	 * @param ws weather sensor
	 */
	public void add(WeatherSensor ws) {
		addSample(ws);
	}

	/** add appropriate data to data set */
	private void addSample(WeatherSensor ws) {
		if(ws == null)
			return;

		Integer measurement = getMeasurement(ws, datatype);
		Color c = getMeasurementColor(datatype, measurement);
		if(c != null && measurement != null)
			color_data.get(c).add(new WeatherMeasurementSample(ws, measurement, c, datatype));
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

	/**
	 * get a measurement from a weather sensor based on request data type
	 *
	 * @param ws weather sensor
	 * @param mtype desired weather data (air temp, wind speed, precipitation, visibility)
	 *
	 * @return Integer object containing the measurement value (nulls allowed)
	 */
	private static Integer getMeasurement(WeatherSensor ws, ItemStyle mtype) {

		if(WeatherSensorHelper.isSampleExpired(ws))
			return null;

		switch (mtype) {
		case AIR_TEMP:
			return ws.getAirTemp();
		case WIND_SPEED:
			return (WeatherSensorHelper.isCrazyState(ws)) ? null : ws.getWindSpeed();
		case PRECIPITATION:
			return ws.getPrecipRate();
		case VISIBILITY:
			return ws.getVisibility();
		}

		return null;
	}

	/** get threshold color for the data type and measurement value */
	private static Color getMeasurementColor(ItemStyle mtype, Integer x) {

		double l = 0, h = 0;

		switch (mtype) {
		case AIR_TEMP:
			l = SystemAttrEnum.RWIS_LOW_AIR_TEMP_C.getFloat();
			h = SystemAttrEnum.RWIS_HIGH_AIR_TEMP_C.getFloat();
			break;

		case PRECIPITATION:
			l = SystemAttrEnum.RWIS_LOW_PRECIP_RATE_MMH.getInt();
			h = SystemAttrEnum.RWIS_HIGH_PRECIP_RATE_MMH.getInt();
			break;

		case VISIBILITY:
			l = SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M
				.getInt();
			h = SystemAttrEnum.RWIS_HIGH_VISIBILITY_DISTANCE_M
				.getInt();
			break;

		case WIND_SPEED:
			l = SystemAttrEnum.RWIS_LOW_WIND_SPEED_KPH.getInt();
			h = SystemAttrEnum.RWIS_HIGH_WIND_SPEED_KPH.getInt();
			break;
		}

		if(x == null)
			return null;

		if(x < l)
			return LOCOLOR;
		else if(x > h)
			return HOCOLOR;
		else
			return MOCOLOR;
	}
}
