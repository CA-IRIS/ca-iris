/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009 AHMCT, University of California
 * Copyright (C) 2016 Southwest Research Institute
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

import java.util.Comparator;

/**
 * Comparator for objects that can be alpha, numeric, or a mixture.
 * If both objects are numeric, they are compared as numbers, otherwise
 * as strings. Numerically equal arguments are compared as strings. For
 * clarity, see the test cases.
 * @author Michael Darter
 * @author Jacob Barde
 *
 * @see NumericAlphaComparatorTest
 */
public class NumericAlphaComparator<T> implements Comparator<T> {

	/**
	 * Used by default when comparing floats to account for rounding errors
	 * on various platforms.
	 */
	public static final float DEFAULT_DELTA = 0.000001F;

	/** Compare two objects */
	public int compare(T a, T b) {
		return compareStrings(a == null ? null : a.toString(),
			b == null ? null : b.toString());
	}

	/** Compare two strings */
	public static int compareStrings(String arg_a, String arg_b) {
		String a = (arg_a == null ? "null" : arg_a);
		String b = (arg_b == null ? "null" : arg_b);
		if(SString.isNumeric(a) && SString.isNumeric(b)) {
			int diff = SString.stringToInt(a) - 
				SString.stringToInt(b);
			// if numerically equal, compare as strings
			if(diff == 0)
				return a.compareTo(b);
			return diff;
		}
		// ignore common alpha prefix
		int pl = SString.alphaPrefixLen(a, b);
		if(pl > 0) {
			a = (pl >= a.length() ? "" : a.substring(pl));
			b = (pl >= b.length() ? "" : b.substring(pl));
			return compareStrings(a, b);	// recursive
		}
		return a.compareTo(b);
	}

	/**
	 * compare two floats properly
	 * nulls are considered less than a non-null
	 * @param a first float
	 * @param b second float
	 * @param delta difference value for both to be considered equal
	 * @return integer. a == b: 0, a < b: -1, a > b: 1
	 */
	public static int compareFloats(Float a, Float b, Float delta) {

		// ensure this is not a floating point comparing issue
		if (a != null && b != null && Math.abs(a - b) < delta)
			return 0;
		else if (a == null && b == null)
			return 0;
		else if (a == null && b != null) // a < b
			return -1;
		else if (a != null && b == null) // a > b
			return 1;

		return a.compareTo(b);
	}

	/**
	 * compare two floats properly using a default delta value.
	 * nulls are considered less than a non-null
	 * @param a first float
	 * @param b second float
	 * @return integer. a == b: 0, a < b: -1, a > b: 1
	 */
	public static int compareFloats(Float a, Float b) {
		return compareFloats(a, b, DEFAULT_DELTA);
	}

	/** Check equality */
	public boolean equals(Object o) {
		return o == this;
	}

	/** hashCode */
	public int hashCode() {
		return super.hashCode();
	}
}
