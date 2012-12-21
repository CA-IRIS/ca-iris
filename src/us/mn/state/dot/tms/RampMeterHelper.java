/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
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
import us.mn.state.dot.sonar.Checker;

/**
 * Ramp meter helper methods.
 *
 * @author Douglas Lau
 */
public class RampMeterHelper extends BaseHelper {

	/** Disallow instantiation */
	protected RampMeterHelper() {
		assert false;
	}

	/** Lookup the ramp meter with the specified name */
	static public RampMeter lookup(String name) {
		return (RampMeter)namespace.lookupObject(RampMeter.SONAR_TYPE,
			name);
	}

	/** Find ramp meters using a Checker */
	static public RampMeter find(final Checker<RampMeter> checker) {
		return (RampMeter)namespace.findObject(RampMeter.SONAR_TYPE,
			checker);
	}

	/** Get a ramp meter iterator */
	static public Iterator<RampMeter> iterator() {
		return new IteratorWrapper<RampMeter>(namespace.iterator(
			RampMeter.SONAR_TYPE));
	}

	/** Lookup the camera for a ramp meter */
	static public Camera getCamera(RampMeter meter) {
		if(meter != null)
			return meter.getCamera();
		else
			return null;
	}

	/** Test if a ramp meter is active */
	static public boolean isActive(RampMeter meter) {
		return ControllerHelper.isActive(meter.getController());
	}

	/** Test if a ramp meter is failed */
	static public boolean isFailed(RampMeter meter) {
		return ControllerHelper.isFailed(meter.getController());
	}
}
