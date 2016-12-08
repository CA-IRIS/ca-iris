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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import us.mn.state.dot.tms.VideoServerCoupler;
import us.mn.state.dot.tms.geo.Position;

import static us.mn.state.dot.tms.CameraHelper.getCamerasForShift;
import static us.mn.state.dot.tms.CameraHelper.getConcurrentMovements;
import static us.mn.state.dot.tms.CameraHelper.getGeographicCenter;
import static us.mn.state.dot.tms.CameraHelper.getShiftPause;
import static us.mn.state.dot.tms.CameraHelper.getShiftTime;
import static us.mn.state.dot.tms.CameraHelper.getSunriseOffset;
import static us.mn.state.dot.tms.CameraHelper.getSunsetOffset;
import static us.mn.state.dot.tms.CameraHelper.isShiftReinit;
import static us.mn.state.dot.tms.PresetAliasName.HOME;
import static us.mn.state.dot.tms.PresetAliasName.NIGHT_SHIFT;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_CONCUR_MOVE;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_MOVE_PAUSE;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_REINIT;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_SUNRISE_OFFSET;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_SUNSET_OFFSET;


/**
 * Used to move cameras to positions according to desired preset name
 * @author Jacob Barde
 */
public class CameraShiftJob extends Job {

	/** log for messages relating to this job */
	static final public DebugLog log = new DebugLog("camerashiftjob");

	/** seconds offset from the minute to perform this job */
	static final private int OFFSET_SECS = 30;

	/** maximum amount of time to run this job (hours) */
	static final private int MAX_RUNTIME = 12;

	/** milliseconds in an hour */
	static final private int HOUR = 3600000;

	/** milliseconds in an minute */
	static final private int MINUTE = 60000;

	/** camera comparator */
	static final private Comparator<Camera> comp = new Comparator<Camera>() {
		@Override
		public int compare(Camera o1, Camera o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/** date format for some logging */
	static final private SimpleDateFormat time_format = new SimpleDateFormat("dd MMM yyyy h:mm a z");

	/** various boilerplate log messages */
	static final private String LOG_COMPUTED_INFO = "Computed astronomical and geographical information...";
	static final private String LOG_CAMERA_SETTINGS = "Camera Shift Settings...";
	static final private String LOG_CAMERAS_FOUND_START = "Cameras found at server start-up that may be shifted, an asterisk (*) denotes a camera with a scheduled shift...";
	static final private String LOG_JOB_BEGIN = "Camera shift job begin...";
	static final private String LOG_JOB_COMPLETED = "Camera shift job completed.";
	static final private String LOG_BEGIN_SHIFT = "Begin performing camera shift...";
	static final private String LOG_COMPLETED_SHIFT = "Completed camera shift.";
	static final private String LOG_SCHEDULING_SHIFT = "Scheduling next camera-shift job.";
	static final private String LOG_SCHEDULED_SHIFT = "Successfully scheduled next camera-shift job.";
	static final private String ERROR_COMPUTE_SHIFT = "ERROR: Unable to compute and schedule next camera-shift job.";
	static final private String NOTICE_MT_HOUR = "NOTICE: Camera Shift Job is taking more than an hour.";
	static final private String WARN_CAMERA_IN_USE = "WARNING: Not moving camera, as it is in use: ";
	static final private String WARN_NO_CAMERAS = new StringBuilder("WARNING: no cameras found with requisite '").append(HOME.alias).append("' and '").append(NIGHT_SHIFT.alias).append("' presets enabled.").toString();
	static final private String WARN_TERMINATING = new StringBuilder("WARNING: Terminating job, as it has taken more than ").append(MAX_RUNTIME).append(" hours.").toString();

	/** instance of the scheduler controlling this job */
	private final Scheduler scheduler;

	/** a map to track what cameras were moved (or attempted to do so) */
	private Map<Camera, Boolean> camMoved;

	/** used to force movement of camera, typically for camera with schedule_shift defined */
	private boolean forceMovement = false;

	/** Preset Alias Name to move cameras to */
	private PresetAliasName destPan = HOME;

	/** VideoServerCoupler to query cameras for their in-use status */
	private VideoServerCoupler videoServerCoupler;

	/** last time a log message was issued warning of excessive time */
	private long lastLogMessage;

	/** used to determine if we execute on server startup */
	private boolean ignoreStartup = false;

	/** used if this is at the start of the IRIS server */
	private final boolean isIrisServerStart;

	/**
	 * Create a camera shift job
	 * @param s      the scheduler in charge of this job, used to add new CameraShiftJob after completing this job.
	 * @param vsc    the video server coupler, to see if a camera is in-use.
	 * @param pan    preset to move cameras to. if null, will move to the last shift's preset
	 * @param offset offset in millis
	 */
	public CameraShiftJob(Scheduler s, VideoServerCoupler vsc, PresetAliasName pan, int offset) {
		this(s, vsc, pan, offset, null);
	}

	/**
	 * Create a camera shift job
	 * @param s      the scheduler in charge of this job, used to add new CameraShiftJob after completing this job.
	 * @param vsc    the video server coupler, to see if a camera is in-use.
	 * @param pan    preset to move cameras to. if null, will move to the last shift's preset
	 * @param offset offset in millis
	 * @param ctm    camera to move - this should only be used internally by this class. Specifically for cameras
	 *               with shift_schedule's defined.
	 */
	private CameraShiftJob(Scheduler s, VideoServerCoupler vsc, PresetAliasName pan, int offset, Camera ctm) {
		super(convertToSubMinuteOffset((offset)));

		scheduler = s;
		videoServerCoupler = vsc;
		isIrisServerStart = (pan == null);
		camMoved = new HashMap<>();
		if (null != ctm) {
			forceMovement = true;
			camMoved.put(ctm, false);
		}

		if (null == pan && !isShiftReinit())
			ignoreStartup = true;
		destPan = (pan != null) ? pan : CameraHelper.calculateLastShift(offset);

		StringBuilder sb = new StringBuilder("Camera shift");
		if (null == ctm && isIrisServerStart)
			sb.append(" re-initialization");
		sb.append(" job created");
		if (null != ctm)
			sb.append(" for camera ").append(ctm.getName());
		sb.append(", will execute in about ").append((offset/MINUTE)).append(" minutes for the ")
			.append(destPan.name()).append(" preset.");
		log.log(sb.toString());

		logServerStartup();
	}

	/** perform job */
	@Override
	public void perform() throws Exception {
		if (!forceMovement && !isIrisServerStart)
			log.log("");
		log.log(LOG_JOB_BEGIN);

		logSettings();

		int concurrent = getConcurrentMovements();

		if (concurrent < 1 || ignoreStartup)
			return;

		List<Camera> camDeferred = new ArrayList<>();
		if (camMoved.isEmpty()) {
			// only load cameras with Home and Night-shift Home preset aliases enabled.
			List<Camera> shiftCams = getCamerasForShift();
			if (shiftCams.isEmpty()) {
				log.log(WARN_NO_CAMERAS);
				return;
			}
			log.log("Found " + shiftCams.size() + " cameras eligible for shift.");

			Collections.sort(shiftCams, comp);
			for (Camera c : shiftCams) {
				if (null != c.getShiftSchedule())
					camDeferred.add(c);
				else
					camMoved.put(c, false);
			}
		}

		long started = TimeSteward.currentTimeMillis();
		lastLogMessage = started;
		int delay = CameraHelper.getShiftPause() * 1000;
		int movingNow = 0;
		long lastMovement = 0L;
		long iuLastUpdate = 0L;
		long diff;

		logComputedInformation();

		log.log(LOG_BEGIN_SHIFT);

		Map<String, Integer> inuse = null;
		List<Camera> camSortedList = new ArrayList<>(camMoved.keySet());
		Collections.sort(camSortedList, comp);

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

			// in-use updated every 5 seconds so as not to overwhelm video server
			diff = (TimeSteward.currentTimeMillis() - iuLastUpdate);
			if (diff > 5000) {
				inuse = videoServerCoupler.getCamerasInUse();
				iuLastUpdate = TimeSteward.currentTimeMillis();
			}

			for (Camera c : camSortedList) {
				if (!forceMovement) {
					if (camMoved.get(c))
						continue;
					if (movingNow >= concurrent)
						break;
				}

				if (inuse.containsKey(c.getName())) {
					camMoved.put(c, true);
					log.log(WARN_CAMERA_IN_USE + c.getName());
					continue;
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
			scheduler.addJob(new CameraShiftJob(scheduler, videoServerCoupler, destPan, offsetMillis, c));
		}

	}

	/** actions to perform upon completion of job */
	@Override
	public void complete() {
		super.complete();

		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				log.log("WARNING: Camera Shift Job was unable to move camera " + c.getName() + ".");
		}

		if (forceMovement) {
			// jobs with forced movement at specific times do not reschedule.
			for (Camera c : camMoved.keySet())
				log.log("Completed scheduled camera shift job for " + c.getName()
					+ " at " + c.getShiftSchedule() + " past the hour.");
			return;
		} else
			log.log(LOG_COMPLETED_SHIFT);

		scheduleNextJob();

		log.log(LOG_JOB_COMPLETED);
	}

	/** schedule the next camera-shift job */
	private void scheduleNextJob() {
		log.log(LOG_SCHEDULING_SHIFT);
		PresetAliasName nsp = (HOME.equals(destPan)) ? NIGHT_SHIFT : HOME;
		GregorianCalendar now = (GregorianCalendar) TimeSteward.getCalendarInstance();

		int offset = 0;
		Calendar tds = getShiftTime(HOME, 0);
		Calendar tns = getShiftTime(NIGHT_SHIFT, 0);
		if (tds != null && HOME.equals(nsp) && now.getTimeInMillis() > tds.getTimeInMillis())
			offset = 1;
		else if (tns != null && NIGHT_SHIFT.equals(nsp) && now.getTimeInMillis() > tns.getTimeInMillis())
			offset = 1;

		Calendar c = null;
		if (tds != null && HOME.equals(nsp))
			c = (offset == 0) ? tds : getShiftTime(HOME, offset);
		else if (tns != null && NIGHT_SHIFT.equals(nsp))
			c = (offset == 0) ? tns : getShiftTime(NIGHT_SHIFT, offset);

		if (c != null) {
			offset = (int) (c.getTimeInMillis() - now.getTimeInMillis());
			scheduler.addJob(new CameraShiftJob(scheduler, videoServerCoupler, nsp, offset));
			log.log(LOG_SCHEDULED_SHIFT);
		} else
			log.log(ERROR_COMPUTE_SHIFT);
	}

	/** determine if the job should continue executing at this point */
	private boolean doJob(long started) {
		boolean rv = false;

		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				rv = true;
		}

		int mxrt = MAX_RUNTIME * HOUR;

		if ((TimeSteward.currentTimeMillis() - lastLogMessage) > HOUR) {
			log.log(NOTICE_MT_HOUR);
			lastLogMessage = TimeSteward.currentTimeMillis();
		}

		if ((TimeSteward.currentTimeMillis() - started) > mxrt) {
			rv = false;
			log.log(WARN_TERMINATING);
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

	/** log current settings to log file */
	private void logSettings() {
		if (forceMovement)
			return;

		log.log(LOG_CAMERA_SETTINGS);
		StringBuilder sb = new StringBuilder()
			.append("    ")
			.append(CAMERA_SHIFT_CONCUR_MOVE.name().toLowerCase())
			.append(": .. ").append(getConcurrentMovements());
		if (getConcurrentMovements() < 1)
			sb.append("   <=== value DISABLES camera-shift job");
		log.log(sb.toString());
		sb = new StringBuilder()
			.append("    ")
			.append(CAMERA_SHIFT_REINIT.name().toLowerCase())
			.append(": ....... ").append(isShiftReinit());
		log.log(sb.toString());
		sb = new StringBuilder()
			.append("    ")
			.append(CAMERA_SHIFT_MOVE_PAUSE.name().toLowerCase())
			.append(": ... ").append(getShiftPause());
		if (getShiftPause() > 180)
			sb.append("   <=== LARGE values, particularly when combined with a large number of cameras, may cause time-related issues with camera-shift jobs.");
		log.log(sb.toString());
		sb = new StringBuilder()
			.append("    ")
			.append(CAMERA_SHIFT_SUNRISE_OFFSET.name().toLowerCase())
			.append(": ").append(getSunriseOffset());
		log.log(sb.toString());
		sb = new StringBuilder()
			.append("    ")
			.append(CAMERA_SHIFT_SUNSET_OFFSET.name().toLowerCase())
			.append(":  ").append(getSunsetOffset());
		log.log(sb.toString());
	}

	/** log some information only at server startup */
	private void logServerStartup() {
		if (!isIrisServerStart || forceMovement)
			return;

		logSettings();
		logComputedInformation();

		List<Camera> shiftCams = getCamerasForShift();
		if (shiftCams.isEmpty()) {
			log.log(WARN_NO_CAMERAS);
			return;
		} else
			log.log("Cameras found eligible for shift: " + shiftCams.size());

		log.log(LOG_CAMERAS_FOUND_START);
		Collections.sort(shiftCams, comp);
		StringBuilder sb;
		for (Camera c : shiftCams) {
			sb = new StringBuilder("    ").append(c.getName());
			if (null != c.getShiftSchedule())
				sb.append(" *");
			log.log(sb.toString());
		}
	}

	/** log some computed information */
	private void logComputedInformation() {
		if (forceMovement)
			return;

		Position pos = getGeographicCenter();
		log.log(LOG_COMPUTED_INFO);
		log.log("    Geographical center: " + pos.toString());
		Calendar c = getShiftTime(HOME, 0);
		Calendar s = (Calendar)c.clone();
		s.add(Calendar.MINUTE, -(getSunriseOffset()));
		log.log("    Today's sunrise: ... " + time_format.format(s.getTime()) + "  (shift @ " + time_format.format(c.getTime()) + ")");
		c = getShiftTime(NIGHT_SHIFT, 0);
		s = (Calendar)c.clone();
		s.add(Calendar.MINUTE, -(getSunsetOffset()));
		log.log("    Today's sunset: .... " + time_format.format(s.getTime()) + "  (shift @ " + time_format.format(c.getTime()) + ")");
		c = getShiftTime(HOME, 1);
		s = (Calendar)c.clone();
		s.add(Calendar.MINUTE, -(getSunriseOffset()));
		log.log("    Tomorrow's sunrise:  " + time_format.format(s.getTime()) + "  (shift @ " + time_format.format(c.getTime()) + ")");
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
