/*
 * IRIS -- Intelligent Roadway Information System
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
package us.mn.state.dot.tms.utils.twilight;

import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * imported into IRIS framework for use in daytime/nighttime CCTV presets
 * full source can be found here: http://www.jstott.com/jsuntimes/
 * All source provided under GPL v2
 *
 * Source was modified to utilize existing classes within IRIS (e.g. Position in
 * place of LatitudeLongitude).
 *
 * Jacob Barde - August 2016
 */

/**
 * <p>
 * jSunTimes unit test suite.
 * </p>
 * 
 * <p>
 * (c) 2011 Jonathan Stott
 * </p>
 *
 * <p>
 * Created on 11-Jun-2011
 * </p>
 *
 * @author Jonathan Stott
 * @version 1.0
 * @since 1.0
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for uk.me.jstott.jcoord");
		// $JUnit-BEGIN$
		suite.addTestSuite(TimeTest.class);
		// $JUnit-END$
		return suite;
	}

}
