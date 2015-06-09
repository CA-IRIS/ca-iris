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

import us.mn.state.dot.sonar.SonarObject;

/**
 * Site data.
 * Extended site data for geo_loc entities.
 *
 * @author Travis Swanston
 */
public interface SiteData extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "site_data";

	/** Get the GeoLoc referenced by this site data entity */
	String getGeoLoc();

	/** Set the GeoLoc referenced by this site data entity */
	void setGeoLoc(String gl);

	/** Get the county string */
	String getCounty();

	/** Set the county string */
	void setCounty(String c);

	/** Get the site name string */
	String getSiteName();

	/** Set the site name string */
	void setSiteName(String sn);

	/** Get the format string */
	String getFormat();

	/** Set the format string */
	void setFormat(String f);
}

