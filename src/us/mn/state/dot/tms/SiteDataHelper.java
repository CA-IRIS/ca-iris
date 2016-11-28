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

import java.util.Iterator;


/**
 * SiteDataHelper has static methods for dealing with SiteData entities.
 *
 * @author Travis Swanston
 */
public class SiteDataHelper extends BaseHelper {

	// [RDFULL]     full road name
	// [RDABBR]     road abbreviation
	// [RD]         equivalent to [RDABBR] if exists, else [RDFULL]
	// [RDDIRFULL]  road direction
	// [RDDIR]      road direction abbreviation
	// [XRDFULL]    full road name of cross-road
	// [XRDABBR]    road abbreviation of cross-road
	// [XRD]        equivalent to [XRDABBR] if exists, else [XRDFULL]
	// [XRDDIRFULL] road direction of cross-road
	// [XRDDIR]     road direction abbreviation of cross-road
	// [XMOD]       prepositional relation of road to cross-road
	// [MILE]       milepoint
	// [CTY]        county code
	// [CTYFULL]    county name
	// [GLNAME]     GeoLoc name

	public final static String TAG_RDFULL     = "\\[RDFULL\\]";
	public final static String TAG_RDABBR     = "\\[RDABBR\\]";
	public final static String TAG_RD         = "\\[RD\\]";
	public final static String TAG_RDDIR      = "\\[RDDIR\\]";
	public final static String TAG_RDDIRFULL  = "\\[RDDIRFULL\\]";
	public final static String TAG_XRDFULL    = "\\[XRDFULL\\]";
	public final static String TAG_XRDABBR    = "\\[XRDABBR\\]";
	public final static String TAG_XRD        = "\\[XRD\\]";
	public final static String TAG_XRDDIR     = "\\[XRDDIR\\]";
	public final static String TAG_XRDDIRFULL = "\\[XRDDIRFULL\\]";
	public final static String TAG_XMOD       = "\\[XMOD\\]";
	public final static String TAG_MILE       = "\\[MILE\\]";
	public final static String TAG_CTY        = "\\[CTY\\]";
	public final static String TAG_CTYFULL    = "\\[CTYFULL\\]";
	public final static String TAG_GLNAME     = "\\[GLNAME\\]";

	static public final String DESCFMT_DEFAULT =
		"[RDFULL] [RDDIR] [XMOD] [XRDFULL] [XRDDIR]";

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
	protected SiteDataHelper() {
		assert false;
	}

	/** Get a SiteData iterator */
	static public Iterator<SiteData> iterator() {
		return new IteratorWrapper<>(namespace.iterator(
			SiteData.SONAR_TYPE));
	}

	/** Lookup a SiteData entity */
	static public SiteData lookup(String n) {
		if (n == null)
			return null;
		return (SiteData) namespace.lookupObject(SiteData.SONAR_TYPE,
			n);
	}

	/** Lookup a SiteData entity by GeoLoc */
	static public SiteData lookupByGeoLoc(GeoLoc gl) {
		if (gl == null)
			return null;
		String gn = gl.getName();
		if (gn == null)
			return null;
		Iterator<SiteData> it = iterator();
		while (it.hasNext()) {
			SiteData sd = it.next();
			if (gn.equals(sd.getGeoLoc()))
				return sd;
		}
		return null;
	}

	/**
	 * Build a site name string for a SiteData entity.
	 * @param gn The GeoLoc name corresponding to the SiteData entity.
	 *
	 * @return A site name string, or null if entity not found or if
	 * entity doesn't contain a site name
	 */
	static public String getSiteName(String gn) {
		if (gn == null)
			return null;
		SiteData siteData = null;
		Iterator<SiteData> it = iterator();
		while (it.hasNext()) {
			SiteData sd = it.next();
			if (gn.equals(sd.getGeoLoc())) {
				siteData = sd;
				break;
			}
		}
		if (siteData == null)
			return null;
		String sn = siteData.getSiteName();
		if (sanitize(sn).equals(""))
			return null;
		return sn;
	}

	/**
	 * Find a GeoLoc name by site name.
	 * @param sn The site name corresponding to the GeoLoc entity.
	 *
	 * @return A GeoLoc name string, or null if site name not found.
	 */
	static public String getGeoLocNameBySiteName(String sn) {
		if (sn == null)
			return null;
		SiteData siteData = null;
		Iterator<SiteData> it = iterator();
		while (it.hasNext()) {
			SiteData sd = it.next();
			if (sn.equals(sd.getSiteName())) {
				siteData = sd;
				break;
			}
		}
		if (siteData == null)
			return null;
		String gl = siteData.getGeoLoc();
		if (sanitize(gl).equals(""))
			return null;
		return gl;
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
			rddirfull = Direction.fromOrdinal(gl.getRoadDir())
				.toString();
			if ("".equals(sanitize(rdabbr)))
				rd = rdfull;
			else
				rd = rdabbr;
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
			xrddirfull = Direction.fromOrdinal(gl.getRoadDir())
				.toString();
			if ("".equals(sanitize(xrdabbr)))
				xrd = xrdfull;
			else
				xrd = xrdabbr;
			if (connect != null)
				xmod = connect;
			else
				xmod = sanitize(GeoLocHelper.getModifier(gl));
		}
		// build the rest
		xmod = ((xmod != null) ? xmod : "");
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
	static protected String sanitize(String s) {
		if (s == null)
			return "";
		return s.trim();
	}

}

