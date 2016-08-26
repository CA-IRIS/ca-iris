/*
 * IRIS -- Intelligent Roadway Information System
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
package us.mn.state.dot.tms.utils;

import java.util.List;
import us.mn.state.dot.tms.geo.Position;

/**
 * methods and functions relating to GPS coordinates
 * @author Jacob Barde
 */
public class GPSutil {

	private GPSutil() {

	}

	/**
	 * given a list of positions, determine the geographical center of list
	 * @param pl list of positions
	 *
	 * @return position of geographical center
	 */
	static public Position getGeographicCenter(List<Position> pl) {
		Position rv = null;
		double northern = -90.0;
		double southern = +90.0;
		double eastern = -180.0;
		double western = +180.0;
		double lat;
		double lon;

		if (!pl.isEmpty()) {
			for (Position p : pl) {
				lat = p.getLatitude();
				lon = p.getLongitude();
				northern = Math.max(northern, lat);
				southern = Math.min(southern, lat);
				eastern = Math.max(eastern, lon);
				western = Math.min(western, lon);
			}

			double latDiff = (northern + 90) - (southern + 90);
			double lonDiff = (eastern + 180) - (western + 180);
			lat = northern - latDiff;
			lon = eastern - lonDiff;
			rv = new Position(lat, lon);
		}

		return rv;
	}
}
