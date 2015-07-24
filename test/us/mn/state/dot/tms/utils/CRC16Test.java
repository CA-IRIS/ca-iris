/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.utils;

import junit.framework.TestCase;

/**
 * Test cases for CRC16.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class CRC16Test extends TestCase {

	/** Constructor. */
	public CRC16Test(String name) {
		super(name);
	}

	/** Test cases. */
	public void test() {
		final int INIT_VALUE = 0xffff;

		// initial value
		assertTrue(new CRC16().getValue() == INIT_VALUE);

		// sequence of updates
		CRC16 c = new CRC16();
		c.update(1);
		c.update(2);
		c.update(3);
		c.update(4);
		c.update(5);
		assertTrue(c.getValue() == 47914);

		// reset
		c.reset();
		assertTrue(new CRC16().getValue() == INIT_VALUE);

		// more updates
		c.update(1);
		c.update(2);
		c.update(3);
		c.update(4);
		c.update(5);
		assertTrue(c.getValue() == 47914);

		// array update
		CRC16 bc1 = new CRC16();
		byte[] a1 = new byte[] {-1, 0, 1, 2, 3, 4, 5, 6};
		bc1.update(a1, 2, 5);
		assertTrue(bc1.getValue() == 47914);

		// array update
		CRC16 bc2 = new CRC16();
		byte[] a2 = new byte[] {1, 2, 3, 4, 5};
		bc2.update(a2, -5, 33);
		assertTrue(bc2.getValue() == 47914);
	}

}
