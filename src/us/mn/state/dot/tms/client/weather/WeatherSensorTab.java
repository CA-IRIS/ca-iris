/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.weather;

import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.StyleSummary;

import java.awt.*;

/**
 * Provides a GUI for the weather sensor tab on the operator interface for IRIS.
 *
 * @author Jacob Barde
 */
public class WeatherSensorTab extends MapTab<WeatherSensor> {

	private final WeatherSensorDispatcher dispatcher;

	private final StyleSummary<WeatherSensor> summary;

	public WeatherSensorTab(Session session, WeatherSensorManager man) {
		super(man);
		dispatcher = new WeatherSensorDispatcher(session, man);
		summary = man.createStyleSummary();
		add(dispatcher, BorderLayout.NORTH);
		add(summary, BorderLayout.CENTER);
	}

	/** Get the tab ID */
	@Override
	public String getTabId() {

		return "weather_sensor";
	}

	/** Initialize the map tab */
	@Override
	public void initialize() {
		dispatcher.initialize();
		summary.initialize();
	}

	/** Perform any clean up necessary */
	@Override
	public void dispose() {
		super.dispose();
		dispatcher.dispose();
		summary.dispose();
	}

}
