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
import us.mn.state.dot.sched.Completer;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.SamplePoller;

/**
 * Job to query 5-minute sample data
 *
 * @author Douglas Lau
 */
public class SampleQuery5MinJob extends Job {

	/** Seconds to offset each poll from start of interval */
	static protected final int OFFSET_SECS = 12;

	/** Job completer */
	protected final Completer comp;

	/** Job to be performed on completion */
	protected final Job flush_job = new Job(500) {
		public void perform() {
			// nothing to do
		}
	};

	/** Create a new 5-minute timer job */
	public SampleQuery5MinJob(Scheduler flush) {
		super(Calendar.MINUTE, 5, Calendar.SECOND, OFFSET_SECS);
		comp = new Completer("5-Minute", flush, flush_job);
	}

	/** Perform the 5-minute timer job */
	public void perform() {
		comp.reset();
		try {
			querySample5Min();
		}
		finally {
			comp.makeReady();
		}
	}

	/** Poll all controllers 5 minute interval */
	private void querySample5Min() {
		Iterator<Controller> it = ControllerHelper.iterator();
		while(it.hasNext()) {
			Controller c = it.next();
			if(c instanceof ControllerImpl)
				querySample5Min((ControllerImpl)c);
		}
	}

	/** Query 5-minute sample data from one controller */
	private void querySample5Min(ControllerImpl c) {
		if(c.hasActiveDetector()) {
			MessagePoller p = c.getPoller();
			if(p instanceof SamplePoller) {
				SamplePoller sp = (SamplePoller)p;
				sp.querySamples(c, 300, comp);
			}
		}
	}
}
