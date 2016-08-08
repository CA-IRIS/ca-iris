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

import javax.swing.*;

import static us.mn.state.dot.tms.ItemStyle.AIR_TEMP;
import static us.mn.state.dot.tms.ItemStyle.PRECIPITATION;
import static us.mn.state.dot.tms.ItemStyle.VISIBILITY;
import static us.mn.state.dot.tms.ItemStyle.WIND_SPEED;
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
import static us.mn.state.dot.tms.client.weather.WeatherSensorManager.DIRECTION_MARKER;
import static us.mn.state.dot.tms.client.weather.WeatherSensorManager.PRECIP_MARKER;
import static us.mn.state.dot.tms.client.weather.WeatherSensorManager.TEMP_MARKER;
import static us.mn.state.dot.tms.client.weather.WeatherSensorManager.VIS_MARKER;

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

	/** Symbols for low, mid, and high air temp */
	private static final VectorSymbol SYM_AIR_TEMP =
		new VectorSymbol(TEMP_MARKER, lsize);

	/** Symbols for low, mid, and high precipitation */
	private static final VectorSymbol SYM_PRECIP =
		new VectorSymbol(PRECIP_MARKER, lsize);

	/** Symbols for low, mid, and high visibility */
	private static final VectorSymbol SYM_VIS =
		new VectorSymbol(VIS_MARKER, lsize);

	/** Symbols for low, mid, and high wind speed */
	private static final VectorSymbol SYM_WIND_SPEED =
		new VectorSymbol(DIRECTION_MARKER, lsize);

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

		//FIXME CA-MN-MERGE will it blend?
		addStyle(new Style(WIND_SPEED.name(), OUTLINE, LCOLOR));
		addStyle(new Style(VISIBILITY.name(), OUTLINE, LCOLOR));
		addStyle(new Style(PRECIPITATION.name(), OUTLINE, LCOLOR));
		addStyle(new Style(AIR_TEMP.name(), OUTLINE, LCOLOR));

		addStyle(ItemStyle.ALL);
	}

	//FIXME CA-MN-MERGE will it blend?
	/** Get a legend icon for a style */
	@Override
	public Icon getLegend(Style sty) {
		ItemStyle is = ItemStyle.lookupStyle(sty.toString());
		switch (is) {
		case WIND_SPEED:
			return SYM_WIND_SPEED.getLegend(sty);
		case VISIBILITY:
			return SYM_VIS.getLegend(sty);
		case PRECIPITATION:
			return SYM_PRECIP.getLegend(sty);
		case AIR_TEMP:
			return SYM_AIR_TEMP.getLegend(sty);
		default:
			return super.getLegend(sty);
		}
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

	//FIXME CA-MN-MERGE will it blend?
	/** Get the style for the given map object */
	@Override
	public Style getStyle(MapObject mo) {
		Style ret = null;
		WeatherSensor ws = manager.findProxy(mo);

		if (ws != null)
			ret = getStyle(ws);
		if (ret == null)
			ret = super.getStyle(mo);

		return ret;
	}

	//FIXME CA-MN-MERGE will it blend?
	/** Get the style for the given weather sensor, based on the measurement
	 *  and its value */
	@Override
	public Style getStyle(WeatherSensor ws) {

		if (isSampleExpired(ws) || isCrazyState(ws))
			return null;

		Boolean lb;
		Boolean hb;
		Number n;

		ItemStyle is = manager.getStyleSummary().getStyle();
		switch (is) {
		case AIR_TEMP:
			lb = isLowAirTempCelsius(ws);
			hb = isHighAirTempCelsius(ws);
			n = getAirTempCelsius(ws);
			break;

		case PRECIPITATION:
			lb = isLowPrecipRate(ws);
			hb = isHighPrecipRate(ws);
			n = getPrecipRate(ws);
			break;

		case VISIBILITY:
			lb = isLowVisibility(ws);
			hb = isHighVisibility(ws);
			n = getVisibilityMeters(ws);
			break;

		case WIND_SPEED:
			lb = isLowWind(ws);
			hb = isHighWind(ws);
			n = getWindSpeedKph(ws);
			break;

		default:
			return null;
		}

		// TODO: MnDOT has steered away from using static instances
		// for reasons unknown (see markers). In keeping with this, the
		// styles below are done in the same manner. See if this hinders
		// performance.
		if (n == null)
			return null;
		if (lb)
			return new Style(is.name(), OUTLINE, LCOLOR);
		if (hb)
			return new Style(is.name(), OUTLINE, HCOLOR);

		return new Style(is.name(), OUTLINE, MCOLOR);
	}
}
