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
package us.mn.state.dot.tms.server.comm.ss105;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.IDebugLog;
import us.mn.state.dot.tms.server.comm.OpController;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation for SS105 device
 *
 * @author Douglas Lau
 */
abstract public class OpSS105 extends OpController<SS105Property> {

	/** SS 105 debug log */
	static private final IDebugLog SS105_LOG = new IDebugLog("ss105");

	/** Log an error msg */
	protected void logError(String msg) {
		if(SS105_LOG.isOpen())
			SS105_LOG.log(controller.getName() + "! " + msg);
	}

	/** Log a property query */
	protected void logQuery(SS105Property prop) {
		if(SS105_LOG.isOpen())
			SS105_LOG.log(controller.getName() + ": " + prop);
	}

	/** Log a property store */
	protected void logStore(SS105Property prop) {
		if(SS105_LOG.isOpen())
			SS105_LOG.log(controller.getName() + ":= " + prop);
	}

	/** Create a new SS105 operation */
	protected OpSS105(PriorityLevel p, ControllerImpl c) {
		super(p, c);
	}
}
