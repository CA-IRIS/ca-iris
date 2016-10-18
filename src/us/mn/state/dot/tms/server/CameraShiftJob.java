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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;
import us.mn.state.dot.tms.PresetAliasHelper;
import us.mn.state.dot.tms.PresetAliasName;
import us.mn.state.dot.tms.geo.Position;

import static us.mn.state.dot.tms.PresetAliasName.HOME;
import static us.mn.state.dot.tms.PresetAliasName.NIGHT_SHIFT;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_CONCUR_MOVE;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_REINIT;


/**
 * Used to move cameras to positions according to desired preset name
 * @author Jacob Barde
 */
public class CameraShiftJob extends Job {

	/** seconds offset from the minute to perform this job */
	static final private int OFFSET_SECS = 30;

	/** maximum amount of time in minutes to run this job */
	static final private int MAX_RUNTIME = 720; // 12 hours

	/** instance of the scheduler controlling this job */
	private final Scheduler scheduler;

	/** log for messages relating to this job */
	static final public DebugLog log = new DebugLog("camerashiftjob");

	/** a map to track what cameras were moved (or attempted to do so) */
	private Map<Camera, Boolean> camMoved;

	/** used to force movement of camera, typically for camera with schedule_shift defined */
	private boolean forceMovement = false;

	/** Preset Alias Name to move cameras to */
	private PresetAliasName destPan = HOME;

	/** last time a log message was issued warning of excessive time */
	private long lastLogMessage;

	/** used to determine if we execute on server startup */
	private boolean ignoreStartup = false;

	/**
	 * Create a camera shift job
	 * @param s      the scheduler in charge of this job, used to add new CameraShiftJob after completing this job.
	 * @param pan    preset to move cameras to. if null, will move to the last shift's preset
	 * @param offset offset in millis
	 */
	public CameraShiftJob(Scheduler s, PresetAliasName pan, int offset) {
		this(s, pan, offset, null);
	}

	/**
	 * Create a camera shift job
	 * @param s      the scheduler in charge of this job, used to add new CameraShiftJob after completing this job.
	 * @param pan    preset to move cameras to. if null, will move to the last shift's preset
	 * @param offset offset in millis
	 * @param ctm    camera to move - this should only be used internally by this class. Specifically for cameras
	 *               with shift_schedule's defined.
	 */
	private CameraShiftJob(Scheduler s, PresetAliasName pan, int offset, Camera ctm) {
		super(convertToSubMinuteOffset((offset)));

		scheduler = s;
		camMoved = new HashMap<>();
		if (ctm != null) {
			forceMovement = true;
			camMoved.put(ctm, false);
		}

		if (!CAMERA_SHIFT_REINIT.getBoolean() && pan == null)
			ignoreStartup = true;
		destPan = (pan != null) ? pan : CameraHelper.calculateLastShift(offset);

		StringBuilder sb = new StringBuilder("Camera shift job created");
		if (ctm != null)
			sb.append(" for camera ").append(ctm.getName());
		sb.append(", will execute in about ").append((offset/60000)).append(" minutes for the ")
			.append(destPan.name()).append(" preset.");
		log.log(sb.toString());
	}

	/** perform job */
	@Override
	public void perform() throws Exception {
		int concurrent = CameraHelper.getConcurrentMovements();

		if (concurrent < 1) {
			log.log("Camera Shift disabled due to " + CAMERA_SHIFT_CONCUR_MOVE.name().toLowerCase()
				+ " set to less than 1.");
			return;
		}

		log.log("Begin performing camera shift job.");

		if (ignoreStartup) {
			log.log("Not shifting cameras, as " + CAMERA_SHIFT_REINIT.name() + " is set to "
				+ CAMERA_SHIFT_REINIT.getBoolean() + ".");
			return;
		}

		List<Camera> camDeferred = new ArrayList<>();
		if (camMoved.isEmpty()) {
			for (Camera c : CameraHelper.getCamerasByShift(destPan)) {
				if (c.isShiftSchedule())
					camDeferred.add(c);
				else
					camMoved.put(c, false);
			}
		}

		if (camMoved.isEmpty()) {
			log.log("WARNING: no cameras to shift-move.");
			return;
		}

		long started = TimeSteward.currentTimeMillis();
		lastLogMessage = started;
		int delay = CameraHelper.getShiftPause() * 1000;
		int movingNow = 0;
		long lastMovement = 0L;
		long diff;

		if(!forceMovement) {
			Position pos = CameraHelper.getGeographicCenter();
			log.log("Sunrise/sunset event is calculated for GPS coordinates: " + pos.toString());
		}

		while (doJob(started)) {
			if (!forceMovement) {
				if (movingNow >= concurrent) {
					TimeSteward.sleep(delay);
					movingNow = 0;
				}

				diff = (TimeSteward.currentTimeMillis() - lastMovement);
				if (delay > 0 && delay > diff)
					TimeSteward.sleep((delay - diff));
			}

			for (Camera c : camMoved.keySet()) {
				if (!forceMovement) {
					if (camMoved.get(c))
						continue;
					if (movingNow >= concurrent)
						break;
				}

				moveCamera(c, destPan);
				movingNow++;
				lastMovement = TimeSteward.currentTimeMillis();
				camMoved.put(c, true);
			}
		}

		for (Camera c : camDeferred) {
			GregorianCalendar cal = (GregorianCalendar) TimeSteward.getCalendarInstance();
			cal.add(Calendar.MINUTE, 1); // exclude executing current minute.

			while (cal.get(Calendar.MINUTE) != c.getShiftSchedule())
				cal.add(Calendar.MINUTE, 1);

			int offsetMillis = (int) (cal.getTimeInMillis() - TimeSteward.currentTimeMillis());
			scheduler.addJob(new CameraShiftJob(scheduler, destPan, offsetMillis, c));
		}
	}

	/** actions to perform upon completion of job */
	@Override
	public void complete() {
		super.complete();

		log.log("Camera shift job wrapping up.");

		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				log.log("WARNING: Camera Shift Job was unable to move camera " + c.getName() + ".");
		}

		if (forceMovement) {
			// jobs with forced movement at specific times do not reschedule.
			for (Camera c : camMoved.keySet())
				log.log("Completed camera shift schedule job for camera " + c.getName()
					+ " at scheduled time of " + c.getShiftSchedule() + " past the hour.");
			return;
		}

		PresetAliasName nsp = (HOME.equals(destPan)) ? NIGHT_SHIFT : HOME;
		GregorianCalendar now = (GregorianCalendar) TimeSteward.getCalendarInstance();

		int offset = 0;
		Calendar tds = CameraHelper.getShiftTime(HOME, 0);
		Calendar tns = CameraHelper.getShiftTime(NIGHT_SHIFT, 0);
		if (HOME.equals(nsp) && now.getTimeInMillis() > tds.getTimeInMillis())
			offset = 1;
		else if (NIGHT_SHIFT.equals(nsp) && now.getTimeInMillis() > tns.getTimeInMillis())
			offset = 1;

		Calendar c = tds;
		if (HOME.equals(nsp))
			c = (offset == 0) ? tds : CameraHelper.getShiftTime(HOME, offset);
		else if (NIGHT_SHIFT.equals(nsp))
			c = (offset == 0) ? tns : CameraHelper.getShiftTime(NIGHT_SHIFT, offset);

		offset = (int) (c.getTimeInMillis() - now.getTimeInMillis());

		scheduler.addJob(new CameraShiftJob(scheduler, nsp, offset));
		log.log("Completed camera shift job.");
	}

	/** determine if the job should continue executing at this point */
	private boolean doJob(long started) {
		boolean rv = false;

		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				rv = true;
		}

		int mxrt = MAX_RUNTIME * 60 * 1000;

		if ((TimeSteward.currentTimeMillis() - lastLogMessage)
			> (3600 * 1000)) {

			log.log("WARNING: Camera Shift Job is taking more than an hour.");
			lastLogMessage = TimeSteward.currentTimeMillis();
		}

		if ((TimeSteward.currentTimeMillis() - started) > mxrt) {
			rv = false;
			log.log("Terminating job, as it has taken more than 12 hours.");
		}

		return rv;
	}

	/** move the camera to desired preset */
	private void moveCamera(Camera c, PresetAliasName pan) {
		Integer p = PresetAliasHelper.getPreset(c, pan);

		if (p != null) {
			c.setRecallPreset(p);
			log.log("Moved camera '" + c.getName() + "' to " + pan.name() + " position.");
		}
	}

	/**
	 * round to the nearest OFFSET_SEC second of the minute the offset calculates to.
	 * @param offset offset in millis
	 *
	 * @return offset in milliseconds
	 */
	static private int convertToSubMinuteOffset(int offset) {
		GregorianCalendar future = (GregorianCalendar) TimeSteward.getCalendarInstance();
		future.add(Calendar.MILLISECOND, offset); // add the offset
		future.set(Calendar.SECOND, OFFSET_SECS);
		future.set(Calendar.MILLISECOND, 0);
		return (int) (future.getTimeInMillis() - TimeSteward.currentTimeMillis());
	}
}
