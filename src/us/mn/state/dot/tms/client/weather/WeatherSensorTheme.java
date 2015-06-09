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

import java.awt.Color;
import java.awt.Shape;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.tms.Angle;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.Length;
import us.mn.state.dot.tms.SystemAttributeHelper;
import us.mn.state.dot.tms.Speed;
import us.mn.state.dot.tms.Temperature;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.ToolTipBuilder;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.utils.STime;

/**
 * Theme for weather sensor objects on the map.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WeatherSensorTheme extends ProxyTheme<WeatherSensor> {

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
		addStyle(ItemStyle.ALL);
	}

	/** Get tooltip text for the given map object */
	public String getTip(MapObject o) {
		WeatherSensor p = manager.findProxy(o);
		if (p == null)
			return null;
		ToolTipBuilder t = new ToolTipBuilder();

		t.addLine(manager.getDescription(p));

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

}
