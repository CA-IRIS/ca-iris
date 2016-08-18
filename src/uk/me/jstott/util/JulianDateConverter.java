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

package uk.me.jstott.util;

import java.util.Calendar;

/**
 * Convert dates to Julian dates.
 * 
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * 
 * @author Jonathan Stott
 * @version 0.1
 */
public class JulianDateConverter {

	/**
	 * Convert a date/time to a Julian date
	 * 
	 * @param date
	 *            the date the convert
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
				- (Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0))
				+ Math.floor((275.0 * month) / 9.0) + day
				+ ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0)
				+ 1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5;
	}
}
