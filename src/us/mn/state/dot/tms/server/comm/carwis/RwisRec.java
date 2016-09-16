/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.carwis;

import java.util.ArrayList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.server.WeatherSensorImpl;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;

/**
 * An RWIS record.
 *
 * @author Travis Swanston
 */
public class RwisRec {

	/** Get the duration of a record in milliseconds */
	static private long durationMs() {
		return 20 * 1000;
	}

	/** Creation time */
	private final long create_time;

	/** Get the RWIS site ID */
	public String getSiteId() {
		return site_id;
	}

	/** RWIS site id */
	private final String site_id;

	/** Observation time */
	public Long obs_time = null;

	/** Air temperature */
	public Integer air_temp = null;

	/** Precipitation rate */
	public Integer precip_rate = null;

	/** Average wind speed in KPH */
	public Integer wind_speed_avg = null;

	/** Average wind direction in degrees */
	public Integer wind_dir_avg = null;

	/** Gust wind speed in KPH */
	public Integer wind_speed_gust = null;

	/** Gust wind direction in degrees */
	public Integer wind_dir_gust = null;

	/** Visibility */
	public Integer visibility = null;

	// individual entries can be null:
	public ArrayList<Integer> surface_temps = new ArrayList<Integer>();
	public ArrayList<Integer> subsurface_temps = new ArrayList<Integer>();

	/** Constructor. */
	public RwisRec(String sid) {
		site_id = sid;
		create_time = TimeSteward.currentTimeMillis();
	}

	/** Check if the record is expired */
	public boolean isExpired() {
		return create_time + durationMs() <
			TimeSteward.currentTimeMillis();
	}

	/**
	 * Add a surface temperature reading.
	 * @param t The temperature in C; can be null
	 */
	public void addSurfaceTemp(Integer t) {
		surface_temps.add(t);
	}

	/**
	 * Add a subsurface temperature reading.
	 * @param t The temperature in C; can be null
	 */
	public void addSubsurfaceTemp(Integer t) {
		subsurface_temps.add(t);
	}

	/** Store the record.  Called via OpRead. */
	public void store(WeatherSensorImpl ws) {
		ws.setAirTempNotify(air_temp);
		ws.setPrecipRateNotify(precip_rate);
		ws.setWindSpeedNotify(wind_speed_avg);
		ws.setGustSpeedNotify(wind_speed_gust);
		ws.setWindDirNotify(wind_dir_avg);
		ws.setGustDirNotify(wind_dir_gust);
		ws.setVisibilityNotify(visibility);
		ws.setSurfaceTempsNotify(surface_temps.toArray(
			new Integer[0]));
		ws.setSubsurfaceTempsNotify(subsurface_temps.toArray(
			new Integer[0]));
		ws.setObsTimeNotify(
			(obs_time!=null) ? obs_time.longValue()
			: MISSING_DATA);
		ws.setStampNotify(create_time);
		CaRwisPoller.log("stored rec=" + this);
	}

}
