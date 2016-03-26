/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
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

import java.awt.*;

import us.mn.state.dot.map.*;
import us.mn.state.dot.tms.*;
import us.mn.state.dot.tms.client.ToolTipBuilder;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.utils.STime;

/**
 * Theme for weather sensor objects on the map.
 *
 * @author Michael Darter
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class WeatherSensorTheme extends ProxyTheme<WeatherSensor> {

	/** The "low" color */
	private static final Color LCOLOR = SystemAttrEnum.RWIS_COLOR_LOW.getColor();

	/** The "mid" color */
	private static final Color MCOLOR = SystemAttrEnum.RWIS_COLOR_MID.getColor();

	/** The "high" color */
	private static final Color HCOLOR = SystemAttrEnum.RWIS_COLOR_HIGH.getColor();

	/** Index for low symbol */
	private static final int LOW_IDX = 0;

	/** Index for mid symbol */
	private static final int MID_IDX = 1;

	/** Index for high symbol */
	private static final int HIGH_IDX = 2;

	/** Symbols for low, mid, and high air temp */
	private static final VectorSymbol[] AIR_TEMP_SYMS = getStyleSymbols(
		ItemStyle.AIR_TEMP,
		WeatherSensorManager.TEMP_MARKER);

	/** Symbols for low, mid, and high precipitation */
	private static final VectorSymbol[] PRECIP_SYMS = getStyleSymbols(
		ItemStyle.PRECIPITATION,
		WeatherSensorManager.PRECIP_MARKER);

	/** Symbols for low, mid, and high visibility */
	private static final VectorSymbol[] VIS_SYMS = getStyleSymbols(
		ItemStyle.VISIBILITY,
		WeatherSensorManager.VIS_MARKER);

	/** Symbols for low, mid, and high wind speed */
	private static final VectorSymbol[] WIND_SPEED_SYMS = getStyleSymbols(
		ItemStyle.WIND_SPEED,
		WeatherSensorManager.DIRECTION_MARKER);

	/** Create a new proxy theme */
	public WeatherSensorTheme(ProxyManager<WeatherSensor> m, Shape s) {
		super (m, s);
		addStyle(ItemStyle.EXPIRED, Color.BLACK);
		addStyle(ItemStyle.CRAZY,
			ProxyTheme.COLOR_CRAZY);
		addStyle(ItemStyle.AWS, ProxyTheme.COLOR_AWS_DEPLOYED);
		addStyle(ItemStyle.NORMAL, Color.GREEN);
		addStyle(ItemStyle.NO_CONTROLLER,
			ProxyTheme.COLOR_NO_CONTROLLER);

		// FIXME: This is nasty and relies on undocumented
		// behavior that is probably actually a proxy bug
		addStyle(WIND_SPEED_SYMS[LOW_IDX].style);
		addSymbol(WIND_SPEED_SYMS[LOW_IDX]);
		addStyle(VIS_SYMS[LOW_IDX].style);
		addSymbol(VIS_SYMS[LOW_IDX]);
		addStyle(PRECIP_SYMS[LOW_IDX].style);
		addSymbol(PRECIP_SYMS[LOW_IDX]);
		addStyle(AIR_TEMP_SYMS[LOW_IDX].style);
		addSymbol(AIR_TEMP_SYMS[LOW_IDX]);
		// FIXME: END

		addStyle(ItemStyle.ALL);
	}

	/** Generated low, mid, and high symbols fir the given args */
	private static VectorSymbol[] getStyleSymbols(ItemStyle is, Shape s) {
		final VectorSymbol[] ret = new VectorSymbol[3];
		Style style = new Style(is.toString(), OUTLINE, LCOLOR);
		ret[LOW_IDX] = new VectorSymbol(style, s);
		style = new Style(is.toString(), OUTLINE, MCOLOR);
		ret[MID_IDX] = new VectorSymbol(style, s);
		style = new Style(is.toString(), OUTLINE, HCOLOR);
		ret[HIGH_IDX] = new VectorSymbol(style, s);
		return ret;
	}

	/** Get tooltip text for the given map object */
	public String getTip(MapObject o) {
		WeatherSensor p = manager.findProxy(o);
		if (p == null)
			return null;
		ToolTipBuilder t = new ToolTipBuilder();

		t.addLine(manager.getDescription(p));
		t.addLine(GeoLocHelper.getDescription(p.getGeoLoc()));

		t.addLine("Visibility",
			new Length(p.getVisibility()).toString2());

		Integer ws = p.getWindSpeed();
		Integer wd = p.getWindDir();
		String windVal = "";
		if ((ws != null) && (!("".equals(ws)))) {
			windVal += new Speed(ws).toString2();
			windVal += " ";
			}
		if ((wd != null) && (!("".equals(wd)))) {
			windVal += new Angle(wd).toShortDir();
			}
		t.addLine("Wind avg.", windVal);

		Integer gs = p.getGustSpeed();
		Integer gd = p.getGustDir();
		String gustVal = "";
		if ((gs != null) && (!("".equals(gs)))) {
			gustVal += new Speed(gs).toString2();
			gustVal += " ";
			}
		if ((gd != null) && (!("".equals(gd)))) {
			gustVal += new Angle(gd).toShortDir();
			}
		t.addLine("Wind gust", gustVal);

		t.addLine("Air temp",
			new Temperature(p.getAirTemp()).toString2());

		t.addLine("Surface temps", WeatherSensorHelper
			.getMultiTempsString(p.getSurfaceTemps()));
		t.addLine("Subsurface temps", WeatherSensorHelper
			.getMultiTempsString(p.getSubsurfaceTemps()));

		t.addLine("Precip rate", p.getPrecipRate());

		t.addLineMaybe("Crazy data state",
			WeatherSensorHelper.isCrazyState(p));
		t.addLineMaybe("Low visibility state", WeatherSensorHelper
			.isLowVisibility(p));
		t.addLineMaybe("High wind state", WeatherSensorHelper
			.isHighWind(p));
		t.addLineMaybe("Last sample is expired", WeatherSensorHelper
			.isSampleExpired(p));

		t.setLast();
		String obsTime = STime.getDateString(p.getObsTime());
		t.addLine("Obs. time", obsTime.isEmpty() ? "?" : obsTime);
		return t.get();
	}

	/** Get a symbol for the given map object */
	public Symbol getSymbol(MapObject mo) {
		Symbol ret = null;
		WeatherSensor ws = manager.findProxy(mo);

		if (ws != null)
			ret = getSymbol(ws);
		if (ret == null)
			ret = super.getSymbol(mo);

		return ret;
	}

	/** Get a symbol for the given weather sensor */
	private Symbol getSymbol(WeatherSensor ws) {
		Number n = null;
		double l = 0, h = 0;
		Symbol[] syms = null;
		switch (manager.getStyleSummary().getStyle()) {
		case AIR_TEMP:
			syms = AIR_TEMP_SYMS;
			n = ws.getAirTemp();
			l = SystemAttrEnum.RWIS_LOW_AIR_TEMP_C.getFloat();
			h = SystemAttrEnum.RWIS_HIGH_AIR_TEMP_C.getFloat();
			break;

		case PRECIPITATION:
			syms = PRECIP_SYMS;
			n = ws.getPrecipRate();
			l = SystemAttrEnum.RWIS_LOW_PRECIP_RATE_MMH.getInt();
			h = SystemAttrEnum.RWIS_HIGH_PRECIP_RATE_MMH.getInt();
			break;

		case VISIBILITY:
			syms = VIS_SYMS;
			n = ws.getVisibility();
			l = SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M.getInt();
			h = SystemAttrEnum.RWIS_HIGH_VISIBILITY_DISTANCE_M.getInt();
			break;

		case WIND_SPEED:
			syms = WIND_SPEED_SYMS;
			n = ws.getWindSpeed();
			l = SystemAttrEnum.RWIS_LOW_WIND_SPEED_KPH.getInt();
			h = SystemAttrEnum.RWIS_HIGH_WIND_SPEED_KPH.getInt();
			break;
		}

		return n != null ? getSymbol(syms, n.doubleValue(), l, h) : null;
	}

	/** Get low, mid, or high symbol based on given num and thresholds */
	private Symbol getSymbol(Symbol[] syms, double num, double low, double high) {
		if (num < low)
			return syms[LOW_IDX];
		if (num > high)
			return syms[HIGH_IDX];
		return syms[MID_IDX];
	}
}
