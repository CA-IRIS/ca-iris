/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.units;

import java.text.NumberFormat;

/**
 * Time interval values.
 *
 * @author Douglas Lau
 */
public final class Interval {

	/** Enumeration of interval units */
	public enum Units {
		MILLISECONDS(0.001, "ms"),
		SECONDS(1, "s"),
		MINUTES(60, "min"),
		HOURS(60 * MINUTES.seconds, "hr"),
		DAYS(24 * HOURS.seconds, "d"),
		WEEKS(7 * DAYS.seconds, "wk");

		/** Conversion rate to seconds */
		public final double seconds;

		/** Unit label */
		public final String label;

		/** Create units */
		private Units(double s, String l) {
			seconds = s;
			label = l;
		}
	}

	/** Interval value */
	public final double value;

	/** Interval units */
	public final Units units;

	/** Create a new time interval.
	 * @param v Value of interval.
	 * @param u Units of interval. */
	public Interval(double v, Units u) {
		value = v;
		units = u;
	}

	/** Create a new time interval.
	 * @param v Value in seconds. */
	public Interval(double v) {
		this(v, Units.SECONDS);
	}

	/** Get the interval in milliseconds */
	public long ms() {
		if(units == Units.MILLISECONDS)
			return Math.round(value);
		else {
			return Math.round(value * units.seconds /
				Units.MILLISECONDS.seconds);
		}
	}

	/** Number of seconds in interval */
	public double seconds() {
		if(units == Units.SECONDS)
			return value;
		else
			return value * units.seconds;
	}

	/** Convert an interval to specified units.
	 * @param u Units to convert to.
	 * @return Interval in specified units. */
	public Interval convert(Units u) {
		if(u == units)
			return this;
		else {
			double s = seconds();
			return new Interval(s / u.seconds, u);
		}
	}

	/** Round an interval to nearest whole unit.
	 * @param u Units to return.
	 * @return Interval rounded to nearest whole unit. */
	public int round(Units u) {
		if(u == units)
			return (int)Math.round(value);
		else
			return (int)Math.round(seconds() / u.seconds);
	}

	/** Get the number of intervals per hour */
	public int per_hour() {
		return (int)Math.round(Units.HOURS.seconds / seconds());
	}

	/** Compare for equality */
	@Override public boolean equals(Object other) {
		if(other instanceof Interval) {
			Interval o = (Interval)other;
			if(units == o.units)
				return value == o.value;
			else
				return seconds() == o.seconds();
		} else
			return false;
	}

	/** Get an interval hash code */
	@Override public int hashCode() {
		return new Double(seconds()).hashCode();
	}

	/** Get a string representation of an interval */
	@Override public String toString() {
		return value + " " + units.label;
	}

	/** Interval formatter */
	static public class Formatter {
		private final NumberFormat format;
		public Formatter(int d) {
			format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(d);
			format.setMinimumFractionDigits(d);
		}
		public String format(Interval i) {
			return format.format(i.value) + " " + i.units.label;
		}
	}
}
