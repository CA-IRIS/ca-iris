/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2002-2015  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;

/**
 * Ntcip ControllerErrorStatus object
 *
 * @author Douglas Lau
 */
public class ControllerErrorStatus extends ASN1Integer {

	/** Other error */
	static public final int OTHER = 1 << 0;

	/** PROM error */
	static public final int PROM = 1 << 1;

	/** Program/processor error */
	static public final int PROCESSOR = 1 << 2;

	/** RAM error */
	static public final int RAM = 1 << 3;

	/** Controller-to-display interface error */
	static public final int DISPLAY = 1 << 4;

	/** Error descriptions */
	static protected final String ERROR[] = {
		"OTHER", "PROM", "PROGRAM/PROCESSOR", "RAM", "DISPLAY"
	};

	/** Create a new ControllerErrorStatus object */
	public ControllerErrorStatus() {
		super(MIB1203.statError.child(new int[] {10, 0}));
	}

	/** Get the object value */
	@Override
	public String getValue() {
		int v = getInteger();
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < ERROR.length; i++) {
			if ((v & 1 << i) != 0) {
				if (buf.length() > 0)
					buf.append(", ");
				buf.append(ERROR[i] + " ERROR");
			}
		}
		if (buf.length() == 0)
			buf.append("OK");
		return buf.toString();
	}
}
