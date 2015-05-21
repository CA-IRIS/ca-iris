/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ntcip.mib1203;

import us.mn.state.dot.tms.server.comm.snmp.Counter;

/**
 * AvailableGraphicMemory is the number of bytes available to store graphics
 * on the sign.
 *
 * @author Douglas Lau
 */
public class AvailableGraphicMemory extends Counter {

	/** Create a new AvailableGraphicMemory object */
	public AvailableGraphicMemory() {
		super(MIB1203.graphicDefinition.child(new int[] {4, 0}));
	}
}
