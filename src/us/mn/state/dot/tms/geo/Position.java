/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       California Department of Transportation
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
package us.mn.state.dot.tms.geo;

import java.util.Formatter;

/**
 * A position is a latitude / longitude pair.  With a geodetic datum, this
 * identifies a position on the Earth.
 *
 * @author Douglas Lau
 * @author Jacob Barde
 */
public class Position {

	/** Mean radius of Earth (in meters) */
	static private final double MEAN_RADIUS = 6371009.0;

	/** Latitude (degrees) */
	protected final double latitude;

	/** Get the latitude (degrees) */
	public double getLatitude() {
		return latitude;
	}

	/** Longitude (degrees) */
	protected final double longitude;

	/** Get the longitude (degrees) */
	public double getLongitude() {
		return longitude;
	}

	/** Create a new position */
	public Position(double lat, double lon) {
		if(lat < -90 || lat > 90)
			throw new IllegalArgumentException("Invalid latitude");
		if(lon < -180 || lon > 180)
			throw new IllegalArgumentException("Invalid longitude");
		latitude = lat;
		longitude = lon;
	}

	/** Calculate the distance to another position (meters).  This method
	 * uses the Haversine formula to calculate distance.
	 * @param op Other position.
	 * @return Distance to other position (meters). */
	public double distanceHaversine(Position op) {
		double lat1 = Math.toRadians(latitude);
		double lat2 = Math.toRadians(op.latitude);
		double lon1 = Math.toRadians(longitude);
		double lon2 = Math.toRadians(op.longitude);
		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;
		double sdlat2 = Math.sin(dlat / 2.0);
		double coslat = Math.cos(lat1) * Math.cos(lat2);
		double sdlon2 = Math.sin(dlon / 2.0);
		double a = sdlat2 * sdlat2 + coslat * sdlon2 * sdlon2;
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
		return MEAN_RADIUS * c;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%1$+.4d\u00B0 lat. %2$+.4d\u00B0 long.", latitude, longitude);
		return sb.toString();
	}
}
