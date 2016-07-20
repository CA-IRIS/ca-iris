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

import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.roads.LocationPanel;

/**
 * PropLocation is a GUI panel for displaying and editing locations on a
 * weather sensor properties form.
 *
 * @author Travis Swanston
 */
public class PropLocation extends LocationPanel {

	/** Weather sensor */
	private final WeatherSensor weather_sensor;

	/** Create a new weather sensor properties location panel */
	public PropLocation(Session s, WeatherSensor ws) {
		super(s);
		weather_sensor = ws;
	}

}
