/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms;

import java.util.LinkedList;

/**
 * California-only County enumeration and helpers.
 * Long-term, this data would ideally be stored in the database, but we are
 * trying to minimize DB differences between CA and MN in order to minimize
 * migration issues.
 *
 * @author Travis Swanston
 */

public enum County {
	NONE("", ""),
	ALAMEDA("Alameda", "ALA"),
	MENDOCINO("Mendocino", "MEN"),
	SAN_FRANCISCO("San Francisco", "SF"),
	ALPINE("Alpine", "ALP"),
	MERCED("Merced", "MER"),
	SHASTA("Shasta", "SHA"),
	AMADOR("Amador", "AMA"),
	MONO("Mono", "MNO"),
	SIERRA("Sierra", "SIE"),
	BUTTE("Butte", "BUT"),
	MODOC("Modoc", "MOD"),
	SISKIYOU("Siskiyou", "SIS"),
	CALAVERAS("Calaveras", "CAL"),
	MONTEREY("Monterey", "MON"),
	SAN_JOAQUIN("San Joaquin", "SJ"),
	CONTRA_COSTA("Contra Costa", "CC"),
	MARIPOSA("Mariposa", "MPA"),
	SAN_LUIS_OBISPO("San Luis Obispo", "SLO"),
	COLUSA("Colusa", "COL"),
	MARIN("Marin", "MRN"),
	SAN_MATEO("San Mateo", "SM"),
	DEL_NORTE("Del Norte", "DN"),
	NAPA("Napa", "NAP"),
	SOLANO("Solano", "SOL"),
	EL_DORADO("El Dorado", "ED"),
	NEVADA("Nevada", "NEV"),
	SONOMA("Sonoma", "SON"),
	FRESNO("Fresno", "FRE"),
	ORANGE("Orange", "ORA"),
	STANISLAUS("Stanislaus", "STA"),
	GLENN("Glenn", "GLE"),
	PLACER("Placer", "PLA"),
	SUTTER("Sutter", "SUT"),
	HUMBOLDT("Humboldt", "HUM"),
	PLUMAS("Plumas", "PLU"),
	TEHAMA("Tehama", "TEH"),
	IMPERIAL("Imperial", "IMP"),
	RIVERSIDE("Riverside", "RIV"),
	TRINITY("Trinity", "TRI"),
	INYO("Inyo", "INY"),
	SACRAMENTO("Sacramento", "SAC"),
	TULARE("Tulare", "TUL"),
	KERN("Kern", "KER"),
	SANTA_BARBARA("Santa Barbara", "SB"),
	TUOLUMNE("Tuolumne", "TUO"),
	KINGS("Kings", "KIN"),
	SAN_BERNADINO("San Bernadino", "SBD"),
	VENTURA("Ventura", "VEN"),
	LOS_ANGELES("Los Angeles", "LA"),
	SAN_BENITO("San Benito", "SBT"),
	YOLO("Yolo", "YOL"),
	LAKE("Lake", "LAK"),
	SANTA_CLARA("Santa Clara", "SCL"),
	YUBA("Yuba", "YUB"),
	LASSEN("Lassen", "LAS"),
	SANTA_CRUZ("Santa Cruz", "SCR"),
	MADERA("Madera", "MAD"),
	SAN_DIEGO("San Diego", "SD");

	/** County name */
	public final String name;

	/** County code */
	public final String code;

	/** Create a new county */
	private County(String n, String c) {
		name = n;
		code = c;
	}

	/** Get a String representation of a county */
	public String toString() {
		return name;
	}

	/** Lookup a county by county name */
	public static County lookup(String name) {
		for (County c : values())
			if ((c.name).equals(name))
				return c;
		return null;
	}

	/** Lookup a county code by county name */
	public static String lookupCode(String name) {
		for (County c : values())
			if ((c.name).equals(name))
				return c.code;
		return null;
	}

	/** Get an array of county names */
	static public String[] getNames() {
		LinkedList<String> a = new LinkedList<String>();
		for(County c : values())
			a.add(c.name);
		return a.toArray(new String[0]);
	}

}
