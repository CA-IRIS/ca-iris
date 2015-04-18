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
package us.mn.state.dot.tms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Temperature;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;
import us.mn.state.dot.tms.utils.SString;

/**
 * Helper class for weather sensors.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WeatherSensorHelper extends BaseHelper {

	/** All styles */
	static public final ItemStyle[] STYLES_ALL = {
		ItemStyle.NORMAL, ItemStyle.EXPIRED,
		ItemStyle.AWS, ItemStyle.CRAZY
	};


	/** Don't allow instances to be created */
	private WeatherSensorHelper() {
		assert false;
	}

	/** Lookup the weather sensor with the specified name */
	static public WeatherSensor lookup(String name) {
		return (WeatherSensor)namespace.lookupObject(
			WeatherSensor.SONAR_TYPE, name);
	}

	/** Get a weather sensor iterator */
	static public Iterator<WeatherSensor> iterator() {
		return new IteratorWrapper<WeatherSensor>(namespace.iterator(
			WeatherSensor.SONAR_TYPE));
	}

	/**
	 * Return true if the weather sensor is in a normal state.
	 * @param p Proxy; never null.
	 */
	static public boolean isNormalState(WeatherSensor p) {
		return !(isAwsState(p) || isSampleExpired(p) || isCrazyState(p));
	}

	/** Test if the sensor has triggered an AWS state (e.g. high wind) */
	static public boolean isAwsState(WeatherSensor proxy) {
		if (isSampleExpired(proxy))
			return false;
		else
			return (isHighWind(proxy) || isLowVisibility(proxy));
	}

	/** Get the high wind limit in kph */
	static public int getHighWindLimitKph() {
		return SystemAttrEnum.RWIS_HIGH_WIND_SPEED_KPH.getInt();
 	}

	/**
	 * Is sensor in crazy data state
	 * (e.g., implausibly high wind speed)?
	 * TODO: add other plausibility checks
	 */
	static public boolean isCrazyState(WeatherSensor ws) {
		if (getMaxValidWindSpeedKph() <= 0)
			return false;
		Integer windSpeed = ws.getWindSpeed();
		if (windSpeed == null)
			return false;
		return (windSpeed > getMaxValidWindSpeedKph());
	}

	/** Is wind speed high? */
	static public boolean isHighWind(WeatherSensor ws) {
		if(isSampleExpired(ws))
			return false;
		Integer s = ws.getWindSpeed();
		if(s == null)
			return false;
		int t = getHighWindLimitKph();
		int m = getMaxValidWindSpeedKph();
		if(m <= 0)
			return s > t;
		else
			return s > t && s <= m;
	}

	/** Get the low visibility limit in meters */
	static public int getLowVisLimitMeters() {
		return SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M.getInt();
	}

	/** Is visibility low? */
	static public boolean isLowVisibility(WeatherSensor ws) {
		if(isSampleExpired(ws))
			return false;
		Integer v = ws.getVisibility();
		return v != null && v < getLowVisLimitMeters();
	}

	/** Get the maximum valid wind speed (kph).
	 * @return Max valid wind speed (kph) or 0 for no maximum. */
	static public int getMaxValidWindSpeedKph() {
		return SystemAttrEnum.RWIS_MAX_VALID_WIND_SPEED_KPH.getInt();
	}

	/** Check if the sample data has expired */
	static public boolean isSampleExpired(WeatherSensor ws) {
		long obsTime = ws.getObsTime();
		if (obsTime < 0)
			return true;
		return (obsTime + (getObsAgeLimitSecs() * 1000) <
			TimeSteward.currentTimeMillis());
	}

	/** Get the sensor observation age limit (secs).
	 * @return The sensor observation age limit. Valid observations have
	 *	   an age less than or equal to this value. Zero indicates 
	 *	   observations never expire. */
	static public int getObsAgeLimitSecs() {
		return SystemAttrEnum.RWIS_OBS_AGE_LIMIT_SECS.getInt();
 	}

	/** Get the high precipitation limit in mm/hr */
	static public int getHighPrecipRate() {
		// FIXME: add a system attribute
		return 8;
 	}

	/** Is precipitation rate high? */
	static public boolean isHighPrecipRate(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer pr = ws.getPrecipRate();
		return (pr != null) && pr >= getHighPrecipRate();
	}

	/**
	 * Return the direction as a human readable string.
	 * @param degs Direction in degrees or MISSING_DATA.
	 * @return The direction as N, NE, E, SE, S, SW, W, NW, or ?
	 * TODO: use Angle class for this?
	 */
	static public String getDirection(int degs) {
		if (degs == MISSING_DATA)
			return "?";
		degs = degs % 360;
		if (degs <= 22)
			return "N";
		else if (degs >= 23 && degs <= 68)
			return "NE";
		else if (degs >= 69 && degs <= 112)
			return "E";
		else if (degs >= 113 && degs <= 158)
			return "SE";
		else if (degs >= 159 && degs <= 202)
			return "S";
		else if (degs >= 203 && degs <= 248)
			return "SW";
		else if (degs >= 249 && degs <= 292)
			return "W";
		else if (degs >= 293 && degs <= 337)
			return "NW";
		else
			return "N";
	}

	/**
	 * Does the proxy match the specified style?
	 * @param is The ItemStyle; may be null.
	 * @param p WeatherSensor proxy; never null.
	 * @return True if the proxy matches the style, else false.
	 */
	static public boolean checkStyle(ItemStyle is, WeatherSensor p) {
		switch(is) {
		case NORMAL:
			return isNormalState(p);
		case EXPIRED:
			return isSampleExpired(p);
		case AWS:
			return isAwsState(p);
		case CRAZY:
			return isCrazyState(p);
		}
		return false;
	}

	/**
	 * Return a string that contains all active styles, separated
	 * by commas.
	 */
	static public String getAllStyles(WeatherSensor proxy) {
		StringBuilder s = new StringBuilder("");
		for(ItemStyle style: STYLES_ALL)
			if(checkStyle(style, proxy))
				s.append(style).append(", ");
		return SString.removeTail(s.toString(), ", ");
	}

	/**
	 * Return a comma-delimited string that contains the given
	 * temperatures, in order, and in the currently configured unit
	 * system.
	 * @param temps the temperatures
	 * @return The string
	 */
	static public String getMultiTempsString(Integer[] temps) {
		String ts = "";
		if (temps == null)
			return ts;
		for (int i=0; i<temps.length; ++i) {
			if (temps[i] != null)
				ts += new Temperature(temps[i]).toString2();
			else
				ts += "?";
			if (i < (temps.length-1))
				ts += ",";
		}
		return ts;
	}

}
