/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * SiteDataHelper has static methods for dealing with SiteData entities.
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class SiteDataHelper extends BaseHelper {

	public final static String TAG_RDFULL = "\\[RDFULL\\]";         // full road name
	public final static String TAG_RDABBR = "\\[RDABBR\\]";         // road abbreviation
	public final static String TAG_RD = "\\[RD\\]";                 // same as [RDABBR] if exists, else [RDFULL]
	public final static String TAG_RDDIR = "\\[RDDIR\\]";           // road direction
	public final static String TAG_RDDIRFULL = "\\[RDDIRFULL\\]";   // road direction abbreviation
	public final static String TAG_XRDFULL = "\\[XRDFULL\\]";       // full road name of cross-road
	public final static String TAG_XRDABBR = "\\[XRDABBR\\]";       // road abbreviation of cross-road
	public final static String TAG_XRD = "\\[XRD\\]";               // same as [XRDABBR] if exists, else [XRDFULL]
	public final static String TAG_XRDDIR = "\\[XRDDIR\\]";         // road direction abbreviation of cross-road
	public final static String TAG_XRDDIRFULL = "\\[XRDDIRFULL\\]"; // road direction of cross-road
	public final static String TAG_XMOD = "\\[XMOD\\]";             // prepositional relation of road to cross-road
	public final static String TAG_MILE = "\\[MILE\\]";             // milepoint
	public final static String TAG_CTY = "\\[CTY\\]";               // county code
	public final static String TAG_CTYFULL = "\\[CTYFULL\\]";       // county name
	public final static String TAG_GLNAME = "\\[GLNAME\\]";         // GeoLoc name

	static public final String DESCFMT_DEFAULT = "[RDFULL] [RDDIR] [XMOD] [XRDFULL] [XRDDIR]";

	private final static Map<String, String> geoLocToSite = new HashMap<>();

	static private String getFormatString(GeoLoc gl) {
		// return format from SiteData if present
		SiteData sd = lookupByGeoLoc(gl);
		if (sd != null) {
			String fmt = sd.getFormat();
			if (!("".equals(sanitize(fmt))))
				return fmt.trim();
		}
		// return format from system attributes if present
		String lf = SystemAttrEnum.LOCATION_FORMAT.getString();
		if (!("".equals(sanitize(lf))))
			return lf.trim();
		// return default
		return DESCFMT_DEFAULT;
	}

	/** Constructor (do not instantiate). */
	private SiteDataHelper() {
		assert false;
	}

	/** Get a SiteData iterator */
	static public Iterator<SiteData> iterator() {
		return new IteratorWrapper<>(namespace.iterator(SiteData.SONAR_TYPE));
	}

	/** Lookup a SiteData entity */
	static public SiteData lookup(String n) {
		if (n == null)
			return null;
		return (SiteData) namespace.lookupObject(SiteData.SONAR_TYPE, n);
	}

	/** Lookup a SiteData entity by GeoLoc */
	static public SiteData lookupByGeoLoc(GeoLoc gl) {
		return gl != null ? lookupByGeoLoc(gl.getName()) : null;
	}

	/** Lookup a site data. if it is not in the cache, query sonar for it. */
	static public SiteData lookupByGeoLoc(String gn) {
		SiteData sd = null;

		if (gn == null)
			return null;

		// try to find cached value first
		String sn;
		synchronized (geoLocToSite) {
			sn = geoLocToSite.get(gn);
		}

		// verify hasn't changed since cached value was added
		if (sn != null) {
			sd = lookup(sn);
			if (sd == null || !gn.equals(sd.getGeoLoc())) {
				synchronized (geoLocToSite) {
					geoLocToSite.remove(gn);
				}
				sd = null;
			}
		}

		// perform the expensive operation (and cache the result)
		if (sd == null) {
			Iterator<SiteData> it = iterator();
			while (it.hasNext()) {
				SiteData tmp = it.next();
				if (gn.equals(tmp.getGeoLoc())) {
					sd = tmp;
					synchronized (geoLocToSite) {
						geoLocToSite.put(gn, sd.getName());
					}
					break;
				}
			}
		}

		return sd;
	}

	/**
	 * Build a site name string for a SiteData entity.
	 * @param gn The GeoLoc name corresponding to the SiteData entity.
	 *
	 * @return A site name string, or null if entity not found or if entity doesn't contain a site name
	 */
	static public String getSiteName(String gn) {
		SiteData sd = lookupByGeoLoc(gn);
		String sn = sd != null ? sd.getSiteName() : null;

		return !sanitize(sn).equals("") ? sn : null;
	}

	/**
	 * Find a GeoLoc name by site name.
	 * @param sn The site name corresponding to the GeoLoc entity.
	 *
	 * @return A GeoLoc name string, or null if site name not found.
	 */
	static public String getGeoLocNameBySiteName(String sn) {
		SiteData sd = lookup(sn);
		String gl = sd != null ? sd.getGeoLoc() : null;

		return !sanitize(gl).equals("") ? gl : null;
	}

	/** Build a string to describe a GeoLoc */
	static public String getLocDescription(GeoLoc gl, String connect) {
		if (gl == null)
			return "Unknown location";
		String fmt = getFormatString(gl);
		Road r = gl.getRoadway();
		Road x = gl.getCrossStreet();
		// build [RD.*] values
		String rdfull = null;
		String rdabbr = null;
		String rd = null;
		String rddir = null;
		String rddirfull = null;
		if (r != null) {
			rdfull = r.getName();
			rdabbr = r.getAbbrev();
			rddir = Direction.fromOrdinal(gl.getRoadDir()).abbrev;
			rddirfull = Direction.fromOrdinal(gl.getRoadDir()).toString();
			rd = "".equals(sanitize(rdabbr)) ? rdfull : rdabbr;
		}
		// build [X.*] values
		String xrdfull = null;
		String xrdabbr = null;
		String xrd = null;
		String xrddir = null;
		String xrddirfull = null;
		String xmod = null;
		if (x != null) {
			xrdfull = x.getName();
			xrdabbr = x.getAbbrev();
			xrddir = Direction.fromOrdinal(gl.getRoadDir()).abbrev;
			xrddirfull = Direction.fromOrdinal(gl.getRoadDir()).toString();
			xrd = "".equals(sanitize(xrdabbr)) ? xrdfull : xrdabbr;
			xmod = connect != null ? connect : sanitize(GeoLocHelper.getModifier(gl));
		}
		// build the rest
		xmod = xmod != null ? xmod : "";
		String mile = gl.getMilepoint();
		String cty = null;
		String ctyfull = null;
		SiteData sd = lookupByGeoLoc(gl);
		if (sd != null) {
			County c = County.lookup(sd.getCounty());
			if (c != null) {
				ctyfull = c.name;
				cty = c.code;
			}
		}
		fmt = fmt.replaceAll(TAG_RD, sanitize(rd));
		fmt = fmt.replaceAll(TAG_RDFULL, sanitize(rdfull));
		fmt = fmt.replaceAll(TAG_RDABBR, sanitize(rdabbr));
		fmt = fmt.replaceAll(TAG_RDDIR, sanitize(rddir));
		fmt = fmt.replaceAll(TAG_RDDIRFULL, sanitize(rddirfull));
		fmt = fmt.replaceAll(TAG_XRD, sanitize(xrd));
		fmt = fmt.replaceAll(TAG_XRDFULL, sanitize(xrdfull));
		fmt = fmt.replaceAll(TAG_XRDABBR, sanitize(xrdabbr));
		fmt = fmt.replaceAll(TAG_XRDDIR, sanitize(xrddir));
		fmt = fmt.replaceAll(TAG_XRDDIRFULL, sanitize(xrddirfull));
		fmt = fmt.replaceAll(TAG_XMOD, xmod);        // don't sanitize
		fmt = fmt.replaceAll(TAG_MILE, sanitize(mile));
		fmt = fmt.replaceAll(TAG_CTY, sanitize(cty));
		fmt = fmt.replaceAll(TAG_CTYFULL, sanitize(ctyfull));
		fmt = fmt.replaceAll(TAG_GLNAME, sanitize(gl.getName()));
		fmt = fmt.replaceAll("\\s+", " ");
		fmt = fmt.trim();
		return fmt;
	}

	/** Trim a string, or convert it to an empty string if null */
	static private String sanitize(String s) {
		return (s != null) ? s.trim() : "";
	}

}

