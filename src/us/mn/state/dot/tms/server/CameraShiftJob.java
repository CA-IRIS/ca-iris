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

import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;
import us.mn.state.dot.tms.PresetAliasHelper;
import us.mn.state.dot.tms.PresetAliasName;
import us.mn.state.dot.tms.VideoServerCoupler;


/**
 * Used to move cameras to positions according to desired preset name
 * @author Jacob Barde
 */
public class CameraShiftJob extends Job {

	/** a map to track what cameras were moved (or attempted to do so) */
	private Map<Camera, Boolean> camMoved = new HashMap<>();

	/** Preset Alias Name to move cameras to */
	private PresetAliasName destPan = PresetAliasName.HOME;

	/** VideoServerCoupler to query cameras for their in-use status */
	private VideoServerCoupler videoServerCoupler;

	/** maximum amount of time in minutes to run this job */
	private int maxRuntime = 120;

	public CameraShiftJob(PresetAliasName pan, VideoServerCoupler vsc,
		Integer mxRuntime) {
		super(0);
		destPan = pan;
		videoServerCoupler = vsc;
		if (mxRuntime != null && mxRuntime > 0)
			maxRuntime = mxRuntime;
	}

	@Override
	public void perform() throws Exception {

		for (Camera c : CameraHelper.getCamerasByShift(destPan)) {
			camMoved.put(c, false);
		}

		if (camMoved.isEmpty())
			return; // nothing to do

		int concurrent = CameraHelper.getConcurrentMovements();
		int movingNow = 0;

		long started = TimeSteward.currentTimeMillis();
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

			Map<String, Integer> camsInUse =
				videoServerCoupler.getCamerasInUse();
			for (Camera c : camMoved.keySet()) {

				// skip moved cameras
				if (camMoved.get(c))
					continue;

				// skip in-use cameras
				if (camsInUse.containsKey(c.getName()))
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

	/** determine if the job should continue executing at this point */
	private boolean doJob(long started) {
		boolean rv = false;

		// if a camera in the list hasn't been moved, do the job
		for (Camera c : camMoved.keySet()) {
			if (!camMoved.get(c))
				rv = true;
		}

		int mxrt = maxRuntime * 60 * 1000;

		// discontinue job if max runtime exceeded (default 120 minutes)
		if ((TimeSteward.currentTimeMillis() - started) > mxrt)
			rv = false;

		return rv;
	}

	private void moveCamera(Camera c, PresetAliasName pan) {
		Integer p = PresetAliasHelper.getPreset(c, pan);

		// move the camera to preset
		if (p != null)
			c.setRecallPreset(p);
	}
}
