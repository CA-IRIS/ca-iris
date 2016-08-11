/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.rtms;

import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * Sample data for a single station, which consists of a collection
 * of lane samples.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class StationSample {

	/** Sample period (seconds) */
	static private final int SAMPLE_PERIOD_SEC = 30;

	/** Starting pin for controller I/O */
	static private final int STARTPIN = 1;

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** RTMS sensor id */
	private int rtms_sensor_id = -1;

	/** Sample data for each lane */
	private final LaneSample[] lane_samples;

	/** Sample creation time */
	private long creation_time;

	/** Sequence number */
	private int seq_num = -1;

	/**
	 * Constructor
	 * @param sid Sensor ID.
	 * @param nl Number of lanes.
	 */
	public StationSample(CommLinkImpl cl, int sid) {
		comm_link = cl;
		creation_time = TimeSteward.currentTimeMillis();
		RtmsPoller.log("cl=" + comm_link + ": create " +
			"StationSample: sid=" + sid);
		rtms_sensor_id = sid;
		lane_samples = createLaneSamples(RtmsRec.NUM_LANES);
	}

	/** Create lane samples */
	static private LaneSample[] createLaneSamples(int nl) {
		LaneSample[] ls = new LaneSample[nl];
		for(int i = 0; i < nl; i++)
			ls[i] = new LaneSample(i + 1);
		return ls;
	}

	/** Get RTMS sensor id */
	public int getRtmsSensorId() {
		return rtms_sensor_id;
	}

	/** Get the station sample's name */
	public String getName() {
		return Integer.toString(getRtmsSensorId());
	}

	/** Return time in MS since station was created */
	public long getAgeDelta() {
		return STime.calcTimeDeltaMS(creation_time);
	}

	/** Set the station creation time in MS */
	public void setAge(long ms) {
		creation_time = ms;
	}

	/** Get the volume array */
	private int[] getVolumes() {
		int nl = lane_samples.length;
		int[] vol = new int[nl];
		for(int i = 0; i < nl; ++i) {
			LaneSample ls = lane_samples[i];
			vol[ls.det - 1] = ls.volume;
		}
		return vol;
	}

	/** Get the scan count as an array */
	private int[] getScans() {
		int nl = lane_samples.length;
		int[] scans = new int[nl];
		for(int i = 0; i < nl; ++i) {
			LaneSample ls = lane_samples[i];
			scans[ls.det - 1] = ls.getScans();
		}
		return scans;
	}

	/** Get the speeds as an array */
	private int[] getSpeeds() {
		int nl = lane_samples.length;
		int[] speed = new int[nl];
		for(int i = 0; i < nl; ++i) {
			LaneSample ls = lane_samples[i];
			speed[ls.det - 1] = ls.speed;
		}
		return speed;
	}

	/** Add speed samples.
	 * @param s Speed samples, ordered by lane. */
	public void addSpeed(int[] s) {
		RtmsPoller.log("cl=" + comm_link + ": adding sp[]=" +
			SString.toString(s));
		assert s.length == lane_samples.length;
		for(int i = 0; i < lane_samples.length; ++i)
			lane_samples[i].speed = s[i];
	}

	/** Add occupancy samples.
	 * @param oc Occupancy samples, ordered by lane. */
	public void addOccupancy(float[] oc) {
		RtmsPoller.log("cl=" + comm_link + ": adding oc[]=" +
			SString.toString(oc));
		assert oc.length == lane_samples.length;
		for(int i = 0; i < lane_samples.length; ++i)
			lane_samples[i].setOccupancy(oc[i]);
	}

	/** Add volume samples.
	 * @param v Volume samples, ordered by lane. */
	public void addVolume(int[] v) {
		RtmsPoller.log("cl=" + comm_link + ": adding vo[]=" +
			SString.toString(v));
		assert v.length == lane_samples.length;
		for(int i = 0; i < lane_samples.length; ++i)
			lane_samples[i].volume = v[i];
	}

	/** Add the sequence number */
	public void addSeqNum(int sn) {
		seq_num = sn;
	}

	/** Get the sequence number */
	public int getSeqNum() {
		return seq_num;
	}

	/** Get a string representation of the sample data */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(StationSample: sensorid=").
			append(getRtmsSensorId());
		sb.append(", NUM_LANES=").append(RtmsRec.NUM_LANES);
		sb.append(", age ms=").append(getAgeDelta());
		sb.append(", seqnum=").append(getSeqNum());
		sb.append(", vols=").append(SString.toString(getVolumes()));
		sb.append(", spds=").append(SString.toString(getSpeeds()));
		sb.append(", occ=").append(SString.toString(getScans()));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Store the station sample to the associated controller. If an
	 * rnode is not defined for the controller, the number of lanes
	 * will be zero, and a data sample will be stored with zero lanes.
	 * @param acs List of active controllers.
	 */
	public void store(LinkedList<Controller> acs) {
		RtmsPoller.log("cl=" + comm_link +
			": storing ss=" + this);
		ControllerImpl ci = findActiveController(acs);
		if(ci == null)
			return;
		LinkedList<String> sids = ControllerHelper.getStationIds(ci);
		if(sids.size() <= 0)
			RtmsPoller.log("cl=" + comm_link + ": no " +
				"rnode for ss=" + getName() + ", c=" + ci);
		RtmsPoller.log("cl=" + comm_link + ": storing " +
			", rec=" + getName() +
			", stationids=" + sids +
			", time=" + new Date(creation_time) +
			", vol=" + SString.toString(getVolumes()) +
			", speed=" + SString.toString(getSpeeds()) +
			", scans=" + SString.toString(getScans()) +
			", to c=" + ci);
		checkStorageFreq(ci);
		ci.storeVolume(creation_time, SAMPLE_PERIOD_SEC, STARTPIN,
			getVolumes());
		ci.storeOccupancy(creation_time, SAMPLE_PERIOD_SEC, STARTPIN,
			getScans(), LaneSample.MAX_SCANS);
		ci.storeSpeed(creation_time, SAMPLE_PERIOD_SEC, STARTPIN,
			getSpeeds());
		ci.completeOperation(ci.toString(), true);
		ci.setErrorStatus("");
	}

	/**
	 * Detect if a store operation is happening too frequently, which
	 * may indicate multiple datagrams are being stored to the same
	 * controller.
	 */
	private void checkStorageFreq(ControllerImpl ci) {
		long lt = ci.getLastStoreTime();
		long delta = TimeSteward.currentTimeMillis() - lt;
		final long nostoretime = RtmsProperty.SAMPLE_PERIOD_MS -
			RtmsProperty.getReadMarginMs();
		if(delta < nostoretime) {
			RtmsPoller.log("cl=" + comm_link +
				": for rec=" + getName() +
				", delta=" + delta + " since last store on " +
				"c=" + ci + " may indicate duplicate " +
				"datagrams being stored on same controller.");
		}
	}

	/**
	 * Find the associated controller. The sensor id in the
	 * station data is used to find the matching controller
	 * with the same drop id.
	 * @return Matching controller else null if not found.
	 */
	private ControllerImpl findActiveController(
		LinkedList<Controller> acs)
	{
		RtmsPoller.log("cl=" + comm_link + ": finding ctrl");
		for(Controller c : acs)
			if(getRtmsSensorId() == c.getDrop()) {
				RtmsPoller.log("cl=" + comm_link +
					": found c=" + c +
					" w/ matching sensor id=" +
					getRtmsSensorId());
				return (ControllerImpl)c;
			}
		RtmsPoller.log("cl=" + comm_link + ": no ctrl " +
			"defined for ss=" + this +
			", SensorId=" + getRtmsSensorId());
		return null;
	}

	/** Store a missing sample to the controller. */
	static public void storeMissingSample(CommLinkImpl cl,
		ControllerImpl ci, long time)
	{
		// FIXME: log as timeout error
		int[] x = new int[RtmsRec.NUM_LANES];
		for(int i = 0; i < RtmsRec.NUM_LANES; ++i)
			x[i] = Constants.MISSING_DATA;
		String xs = SString.toString(x);
		RtmsPoller.log("cl=" + cl + ": storing " +
			"missing sample: time=" + new Date(time) +
			", nlanes=" + RtmsRec.NUM_LANES +
			", vo=" + xs + ", sp=" + xs + ", sc=" + xs);
		ci.storeVolume(time, SAMPLE_PERIOD_SEC, STARTPIN, x);
		ci.storeOccupancy(time, SAMPLE_PERIOD_SEC, STARTPIN, x,
			LaneSample.MAX_SCANS);
		ci.storeSpeed(time, SAMPLE_PERIOD_SEC, STARTPIN, x);
	}

}
