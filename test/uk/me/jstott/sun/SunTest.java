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
package uk.me.jstott.sun;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import us.mn.state.dot.tms.geo.Position;
/**
 * imported into IRIS framework for use in daytime/nighttime CCTV presets
 * full source can be found here: http://www.jstott.com/jsuntimes/
 * All source provided under GPL v2
 *
 * Source was modified to utilize existing classes within IRIS (e.g. Position in
 * place of LatitudeLongitude).
 *
 * Jacob Barde - August 2016
 */

public class SunTest extends TestCase {

	public void testSunrise() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.sunriseTime(cal, ll, gmt, dst);
		assertEquals("04:41:55", t.toString());
	}

	public void testMorningCivilTwilight() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.morningCivilTwilightTime(cal, ll, gmt, dst);
		assertEquals("03:54:27", t.toString());
	}

	public void testMorningNauticalTwilight() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.morningNauticalTwilightTime(cal, ll, gmt, dst);
		assertEquals("02:40:51", t.toString());
	}

	public void testMorningAstronomicalTwilight() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.morningAstronomicalTwilightTime(cal, ll, gmt, dst);
		assertEquals("00:00:00", t.toString());
	}

	public void testSunset() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.sunsetTime(cal, ll, gmt, dst);
		assertEquals("21:18:35", t.toString());
	}

	public void testEveningCivilTwilight() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.eveningCivilTwilightTime(cal, ll, gmt, dst);
		assertEquals("22:06:13", t.toString());
	}

	public void testEveningNauticalTwilight() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.eveningNauticalTwilightTime(cal, ll, gmt, dst);
		assertEquals("23:20:20", t.toString());
	}

	public void testEveningAstronomicalTwilight() {
		Position ll = new Position(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.eveningAstronomicalTwilightTime(cal, ll, gmt, dst);
		assertEquals("00:00:00", t.toString());
	}
}
