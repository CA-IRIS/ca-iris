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
package us.mn.state.dot.tms.server.comm.ntcip.mibskyline;

import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;

/**
 * DmsTempCritical object
 *
 * @author Douglas Lau
 */
public class DmsTempCritical extends ASN1Integer {

	/** Create a new DmsTempCritical object */
	public DmsTempCritical() {
		super(MIB.skylineDmsSignCfg.child(new int[] {11, 0}));
	}
}
