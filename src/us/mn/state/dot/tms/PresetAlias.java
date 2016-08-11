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
 * Camera preset alias.
 * Represents a single CCTV alias:preset# mapping.
 *
 * @author Travis Swanston
 */
public interface PresetAlias extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "camera_preset_alias";

	/** Get camera */
	Camera getCamera();

	/** Get preset alias enumeration */
	int getPresetAliasName();

	/** Set preset number mapping for this alias */
	void setPresetNum(int p);

	/** Get preset number mapping for this alias */
	int getPresetNum();

}
