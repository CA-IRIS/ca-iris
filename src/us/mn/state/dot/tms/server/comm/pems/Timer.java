/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010  Minnesota Department of Transportation
 * Copyright (C) 2010-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.pems;

import java.util.Calendar;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;

/**
 * A timer class.
 * @author Michael Darter
 * @author Travis Swanston
 */
public class Timer {

	/** Timer event */
	public interface TimerEvent {
		void readEvent();
	}

	/** Create scheduler that runs reader job */
	private Scheduler job_scheduler;

	/** Timer job */
	private Job timer_job;

	/** Timer event, called periodically */
	private TimerEvent timer_event;

	/** Time period */
	private int period_secs;

	/** Timer offset value */
	private int offset_secs;

	/** Timer name */
	private String timer_name;

	/**
	 * Constructor.
	 * @param n Descriptive name of timer job.
	 * @param p System attribute for timer period, in seconds.
	 *          Use a value of 0 to not read.
	 * @param o System attribute for timer offset, in seconds. Use
	 *        a value of null to ignore, which is equivalent of using 0.
	 * @param te Timer event, never null, called periodically.
	 */
	public Timer(String n, int p, int o, TimerEvent te)
	{
		timer_name = n;
		period_secs = p;
		offset_secs = o;
		timer_event = te;
		job_scheduler = new Scheduler("Scheduler: " + timer_name);
		timer_job = new LocalJob(period_secs, offset_secs);
		job_scheduler.addJob(timer_job);
	}

	/** Local timer job */
	private class LocalJob extends Job {

		/** Constructor */
		public LocalJob(int pp, int po) {
			super(Calendar.SECOND, pp, Calendar.SECOND, po);
		}

		/** Perform the timer job */
		public void perform() throws Exception {
			timer_event.readEvent();	// throws
		}
	}

}
