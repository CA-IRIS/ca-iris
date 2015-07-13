/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
 * Copyright (C) 2008-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import us.mn.state.dot.sched.TimeSteward;

/**
 * Time convenience methods.
 *
 * @author Michael Darter
 * @author Douglas Lau
 * @author Travis Swanston
 */
public final class STime {

	/** Constructor (no instantiation). */
	private STime() {
	}

	/**
	 * Calculate time difference in ms between now and given start time.
	 * @param start The start time as an epoch value.
	 * @return The time difference in ms.
	 */
	static public long calcTimeDeltaMS(long start) {
		return TimeSteward.currentTimeMillis() - start;
	}

	/** Get current time as short string in local time. */
	static public String getCurTimeShortString() {
		return getCurTimeShortString(true);
	}

	/**
	 * Get current time as short string in either UTC or local time.
	 * e.g.: '23:98:74'
	 */
	static public String getCurTimeShortString(boolean local) {
		return formatDate("HH:mm:ss", local);
	}

	/**
	 * Get current date and time as string in either UTC or local time.
	 * e.g. '2006-10-09 19:48:48'
	 */
	static public String getCurDateTimeString(boolean local) {
		return formatDate("yyyy-MM-dd HH:mm:ss", local);
	}

	/**
	 * Format a date to a string.
	 * @param format Format specifier.
	 * @param local Use local time or UTC.
	 * @param date Date to format.
	 * @return Formatted string.
	 */
	static private String formatDate(String format, boolean local,
		Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(getTimeZone(local));
		return sdf.format(date);
	}

	/** Get a time zone */
	static private TimeZone getTimeZone(boolean local) {
		if(local)
			return TimeZone.getDefault();
		else
			return TimeZone.getTimeZone("UTC");
	}

	/**
	 * Format the current date/time.
	 * @param format Format specifier.
	 * @param local Use local time or UTC.
	 * @return Formatted string.
	 */
	static private String formatDate(String format, boolean local) {
		return formatDate(format, local, new Date());
	}

	/**
	 * Convert from a Calendar to XML in UTC format.
	 * @param c Calendar
	 * @return XML date string in UTC:
	 *           format 'YYYY-MM-DDThh:mm:ssZ'.
	 *              e.g. 2008-03-22T02:04:21Z
	 *                   01234567890123456789
	 */
	static public String CalendarToXML(Calendar c) {
		return formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", false,
			c.getTime());
	}

	/**
	 * Given a date in XML time format (UTC), return a Date.
	 * this method only handles times in the format below. Note
	 * the terminating Z indicates UTC.
	 *
	 * 'YYYY-MM-DDThh:mm:ssZ'
	 *  01234567890123456789
	 *
	 * @throws IllegalArgumentException if an illegal date string is
	 *                                  received.
	 */
	static public Date XMLtoDate(String xml)
		throws IllegalArgumentException
	{
		if(xml != null) {
			try {
				return parseDate("yyyy-MM-dd'T'HH:mm:ss'Z'",
					false, xml);
			}
			catch(ParseException e) {
				// throw illegal arg exception below
			}
		}
		throw new IllegalArgumentException(
		    "Bogus XML date string received: " + xml);
	}

	/**
	 * Parse a date as a string.
	 * @param The date (format "yyyy-MM-dd HH:mm:ss")
	 * @return The date's corresponding epoch value, or -1 on error.
	 */
	static public long parseDate(String d) {
		if (d == null)
			return -1;
		try {
			Date pd = parseDate("yyyy-MM-dd HH:mm:ss", true, d);
			return pd.getTime();
		}
		catch(ParseException e) {}
		return -1;
	}

	/**
	 * Parse a date from a string.
	 * @param format Format specifier.
	 * @param local Use local time or UTC.
	 * @param date Date to format.
	 * @return Parsed date.
	 */
	static private Date parseDate(String format, boolean local,
		String date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		sdf.setTimeZone(getTimeZone(local));
		return sdf.parse(date);
	}

	/**
	 * Return the specified date as a string in local time.
	 * @param stamp An epoch value, or a value less than 0 for missing.
	 * @return A string in local time as HH:mm:ss MM-dd-yyyy or an
	 *         empty string if stamp is less than 0.
	 */
	static public String getDateString(long stamp) {
		if(stamp < 0)
			return "";
		Date d = new Date(stamp);
		return new SimpleDateFormat("HH:mm:ss MM-dd-yyyy").format(d);
	}

}
