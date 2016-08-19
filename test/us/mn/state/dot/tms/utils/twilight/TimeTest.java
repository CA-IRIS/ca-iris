/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016       Southwest Research Institute
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

import junit.framework.TestCase;

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
 * jSunTimes unit tests for uk.me.jstott.Time class.
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
public class TimeTest extends TestCase {

	/*
	 * Test method for 'uk.me.jstott.sun.Time.toString()'
	 */
	public void testToString() {
		Time t1 = new Time(10, 10, 10);
		assertEquals("10:10:10", t1.toString());
		t1.setRoundedSeconds(false);
		assertEquals("10:10:10.0", t1.toString());

		Time t2 = new Time(10, 10, 10.45);
		assertEquals("10:10:10", t2.toString());
		t2.setRoundedSeconds(false);
		assertEquals("10:10:10.45", t2.toString());

		Time t3 = new Time(10, 10, 10.55);
		assertEquals("10:10:11", t3.toString());
		t3.setRoundedSeconds(false);
		assertEquals("10:10:10.55", t3.toString());

		Time t4 = new Time(10, 10, 59.55);
		assertEquals("10:11:00", t4.toString());
		t4.setRoundedSeconds(false);
		assertEquals("10:10:59.55", t4.toString());

		Time t5 = new Time(10, 59, 59.55);
		assertEquals("11:00:00", t5.toString());
		t5.setRoundedSeconds(false);
		assertEquals("10:59:59.55", t5.toString());

		Time t6 = new Time(23, 59, 59.55);
		assertEquals("00:00:00", t6.toString());
		t6.setRoundedSeconds(false);
		assertEquals("23:59:59.55", t6.toString());
	}

}
