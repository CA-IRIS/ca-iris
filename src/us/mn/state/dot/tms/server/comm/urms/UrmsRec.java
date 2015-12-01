/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2015  AHMCT, University of California
 * Copyright (C) 2015       Southwest Research Institute
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
package us.mn.state.dot.tms.server.comm.urms;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.utils.ByteBlob;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * URMS protocol record.
 *
 * @author Michael Darter
 * @author Travis Swanston
 * @author Jacob Barde
 */
public class UrmsRec {

	/** Detector status, per NTCIP */
	public enum DetStatus {
		DISABLED(1), WORKING(2), OTHER_ERROR(3),
		ERRATIC_COUNT(4), MAX_PRESENCE(5), NO_ACTIVITY(6),
		ERROR_AT_SENSOR(7), FLOW_BASED_NO_ACTIVITY(8);

		int code;

		DetStatus(int c) {
			code = c;
		}

		static DetStatus fromCode(int c) {
			for (DetStatus s : DetStatus.values())
				if (s.code == c)
					return s;
			return null;
		}
	}

	/** Lane status, per NTCIP */
	public enum LaneStatus {
		WORKING(1), DISABLED(2), PARTIAL_FAILURE(3),
		TOTAL_FAILURE(4);

		int code;

		LaneStatus(int c) {
			code = c;
		}

		static LaneStatus fromCode(int c) {
			for (LaneStatus s : LaneStatus.values())
				if (s.code == c)
					return s;
			return null;
		}
	}

	/** field group in the packet */
	private enum FieldMarker {

		MAINLINE(25), // header info is 25 bytes
		//MAINLINE_DIR(105), // above + 8 ML lanes * 10 bytes
		OPP_MAINLINE(106), // above + 1 (ML direction)
		//OPP_MAINLINE_DIR(186), // above + 8 OML lanes * 10 bytes
		//ADD_DETECTORS(187), // above + 1 (OML direction)
		//METERED_INFO(251), // above + 16 detectors * 4 bytes
		//IS_METERING(283), // above + 4 groups * 8 bytes
		//TR_INFO(284), // above + 1 bytes (is metering)
		//METERED_LANES(291), // above + 7 bytes (TR info)
		//QUEUE(335), // above + 4 MET lanes * 11 bytes
		CRC(415);

		int offset;

		FieldMarker(int o) {
			offset = o;
		}

		static FieldMarker fromCode(int o) {
			for (FieldMarker p : FieldMarker.values())
				if (p.offset == o)
					return p;
			return null;
		}
	}

	/**
	 * Maximum number of lanes in sample. 8 mainline, 8 opposite mainlines.
	 * the 16 additional detectors, 4 demand detectors, 4 passage detectors
	 * and 16 queue detectors are to be dealt with at a later time
	 */
	static private final int MAX_NUM_LANES = 16;

	/** URMS record length in bytes */
	static private final int URMS_REC_LEN = 417;

	/** Sample period (seconds) */
	static private final int SAMPLE_PERIOD_SEC = 30;

	/** Starting pin for controller I/O */
	static private final int STARTPIN = 1;

	/** Raw record */
	private final ByteBlob urms_blob;

	/** Station id, unsigned 32 bit value */
	private long station_id = 0;

	/** True if record is valid */
	public boolean valid_rec = false;

	/** Origin internet address, may be null. */
	private final InetAddress origin_addr;

	/** Number of lanes */
	private int num_lanes = -1;

	/** Number of mainlines */
	private int num_mainline = 0;

	/** Number of opposite mainlines */
	private int num_opp_mainline = 0;

	/** Sample data for all lanes */
	private LaneSample[] lane_samples = new LaneSample[0];

	/** Creation time */
	private final long create_time;

	/** Record time, read from urms record */
	private long rec_time = Constants.MISSING_DATA;

	/**
	 * Constructor.
	 *
	 * @param bb A ByteBlob containing 1 record, never null.
	 * @param oa Datagram origin address, may be null.
	 */
	public UrmsRec(ByteBlob bb, InetAddress oa) {
		origin_addr = oa;
		create_time = TimeSteward.currentTimeMillis();
		urms_blob = bb.clone();
		valid_rec = parse();
		UrmsPoller.log("created rec=" + this);
	}

	/**
	 * Parse the raw record into fields.
	 *
	 * @return True if the record is valid, else false.
	 */
	private boolean parse() {
		UrmsPoller.log("parsing");
		if (urms_blob.isEmpty() || origin_addr == null)
			return false;
		if (!validLength() || !validHeader() || !validCrc())
			return false;
		logPacket();
		station_id = getStationId();
		UrmsPoller.log("station id=" + station_id);
		num_lanes = getNumLanes();
		if (num_lanes < 1)
			return false;
		lane_samples = parseLanes();
		rec_time = getTime();
		return true;
	}

	/** Valid length? */
	private boolean validLength() {
		boolean v = urms_blob.size() == URMS_REC_LEN;
		UrmsPoller.log("valid rec length=" + v);
		return v;
	}

	/** Valid header? */
	private boolean validHeader() {
		boolean v = urms_blob.getByte(0) == 0x55
			&& urms_blob.getByte(1) == 0x52
			&& urms_blob.getByte(2) == 0x40
			&& urms_blob.getByte(3) == 0x53
			&& urms_blob.getByte(4) == 0x32;
		UrmsPoller.log("valid header=" + v);
		return v;
	}

	/** Valid CRC? */
	private boolean validCrc() {
		int ncsb = urms_blob.getTwoByteValue(5);
		boolean v = ncsb == 410;
		UrmsPoller.log("valid number of expected bytes to CRC=" + v
			+ ", ncsb=" + ncsb);
		if (!v)
			return false;
		int ecrc = urms_blob.getTwoByteValue(FieldMarker.CRC.offset);
		int ccrc = urms_blob.crc16(0, FieldMarker.CRC.offset);
		v = ecrc == ccrc;
		UrmsPoller.log("valid CRC16=" + v);
		if (!v) {
			UrmsPoller.log("expected crc16=" + ecrc
				+ ", calc crc16=" + ccrc);
			UrmsPoller.log("ignoring CRC mismatch");
			return true;
		}
		return v;
	}

	/** Log various values in packet */
	private void logPacket() {
		UrmsPoller.log("station id=" + getStationId());
		UrmsPoller.log("day=" + urms_blob.getByte(14));
		UrmsPoller.log("month=" + urms_blob.getByte(15));
		UrmsPoller.log("year=" + urms_blob.getTwoByteValue(16));
		UrmsPoller.log("hour=" + urms_blob.getByte(18));
		UrmsPoller.log("min=" + urms_blob.getByte(19));
		UrmsPoller.log("sec=" + urms_blob.getByte(20));
		UrmsPoller.log("total lanes=" + num_lanes);
		UrmsPoller.log("num metered lanes=" + urms_blob.getByte(21));
		UrmsPoller.log("num mainline lanes=" + num_mainline);
		UrmsPoller.log("num opp main lanes=" + num_opp_mainline);
		for (int li = 0; li < num_mainline; ++li)
			logLane(li, "mainlines L" + new Integer(li + 1),
				FieldMarker.MAINLINE);
		for (int li = 0; li < num_opp_mainline; ++li)
			logLane(li, "opp mainlines L" + new Integer(li + 1),
				FieldMarker.OPP_MAINLINE);


	}

	/**
	 * Log various values in lane.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 */
	private void logLane(int li, String label, FieldMarker f) {
		UrmsPoller.log(label);
		UrmsPoller.log("speed=" + urms_blob
			.getByte(f.offset + li * 10 + 0));
		UrmsPoller.log("leading vol=" + urms_blob
			.getByte(f.offset + li * 10 + 1));
		UrmsPoller.log("leading occ=" + urms_blob
			.getTwoByteValue(f.offset + li * 10 + 2));

		DetStatus ls = getLeadingStatus(li, f);
		UrmsPoller.log("leading status=" + ls + "(" + ls.code + ")");

		UrmsPoller.log("trailing vol=" + urms_blob
			.getByte(f.offset + li * 10 + 5));
		UrmsPoller.log("trailing occ=" + urms_blob
			.getTwoByteValue(f.offset + li * 10 + 6));

		DetStatus ts = getTrailingStatus(li, f);
		UrmsPoller.log("trailing status=" + ts + "(" + ts.code + ")");

		LaneStatus lns = getLaneStatus(li, f);
		UrmsPoller.log("lane status=" + lns + "(" + lns.code + ")");
	}

	/**
	 * Get the mainline leading detector status.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 * @return the leading status or null on error.
	 */
	private DetStatus getLeadingStatus(int li, FieldMarker f) {
		return DetStatus.fromCode(urms_blob
			.getByte(f.offset + li * 10 + 4));
	}

	/**
	 * Get the mainline trailing detector status.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 * @return the trailing status or null on error.
	 */
	private DetStatus getTrailingStatus(int li, FieldMarker f) {
		return DetStatus.fromCode(urms_blob
			.getByte(f.offset + li * 10 + 8));
	}

	/**
	 * Get the mainline lane status.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 * @return the lane status or null on error.
	 */
	private LaneStatus getLaneStatus(int li, FieldMarker f) {
		return LaneStatus.fromCode(urms_blob
			.getByte(f.offset + li * 10 + 9));
	}

	/** Get station id as a 32 bit unsigned value */
	private long getStationId() {
		return urms_blob.getFourByteValue(7);
	}

	/**
	 * Parse number of lanes.
	 *
	 * @return Number of lanes which is > 1, otherwise -1 on error.
	 */
	private int getNumLanes() {
		num_mainline = urms_blob.getInt(22);
		num_opp_mainline = urms_blob.getInt(23);

		int nl = num_mainline + num_opp_mainline;
		UrmsPoller.log("# lanes=" + nl);
		if (nl < 0 || nl > MAX_NUM_LANES) {
			UrmsPoller.log("bogus # lanes=" + nl);
			return -1;
		}
		return nl;
	}

	/** Parse lane data */
	private LaneSample[] parseLanes() {
		LaneSample[] ld = new LaneSample[num_lanes];
		for (int li = 0; li < num_mainline; ++li) {
			ld[li] = new LaneSample(li + 1);
			ld[li].volume = getVolume(li, FieldMarker.MAINLINE);
			ld[li].speed = getSpeed(li, FieldMarker.MAINLINE);
			ld[li].occ = getOccupancy(li, FieldMarker.MAINLINE);
		}
		for (int li = 0; li < num_opp_mainline; li++) {
			int i = li + num_mainline;
			ld[i] = new LaneSample(i + 1);
			ld[i].volume = getVolume(i, FieldMarker.OPP_MAINLINE);
			ld[i].speed = getSpeed(i, FieldMarker.OPP_MAINLINE);
			ld[i].occ = getOccupancy(i, FieldMarker.OPP_MAINLINE);
		}
		return ld;
	}

	/**
	 * Get mainline speed for the specified lane.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 * @return Speed in MPH.
	 */
	private int getSpeed(int li, FieldMarker f) {
		return urms_blob.getInt(f.offset + li * 10);
	}

	/**
	 * Get mainline volume for the specified lane.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 * @return Volume from the leading sensor.
	 */
	private int getVolume(int li, FieldMarker f) {
		return urms_blob.getInt(f.offset + li * 10 + 1);
	}

	/**
	 * Get mainline occupancy for the specified lane.
	 *
	 * @param li Lane index, zero based.
	 * @param f Field marker
	 * @return Occupancy from the leading sensor, 0 - 100
	 */
	private float getOccupancy(int li, FieldMarker f) {
		int tenths = urms_blob.getTwoByteValue(f.offset + li * 10 + 2);
		return tenths / (float) 10;
	}

	/**
	 * Parse the date field into a long.
	 *
	 * @return The date as a long.
	 */
	private long getTime() {
		int da = urms_blob.getInt(14);
		int mo = urms_blob.getInt(15);
		int ye = urms_blob.getTwoByteValue(16);
		int ho = urms_blob.getInt(18);
		int mi = urms_blob.getInt(19);
		int se = urms_blob.getInt(20);
		Calendar dt = new GregorianCalendar();
		dt.set(ye, mo, da, ho, mi, se);
		return dt.getTime().getTime();
	}

	/** Return time record was created */
	private long getAge() {
		return create_time;
	}

	/** Return time in MS since the record was created */
	private long getAgeDelta() {
		return STime.calcTimeDeltaMS(create_time);
	}

	/** Get the IP associated with the record. May be null. */
	private InetAddress getOriginAddress() {
		return origin_addr;
	}

	/** Get the origin IP address, which is never null. */
	private String getOriginAddressString() {
		InetAddress a = getOriginAddress();
		return (a == null ? "" : a.getHostAddress());
	}

	/** Get record name, which is never null. */
	public String getName() {
		return getOriginAddressString();
	}

	/** To string */
	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append("(UrmsRec: age ms=").append(getAgeDelta());
		r.append(", valid=").append(valid_rec);
		r.append(", sid=").append(station_id);
		r.append(", nlanes=").append(num_lanes);
		for (LaneSample ls : lane_samples)
			r.append(ls);
		r.append(", create_time=").append(new Date(create_time));
		if (rec_time < 0)
			r.append(", rec_time=").append(rec_time);
		else {
			r.append(", rec_time=").append(new Date(rec_time));
			r.append(", time delta ms=").
				append(create_time - rec_time);
		}
		r.append(", oaddr=").append(origin_addr);
		r.append(")");
		return r.toString();
	}

	/** Get the volume array */
	private int[] getVolumes() {
		int[] vol = new int[num_lanes];
		for (LaneSample ls : lane_samples)
			vol[ls.lanenum - 1] = ls.volume;
		return vol;
	}

	/** Get the scan count as an array */
	private int[] getScans() {
		int[] scans = new int[num_lanes];
		for (LaneSample ls : lane_samples)
			scans[ls.lanenum - 1] = ls.getScans();
		return scans;
	}

	/** Get the speeds as an array */
	private int[] getSpeeds() {
		int[] speed = new int[num_lanes];
		for (LaneSample ls : lane_samples)
			speed[ls.lanenum - 1] = ls.speed;
		return speed;
	}

	/** Is the record valid? */
	public boolean getValid() {
		return valid_rec;
	}

	/**
	 * Store the record to the associated controller. If an rnode is not
	 * defined for the controller, the number of lanes will be zero, and a
	 * data sample will be stored with zero lanes.
	 *
	 * @param acs List of active controllers.
	 */
	public void store(LinkedList<Controller> acs) {
		if (getValid())
			UrmsPoller.log("storing valid rec=" + getName());
		else {
			UrmsPoller.log("ignoring invalid rec=" + getName());
			return;
		}
		ControllerImpl ci = findActiveController(acs);
		if (ci == null)
			return;
		LinkedList<String> sids = ControllerHelper.getStationIds(ci);
		if (sids.size() <= 0) {
			String m = "No rnode for rec=" + getName()
				+ ", c=" + ci;
			UrmsPoller.log(m);
		}
		UrmsPoller.log("storing rec=" + getName()
			+ ", stationids=" + sids
			+ ", create_time=" + new Date(create_time)
			+ ", vol=" + SString.toString(getVolumes())
			+ ", speed=" + SString.toString(getSpeeds())
			+ ", scans=" + SString.toString(getScans())
			+ ", to c=" + ci);
		checkStorageFreq(ci);
		ci.storeVolume(create_time, SAMPLE_PERIOD_SEC, STARTPIN,
			getVolumes());
		ci.storeOccupancy(create_time, SAMPLE_PERIOD_SEC, STARTPIN,
			getScans(), LaneSample.MAX_SCANS);
		ci.storeSpeed(create_time, SAMPLE_PERIOD_SEC, STARTPIN,
			getSpeeds());
		ci.completeOperation(ci.toString(), true);
		ci.setErrorStatus("");
	}

	/**
	 * Detect if a store operation is happening too frequently, which may
	 * indicate multiple datagrams are being stored to the same controller.
	 */
	private void checkStorageFreq(ControllerImpl ci) {
		long lt = ci.getLastStoreTime();
		long delta = TimeSteward.currentTimeMillis() - lt;
		final long nostoretime = UrmsProperty.SAMPLE_PERIOD_MS -
			UrmsProperty.getReadMarginMs();
		if (delta < nostoretime) {
			UrmsPoller.log("For rec=" + getName()
				+ ", delta=" + delta
				+ " since last store on "
				+ "c=" + ci
				+ " may indicate duplicate "
				+ "datagrams being stored on same controller.");
		}
	}

	/**
	 * Find the associated controller. The sensor id in the URMS record is
	 * used to find the matching controller with the same drop id.
	 *
	 * @return Matching controller, else null if not found.
	 */
	private ControllerImpl findActiveController(
		LinkedList<Controller> acs) {
		UrmsPoller.log("finding ctrl");
		for (Controller c : acs)
			if (station_id == c.getDrop()) {
				UrmsPoller.log("found c=" + c
					+ " w/ matching station id="
					+ station_id);
				return (ControllerImpl) c;
			}
		UrmsPoller.log("no ctrl defined for ss=" + this
			+ ", station id=" + station_id);
		return null;
	}

	/**
	 * Does the specified string contain the specified IP address? A string
	 * "12" does not match "123". A string comparison is made, so "012" will
	 * not match "12".
	 */
	private boolean containsIp(String str, String ip) {
		int n = SString.count(str, ip);
		if (n <= 0)
			return false;
		else if (n > 1) {
			UrmsPoller.log("The string ip=" + ip + " occurs=" + n
				+ " times in notes field. "
				+ "This is probably a config error in rec="
				+ this);
		}
		for (int j = 0; j < str.length(); ++j) {
			int i = str.indexOf(ip, j);
			if (i < 0)
				return false;
			int fci = i + ip.length();
			if (fci >= str.length())
				return true;
			if (!Character.isDigit(str.charAt(fci)))
				return true;
			j = fci;
		}
		return false;
	}

	/** Store a missing sample to the controller. */
	static public void storeMissingSample(ControllerImpl ci, long time) {
		int[] x = new int[MAX_NUM_LANES];
		for (int i = 0; i < MAX_NUM_LANES; ++i)
			x[i] = Constants.MISSING_DATA;
		String xs = SString.toString(x);
		UrmsPoller.log("storing missing sample: time=" + new Date(time)
			+ ", nlanes=" + MAX_NUM_LANES
			+ ", vo=" + xs
			+ ", sp=" + xs
			+ ", sc=" + xs);
		ci.storeVolume(time, SAMPLE_PERIOD_SEC, STARTPIN, x);
		ci.storeOccupancy(time, SAMPLE_PERIOD_SEC, STARTPIN, x,
			LaneSample.MAX_SCANS);
		ci.storeSpeed(time, SAMPLE_PERIOD_SEC, STARTPIN, x);
	}

}
