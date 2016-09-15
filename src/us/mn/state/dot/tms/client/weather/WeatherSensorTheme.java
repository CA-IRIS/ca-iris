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
import java.awt.geom.Point2D;

import us.mn.state.dot.tms.client.map.*;
import us.mn.state.dot.tms.*;
import us.mn.state.dot.tms.client.ToolTipBuilder;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.weather.markers.DirectionMarker;
import us.mn.state.dot.tms.client.weather.markers.PrecipitationMarker;
import us.mn.state.dot.tms.client.weather.markers.TemperatureMarker;
import us.mn.state.dot.tms.client.weather.markers.VisibilityMarker;
import us.mn.state.dot.tms.utils.STime;

import javax.swing.*;

import static us.mn.state.dot.tms.ItemStyle.AIR_TEMP;
import static us.mn.state.dot.tms.ItemStyle.AWS;
import static us.mn.state.dot.tms.ItemStyle.CRAZY;
import static us.mn.state.dot.tms.ItemStyle.EXPIRED;
import static us.mn.state.dot.tms.ItemStyle.NORMAL;
import static us.mn.state.dot.tms.ItemStyle.NO_CONTROLLER;
import static us.mn.state.dot.tms.ItemStyle.PRECIPITATION;
import static us.mn.state.dot.tms.ItemStyle.VISIBILITY;
import static us.mn.state.dot.tms.ItemStyle.WIND_SPEED;
import static us.mn.state.dot.tms.SystemAttrEnum.RWIS_COLOR_HIGH;
import static us.mn.state.dot.tms.SystemAttrEnum.RWIS_COLOR_LOW;
import static us.mn.state.dot.tms.SystemAttrEnum.RWIS_COLOR_MID;
import static us.mn.state.dot.tms.WeatherSensorHelper.getAirTempCelsius;
import static us.mn.state.dot.tms.WeatherSensorHelper.getMultiTempsString;
import static us.mn.state.dot.tms.WeatherSensorHelper.getPrecipRate;
import static us.mn.state.dot.tms.WeatherSensorHelper.getVisibilityMeters;
import static us.mn.state.dot.tms.WeatherSensorHelper.getWindSpeedKph;
import static us.mn.state.dot.tms.WeatherSensorHelper.isAwsState;
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
	static public final Color LCOLOR = RWIS_COLOR_LOW.getColor();

	/** The "mid" color */
	static public final Color MCOLOR = RWIS_COLOR_MID.getColor();

	/** The "high" color */
	static public final Color HCOLOR = RWIS_COLOR_HIGH.getColor();

	/** Symbols for measurement types */
	static private final VectorSymbol SYM_AIR_TEMP =
		new VectorSymbol(new TemperatureMarker(), lsize);
	static private final VectorSymbol SYM_PRECIP =
		new VectorSymbol(new PrecipitationMarker(), lsize);
	static private final VectorSymbol SYM_VIS =
		new VectorSymbol(new VisibilityMarker(), lsize);
	static private final VectorSymbol SYM_WIND_SPEED =
		new VectorSymbol(new DirectionMarker(), lsize);

	/** Styles for measurement types */
	static private final Style[] STY_AIR_TEMP = createStyle(AIR_TEMP);
	static private final Style[] STY_PRECIP = createStyle(PRECIPITATION);
	static private final Style[] STY_VIS = createStyle(VISIBILITY);
	static private final Style[] STY_WIND_SPEED = createStyle(WIND_SPEED);

	/** Create a new proxy theme */
	public WeatherSensorTheme(WeatherSensorManager m) {
		super (m, new DirectionMarker());

		addStyle(EXPIRED, Color.BLACK);
		addStyle(CRAZY, ProxyTheme.COLOR_CRAZY);
		addStyle(AWS, ProxyTheme.COLOR_AWS_DEPLOYED);
		addStyle(NO_CONTROLLER, ProxyTheme.COLOR_NO_CONTROLLER);
		addStyle(NORMAL, Color.GREEN);

		addStyle(WIND_SPEED, LCOLOR);
		addStyle(VISIBILITY, LCOLOR);
		addStyle(PRECIPITATION, LCOLOR);
		addStyle(AIR_TEMP, LCOLOR);

		addStyle(ItemStyle.ALL);
	}


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

	/** Get the style for the given weather sensor, based on the measurement
	 *  and its value */
	@Override
	public Style getStyle(WeatherSensor ws) {

		Boolean lb;
		Boolean hb;
		Number n;

		ItemStyle is = manager.getStyleSummary().getStyle();
		Style[] base = null;

		if (EXPIRED.equals(is) && isSampleExpired(ws))
			return super.getStyle(ws);
		if (CRAZY.equals(is) && isCrazyState(ws))
			return super.getStyle(ws);
		if (AWS.equals(is) && isAwsState(ws))
			return super.getStyle(ws);

		switch (is) {
		case WIND_SPEED:
			base = STY_WIND_SPEED;
			lb = isLowWind(ws);
			hb = isHighWind(ws);
			n = getWindSpeedKph(ws);
			break;

		case AIR_TEMP:
			base = STY_AIR_TEMP;
			lb = isLowAirTempCelsius(ws);
			hb = isHighAirTempCelsius(ws);
			n = getAirTempCelsius(ws);
			break;

		case PRECIPITATION:
			base = STY_PRECIP;
			lb = isLowPrecipRate(ws);
			hb = isHighPrecipRate(ws);
			n = getPrecipRate(ws);
			break;

		case VISIBILITY:
			base = STY_VIS;
			lb = isLowVisibility(ws);
			hb = isHighVisibility(ws);
			n = getVisibilityMeters(ws);
			break;

		default:
			return super.getStyle(ws);
		}

		if (n == null)
			return super.getStyle(ws);

		if (lb)
			return base[0];
		if (hb)
			return base[2];
		return base[1];
	}

	private static Style[] createStyle(ItemStyle is) {
		Style[] rv = new Style[3];
		rv[0] = new Style(is.toString(), OUTLINE, LCOLOR, true);
		rv[1] = new Style(is.toString(), OUTLINE, MCOLOR, false);
		rv[2] = new Style(is.toString(), OUTLINE, HCOLOR, false);
		return rv;
	}

	@Override
	public void setScale(float scale) {
		lookupSymbol().setScale(scale);
	}

	@Override
	public void draw(Graphics2D g, MapObject mo) {
		lookupSymbol().draw(g, mo, getStyle(mo));
	}

	@Override
	public void drawSelected(Graphics2D g, MapObject mo) {
		lookupSymbol().drawSelected(g, mo, getStyle(mo));
	}

	@Override
	public boolean hit(Point2D p, MapObject mo) {
		return lookupSymbol().hit(p, mo);
	}

	private Symbol lookupSymbol() {
		ItemStyle is = manager.getStyleSummary().getStyle();

		switch (is) {
		case VISIBILITY:
			return SYM_VIS;
		case PRECIPITATION:
			return SYM_PRECIP;
		case AIR_TEMP:
			return SYM_AIR_TEMP;
		default:
			return SYM_WIND_SPEED;
		}
	}
}
