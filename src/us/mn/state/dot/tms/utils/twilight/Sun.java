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
//-----------------------------------------------------------------------------
// Sun.java
//
// (c) 2004 Jonathan Stott
//
// Created on 30-Mar-2004
//
// 0.3 - 06 Mar 2005
//  - Updated incorrect sign of longitude (should be negative for west)
// 0.2 - 13 Apr 2004
//  - Change time zones to use the TimeZone class
//  - Adjust hours greater than 23 in convertTime()
// 0.1 - 30 Mar 2004
//  - First version 
//-----------------------------------------------------------------------------

package us.mn.state.dot.tms.utils.twilight;

import java.util.Calendar;
import java.util.TimeZone;

import us.mn.state.dot.tms.geo.Position;

import static java.lang.Math.*;

/**
 * imported into IRIS framework for use in daytime/nighttime CCTV presets
 * full source can be found here: http://www.jstott.com/jsuntimes/
 * All source provided by Jonathan Stott under GPL v2
 *
 * Source was modified to utilize existing classes within IRIS (e.g. Position in
 * place of LatitudeLongitude).
 *
 * Jacob Barde - August 2016
 */

/**
 * Provides methods to calculate sunrise, sunset and civil, nautical and
 * astronomical twilight times for a given latitude and longitude.
 *
 * Based on functions available at
 * http://www.srrb.noaa.gov/highlights/sunrise/sunrise.html
 *
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * @author Jonathan Stott
 * @version 1.0
 */
public class Sun {

	private static final double SUNRISE_SUNSET_ZENITH_DISTANCE = 90.83333;
	private static final double CIVIL_TWILIGHT_ZENITH_DISTANCE = 96.0;
	private static final double NAUTICAL_TWILIGHT_ZENTITH_DISTANCE = 102.0;
	private static final double ASTRONOMICAL_TWILIGHT_ZENITH_DISTANCE = 108.0;

	/**
	 * Calculate the UTC of a morning phenomenon for the given day at the
	 * given latitude and longitude on Earth
	 * @param julian         Julian day
	 * @param latitude       latitude of observer in degrees
	 * @param longitude      longitude of observer in degrees
	 * @param zenithDistance one of Sun.SUNRISE_SUNSET_ZENITH_DISTANCE,
	 *                       Sun.CIVIL_TWILIGHT_ZENITH_DISTANCE,
	 *                       Sun.NAUTICAL_TWILIGHT_ZENITH_DISTANCE,
	 *                       Sun.ASTRONOMICAL_TWILIGHT_ZENITH_DISTANCE.
	 *
	 * @return time in minutes from zero Z
	 */
	private static double morningPhenomenon(double julian, double latitude,
		double longitude, double zenithDistance) {
		// longitude = -longitude;
		double t = julianDayToJulianCenturies(julian);
		double eqtime = equationOfTime(t);
		double solarDec = sunDeclination(t);
		double hourangle = hourAngleMorning(latitude, solarDec,
			zenithDistance);
		double delta = longitude - toDegrees(hourangle);
		double timeDiff = 4 * delta;
		double timeUTC = 720 + timeDiff - eqtime;

		// Second pass includes fractional julian day in gamma calc
		double newt = julianDayToJulianCenturies(
			julianCenturiesToJulianDay(t)
				+ timeUTC / 1440);
		eqtime = equationOfTime(newt);
		solarDec = sunDeclination(newt);
		hourangle = hourAngleMorning(latitude, solarDec,
			zenithDistance);
		delta = longitude - toDegrees(hourangle);
		timeDiff = 4 * delta;

		return 720 + timeDiff - eqtime;
	}

	/**
	 * Calculate the UTC of sunrise for the given day at the given latitude
	 * and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time sunriseTime(Calendar cal, Position ll,
		TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return sunriseTime(julian, ll, timeZone, dst);
	}

	/**
	 * Calculate the UTC of sunrise for the given day at the given latitude
	 * and longitude on Earth
	 * @param julian   Julian day
	 * @param ll       latitude and longitude of observer in degrees
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST)
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z
	 */
	public static Time sunriseTime(double julian, Position ll,
		TimeZone timeZone, boolean dst) {
		double timeMins = morningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(), SUNRISE_SUNSET_ZENITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of morning civil twilight for the given day at the
	 * given latitude and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time morningCivilTwilightTime(Calendar cal,
		Position ll, TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return morningCivilTwilightTime(julian, ll, timeZone, dst);
	}

	/**
	 * Calculate the UTC of morning civil twilight for the given day at the
	 * given latitude and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time morningCivilTwilightTime(double julian,
		Position ll, TimeZone timeZone, boolean dst) {
		double timeMins = morningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(), CIVIL_TWILIGHT_ZENITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of morning nautical twilight for the given day at
	 * the nautical latitude and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time morningNauticalTwilightTime(Calendar cal,
		Position ll, TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return morningNauticalTwilightTime(julian, ll, timeZone, dst);
	}

	/**
	 * Calculate the UTC of morning nautical twilight for the given day at
	 * the nautical latitude and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time morningNauticalTwilightTime(double julian,
		Position ll, TimeZone timeZone, boolean dst) {
		double timeMins = morningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(), NAUTICAL_TWILIGHT_ZENTITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of morning astronomical twilight for the given day
	 * at the given latitude and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time morningAstronomicalTwilightTime(Calendar cal,
		Position ll, TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return morningAstronomicalTwilightTime(julian, ll, timeZone,
			dst);
	}

	/**
	 * Calculate the UTC of morning astronomical twilight for the given day
	 * at the given latitude and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time morningAstronomicalTwilightTime(double julian,
		Position ll, TimeZone timeZone, boolean dst) {
		double timeMins = morningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(),
			ASTRONOMICAL_TWILIGHT_ZENITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of an evening phenomenon for the given day at the
	 * given latitude and longitude on Earth.
	 * @param julian         Julian day.
	 * @param latitude       latitude of observer in degrees.
	 * @param longitude      longitude of observer in degrees.
	 * @param zenithDistance one of Sun.SUNRISE_SUNSET_ZENITH_DISTANCE,
	 *                       Sun.CIVIL_TWILIGHT_ZENITH_DISTANCE,
	 *                       Sun.NAUTICAL_TWILIGHT_ZENITH_DISTANCE,
	 *                       Sun.ASTRONOMICAL_TWILIGHT_ZENITH_DISTANCE.
	 *
	 * @return time in minutes from zero Z.
	 */
	private static double eveningPhenomenon(double julian, double latitude,
		double longitude, double zenithDistance) {
		double t = julianDayToJulianCenturies(julian);

		// First calculates sunrise and approx length of day
		double eqtime = equationOfTime(t);
		double solarDec = sunDeclination(t);
		double hourangle = hourAngleEvening(latitude, solarDec,
			zenithDistance);

		double delta = longitude - toDegrees(hourangle);
		double timeDiff = 4 * delta;
		double timeUTC = 720 + timeDiff - eqtime;

		// first pass used to include fractional day in gamma calc
		double newt = julianDayToJulianCenturies(
			julianCenturiesToJulianDay(t)
				+ timeUTC / 1440);
		eqtime = equationOfTime(newt);
		solarDec = sunDeclination(newt);
		hourangle = hourAngleEvening(latitude, solarDec,
			zenithDistance);

		delta = longitude - toDegrees(hourangle);
		timeDiff = 4 * delta;

		return 720 + timeDiff - eqtime;
	}

	/**
	 * Calculate the UTC of sunset for the given day at the given latitude
	 * and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 * @since 1.0
	 */
	public static Time sunsetTime(Calendar cal, Position ll,
		TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return (sunsetTime(julian, ll, timeZone, dst));
	}

	/**
	 * Calculate the UTC of sunset for the given day at the given latitude
	 * and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time sunsetTime(double julian, Position ll,
		TimeZone timeZone, boolean dst) {
		double timeMins = eveningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(), SUNRISE_SUNSET_ZENITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of evening civil twilight for the given day at the
	 * given latitude and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use.
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 * @since 1.0
	 */
	public static Time eveningCivilTwilightTime(Calendar cal,
		Position ll, TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return (eveningCivilTwilightTime(julian, ll, timeZone, dst));
	}

	/**
	 * Calculate the UTC of evening civil twilight for the given day at the
	 * given latitude and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use.
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time eveningCivilTwilightTime(double julian,
		Position ll, TimeZone timeZone, boolean dst) {
		double timeMins = eveningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(), CIVIL_TWILIGHT_ZENITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of evening nautical twilight for the given day at
	 * the given latitude and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 * @since 1.0
	 */
	public static Time eveningNauticalTwilightTime(Calendar cal,
		Position ll, TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return (eveningNauticalTwilightTime(julian, ll, timeZone, dst));
	}

	/**
	 * Calculate the UTC of evening nautical twilight for the given day at
	 * the given latitude and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time eveningNauticalTwilightTime(double julian,
		Position ll, TimeZone timeZone, boolean dst) {
		double timeMins = eveningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(), NAUTICAL_TWILIGHT_ZENTITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Calculate the UTC of evening astronomical twilight for the given day
	 * at the given latitude and longitude on Earth.
	 * @param cal      a Calendar specifying the date.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 * @since 1.0
	 */
	public static Time eveningAstronomicalTwilightTime(Calendar cal,
		Position ll, TimeZone timeZone, boolean dst) {
		double julian = getJulianDate(cal);
		return (eveningAstronomicalTwilightTime(julian, ll, timeZone,
			dst));
	}

	/**
	 * Calculate the UTC of evening astronomical twilight for the given day
	 * at the given latitude and longitude on Earth.
	 * @param julian   Julian day.
	 * @param ll       latitude and longitude of observer in degrees.
	 * @param timeZone the timeZone to use (e.g. Sun.GMT or Sun.PST).
	 * @param dst      true if daylight savings time (summer time) should
	 *                 be taken into account
	 *
	 * @return time in minutes from zero Z.
	 */
	public static Time eveningAstronomicalTwilightTime(double julian,
		Position ll, TimeZone timeZone, boolean dst) {
		double timeMins = eveningPhenomenon(julian, ll.getLatitude(),
			-ll.getLongitude(),
			ASTRONOMICAL_TWILIGHT_ZENITH_DISTANCE)
			+ (timeZone.getRawOffset() / 60000.0);
		if (dst)
			timeMins += 60.0;

		return convertTime(timeMins);
	}

	/**
	 * Gets the Julian date to be used for calculating sun phenomena times.
	 * @param cal the calendar reprsenting the date.
	 *
	 * @return the Julian date for 12:00:00 on the given date.
	 */
	private static double getJulianDate(Calendar cal) {
		Calendar cal2 = (Calendar) cal.clone();
		cal2.set(Calendar.MILLISECOND, 0);
		cal2.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.HOUR_OF_DAY, 12);
		double julian = JulianDateConverter.dateToJulian(cal2);
		return julian;
	}

	/**
	 * Convert a double representing a time to a Time object
	 * @param time
	 *
	 * @return
	 */
	private static Time convertTime(double time) {
		int hours = (int) (time / 60.0);
		int minutes = (int) (time - (hours * 60));
		double seconds = (time - minutes - (hours * 60)) * 60;
		if (hours > 23)
			hours %= 24;
		return new Time(hours, minutes, seconds);
	}

	/**
	 * Convert Julian Day to centuries since J2000.0
	 * @param julian The Julian Day to convert
	 *
	 * @return the value corresponding to the Julian Day
	 */
	private static double julianDayToJulianCenturies(double julian) {
		return (julian - 2451545) / 36525;
	}

	/**
	 * Convert centuries since J2000.0 to Julian Day
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return The Julian Day corresponding to the value of t
	 */
	private static double julianCenturiesToJulianDay(double t) {
		return (t * 36525) + 2451545;
	}

	/**
	 * Calculate the difference between true solar time and mean solar time
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return
	 */
	private static double equationOfTime(double t) {
		double epsilon = obliquityCorrection(t);
		double l0 = geomMeanLongSun(t);
		double e = eccentricityOfEarthsOrbit(t);
		double m = geometricMeanAnomalyOfSun(t);
		double y = Math.pow((tan(toRadians(epsilon) / 2)), 2);

		double Etime =
			y * sin(2 * toRadians(l0)) - 2 * e * sin(toRadians(m))
				+ 4 * e * y
				* sin(toRadians(m)) * cos(2 * toRadians(l0))
				- 0.5 * y * y
				* sin(4 * toRadians(l0)) - 1.25 * e * e * sin(
				2 * toRadians(m));

		return Math.toDegrees(Etime) * 4;
	}

	/**
	 * Calculate the declination of the sun
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return The Sun's declination in degrees
	 */
	private static double sunDeclination(double t) {
		double e = obliquityCorrection(t);
		double lambda = sunsApparentLongitude(t);

		double sint = sin(toRadians(e)) * sin(toRadians(lambda));
		return toDegrees(asin(sint));
	}

	/**
	 * calculate the hour angle of the sun for a morning phenomenon for the
	 * given latitude
	 * @param lat            Latitude of the observer in degrees
	 * @param solarDec       declination of the sun in degrees
	 * @param zenithDistance zenith distance of the sun in degrees
	 *
	 * @return hour angle of sunrise in radians
	 */
	private static double hourAngleMorning(double lat, double solarDec,
		double zenithDistance) {
		return (acos(cos(toRadians(zenithDistance))
			/ (cos(toRadians(lat)) * cos(toRadians(solarDec)))
			- tan(toRadians(lat))
			* tan(toRadians(solarDec))));
	}

	/**
	 * Calculate the hour angle of the sun for an evening phenomenon for the
	 * given latitude
	 * @param lat            Latitude of the observer in degrees
	 * @param solarDec       declination of the Sun in degrees
	 * @param zenithDistance zenith distance of the sun in degrees
	 *
	 * @return hour angle of sunset in radians
	 */
	private static double hourAngleEvening(double lat, double solarDec,
		double zenithDistance) {
		return -hourAngleMorning(lat, solarDec, zenithDistance);
	}

	/**
	 * Calculate the corrected obliquity of the ecliptic
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return Corrected obliquity in degrees
	 */
	private static double obliquityCorrection(double t) {
		return meanObliquityOfEcliptic(t) + 0.00256
			* cos(toRadians(125.04 - 1934.136 * t));
	}

	/**
	 * Calculate the mean obliquity of the ecliptic
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return Mean obliquity in degrees
	 */
	private static double meanObliquityOfEcliptic(double t) {
		return 23 + (26 + (21.448 - t
			* (46.815 + t * (0.00059 - t * (0.001813))) / 60)) / 60;
	}

	/**
	 * Calculate the geometric mean longitude of the sun
	 * @param t of Julian centuries since J2000.0
	 *
	 * @return the geometric mean longitude of the sun in degrees
	 */
	private static double geomMeanLongSun(double t) {
		double l0 = 280.46646 + t * (36000.76983 + 0.0003032 * t);

		while ((l0 >= 0) && (l0 <= 360)) {
			if (l0 > 360)
				l0 = l0 - 360;
			if (l0 < 0)
				l0 = l0 + 360;
		}

		return l0;
	}

	/**
	 * Calculate the eccentricity of Earth's orbit
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return the eccentricity
	 */
	private static double eccentricityOfEarthsOrbit(double t) {
		return 0.016708634 - t * (0.000042037 + 0.0000001267 * t);
	}

	/**
	 * Calculate the geometric mean anomaly of the Sun
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return the geometric mean anomaly of the Sun in degrees
	 */
	private static double geometricMeanAnomalyOfSun(double t) {
		return 357.52911 + t * (35999.05029 - 0.0001537 * t);
	}

	/**
	 * Calculate the apparent longitude of the sun
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return The apparent longitude of the Sun in degrees
	 */
	private static double sunsApparentLongitude(double t) {
		return sunsTrueLongitude(t) - 0.00569 - 0.00478
			* sin(toRadians(125.04 - 1934.136 * t));
	}

	/**
	 * Calculate the true longitude of the sun
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return The Sun's true longitude in degrees
	 */
	private static double sunsTrueLongitude(double t) {
		return geomMeanLongSun(t) + equationOfCentreForSun(t);
	}

	/**
	 * Calculate the equation of centre for the Sun
	 * @param t Number of Julian centuries since J2000.0
	 *
	 * @return The equation of centre for the Sun in degrees
	 */
	private static double equationOfCentreForSun(double t) {
		double m = geometricMeanAnomalyOfSun(t);

		return sin(toRadians(m)) * (1.914602 - t * (0.004817
			+ 0.000014 * t))
			+ sin(2 * toRadians(m)) * (0.019993 - 0.000101 * t)
			+ sin(3 * toRadians(m))
			* 0.000289;
	}
}
