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
package us.mn.state.dot.tms.server;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;
import us.mn.state.dot.tms.server.comm.DevicePoller;
import us.mn.state.dot.tms.server.comm.WeatherPoller;
import us.mn.state.dot.tms.utils.STime;

/**
 * A weather sensor is a device for sampling weather data, such as precipitation
 * rates, visibility and wind speed.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WeatherSensorImpl extends DeviceImpl implements WeatherSensor {

	/** Sample period for weather sensors (seconds) */
	static protected final int SAMPLE_PERIOD_SEC = 60;

	/** Sample period for weather sensors (ms) */
	static protected final int SAMPLE_PERIOD_MS = SAMPLE_PERIOD_SEC * 1000;

	/** Load all the weather sensors */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, WeatherSensorImpl.class);
		store.query("SELECT name, geo_loc, controller, pin, notes " +
			"FROM iris." + SONAR_TYPE + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new WeatherSensorImpl(
					namespace,
					row.getString(1),	// name
					row.getString(2),	// geo_loc
					row.getString(3),	// controller
					row.getInt(4),		// pin
					row.getString(5)	// notes
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("geo_loc", geo_loc);
		map.put("controller", controller);
		map.put("pin", pin);
		map.put("notes", notes);
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new weather sensor with a string name */
	public WeatherSensorImpl(String n) 
		throws TMSException, SonarException 
	{
		super(n);
		GeoLocImpl g = new GeoLocImpl(name);
		g.notifyCreate();
		geo_loc = g;
		cache = new PeriodicSampleCache(PeriodicSampleType.PRECIP_RATE);
		pt_cache = new PeriodicSampleCache(
			PeriodicSampleType.PRECIP_TYPE);
	}

	/** Create a weather sensor */
	protected WeatherSensorImpl(String n, GeoLocImpl l, ControllerImpl c,
		int p, String nt)
	{
		super(n, c, p, nt);
		geo_loc = l;
		cache = new PeriodicSampleCache(PeriodicSampleType.PRECIP_RATE);
		pt_cache = new PeriodicSampleCache(
			PeriodicSampleType.PRECIP_TYPE);
		initTransients();
	}

	/** Create a weather sensor */
	protected WeatherSensorImpl(Namespace ns, String n, String l, String c,
		int p, String nt)
	{
		this(n, (GeoLocImpl)ns.lookupObject(GeoLoc.SONAR_TYPE, l),
		     (ControllerImpl)ns.lookupObject(Controller.SONAR_TYPE, c),
		     p, nt);
	}

	/** Destroy an object */
	@Override
	public void doDestroy() throws TMSException {
		super.doDestroy();
		geo_loc.notifyRemove();
	}

	/** Device location */
	protected GeoLocImpl geo_loc;

	/** Get the device location */
	public GeoLoc getGeoLoc() {
		return geo_loc;
	}

	/** Air temp in C (null for missing) */
	private transient Integer air_temp;

	/** Get the air temp in C (null if missing) */
	public Integer getAirTemp() {
		return air_temp;
	}

	/** Set the air temperature.
	 * @param at Air temperature in Celsius (null for missing) */
	public void setAirTempNotify(Integer at) {
		if(!integerEquals(at, air_temp)) {
			air_temp = at;
			notifyAttribute("airTemp");
		}
	}

	/** Surface temps in C (never null; entries null for missing) */
	private transient Integer[] surface_temps = new Integer[0];

	/** Get surface temps in C (never null; entries null for missing) */
	public Integer[] getSurfaceTemps() {
		return surface_temps;
	}

	/**
	 * Set the surface temperatures.
	 * @param st Surface temperatures in Celsius (never null; entries null
	 *           for missing)
	 */
	public void setSurfaceTempsNotify(Integer[] st) {
		if (!(surface_temps == st)) {	// reference check only
			surface_temps = st;
			notifyAttribute("surfaceTemps");
		}
	}

	/** Subsurface temps in C (never null; entries null for missing) */
	private transient Integer[] subsurface_temps = new Integer[0];

	/** Get subsurface temps in C (never null; entries null for missing) */
	public Integer[] getSubsurfaceTemps() {
		return subsurface_temps;
	}

	/**
	 * Set the subsurface temperatures.
	 * @param st Subsurface temperatures in Celsius (never null; entries
	 *           null for missing)
	 */
	public void setSubsurfaceTempsNotify(Integer[] st) {
		if (!(subsurface_temps == st)) {	// reference check only
			subsurface_temps = st;
			notifyAttribute("subsurfaceTemps");
		}
	}

	/** Wind speed in KPH (null if missing) */
	private transient Integer wind_speed;

	/** Get the wind speed in KPH (null if missing) */
	public Integer getWindSpeed() {
		return wind_speed;
	}

	/** Set the wind speed in KPH */
	public void setWindSpeedNotify(Integer ws) {
		if(!integerEquals(ws, wind_speed)) {
			wind_speed = ws;
			notifyAttribute("windSpeed");
		}
	}

	/** Average wind direction in degrees (null for missing) */
	private transient Integer wind_dir;

	/** Get the average wind direction.
	 * @return Wind direction in degrees (null for missing) */
	public Integer getWindDir() {
		return wind_dir;
	}

	/** Round an integer to the nearest 45 */
	static private Integer round45(Integer d) {
		if(d != null)
			return 45 * Math.round(d / 45.0f);
		else
			return null;
	}

	/** Set the wind direction.
	 * @param wd Wind direction in degrees (null for missing). This 
	 *	  angle is rounded to the nearest 45 degrees. */
	public void setWindDirNotify(Integer wd) {
		Integer a = round45(wd);
		if(!integerEquals(a, wind_dir)) {
			wind_dir = a;
			notifyAttribute("windDir");
		}
	}

	/** Gust speed in KPH (null if missing) */
	private transient Integer gust_speed;

	/** Get the gust speed in KPH (null if missing) */
	public Integer getGustSpeed() {
		return gust_speed;
	}

	/** Set the gust speed in KPH */
	public void setGustSpeedNotify(Integer gs) {
		if(!integerEquals(gs, gust_speed)) {
			gust_speed = gs;
			notifyAttribute("gustSpeed");
		}
	}

	/** Average gust direction in degrees (null for missing) */
	private transient Integer gust_dir;

	/** Get the average gust direction.
	 * @return Gust direction in degrees (null for missing) */
	public Integer getGustDir() {
		return gust_dir;
	}

	/** Set the gust direction.
	 * @param gd Gust direction in degrees (null for missing).  This
	 *           angle is rounded to the nearest 45 degrees. */
	public void setGustDirNotify(Integer gd) {
		Integer a = round45(gd);
		if(!integerEquals(a, gust_dir)) {
			gust_dir = a;
			notifyAttribute("gustDir");
		}
	}

	/** Cache for precipitation samples */
	private transient final PeriodicSampleCache cache;

	/** Cache for precipitation type samples */
	private transient final PeriodicSampleCache pt_cache;

	/** Accumulation of precipitation (micrometers) */
	private transient int accumulation = MISSING_DATA;

	/**
	 * Set the accumulation of precipitation (micrometers).
	 * With upr==true, precipitation rate will also be updated with a
	 * value calculated from the precipitation value cache.  In general,
	 * RWIS drivers should either use this approach, or set the
	 * precipitation rate directly, but not both.
	 * @param a the accumulation (um)
	 * @param st timestamp after end after end of sample period
	 * @param upr update precipitation rate with calculated value?
	 */
	public void updateAccumulation(Integer a, long st, boolean upr) {
		int period = calculatePeriod(st);
		int value = calculatePrecipValue(a);
		if(period > 0 && value >= 0) {
			cache.add(new PeriodicSample(st, period, value));
			if (upr) {
				float period_h = 3600f / period;// periods/hr
				float umph = value * period_h;	// um/hr
				float mmph = umph / 1000;	// mm/hr
				setPrecipRateNotify(Math.round(mmph));
			}
		}
		if ((upr) && (value < 0))
			setPrecipRateNotify(null);
		if(period > 0 || value < 0)
			accumulation = a != null ? a : MISSING_DATA;
	}

	/** Reset the precipitation accumulation */
	public void resetAccumulation() {
		accumulation = 0;
	}

	/** Calculate the period since the last recorded sample.  If
	 * communication is interrupted, this will allow accumulated
	 * precipitation to be spread out over the appropriate samples. */
	private int calculatePeriod(long now) {
		if(stamp > 0 && now >= stamp) {
			int n = (int)(now / SAMPLE_PERIOD_MS);
			int s = (int)(stamp / SAMPLE_PERIOD_MS);
			return (n - s) * SAMPLE_PERIOD_SEC;
		} else
			return 0;
	}

	/** Calculate the precipitation since the last recorded sample.
	 * @param a New accumulated precipitation. */
	private int calculatePrecipValue(Integer a) {
		if(a != null && accumulation >= 0) {
			int val = a - accumulation;
			if(val >= 0)
				return val;
		}
		return MISSING_DATA;
	}

	/** Precipitation rate in mm/hr (null for missing) */
	private transient Integer precip_rate;

	/** Get precipitation rate in mm/hr (null for missing) */
	public Integer getPrecipRate() {
		return precip_rate;
	}

	/**
	 * Set precipitation rate in mm/hr (null for missing).
	 * In general, the RWIS driver should either use this,
	 * or use updateAccumulation with upr==true, but not both.
	 */
	public void setPrecipRateNotify(Integer pr) {
		if(!integerEquals(pr, precip_rate)) {
			precip_rate = pr;
			notifyAttribute("precipRate");
		}
	}

	/** Set the type of precipitation */
	public void setPrecipitationType(PrecipitationType pt, long st) {
		pt_cache.add(new PeriodicSample(st, SAMPLE_PERIOD_SEC,
			pt.ordinal()));
	}

	/** Visiblity in meters (null for missing) */
	private transient Integer visibility_m;

	/** Get visibility in meters (null for missing) */
	public Integer getVisibility() {
		return visibility_m;
	}

	/** Set visibility in meters (null for missing) */
	public void setVisibilityNotify(Integer v) {
		if(!integerEquals(v, visibility_m)) {
			visibility_m = v;
			notifyAttribute("visibility");
		}
	}

	/** Time stamp from the last sample */
	private transient long stamp = 0;

	/** Get the time stamp from the last sample.
	 * @return Time as long */
	public long getStamp() {
		return stamp;
	}

	/** Set the time stamp for the current sample */
	public void setStampNotify(long s) {
		if(s > 0 && s != stamp) {
			stamp = s;
			notifyAttribute("stamp");
		}
	}

	/** Observation time of the last sample */
	private transient long obs_time = MISSING_DATA;

	/**
	 * Get the observation time of the latest sample
	 * @return Time as long
	 */
	public long getObsTime() {
		return obs_time;
	}

	/** Set the observation time for the current sample */
	public void setObsTimeNotify(long s) {
		if(s > 0 && s != obs_time) {
			obs_time = s;
			notifyAttribute("obsTime");
		}
	}

	/** Get a weather sensor poller */
	public WeatherPoller getWeatherPoller() {
		if (isActive()) {
			DevicePoller dp = getPoller();
			if (dp instanceof WeatherPoller)
				return (WeatherPoller)dp;
		}
		return null;
	}

	/** Request a device operation */
	@Override
	public void setDeviceRequest(int r) {
		// no device requests from clients are supported
	}

	/** Send a device request */
	public void sendDeviceRequest(DeviceRequest req) {
		WeatherPoller p = getWeatherPoller();
		if (p != null)
			p.sendRequest(this, req);
	}

	/** Flush buffered sample data to disk */
	public void flush(PeriodicSampleWriter writer) throws IOException {
		writer.flush(cache, name);
		writer.flush(pt_cache, name);
	}

	/** Purge all samples before a given stamp. */
	public void purge(long before) {
		cache.purge(before);
		pt_cache.purge(before);
	}

	/** Render the object as xml */
	public void printXmlElement(Writer out) throws IOException {
		final String ABBR = "WeatherSensor";
		out.write("<" + ABBR);
		out.write(XmlWriter.createAttribute("id", getName()));
		out.write(XmlWriter.createAttribute("status",
			WeatherSensorHelper.getAllStyles(this)));
		out.write(XmlWriter.createAttribute("notes", getNotes()));
		final GeoLoc loc = getGeoLoc();		// Avoid race
		if(loc != null)
			out.write(XmlWriter.createAttribute("geoloc",
				loc.getName()));
		out.write(XmlWriter.createAttribute("time_stamp",
			STime.getDateString(getStamp())));
		out.write(XmlWriter.createAttribute("visibility_m",
			getVisibility()));
		out.write(XmlWriter.createAttribute("wind_speed_kph",
			getWindSpeed()));
		out.write(XmlWriter.createAttribute("air_temp_c",
			getAirTemp()));
		out.write(XmlWriter.createAttribute("wind_dir_avg_degs",
			getWindDir()));
		out.write(XmlWriter.createAttribute("precip_rate_mmhr",
			getPrecipRate()));
		out.write(XmlWriter.createAttribute("expired_state",
			WeatherSensorHelper.isSampleExpired(this)));
		out.write(XmlWriter.createAttribute("high_wind_state",
			WeatherSensorHelper.isHighWind(this)));
		out.write(XmlWriter.createAttribute("low_visibility_state",
			WeatherSensorHelper.isLowVisibility(this)));
		out.write(XmlWriter.createAttribute("surface_temps",
			WeatherSensorHelper.getMultiTempsString(
			surface_temps)));
		out.write(XmlWriter.createAttribute("subsurface_temps",
			WeatherSensorHelper.getMultiTempsString(
			subsurface_temps)));
		out.write(">\n");
		out.write("</" + ABBR + ">\n");
	}
}
