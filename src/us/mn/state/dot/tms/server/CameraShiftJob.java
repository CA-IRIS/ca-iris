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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;
import us.mn.state.dot.tms.PresetAliasHelper;
import us.mn.state.dot.tms.PresetAliasName;

import static us.mn.state.dot.tms.PresetAliasName.HOME;
import static us.mn.state.dot.tms.PresetAliasName.NIGHT_SHIFT;


/**
 * Used to move cameras to positions according to desired preset name
 * @author Jacob Barde
 */
public class CameraShiftJob extends Job {

	/** maximum amount of time in minutes to run this job */
	static final private int MAX_RUNTIME = 720; // 12 hours

	/** instance of the scheduler controlling this job */
	private final Scheduler scheduler;

	/** log for messages relating to this job */
	private final DebugLog log = new DebugLog("camerashiftjob");

	/** a map to track what cameras were moved (or attempted to do so) */
	private Map<Camera, Boolean> camMoved = new HashMap<>();

	/** Preset Alias Name to move cameras to */
	private PresetAliasName destPan = HOME;

	/**
	 * don't send the camera movements, but process cameras normally.
	 * for testing only
	 */
	private boolean disableCameraMovements = false;

	/** last time a log message was issued warning of excessive time */
	private long lastLogMessage = 0;

	/**
	 * Create a camera shift job
	 * @param s      the scheduler in charge of this job, used to add a
	 *               new CameraShiftJob after completing this job.
	 * @param pan    preset to move cameras to. if null, will move to the
	 *               last shift's preset
	 * @param offset offset in minutes
	 */
	public CameraShiftJob(Scheduler s, PresetAliasName pan, int offset) {

		super(Calendar.SECOND, 0, Calendar.MINUTE, offset);
		scheduler = s;
		destPan = (pan != null) ? pan : CameraHelper.calculateLastShift();
		log.log(TimeSteward.currentDateTimeString(true)
			+ " Camera shift job created.");
	}

	/** perform job */
	@Override
	public void perform() throws Exception {
		log.log(TimeSteward.currentDateTimeString(true)
			+ " Begin performing camera shift job.");

		for (Camera c : CameraHelper.getCamerasByShift(destPan)) {
			camMoved.put(c, false);
		}

		if (camMoved.isEmpty()) {
			log.log("WARNING: no cameras to shift-move.");
			return; // nothing to do
		}

		int concurrent = CameraHelper.getConcurrentMovements();
		int movingNow = 0;

		long started = TimeSteward.currentTimeMillis();
		lastLogMessage = started;
		int delay = CameraHelper.getShiftPause() * 1000;
		long lastMovement = 0L;
		long diff;

		while (doJob(started)) {
			if (movingNow >= concurrent) {
				TimeSteward.sleep(delay);
				movingNow = 0;
			}

			diff = (TimeSteward.currentTimeMillis() - lastMovement);
			if (delay > 0 && delay > diff)
				TimeSteward.sleep((delay - diff));

			for (Camera c : camMoved.keySet()) {

				// skip moved cameras
				if (camMoved.get(c))
					continue;

				// too many moving cameras, break for delay
				if (movingNow >= concurrent)
					break;

				// move camera
				moveCamera(c, destPan);
				movingNow++;
				lastMovement = TimeSteward.currentTimeMillis();
				camMoved.put(c, true);
			}
		}
	}

	/** actions to perform upon completion of job */
	@Override
	public void complete() {
		super.complete();

		// log cameras that the job was unable to move for shift
		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				log.log("WARNING: Camera Shift Job was unable "
					+ "to move camera '" + c.getName()
					+ "'.");
		}

		// calculate when next job should occur
		PresetAliasName nsp = (HOME.equals(destPan))
			? NIGHT_SHIFT : HOME;

		GregorianCalendar now =
			(GregorianCalendar) TimeSteward.getCalendarInstance();

		// if the current time is after the today's dayshift, calculate
		// for tomorrow's dayshift
		int dayOffset = 0;
		Calendar tds = CameraHelper.getShiftTime(HOME, 0);
		if (now.getTimeInMillis() > tds.getTimeInMillis())
			dayOffset = 1;
		Calendar c = (NIGHT_SHIFT.equals(nsp))
			? CameraHelper.getShiftTime(NIGHT_SHIFT, 0)
			: CameraHelper.getShiftTime(HOME, dayOffset);

		int offset = (int) (c.getTimeInMillis()
			- now.getTimeInMillis()) / 1000 / 60;

		scheduler.addJob(new CameraShiftJob(scheduler, nsp, offset));

		log.log(TimeSteward.currentDateTimeString(true)
			+ " Completed camera shift job.");
	}

	/** determine if the job should continue executing at this point */
	private boolean doJob(long started) {
		boolean rv = false;

		// if a camera in the list hasn't been moved, do the job
		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				rv = true;
		}

		int mxrt = MAX_RUNTIME * 60 * 1000;

		if ((TimeSteward.currentTimeMillis() - lastLogMessage) > 3600) {
			log.log("WARNING: Camera Shift Job is taking more than "
				+ "an hour.");
			lastLogMessage = TimeSteward.currentTimeMillis();
		}
		// discontinue job if max runtime exceeded (default 12 hours)
		if ((TimeSteward.currentTimeMillis() - started) > mxrt)
			rv = false;

		return rv;
	}

	/** move the camera to desired preset */
	private void moveCamera(Camera c, PresetAliasName pan) {
		if (disableCameraMovements)
			return;

		Integer p = PresetAliasHelper.getPreset(c, pan);

		// move the camera to preset
		if (p != null)
			c.setRecallPreset(p);
	}

	/** set the disableCameraMovements property */
	public void setDisableCameraMovements(boolean f) {
		this.disableCameraMovements = f;
	}

}
