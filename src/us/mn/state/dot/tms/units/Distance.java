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
 * Distance between two points.
 *
 * @author Douglas Lau
 */
public final class Distance {

	/** Enumeration of distance units */
	public enum Units {
		METERS(1.0, "m"),
		KILOMETERS(1000.0, "km"),
		DECIMETERS(0.1, "dm"),
		CENTIMETERS(0.01, "cm"),
		MILLIMETERS(0.001, "mm"),
		MILES(1609.344, "mi"),
		FEET(MILES.meters / 5280, "ft"),
		INCHES(FEET.meters / 12, "in"),
		YARDS(FEET.meters * 3, "yd");

		/** Conversion rate to meters */
		public final double meters;

		/** Unit label */
		public final String label;

		/** Create units */
		private Units(double m, String l) {
			meters = m;
			label = l;
		}
	}

	/** Distance value */
	public final double value;

	/** Distance units */
	public final Units units;

	/** Create a new distance.
	 * @param v Value of distance.
	 * @param u Units of distance. */
	public Distance(double v, Units u) {
		value = v;
		units = u;
	}

	/** Create a new distance.
	 * @param v Value in meters. */
	public Distance(double v) {
		this(v, Units.METERS);
	}

	/** Get the distance in meters */
	public double m() {
		return value * units.meters;
	}

	/** Convert a distance to specified units.
	 * @param u Units to convert to.
	 * @return Distance in specified units. */
	public Distance convert(Units u) {
		if(u == units)
			return this;
		else {
			double v = m();
			return new Distance(v / u.meters, u);
		}
	}

	/** Get a distance as a float in specified units.
	 * @param u Units to return.
	 * @return Distance as a float value. */
	public float asFloat(Units u) {
		if(u == units)
			return (float)value;
		else
			return (float)(m() / u.meters);
	}

	/** Round a distance to nearest whole unit.
	 * @param u Units to return.
	 * @return Distance rounded to nearest whole unit. */
	public int round(Units u) {
		if(u == units)
			return (int)Math.round(value);
		else
			return (int)Math.round(m() / u.meters);
	}

	/** Compare for equality */
	@Override public boolean equals(Object other) {
		if(other instanceof Distance) {
			Distance o = (Distance)other;
			if(units == o.units)
				return value == o.value;
			else
				return m() == o.m();
		} else
			return false;
	}

	/** Get a distance hash code */
	@Override public int hashCode() {
		return new Double(m()).hashCode();
	}

	/** Get a string representation of a distance */
	@Override public String toString() {
		return value + " " + units.label;
	}

	/** Distance formatter */
	static public class Formatter {
		private final NumberFormat format;
		public Formatter(int d) {
			format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(d);
			format.setMinimumFractionDigits(d);
		}
		public String format(Distance d) {
			return format.format(d.value) + " " + d.units.label;
		}
	}
}
