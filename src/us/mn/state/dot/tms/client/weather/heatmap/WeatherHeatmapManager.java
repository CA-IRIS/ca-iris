/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.weather.heatmap;

import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Angle;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.SonarObjectForm;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;
import us.mn.state.dot.tms.client.weather.WeatherSensorMarker;
import us.mn.state.dot.tms.client.weather.WeatherSensorProperties;
import us.mn.state.dot.tms.client.weather.WeatherSensorTheme;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

import static us.mn.state.dot.tms.client.widget.SwingRunner.runSwing;

/**
 * A weather sensor manager is a container for SONAR weather sensor objects.
 *
 * @author Jacob Barde
 */
public class WeatherHeatmapManager extends WeatherSensorManager {

	private final HeatmapLayer heatmapLayer;

	/** Create a new weather sensor manager */
	public WeatherHeatmapManager(Session s, GeoLocManager lm) {

		super(s, lm);
		heatmapLayer = new HeatmapLayer(s, this);
	}

	/** Create a map tab for the managed proxies */
	@Override
	public WeatherHeatmapTab createTab() {

		return new WeatherHeatmapTab(session, this);
	}

	/**
	 * getter for heatmapLayer
	 * @return
	 */
	public HeatmapLayer getHeatmapLayer() {

		return heatmapLayer;
	}
}
