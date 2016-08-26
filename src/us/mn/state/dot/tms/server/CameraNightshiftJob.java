/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016       Southwest Research Institute
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
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.TimeSteward;

import static us.mn.state.dot.sched.TimeSteward.getCalendarInstance;

/**
 * Used to move cameras to their night-shift and day-shift positions
 * @author Jacob Barde
 */
public class CameraNightshiftJob extends Job{

	public CameraNightshiftJob() {
		super(0);
	}

	@Override
	public void perform() throws Exception {

	}
}
