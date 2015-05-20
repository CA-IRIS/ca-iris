/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2015  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.server.comm.snmp.ASN1OctetString;

/**
 * Ntcip LampFailureStuckOff object
 *
 * @author Douglas Lau
 */
public class LampFailureStuckOff extends ASN1OctetString {

	/** Create a new LampFailureStuckOff object */
	public LampFailureStuckOff() {
		super(MIB1203.statError.create(new int[] {6, 0}));
	}

	/** Get the object value */
	public String getValue() {
		StringBuilder b = new StringBuilder();
		int f = 1;
		for(byte v: value) {
			for(int bit = 0x01; bit < 0x0100; bit <<= 1, f++) {
				if((v & bit) != 0) {
					b.append("#");
					b.append(f);
					b.append(" STUCK OFF, ");
				}
			}
		}
		if(b.length() == 0)
			return "OK";
		else {
			// remove trailing comma and space
			b.setLength(b.length() - 2);
			return b.toString();
		}
	}
}
