//-----------------------------------------------------------------------------
// SunTestConsole.java
//
// (c) 2004 Jonathan Stott
//
// Created on 30-Mar-2004
//
// 0.2 - 13 Apr 2004
//  - Updated handling of time zones to use the TimeZone class
//  - Changed dates to Calendar objects and used 
//    uk.me.jstott.util.JulianDateConverter to convert them to Julian dates
// 0.1 - 30 Mar 2004
//  - First version
//-----------------------------------------------------------------------------

package uk.me.jstott.sun;

import java.util.Calendar;
import java.util.TimeZone;

import uk.me.jstott.coordconv.LatitudeLongitude;

/**
 * Example usage of the Sun class.
 * 
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * 
 * @author Jonathan Stott
 * @version 0.1
 */
public class SunTestConsole {

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// -------------------------------------------------------------------------
		// Calculate sunrise and sunet times for Canterbury, England for today
		// -------------------------------------------------------------------------

		LatitudeLongitude ll = new LatitudeLongitude(LatitudeLongitude.NORTH,
				51, 17, 38.0, LatitudeLongitude.EAST, 1, 5, 27.0);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		boolean dst = false;

		System.out.println("\n\nCanterbury, England - "
				+ cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR)
				+ "(" + ll.toString() + ")");

		System.out.println("Astronomical twilight = "
				+ Sun.morningAstronomicalTwilightTime(cal, ll, gmt, dst));
		System.out.println("Nautical twilight     = "
				+ Sun.morningNauticalTwilightTime(cal, ll, gmt, dst));
		System.out.println("Civil twilight        = "
				+ Sun.morningCivilTwilightTime(cal, ll, gmt, dst));
		System.out.println("Sunrise               = "
				+ Sun.sunriseTime(cal, ll, gmt, dst));
		System.out.println("Sunset                = "
				+ Sun.sunsetTime(cal, ll, gmt, dst));
		System.out.println("Civil twilight        = "
				+ Sun.eveningCivilTwilightTime(cal, ll, gmt, dst));
		System.out.println("Nautical twilight     = "
				+ Sun.eveningNauticalTwilightTime(cal, ll, gmt, dst));
		System.out.println("Astronomical twilight = "
				+ Sun.eveningAstronomicalTwilightTime(cal, ll, gmt, dst));

		// -------------------------------------------------------------------------
		// Calculate sunrise and sunset time for Philadelphia, USA for today
		// -------------------------------------------------------------------------

		LatitudeLongitude ll2 = new LatitudeLongitude(39.9561, -75.1645);
		TimeZone est = TimeZone.getTimeZone("US/Eastern");

		System.out.println("\n\nPhiladelphia, USA - "
				+ cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));

		System.out.println("Astronomical twilight = "
				+ Sun.morningAstronomicalTwilightTime(cal, ll2, est, dst));
		System.out.println("Nautical twilight     = "
				+ Sun.morningNauticalTwilightTime(cal, ll2, est, dst));
		System.out.println("Civil twilight        = "
				+ Sun.morningCivilTwilightTime(cal, ll2, est, dst));
		System.out.println("Sunrise               = "
				+ Sun.sunriseTime(cal, ll2, est, dst));
		System.out.println("Sunset                = "
				+ Sun.sunsetTime(cal, ll2, est, dst));
		System.out.println("Civil twilight        = "
				+ Sun.eveningCivilTwilightTime(cal, ll2, est, dst));
		System.out.println("Nautical twilight     = "
				+ Sun.eveningNauticalTwilightTime(cal, ll2, est, dst));
		System.out.println("Astronomical twilight = "
				+ Sun.eveningAstronomicalTwilightTime(cal, ll2, est, dst));
	}
}
