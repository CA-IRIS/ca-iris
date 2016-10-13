/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2015 AHMCT, University of California
 * Copyright (C) 2016      California Department of Transportation
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
package us.mn.state.dot.tms.client.proxy;

import java.util.Comparator;
import java.util.regex.Pattern;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.utils.NaturalOrderComparator;
import us.mn.state.dot.tms.utils.NumericAlphaComparator;

/**
 * Comparator for SONAR proxy objects.
 * @author Travis Swanston
 * @author Jacob Barde
 */

// TODO: Refactor this, along with NumericAlphaComparator.
//
// A lot of cruft has accumulated here due to various requests by CA
// districts, and it should all probably be redesigned.
//
// Also, we play pretty fast and loose with relational consistency...
// There are issues where the comparison methods return 0 (for example,
// numerically comparing "D5-ABC" and "D5-DEF", or alphanumerically
// comparing "D5-ABC" and "D5-abc").  This is bad, because it ultimately
// causes the proxy items to not be visible in the UI.  I suspect this is
// because SwingProxyAdapter uses TreeSet, and true sets should not
// contain objects which compare as equivalent.
//
// It's probably a good idea to bring this whole sorting issue back to the
// table in order to determine what the CA-IRIS users actually need from it.
// It will probably tie in somewhat closely with the upcoming "autocomplete"
// feature requests that are currently in the Trac DB.
public class ProxyComparator<T extends SonarObject> implements Comparator<T> {

	/** Compare two objects */
	public int compare(T a, T b) {
		// consider null to be less than non-null.
		if ((a == null) && (b != null))
			return -1;
		if ((a == null) && (b == null))
			return 0;
		if ((a != null) && (b == null))
			return 1;

		String atype = a.getTypeName();
		String btype = b.getTypeName();
		String sa = getProxyString(a);
		String sb = getProxyString(b);

		int sort_mode = 0;
		if ((atype == null) || (!atype.equals(btype)))
			sort_mode = 0;
		else if (Camera.SONAR_TYPE.equals(atype))
			sort_mode = SystemAttrEnum.CAMERA_SORT.getInt();
		else if (DMS.SONAR_TYPE.equals(atype))
			sort_mode = SystemAttrEnum.DMS_SORT.getInt();
		else if (WeatherSensor.SONAR_TYPE.equals(atype))
			sort_mode = SystemAttrEnum.RWIS_SORT.getInt();
		else
			sort_mode = 0;

		// 1: alphanumeric [ascii-betical], case-insensitive
		if (sort_mode == 1)
			return compareAlphaNumeric(sa, sb);
		// 2: numeric only
		else if (sort_mode == 2)
			return compareNumeric(sa, sb);
		// 0:human-natural case-insensitive
		else
			return NaturalOrderComparator.compareStrings(sa, sb, false);
	}

	/** Compare two strings alphanumerically. */
	private int compareAlphaNumeric(String a, String b) {
		return a.toLowerCase().compareTo(b.toLowerCase());
	}

	/** Compare two strings numerically, integer-wise. */
	private int compareNumeric(String a, String b) {
		Integer ia = getFirstNumSeq(a);
		Integer ib = getFirstNumSeq(b);
		// consider to be 0 if no numeric sequence found
		if (ia == null)
			ia = Integer.valueOf(0);
		if (ib == null)
			ib = Integer.valueOf(0);
		return (ia.intValue() - ib.intValue());
	}

	/**
	 * Get a string to represent a SonarObject.
	 * @param a the SonarObject
	 * @return the SonarObject's site name if found, else its sonar name.
	 */
	private String getProxyString(SonarObject a) {
		String proxyName = a.getName();
		String siteName = SiteDataHelper.getSiteName(proxyName);
		if (siteName != null)
			return siteName;
		return proxyName;
	}

	/**
	 * Get the first full numeric sequence (greedy "[0-9]+") from a string.
	 * @param s the string
	 * @return the sequence as an Integer, or null if none found or error
	 */
	private static Integer getFirstNumSeq(String s) {
		if (s == null)
			return null;

		Pattern p = Pattern.compile("[^0-9]+");
		String[] seqs = p.split(s);
		if (seqs.length < 1)
			return null;

		String first = null;
		for (String t : seqs) {
			if (!("".equals(t))) {
				first = t;
				break;
			}
		}
		if (first == null)
			return null;

		Integer i = null;
		try {
			i = Integer.parseInt(first);
		}
		catch (NumberFormatException e) {
			return null;
		}
		return i;
	}

}

