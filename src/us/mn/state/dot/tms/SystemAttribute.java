/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008  Minnesota Department of Transportation
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
 * A system attribute is a name mapped to a string value.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public interface SystemAttribute extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "system_attribute";

	/** attribute names common to all agencies */
	// note: please append units to names!
	String DATABASE_VERSION = "database_version";
	String DMS_POLL_FREQ_SECS = "dms_poll_freq_secs";

	/** Set the attribute value */
	void setValue(String arg_value);

	/** Get the attribute value */
	String getValue();
}
