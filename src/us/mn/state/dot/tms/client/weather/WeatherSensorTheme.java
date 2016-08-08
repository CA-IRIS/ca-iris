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

import static us.mn.state.dot.tms.WeatherSensorHelper.getAirTempCelsius;
import static us.mn.state.dot.tms.WeatherSensorHelper.getMultiTempsString;
import static us.mn.state.dot.tms.WeatherSensorHelper.getPrecipRate;
import static us.mn.state.dot.tms.WeatherSensorHelper.getVisibilityMeters;
import static us.mn.state.dot.tms.WeatherSensorHelper.getWindSpeedKph;
import static us.mn.state.dot.tms.WeatherSensorHelper.isCrazyState;
import static us.mn.state.dot.tms.WeatherSensorHelper.isHighAirTempCelsius;
import static us.mn.state.dot.tms.WeatherSensorHelper.isHighPrecipRate;
import static us.mn.state.dot.tms.WeatherSensorHelper.isHighVisibility;
import static us.mn.state.dot.tms.WeatherSensorHelper.isHighWind;
import static us.mn.state.dot.tms.WeatherSensorHelper.isLowAirTempCelsius;
import static us.mn.state.dot.tms.WeatherSensorHelper.isLowPrecipRate;
import static us.mn.state.dot.tms.WeatherSensorHelper.isLowVisibility;
import static us.mn.state.dot.tms.WeatherSensorHelper.isLowWind;
import static us.mn.state.dot.tms.WeatherSensorHelper.isSampleExpired;

/**
 * Theme for weather sensor objects on the map.
 *
 * @author Michael Darter
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class WeatherSensorTheme extends ProxyTheme<WeatherSensor> {

	/** The "low" color */
	public static final Color LCOLOR = SystemAttrEnum.RWIS_COLOR_LOW.getColor();

	/** The "mid" color */
	public static final Color MCOLOR = SystemAttrEnum.RWIS_COLOR_MID.getColor();

	/** The "high" color */
	public static final Color HCOLOR = SystemAttrEnum.RWIS_COLOR_HIGH.getColor();

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
	public WeatherSensorTheme(ProxyManager<WeatherSensor> m, AbstractMarker s) {
		super (m, s);
		addStyle(ItemStyle.EXPIRED, Color.BLACK);
		addStyle(ItemStyle.CRAZY,
			ProxyTheme.COLOR_CRAZY);
		addStyle(ItemStyle.AWS, ProxyTheme.COLOR_AWS_DEPLOYED);
		addStyle(ItemStyle.NORMAL, Color.GREEN);
		addStyle(ItemStyle.NO_CONTROLLER,
			ProxyTheme.COLOR_NO_CONTROLLER);

		//FIXME CA-MN-MERGE
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

	//FIXME CA-MN-MERGE
	/** Generated low, mid, and high symbols for the given args */
	private static VectorSymbol[] getStyleSymbols(ItemStyle is, AbstractMarker s) {
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
		StringBuilder val = new StringBuilder();
		if ((ws != null) && (!("".equals(ws))))
			val.append(new Speed(ws).toString2()).append(" ");
		if ((wd != null) && (!("".equals(wd))))
			val.append(new Angle(wd).toShortDir());
		t.addLine("Wind avg.", val.toString());

		Integer gs = p.getGustSpeed();
		Integer gd = p.getGustDir();
		val = new StringBuilder();
		if ((gs != null) && (!("".equals(gs))))
			val.append(new Speed(gs).toString2()).append(" ");
		if ((gd != null) && (!("".equals(gd))))
			val.append(new Angle(gd).toShortDir());
		t.addLine("Wind gust", val.toString());

		t.addLine("Air temp",
			new Temperature(p.getAirTemp()).toString2());

		t.addLine("Surface temps",
			getMultiTempsString(p.getSurfaceTemps()));
		t.addLine("Subsurface temps",
			getMultiTempsString(p.getSubsurfaceTemps()));

		t.addLine("Precip rate", getPrecipRate(p));

		t.addLineMaybe("Crazy data state", isCrazyState(p));
		t.addLineMaybe("Low visibility state", isLowVisibility(p));
		t.addLineMaybe("High wind state", isHighWind(p));
		t.addLineMaybe("Last sample is expired", isSampleExpired(p));

		t.setLast();
		String obsTime = STime.getDateString(p.getObsTime());
		t.addLine("Obs. time", obsTime.isEmpty() ? "?" : obsTime);
		return t.get();
	}

	//FIXME CA-MN-MERGE
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

		if (isSampleExpired(ws) || isCrazyState(ws))
			return null;

		Boolean lb;
		Boolean hb;
		Number n;

		Symbol[] syms;

		switch (manager.getStyleSummary().getStyle()) {
		case AIR_TEMP:
			syms = AIR_TEMP_SYMS;
			lb = isLowAirTempCelsius(ws);
			hb = isHighAirTempCelsius(ws);
			n = getAirTempCelsius(ws);
			break;

		case PRECIPITATION:
			syms = PRECIP_SYMS;
			lb = isLowPrecipRate(ws);
			hb = isHighPrecipRate(ws);
			n = getPrecipRate(ws);
			break;

		case VISIBILITY:
			syms = VIS_SYMS;
			lb = isLowVisibility(ws);
			hb = isHighVisibility(ws);
			n = getVisibilityMeters(ws);
			break;

		case WIND_SPEED:
			syms = WIND_SPEED_SYMS;
			lb = isLowWind(ws);
			hb = isHighWind(ws);
			n = getWindSpeedKph(ws);
			break;

		default:
			return null;
		}

		if (n == null)
			return null;
		if (lb)
			return syms[LOW_IDX];
		if (hb)
			return syms[HIGH_IDX];

		return syms[MID_IDX];
	}
}
