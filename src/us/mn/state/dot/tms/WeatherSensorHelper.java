/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2015  Minnesota Department of Transportation
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

import java.util.Iterator;

import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.utils.SString;

/**
 * Helper class for weather sensors.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class WeatherSensorHelper extends BaseHelper {

	/**
	 * a "clear" day visibility distance defined
	 * <p>
	 * distance to horizon on a clear day from a height of 2 meters is 5048m
	 * distance in meters = 3570 * √h
	 * aviation standards may be higher
	 */
	static private final int CLEAR_DISTANCE_M = 5048;

	/**
	 * crazy high temperature.
	 * world (USA) record is 56.7 ℃ in Death Valley, California
	 * This is higher, in case of global warming
	 */
	static private final float CRAZY_HIGH_AIR_TEMPERATURE_C = 60.0f;

	/**
	 * crazy low temperature.
	 * USA record is -62.1 ℃ at Prospect Creek Camp, Alaska
	 * This is lower, in case of global warming
	 */
	static private final float CRAZY_LOW_AIR_TEMPERATURE_C = -65.0f;

	/**
	 * crazy high precipitation rate
	 * world record is 304.8 mm/hr at Holt, Missouri
	 * This is higher, in case of global warming
	 */
	static private final int CRAZY_PRECIP_RATE = 310;

	/** All styles */
	static public final ItemStyle[] STYLES_ALL = {
		ItemStyle.NORMAL, ItemStyle.EXPIRED,
		ItemStyle.AWS, ItemStyle.CRAZY,
		ItemStyle.AIR_TEMP, ItemStyle.PRECIPITATION,
		ItemStyle.WIND_SPEED, ItemStyle.VISIBILITY
	};


	/** Don't allow instances to be created */
	private WeatherSensorHelper() {
		assert false;
	}

	/** Lookup the weather sensor with the specified name */
	static public WeatherSensor lookup(String name) {
		return (WeatherSensor) namespace.lookupObject(
			WeatherSensor.SONAR_TYPE, name);
	}

	/** Get a weather sensor iterator */
	static public Iterator<WeatherSensor> iterator() {
		return new IteratorWrapper<WeatherSensor>(namespace.iterator(
			WeatherSensor.SONAR_TYPE));
	}

	/**
	 * Return true if the weather sensor is in a normal state.
	 *
	 * @param p Proxy; never null.
	 */
	static public boolean isNormalState(WeatherSensor p) {
		return !(isAwsState(p) || isSampleExpired(p) || isCrazyState(
			p));
	}

	/** Test if the sensor has triggered an AWS state (e.g. high wind) */
	static public boolean isAwsState(WeatherSensor proxy) {
		if (isSampleExpired(proxy))
			return false;
		else
			return (isHighWind(proxy) || isLowVisibility(proxy));
	}

	/** Get the high air temperature limit in ℃ */
	static public float getHighAirTempCelsius() {
		return SystemAttrEnum.RWIS_HIGH_AIR_TEMP_C.getFloat();
	}

	/** Get the low air temperature limit in ℃ */
	static public float getLowAirTempCelsius() {
		return SystemAttrEnum.RWIS_LOW_AIR_TEMP_C.getFloat();
	}

	/** get the air temperature in ℃ */
	static public Float getAirTempCelsius(WeatherSensor ws) {
		return isSampleExpired(ws) ? null : ws.getAirTemp()
			.floatValue();

	}

	/** is high air temperature? */
	static public boolean isHighAirTempCelsius(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;

		Integer s = ws.getAirTemp();
		if (s == null)
			return false;

		float t = s.floatValue();
		float h = getHighAirTempCelsius();
		float m = CRAZY_HIGH_AIR_TEMPERATURE_C;
		return (t <= m && t > h);
	}

	/** is low air temperature? */
	static public boolean isLowAirTempCelsius(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;

		Integer s = ws.getAirTemp();
		if (s == null)
			return false;

		float t = s.floatValue();
		float l = getLowAirTempCelsius();
		float m = CRAZY_LOW_AIR_TEMPERATURE_C;
		return (t >= m && t < l);
	}

	/** Get the high wind limit in kph */
	static public int getHighWindLimitKph() {
		return SystemAttrEnum.RWIS_HIGH_WIND_SPEED_KPH.getInt();
	}

	/** Get the low wind limit in kph */
	static public int getLowWindLimitKph() {
		return SystemAttrEnum.RWIS_LOW_WIND_SPEED_KPH.getInt();
	}

	/**
	 * Get the maximum valid wind speed (kph).
	 *
	 * @return Max valid wind speed (kph) or 0 for no maximum.
	 */
	static public int getMaxValidWindSpeedKph() {
		return SystemAttrEnum.RWIS_MAX_VALID_WIND_SPEED_KPH.getInt();
	}

	/** Get the wind speed in kph */
	static public Integer getWindSpeedKph(WeatherSensor ws) {
		return isSampleExpired(ws) ? null : ws.getWindSpeed();
	}

	/**
	 * Is sensor in crazy data state
	 * (e.g., implausibly high wind speed)?
	 */
	static public boolean isCrazyState(WeatherSensor ws) {
		if (getMaxValidWindSpeedKph() <= 0)
			return false;

		Integer windSpeed = getWindSpeedKph(ws);
		if (windSpeed > getMaxValidWindSpeedKph())
			return true;

		Integer precip = getPrecipRate(ws);
		if (precip > CRAZY_PRECIP_RATE
			|| precip < 0)
			return true;

		Float airTemp = getAirTempCelsius(ws);
		if (airTemp > CRAZY_HIGH_AIR_TEMPERATURE_C
			|| airTemp < CRAZY_LOW_AIR_TEMPERATURE_C)
			return true;

		return false;
	}

	/** Is wind speed high? */
	static public boolean isHighWind(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer s = ws.getWindSpeed();
		if (s == null)
			return false;
		int t = getHighWindLimitKph();
		int m = getMaxValidWindSpeedKph();
		if (m <= 0)
			return s > t;
		else
			return s > t && s <= m;
	}

	/** Is wind speed low? */
	static public boolean isLowWind(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer s = ws.getWindSpeed();
		if (s == null)
			return false;
		int t = getLowWindLimitKph();
		int m = getMaxValidWindSpeedKph();
		if (m <= 0)
			return s < t;
		else
			return s < t && s <= m;
	}

	/**
	 * Get the clear visibility limit in meters.
	 * <p>
	 * Return the standard visibility limit unless the "high" visibility
	 * limit is greater than the standard CLEAR_DISTANCE_M, the returned
	 * value will be either:
	 * a) high + the difference between high and CLEAR_DISTANCE_M
	 * b) high + 50% of high
	 * whichever is lesser.
	 */
	static public int getClearVisibilityLimitMeters() {
		int h = getHighVisibilityLimitMeters();

		if (CLEAR_DISTANCE_M > h)
			return CLEAR_DISTANCE_M;

		int hp50 = (int) (h + h * 0.5);
		int hpdiff = h + (h - CLEAR_DISTANCE_M);
		return Math.min(hp50, hpdiff);
	}

	/** Get the high visibility limit in meters */
	static public int getHighVisibilityLimitMeters() {
		return SystemAttrEnum.RWIS_HIGH_VISIBILITY_DISTANCE_M.getInt();
	}

	/** Get the low visibility limit in meters */
	static public int getLowVisLimitMeters() {
		return SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M.getInt();
	}

	static public Integer getVisibilityMeters(WeatherSensor ws) {
		return isSampleExpired(ws) ? null : ws.getVisibility();
	}

	/** Is visibility low? */
	static public boolean isLowVisibility(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer v = ws.getVisibility();
		return v != null && v < getLowVisLimitMeters();
	}

	/** Is visibility high? */
	static public boolean isHighVisibility(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer v = ws.getVisibility();
		return v != null && v > getHighVisibilityLimitMeters();
	}

	/** is visibility considered "clear"? */
	static public boolean isClearVisibility(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer v = ws.getVisibility();
		return v != null && v >= getClearVisibilityLimitMeters();
	}

	/** Check if the sample data has expired */
	static public boolean isSampleExpired(WeatherSensor ws) {
		long obsTime = ws.getObsTime();
		if (obsTime < 0)
			return true;
		return (obsTime + (getObsAgeLimitSecs() * 1000) <
			TimeSteward.currentTimeMillis());
	}

	/**
	 * Get the sensor observation age limit (secs).
	 *
	 * @return The sensor observation age limit. Valid observations have
	 * an age less than or equal to this value. Zero indicates
	 * observations never expire.
	 */
	static public int getObsAgeLimitSecs() {
		return SystemAttrEnum.RWIS_OBS_AGE_LIMIT_SECS.getInt();
	}

	/** Get the high precipitation limit in mm/hr */
	static public int getHighPrecipRate() {
		return SystemAttrEnum.RWIS_HIGH_PRECIP_RATE_MMH.getInt();
	}

	/** Get the low precipitation limit in mm/hr */
	static public int getLowPrecipRate() {
		return SystemAttrEnum.RWIS_LOW_PRECIP_RATE_MMH.getInt();
	}

	/** Is precipitation rate high? */
	static public boolean isHighPrecipRate(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer pr = ws.getPrecipRate();
		return (pr != null) && pr >= getHighPrecipRate();
	}

	/** Is precipitation rate low? */
	static public boolean isLowPrecipRate(WeatherSensor ws) {
		if (isSampleExpired(ws))
			return false;
		Integer pr = ws.getPrecipRate();
		return (pr != null) && pr <= getLowPrecipRate();
	}

	/** Get a valid precipitation rate, or null */
	static public Integer getPrecipRate(WeatherSensor ws) {
		return isSampleExpired(ws) ? null : ws.getPrecipRate();
	}

	/**
	 * Does the proxy match the specified style?
	 *
	 * @param is The ItemStyle; may be null.
	 * @param p  WeatherSensor proxy; never null.
	 * @return True if the proxy matches the style, else false.
	 */
	static public boolean checkStyle(ItemStyle is, WeatherSensor p) {
		switch (is) {
		case NORMAL:
			return isNormalState(p);
		case EXPIRED:
			return isSampleExpired(p);
		case AWS:
			return isAwsState(p);
		case CRAZY:
			return isCrazyState(p);
		case AIR_TEMP:
			return null != p.getAirTemp();
		case PRECIPITATION:
			return null != p.getPrecipRate();
		case WIND_SPEED:
			return null != p.getWindSpeed();
		case VISIBILITY:
			return null != p.getVisibility();
		}
		return false;
	}

	/**
	 * Return a string that contains all active styles, separated
	 * by commas.
	 */
	static public String getAllStyles(WeatherSensor proxy) {
		StringBuilder s = new StringBuilder("");
		for (ItemStyle style : STYLES_ALL)
			if (checkStyle(style, proxy))
				s.append(style).append(", ");
		return SString.removeTail(s.toString(), ", ");
	}

	/**
	 * Return a comma-delimited string that contains the given
	 * temperatures, in order, and in the currently configured unit
	 * system.
	 *
	 * @param temps the temperatures
	 * @return The string
	 */
	static public String getMultiTempsString(Integer[] temps) {
		String ts = "";
		if (temps == null)
			return ts;
		for (int i = 0; i < temps.length; ++i) {
			if (temps[i] != null)
				ts += new Temperature(temps[i]).toString2();
			else
				ts += "?";
			if (i < (temps.length - 1))
				ts += ",";
		}
		return ts;
	}

}
