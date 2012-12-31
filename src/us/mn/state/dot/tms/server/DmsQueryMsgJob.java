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
package us.mn.state.dot.tms.server;

import java.util.Calendar;
import java.util.Iterator;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;

/**
 * Job to periodically query all DMS messages.
 *
 * @author Douglas Lau
 */
public class DmsQueryMsgJob extends Job {

	/** Seconds to offset each poll from start of interval */
	static private final int OFFSET_SECS = 4;

	/** Create a new job to query DMS messages */
	public DmsQueryMsgJob(int i_secs) {
		super(Calendar.SECOND, i_secs, Calendar.SECOND, OFFSET_SECS);
	}

	/** Perform the DMS query messages job */
	public void perform() {
		int req = DeviceRequest.QUERY_MESSAGE.ordinal();
		Iterator<DMS> it = DMSHelper.iterator();
		while(it.hasNext()) {
			DMS d = it.next();
			if(d instanceof DMSImpl) {
				DMSImpl dms = (DMSImpl)d;
				if(dms.isPeriodicallyQueriable())
					dms.setDeviceRequest(req);
			}
		}
	}
}
