/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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

/**
 * Device is the base interface for all field devices, including detectors,
 * cameras, ramp meters, dynamic message signs, etc.
 *
 * @author Douglas Lau
 */
public interface Device extends ControllerIO {

	/** SONAR type names.  This is a list of sub-interfaces required by
	 * SONAR for the TimingPlan interface, which references Device. */
	String[] SONAR_TYPES = {
		Camera.SONAR_TYPE,
		Detector.SONAR_TYPE,
		DMS.SONAR_TYPE,
		LCSArray.SONAR_TYPE,
		RampMeter.SONAR_TYPE,
		WarningSign.SONAR_TYPE
	};

	/** Set the administrator notes */
	void setNotes(String n);

	/** Get the administrator notes */
	String getNotes();

	/** Request a device operation (query message, test pixels, etc.) */
	void setDeviceRequest(int r);

	/** Get the operation description */
	String getOperation();

	/** Get the operation status */
	String getOpStatus();
}
