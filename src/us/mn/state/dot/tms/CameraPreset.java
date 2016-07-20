/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014  Minnesota Department of Transportation
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
 * Camera Preset
 *
 * @author Douglas Lau
 * @author Travis Swanston
 */
public interface CameraPreset extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "camera_preset";

	/** Minimum allowed preset number */
	static final int MIN_PRESET = 1;

	/** Maximum allowed preset number */
	static final int MAX_PRESET = 12;

	/** Get camera */
	Camera getCamera();

	/** Get preset number */
	int getPresetNum();

	/** Set direction */
	void setDirection(short d);

	/** Get direction */
	short getDirection();

	/** Get assigned flag */
	boolean getAssigned();
}
