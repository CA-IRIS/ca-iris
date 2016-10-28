/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2015 AHMCT, University of California
 * Copyright (C) 2016      California Department of Transportation
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
package us.mn.state.dot.tms.client.camera;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.VideoServerCoupler;
import us.mn.state.dot.tms.client.Session;


/**
 * Class that manages the state and control of a video wall, via
 * SwitchServer, a CA-only component of the video server.
 * NOTE: THIS IS PURE PROTOTYPE CODE THAT UNFORTUNATELY, DO TO SCHEDULE
 * CONSTRAINTS, BECAME PRODUCTION CODE.  IT NEEDS A FULL REDESIGN AND
 * REWRITE.
 * NOTE: There are some race conditions...
 * @author Travis Swanston
 * @author Jacob Barde
 */
public class VideoWallManager extends VideoServerCoupler {

	private final Session session;

	// current status maps.
	// do not manipulate contents; only change references.
	private volatile Map<String, String> decstat_map = new HashMap<>();
	private volatile Map<String, String> grouputil_map = new HashMap<>();


	/** Scheduler that runs refresh job */
	static private final Scheduler REFRESH
		= new Scheduler("VideoWallManager");

	static private final int REFRESH_PERIOD_SEC = 4;

	private class RefreshJob extends Job {
		private RefreshJob() {
			super(Calendar.SECOND, REFRESH_PERIOD_SEC,
				Calendar.SECOND, 2);
		}
		public void perform() throws Exception {
			updateStatus();
		}
	}

	/** Refresh job */
	private final Job refresh_job = new RefreshJob();

	/** Create a new form */
	public VideoWallManager(Session sess, Properties props) {
		super(props);
		session = sess;
		initialize();
		// initial update to prevent race with CameraDispatcher
		updateStatus();
	}

	private void initialize() {
		REFRESH.addJob(refresh_job);
	}

	public void dispose() {
		REFRESH.removeJob(refresh_job);
	}

	private synchronized void updateStatus() {
		if (base_url == null)
			return;

		String url = null;
		String resp = null;

		Map<String, String> dmap = null;
		Map<String, String> gumap = null;

		// get decoder map
		url = base_url + "?cmd=decstat";
		resp = querySwitchServer(url);
		if (resp != null)
			dmap = decodeDecstatResponse(resp.trim());

		if (dmap != null)
			decstat_map = dmap;

		// get grouputil map
		url = base_url + "?cmd=grouputil";
		resp = querySwitchServer(url);
		if (resp != null)
			gumap = decodeGrouputilResponse(resp.trim());

		if (gumap != null)
			grouputil_map = gumap;

		updateCameraCounts();
	}

	public Map<String, String> getDecoderMap() {
		return decstat_map;
	}

	public Map<String, String> getGroupUtilMap() {
		return grouputil_map;
	}

	// synchronous get.  -1 on error.
	public int getNumConns(String cid) {
		if (cid == null)
			return -1;

		if (ccmap == null)
			return -3;

		if (ccmap.isEmpty())
			return 0;

		Integer num = ccmap.get(cid);

		return (num != null) ? num.intValue() : 0;
	}

	public List<String> getInUseCameraList() {
		List<String> rv = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : getCamerasInUse().entrySet())
			if (entry.getValue() != null && entry.getValue() > 0)
				rv.add(entry.getKey());

		return rv;
	}

	public List<String> getCameraList() {
		ArrayList<String> cams = new ArrayList<>();
		Iterator<Camera> it = session.getSonarState().getCamCache()
			.getCameras().iterator();
		while (it.hasNext())
			cams.add(it.next().getName());
		Collections.sort(cams);

		return cams;
	}

	// which camera is did connected to?
	public String getCameraByDecoder(String did) {
		return (did == null) ? null : decstat_map.get(did);
	}

	// which decoder is connected to cid?
	public String getDecoderByCamera(String cid) {
		if (cid == null)
			return null;
		for (String did : decstat_map.keySet()) {
			if (cid.equals(decstat_map.get(did)))
				return did;
		}

		return null;
	}

	// returns false on any detected error, else true.
	public boolean connect(String did, String cid) {
		if ((cid == null) || (did == null))
			return false;
		if (base_url == null)
			return false;
		String url = base_url + "?cmd=conn&did="+ did +"&cid=" + cid;
		String resp = querySwitchServer(url);
		if (resp == null)
			return false;
		if ("OK".equals(resp.trim()))
			return true;

		return false;
	}


	// returns false on any detected error or if camera is not known to be
	// connectd to any decoders, else true.
	public boolean disconnectCam(String cid) {
		if (cid == null)
			return false;
		if (base_url == null)
			return false;
		String url = base_url + "?cmd=disccam&cid=" + cid;
		String resp = querySwitchServer(url);
		if (resp == null)
			return false;
		if ("OK".equals(resp.trim()))
			return true;

		return false;
	}


	// returns false on any detected error, else true.
	public boolean disconnectDec(String did) {
		if (did == null)
			return false;
		if (base_url == null)
			return false;
		String url = base_url + "?cmd=discdec&did=" + did;
		String resp = querySwitchServer(url);
		if (resp == null)
			return false;
		if ("OK".equals(resp.trim()))
			return true;

		return false;
	}


}

