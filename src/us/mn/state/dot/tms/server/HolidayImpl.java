/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2003-2012  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.Holiday;
import us.mn.state.dot.tms.TMSException;

/**
 * A holiday is a date which ramp meters (and travel times) are not deployed.
 *
 * @author Douglas Lau
 */
public class HolidayImpl extends BaseObjectImpl implements Holiday,
	Comparable<HolidayImpl>
{
	/** Load all the holidays */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, HolidayImpl.class);
		store.query("SELECT name, month, day, week, weekday, " +
			"shift FROM iris." + SONAR_TYPE  + ";",
			new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new HolidayImpl(
					row.getString(1),	// name
					row.getInt(2),		// month
					row.getInt(3),		// day
					row.getInt(4),		// week
					row.getInt(5),		// weekday
					row.getInt(6)		// shift
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("month", month);
		map.put("day", day);
		map.put("week", week);
		map.put("weekday", weekday);
		map.put("shift", shift);
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

	/** Create a new holiday */
	public HolidayImpl(String n) {
		super(n);
	}

	/** Create a new holiday */
	protected HolidayImpl(String n, int m, int d, int w, int wd, int s) {
		this(n);
		month = m;
		day = d;
		week = w;
		weekday = wd;
		shift = s;
	}

	/** Compare to another holiday */
	public int compareTo(HolidayImpl o) {
		return name.compareTo(o.name);
	}

	/** Test if the holiday equals another holiday */
	public boolean equals(Object o) {
		if(o instanceof HolidayImpl)
			return name.equals(((HolidayImpl)o).name);
		else
			return false;
	}

	/** Check if a selection of properties is valid */
	protected void checkSelection(int d, int w, int s)
		throws ChangeVetoException
	{
		if(d != ANY_DAY && (w != ANY_WEEK || s != 0))
			throw new ChangeVetoException("Invalid selection");
	}

	/** Month of year */
	protected int month = ANY_MONTH;

	/** Set the month */
	public void setMonth(int m) {
		month = m;
	}

	/** Set the month */
	public void doSetMonth(int m) throws TMSException {
		if(m == month)
			return;
		if(m != ANY_MONTH &&
			(m < Calendar.JANUARY || m > Calendar.DECEMBER))
		{
			throw new ChangeVetoException("Invalid month:" + m);
		}
		store.update(this, "month", m);
		setMonth(m);
	}

	/** Get the month */
	public int getMonth() {
		return month;
	}

	/** Day of month */
	protected int day = ANY_DAY;

	/** Set the day-of-month */
	public void setDay(int d) {
		day = d;
	}

	/** Set the day-of-month */
	public void doSetDay(int d) throws TMSException {
		if(d == day)
			return;
		if(d != ANY_DAY) {
			if(d < 1 || d > 31) {
				throw new ChangeVetoException(
					"Invalid day:" + d);
			}
			checkSelection(d, week, shift);
		}
		store.update(this, "day", d);
		setDay(d);
	}

	/** Get the day-of-month */
	public int getDay() {
		return day;
	}

	/** Week of month */
	protected int week = ANY_WEEK;

	/** Set the week-of-month */
	public void setWeek(int w) {
		week = w;
	}

	/** Set the week-of-month */
	public void doSetWeek(int w) throws TMSException {
		if(w == week)
			return;
		if(w != ANY_WEEK) {
			if(w < -1 || w > 5) {
				throw new ChangeVetoException(
					"Invalid week:" + w);
			}
			checkSelection(day, w, shift);
		}
		store.update(this, "week", w);
		setWeek(w);
	}

	/** Get the week-of-month */
	public int getWeek() {
		return week;
	}

	/** Day of week */
	protected int weekday = ANY_WEEKDAY;

	/** Set the day-of-week */
	public void setWeekday(int wd) {
		weekday = wd;
	}

	/** Set the day-of-week */
	public void doSetWeekday(int wd) throws TMSException {
		if(wd == weekday)
			return;
		if(wd != ANY_WEEKDAY) {
			if(wd < Calendar.SUNDAY || wd > Calendar.SATURDAY) {
				throw new ChangeVetoException(
					"Invalid weekday:" + wd);
			}
		}
		store.update(this, "weekday", wd);
		setWeekday(wd);
	}

	/** Get the day-of-week */
	public int getWeekday() {
		return weekday;
	}

	/** Shift (in days) from actual "holiday" (for Shopping day, etc) */
	protected int shift = 0;

	/** Set the shift from the actual holiday */
	public void setShift(int s) {
		shift = s;
	}

	/** Set the shift from the actual holiday */
	public void doSetShift(int s) throws TMSException {
		if(s == shift)
			return;
		if(s < -6 || s > 6)
			throw new ChangeVetoException("Invalid shift:" + s);
		if(s != 0)
			checkSelection(day, week, s);
		store.update(this, "shift", s);
		setShift(s);
	}

	/** Get the shift from the actual holiday */
	public int getShift() {
		return shift;
	}
}
