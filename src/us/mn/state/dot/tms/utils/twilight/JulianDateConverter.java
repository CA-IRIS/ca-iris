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
// JulianDateConverter.java
//
// (c) 2004 Jonathan Stott
//
// Created on 13-Apr-2004
//
// 0.1 - 13 Apr 2004
//  Initial Version
//-----------------------------------------------------------------------------

package us.mn.state.dot.tms.utils.twilight;

import java.util.Calendar;
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

/**
 * Convert dates to Julian dates.
 *
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * @author Jonathan Stott
 * @version 0.1
 */
public class JulianDateConverter {

	/**
	 * Convert a date/time to a Julian date
	 * @param date the date the convert
	 *
	 * @return the Julian date
	 */
	public static double dateToJulian(Calendar date) {
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int day = date.get(Calendar.DAY_OF_MONTH);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int minute = date.get(Calendar.MINUTE);
		int second = date.get(Calendar.SECOND);

		double extra = (100.0 * year) + month - 190002.5;
		return (367.0 * year)
			- (Math
			.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0))
				/ 4.0))
			+ Math.floor((275.0 * month) / 9.0) + day
			+ ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0)
			+ 1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5;
	}
}
