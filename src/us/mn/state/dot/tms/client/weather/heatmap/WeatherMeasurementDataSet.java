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
 * WeatherMeasurementDataSet is for...
 *
 * FIXME come back and redo this.
 * @author Jacob Barde
 */
public class WeatherMeasurementDataSet {
	public static final int OPACITY = (int) (RWIS_OPACITY_PERCENTAGE.getInt() * (0xFF/100));
	public static final Color LOCOLOR = new Color(LCOLOR.getRed(), LCOLOR.getGreen(), LCOLOR.getBlue(), OPACITY);
	public static final Color MOCOLOR = new Color(MCOLOR.getRed(), MCOLOR.getGreen(), MCOLOR.getBlue(), OPACITY);
	public static final Color HOCOLOR = new Color(HCOLOR.getRed(), HCOLOR.getGreen(), HCOLOR.getBlue(), OPACITY);

	public ItemStyle getDatatype() {
		return datatype;
	}

	private ItemStyle datatype = ItemStyle.WIND_SPEED;

	private Map<Color, List<WeatherMeasurementSample>> list = new HashMap<Color, List<WeatherMeasurementSample>>();

	public WeatherMeasurementDataSet() {
		init();
	}

	public void changeDataType(ItemStyle mtype) {
		datatype = mtype;
		reset();
		init();
	}

	public void init() {
		list.put(LOCOLOR, new ArrayList<WeatherMeasurementSample>());
		list.put(MOCOLOR, new ArrayList<WeatherMeasurementSample>());
		list.put(HOCOLOR, new ArrayList<WeatherMeasurementSample>());
	}

	public void add(WeatherSensor ws) {
		addSample(ws);
	}

	private void addSample(WeatherSensor ws) {
		if(ws == null)
			return;
		Integer measurement = getMeasurement(ws, datatype);
		Color c = getMeasurementColor(datatype, measurement);
		if(c != null && measurement != null)
			list.get(c).add(new WeatherMeasurementSample(ws, measurement, c, datatype));
	}

	public List<WeatherMeasurementSample> getList(Color c) {
		return list.get(c);
	}

	public void reset() {
		clearAll();
	}

	private void clearAll() {
		list.clear();
	}

	public static Integer getMeasurement(WeatherSensor ws, ItemStyle mtype) {

		switch (mtype) {
		case AIR_TEMP:
			return ws.getAirTemp();
		case WIND_SPEED:
			return ws.getWindSpeed();
		case PRECIPITATION:
			return ws.getPrecipRate();
		case VISIBILITY:
			return ws.getPrecipRate();
		}

		return null;
	}

	public static Color getMeasurementColor(ItemStyle mtype, Integer x) {

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
