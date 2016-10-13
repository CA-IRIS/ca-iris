/*
 * IRIS -- Intelligent Roadway Information System
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
//-----------------------------------------------------------------------------
// Time.java
//
// (c) 2004 Jonathan Stott
//
// Created on 31-Mar-2004
//
// 0.1 - 31 Mar 2004
//  - First version
//-----------------------------------------------------------------------------

package us.mn.state.dot.tms.utils.twilight;
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
 * Class to represent a time (hh:mm.ss) object.
 *
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * @author Jonathan Stott
 * @version 0.1
 */
public class Time {

	private int hours;
	private int minutes;
	private double seconds;
	private boolean roundedSeconds = true;

	/**
	 * Create a time object
	 * @param h hours
	 * @param m minute
	 * @param s seconds
	 */
	public Time(int h, int m, double s) {
		if (h < 0 || h > 23) {
			throw new IllegalArgumentException("Hours (" + h
				+ ") must be an integer from 0 through 23");
		}

		if (m < 0 || m > 59) {
			throw new IllegalArgumentException("Minutes (" + m
				+ ") must be an integer from 0 through 59");
		}

		if (s < 0 || s >= 60) {
			throw new IllegalArgumentException("Seconds (" + s
				+ ") must be from 0 to 60");
		}

		hours = h;
		minutes = m;
		seconds = s;
	}

	/**
	 * Determines whether the seconds should be rounded to the nearest whole
	 * value when using the toString() method.
	 * @param r true to cause seconds to be rounded
	 */
	public void setRoundedSeconds(boolean r) {
		roundedSeconds = r;
	}

	/**
	 * Return a String representation of the Time
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String h = "";
		String m = "";
		String s = "";
		int hrs = hours;
		int mins = minutes;

		if (roundedSeconds) {
			int secs = ((int) Math.round(seconds));
			if (secs < 10)
				s = "0";
			s += Integer.toString(secs);
			if (secs >= 60) {
				s = "00";
				mins++;
			}
		} else {
			if (seconds < 10)
				s = "0";
			s += Double.toString(seconds);
		}

		if (mins >= 60) {
			mins = 0;
			hrs++;
		}

		if (mins < 10)
			m = "0";
		m += Integer.toString(mins);

		if (hrs >= 24) {
			hrs = 0;
		}

		if (hrs < 10)
			h = "0";
		h += Integer.toString(hrs);

		return h + ":" + m + ":" + s;
	}

	/**
	 * @return Returns the hours.
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * @param hours The hours to set.
	 */
	public void setHours(int hours) {
		this.hours = hours;
	}

	/**
	 * @return Returns the minutes.
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * @param minutes The minutes to set.
	 */
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	/**
	 * @return Returns the seconds.
	 */
	public double getSeconds() {
		return seconds;
	}

	/**
	 * @param seconds The seconds to set.
	 */
	public void setSeconds(double seconds) {
		this.seconds = seconds;
	}
}
