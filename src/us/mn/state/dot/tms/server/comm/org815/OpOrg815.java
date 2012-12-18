/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.org815;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.WeatherSensorImpl;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation for ORG-815 device
 *
 * @author Douglas Lau
 */
abstract public class OpOrg815 extends OpDevice<Org815Property> {

	/** ORG-815 debug log */
	static private final DebugLog ORG815_LOG = new DebugLog("org815");

	/** Log a property query */
	protected void logQuery(Org815Property prop) {
		if(ORG815_LOG.isOpen())
			ORG815_LOG.log(device.getName() + ": " + prop);
	}

	/** Weather sensor device */
	protected final WeatherSensorImpl sensor;

	/** Create a new ORG-815 operation */
	protected OpOrg815(PriorityLevel p, WeatherSensorImpl ws) {
		super(p, ws);
		sensor = ws;
	}
}
