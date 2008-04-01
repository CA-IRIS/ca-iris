/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2008  Minnesota Department of Transportation
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
 * A video monitor output from a video switch
 *
 * @author Douglas Lau
 */
public interface VideoMonitor extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "video_monitor";

	/** Set the video monitor description */
	void setDescription(String d);

	/** Get the video monitor description */
	String getDescription();

	/** Set flag to restrict publishing camera images */
	void setRestricted(boolean r);

	/** Get flag to restrict publishing camera images */
	boolean getRestricted();
}
