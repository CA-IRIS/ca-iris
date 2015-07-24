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
package us.mn.state.dot.tms.server;

import java.util.Calendar;
import java.util.Iterator;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.comm.DevicePoller;
import us.mn.state.dot.tms.server.comm.SamplePoller;

/**
 * Job to periodically query comm links.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class CommLinkQuery30SecJob extends Job {

	/** Seconds to offset each poll from start of interval */
	static protected final int OFFSET_SECS = 20;

	/** Create a new 30-second timer job */
	public CommLinkQuery30SecJob() {
		super(Calendar.SECOND, 30, Calendar.SECOND, OFFSET_SECS);
	}

	/** Perform the 30-second timer job */
	public void perform() {
		query30Sec();
	}

	/** Poll all sample pollers */
	protected void query30Sec() {
		Iterator<CommLink> it = CommLinkHelper.iterator();
		while (it.hasNext()) {
			CommLink cl = it.next();
			query30Sec((CommLinkImpl)cl);
		}
	}

	/** Query a comm link's sample poller */
	protected void query30Sec(CommLinkImpl cl) {
		DevicePoller p = cl.getPoller();
		if (p instanceof SamplePoller) {
			SamplePoller sp = (SamplePoller)p;
			sp.queryPoller();
		}
	}

}
