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
package us.mn.state.dot.tms.server;

import us.mn.state.dot.tms.SiteData;
import us.mn.state.dot.tms.TMSException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static us.mn.state.dot.tms.utils.SString.emptyBecomesNull;

/**
 * SiteDataImpl represents a single site data entity, which corresponds to a
 * GeoLoc entity.
 *
 * @author Travis Swanston
 */
public class SiteDataImpl extends BaseObjectImpl implements SiteData {

	/** Load all the site data entries */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, SiteDataImpl.class);
		store.query("SELECT name, geo_loc, county, site_name, format " +
			"FROM iris." + SONAR_TYPE + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new SiteDataImpl(
					row.getString(1),	// name
					row.getString(2),	// geo_loc
					row.getString(3),	// county
					row.getString(4),	// site_name
					row.getString(5)	// format
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("geo_loc", geo_loc);
		map.put("county", county);
		map.put("site_name", site_name);
		map.put("format", format);
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

	/** Constructor */
	public SiteDataImpl(String n) throws TMSException {
		super(n);
	}

	/** Constructor */
	protected SiteDataImpl(String n, String gl, String c, String sn, String f) {
		super(n);
		geo_loc = gl;
		county = c;
		site_name = sn;
		format = f;
	}

	/** GeoLoc referenced by this site data entity */
	private String geo_loc;

	/** Get the GeoLoc referenced by this site data entity */
	@Override
	public String getGeoLoc() {
		return geo_loc;
	}

	/** Set the GeoLoc referenced by this site data entity */
	@Override
	public void setGeoLoc(String gl) {
		geo_loc = gl;
	}

	/** Set the GeoLoc referenced by this site data entity */
	public void doSetGeoLoc(String gl) throws TMSException {
		if ((gl != null) && (gl.equals(geo_loc)))
			return;
		store.update(this, "geo_loc", gl);
		setGeoLoc(gl);
	}


	/** The county string */
	private String county;

	/** Get the county string */
	@Override
	public String getCounty() {
		return county;
	}

	/** Set the county string */
	@Override
	public void setCounty(String c) {
		county = c;
	}

	/** Set the county string */
	public void doSetCounty(String c) throws TMSException {
		if ((c != null) && (c.equals(county)))
			return;
		store.update(this, "county", c);
		setCounty(c);
	}


	/** The site name string */
	private String site_name;

	/** Get the site name string */
	@Override
	public String getSiteName() {
		return site_name;
	}

	/** Set the site name string */
	@Override
	public void setSiteName(String sn) {
		site_name = sn;
	}

	/** Set the site name string */
	public void doSetSiteName(String sn) throws TMSException {
		if ((sn != null) && (sn.equals(site_name)))
			return;
		// treat empty strings as null for database constraints
		String tsn = emptyBecomesNull(sn);
		store.update(this, "site_name", tsn);
		setSiteName(tsn);
	}

	/** The format string */
	private String format;

	/** Get the format string */
	@Override
	public String getFormat() {
		return format;
	}

	/** Set the format string */
	@Override
	public void setFormat(String f) {
		format = f;
	}

	/** Set the format string */
	public void doSetFormat(String f) throws TMSException {
		if ((f != null) && (f.equals(format)))
			return;
		store.update(this, "format", f);
		setFormat(f);
	}

}
