/*
 * IRIS -- Intelligent Roadway Information System
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
package us.mn.state.dot.tms.server.comm.wizard;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.NumberFormatException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.utils.ByteBlob;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * Wizard protocol record.
 * A record consists of station data on a single line, with fields delimited
 * by commas.  A line has this format:
 *   ignore, num_lanes, lane_records, date_time
 * where:
 *   ignore = an integer that is ignored,
 *   lane_records = data repeated per lane, and each lane contains:
 *     Single loop: volume, speed_mph, occ_x10, vol_c1, vol_c2
 *     Double loop: volume, speed_mph, occ_x10, vol_c1, vol_c2, vol_c3, vol_c4
 *     date_time = local time as yyyy-mm-dd hh:mm:ss
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WizardRec {

	/** Sample period (seconds) */
	static private final int SAMPLE_PERIOD_SEC = 30;

	/** Starting pin for controller I/O */
	static private final int STARTPIN = 1;

	/** Maximum number of lanes in sample, arbitrarily set to */
	static private final int MAX_NUM_LANES = 64;

	/** Record field type */
	private enum Field {
		IGNORE(0), NUMLANES(1), VOLUME(0), SPEED(1), OCC(2), C1(3),
			C2(4), C3(5), C4(6), DATE(0);

		/** field offset */
		private final int field_offset;

		/** Constructor */
		Field(int os) {
			field_offset = os;
		}

		/**
		 *Get field index
		 * @param fpl Fields per lane
		 * @param li Lane index
		 * @param nl Number of lanes
		 */
		public int idx(int fpl, int li, int nl) {
			if(this == IGNORE || this == NUMLANES)
				return field_offset;
			else if(this == DATE)
				return 2 + fpl * nl;
			else
				return 2 + fpl * li + field_offset;
		}

	}

	/** Record format type. */
	private enum RecFormat {
		UNKNOWN(0),
		SINGLE_LOOP(5),
		DOUBLE_LOOP(7);

		/** Fields per lane */
		private final int fields_per_lane;

		/** Constructor */
		private RecFormat(int fpl) {
			fields_per_lane = fpl;
		}

		/** Get the number of fields per lane */
		public int fieldsPerLane() {
			return fields_per_lane;
		}

		/**
		 * Return the record format, which is a function of the
		 * number of fields in a record. The record may have between
		 * one and MAX_NUM_LANES lanes of data.
		 * @param nfr Number of fields in a record.
		 * @param enl Expected number of lanes
		 * @return The record format, which may be unknown.
		 */
		static RecFormat getFormat(int nfr, int enl) {
			if(nfr == SINGLE_LOOP.numFields(enl))
				return SINGLE_LOOP;
			else if(nfr == DOUBLE_LOOP.numFields(enl))
				return DOUBLE_LOOP;
			return UNKNOWN;
		}

		/** Get the number of fields for the specified # of lanes. */
		private int numFields(int nl) {
			return 2 + nl * fieldsPerLane() + 1;
		}
	}

	/** Record format */
	private RecFormat rec_format = RecFormat.UNKNOWN;

	/** Raw record data */
	private final String raw_rec;

	/** True if record is valid */
	public boolean valid_rec = false;

	/** Origin internet address, may be null. */
	private final InetAddress origin_addr;

	/** Number of lanes */
	private int num_lanes;

	/** Sample data for all lanes */
	private LaneSample[] lane_samples = new LaneSample[0];

	/** Creation time */
	private final long create_time;

	/** Record time, read from wizard record */
	private long rec_time = Constants.MISSING_DATA;

	/**
	 * Constructor.
	 * @param bb A ByteBlob containing 1 record, never null.
	 * @param oa Datagram origin address, may be null.
	 */
	public WizardRec(ByteBlob bb, InetAddress oa) {
		origin_addr = oa;
		create_time = TimeSteward.currentTimeMillis();
		raw_rec = bb.toStringChars();
		valid_rec = parse();
		WizardPoller.log("Created rec=" + this);
	}

	/**
	 * Parse the raw record into fields. Stations can send data in
	 * two formats:
	 * 2,2,0,0,0,0,0,0,0,4,67,27,4,0,0,0,2010-11-03 19:56:00
	 * 2,2,5,55,41,5,0,6,52,79,5,1,2010-12-08 12:19:00
	 * @return True if the record is valid else false.
	 */
	private boolean parse() {
		// FIXME: use SCsv methods
		WizardPoller.log("Will parse rec=" + raw_rec);
		if(raw_rec.length() <= 0 || origin_addr == null)
			return false;
		String[] fs = preprocess(raw_rec.split(","));
		int ignore = getFieldInt(fs, 0);
		num_lanes = parseNumLanes(fs);
		if(num_lanes < 1)
			return false;
		rec_format = parseRecFormat(fs, num_lanes);
		if(rec_format == RecFormat.UNKNOWN)
			return false;
		lane_samples = parseLanes(fs, num_lanes);
		if(lane_samples.length < num_lanes) /* consider != */
			return false;
		rec_time = parseDateField(fs);
		if(rec_time < 0)
			return false;
		return true;
	}

	/** Parse record type. */
	private RecFormat parseRecFormat(String[] fs, int nl) {
		RecFormat rf = RecFormat.getFormat(fs.length, nl);
		WizardPoller.log("rec format=" + rf);
		if(rf == RecFormat.UNKNOWN)
			WizardPoller.log("unknown rec format, rec=" +
				raw_rec);
		return rf;
	}

	/**
	 * Parse number of lanes.
	 * @return Number of lanes (which is > 1), else -1 on error.
	 */
	static private int parseNumLanes(String[] fs) {
		if(fs.length < 2) {
			WizardPoller.log("Unexpectedly short record");
			return -1;
		}
		int nl = getFieldInt(fs, 1);
		WizardPoller.log("#lanes=" + nl);
		if(nl < 0 || nl > MAX_NUM_LANES) {
			WizardPoller.log("bogus # lanes=" +
				nl + ", in rec=" + SString.toString(fs));
			return -1;
		}
		return nl;
	}

	/** Preprocess fields. */
	static private String[] preprocess(String[] fs) {
		if(fs == null)
			return new String[0];
		for(int i = 0; i < fs.length; ++i)
			fs[i] = fs[i].trim();
		return fs;
	}

	/** Parse lane data. */
	private LaneSample[] parseLanes(String fs[], int nlanes) {
		LaneSample[] ld = new LaneSample[nlanes];
		final int fpl = rec_format.fieldsPerLane();
		for(int li = 0; li < ld.length; ++li) {
			ld[li] = new LaneSample(li + 1);
			ld[li].volume = getFieldInt(fs,
				Field.VOLUME.idx(fpl, li, num_lanes));
			ld[li].speed = getFieldInt(fs,
				Field.SPEED.idx(fpl, li, num_lanes));
			float occf = getFieldFloat(fs,
				Field.OCC.idx(fpl, li, num_lanes));
			if (occf >= 0.0F)
				occf /= 10.0F;
			ld[li].occ = occf;
			ld[li].vol_c1 = getFieldInt(fs,
				Field.C1.idx(fpl, li, num_lanes));
			ld[li].vol_c2 = getFieldInt(fs,
				Field.C2.idx(fpl, li, num_lanes));
			if(rec_format == RecFormat.DOUBLE_LOOP) {
				ld[li].vol_c3 = getFieldInt(fs,
					Field.C3.idx(fpl, li, num_lanes));
				ld[li].vol_c4 = getFieldInt(fs,
					Field.C4.idx(fpl, li, num_lanes));
			}
		}
		return ld;
	}

	/** Get field as String. */
	static private String getFieldString(String[] fs, int i) {
		if(i >= 0 && i < fs.length)
			return fs[i];
		WizardPoller.log("bogus index=" + i + ", fs=" +
			SString.toString(fs));
		return "";
	}

	/** Get field as int. */
	static private int getFieldInt(String[] fs, int i) {
		String f = getFieldString(fs, i);
		if (f == null)
			return Constants.MISSING_DATA;
		try {
			return Integer.parseInt(f);
		}
		catch (NumberFormatException e) {
			return Constants.MISSING_DATA;
		}
	}

	/** Get field as float. */
	static private float getFieldFloat(String[] fs, int i) {
		String f = getFieldString(fs, i);
		if (f == null)
			return Constants.MISSING_DATA;
		try {
			return Float.parseFloat(f);
		}
		catch (NumberFormatException e) {
			return Constants.MISSING_DATA;
		}
	}

	/**
	 * Parse the date field into a long.
	 * @return The date as a long, or -1 on error.
	 */
	private long parseDateField(String[] fs) {
		// FIXME: use SCSV methods
		final int fpl = rec_format.fieldsPerLane();
		final int idx = Field.DATE.idx(fpl, 0, num_lanes);
		String d = getFieldString(fs, idx);
		boolean valid = SString.count(d, ':') == 2 &&
			SString.count(d, '-') == 2 &&
			SString.count(d, ' ') == 1;
		if(!valid) {
			WizardPoller.log("Bad date in rec=" + this);
			return -1;
		}
		long l = STime.parseDate(d);
		WizardPoller.log("Parsed " + d + " to " + new Date(l));
		return l;
	}

	/** Return time record was created. */
	private long getAge() {
		return create_time;
	}

	/** Return time in MS since the record was created. */
	private long getAgeDelta() {
		return STime.calcTimeDeltaMS(create_time);
	}

	/**
	 * Get the IP associated with the record.
	 * @return The IP associated with the record (may be null).
	 */
	private InetAddress getOriginAddress() {
		return origin_addr;
	}

	/**
	 * Get the origin IP address.
	 * @return The origin IP address (never null).
	 */
	private String getOriginAddressString() {
		InetAddress a = getOriginAddress();
		return (a == null ? "" : a.getHostAddress());
	}

	/**
	 * Get the record name.
	 * @return The record name (never null).
	 */
	public String getName() {
		return getOriginAddressString();
	}

	/** Return a string representation. */
	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append("(WizardRec: age ms=").append(getAgeDelta());
		r.append(", valid=").append(valid_rec);
		r.append(", format=").append(rec_format);
		r.append(", nlanes=").append(num_lanes);
		for(int l = 0; l < num_lanes; ++l)
			r.append(lane_samples[l]);
		r.append(", create_time=").append(new Date(create_time));
		if(rec_time < 0)
			r.append(", rec_time=").append(rec_time);
		else {
			r.append(", rec_time=").append(new Date(rec_time));
			r.append(", time delta ms=").
				append(create_time - rec_time);
		}
		r.append(", oaddr=").append(origin_addr);
		r.append(", raw=").append(raw_rec);
		r.append(")");
		return r.toString();
	}

	/** Get the volume array. */
	private int[] getVolumes() {
		int[] vol = new int[num_lanes];
		for(LaneSample ls: lane_samples)
			vol[ls.lanenum - 1] = ls.volume;
		return vol;
	}

	/** Get the scan count as an array. */
	private int[] getScans() {
		int[] scans = new int[num_lanes];
		for(LaneSample ls: lane_samples)
			scans[ls.lanenum - 1] = ls.getScans();
		return scans;
	}

	/** Get the speeds as an array. */
	private int[] getSpeeds() {
		int[] speed = new int[num_lanes];
		for(LaneSample ls: lane_samples)
			speed[ls.lanenum - 1] = ls.speed;
		return speed;
	}

	/** Is the record valid? */
	public boolean getValid() {
		return valid_rec;
	}

	/**
	 * Store the record to the associated controller. If an rnode is
	 * not defined for the controller, the number of lanes will be
	 * zero, and a data sample will be stored with zero lanes.
	 * @param acs List of active controllers.
	 */
	public void store(LinkedList<Controller> acs) {
		if (!getValid()) {
			WizardPoller.log("ignoring invalid rec=" + getName());
			return;
		}
		ControllerImpl ci = findActiveController(acs);
		if(ci == null)
			return;
		WizardPoller.log("storing valid rec=" + getName());
		LinkedList<String> sids = ControllerHelper.getStationIds(ci);
		if(sids.size() <= 0) {
			String m = "No rnode for rec=" +
				getName() + ", c=" + ci;
			WizardPoller.log(m);
		}
		WizardPoller.log("storing rec=" + getName() +
			", stationids=" + sids +
			", create_time=" + new Date(create_time) +
			", vol=" + SString.toString(getVolumes()) +
			", speed=" + SString.toString(getSpeeds()) +
			", scans=" + SString.toString(getScans()) +
			", to c=" + ci);
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
	 * Detect if a store operation is happening too frequently, which
	 * may indicate multiple datagrams are being stored to the same
	 * controller.
	 */
	private void checkStorageFreq(ControllerImpl ci) {
		long lt = ci.getLastStoreTime();
		long delta = TimeSteward.currentTimeMillis() - lt;
		final long nostoretime = WizardProperty.SAMPLE_PERIOD_MS -
			WizardProperty.getReadMarginMs();
		if(delta < nostoretime) {
			WizardPoller.log("For rec=" + getName() +
				", delta=" + delta + " since last store on " +
				"c=" + ci + " may indicate duplicate " +
				"datagrams being stored on same controller.");
		}
	}

	/**
	 *Find the associated active controller. The record origin
	 * address is used to find the matching controller, which will
	 * contain the IP address in the notes field.
	 * @param acs List of active controllers.
	 * @return Matching active controller else null if not found
	 * or not active.
	 */
	private ControllerImpl findActiveController(LinkedList<Controller> acs)
	{
		WizardPoller.log("finding active controller");
		String h = getOriginAddressString();
		WizardPoller.log("Searching for origin=" + h);
		for(Controller c : acs) {
			String notes = c.getNotes();
			if(containsIp(notes, h)) {
				WizardPoller.log("Found c=" + c +
					", note contains '" + h + "'");
				return (ControllerImpl)c;
			}
		}
		WizardPoller.log("No controller defined for rec=" +
			this + ", that contains a notes field with h=" + h);
		return null;
	}

	/**
	 * Does the specified string contain the specified IP address?
	 * A string "12" does not match "123". A string comparison is made,
	 * so "012" will not match "12".
	 */
	private boolean containsIp(String str, String ip) {
		int n = SString.count(str, ip);
		if(n <= 0)
			return false;
		else if(n > 1) {
			WizardPoller.log("The string ip=" + ip +
				" occurs=" + n + " times in notes field. " +
				"This is probably a config error in rec=" +
				this);
		}
		for(int j = 0; j < str.length(); ++j) {
			int i = str.indexOf(ip, j);
			if(i < 0)
				return false;
			int fci = i + ip.length();
			if(fci >= str.length())
				return true;
			if(!Character.isDigit(str.charAt(fci)))
				return true;
			j = fci;
		}
		return false;
	}

	/** Store a missing sample to the controller. */
	static public void storeMissingSample(ControllerImpl ci, long time) {
		int[] x = new int[MAX_NUM_LANES];
		for(int i = 0; i < MAX_NUM_LANES; ++i)
			x[i] = Constants.MISSING_DATA;
		String xs = SString.toString(x);
		WizardPoller.log("storing missing sample: " +
			"time=" + new Date(time) + ", nlanes=" + MAX_NUM_LANES +
			", vo=" + xs + ", sp=" + xs + ", sc=" + xs);
		ci.storeVolume(time, SAMPLE_PERIOD_SEC, STARTPIN, x);
		ci.storeOccupancy(time, SAMPLE_PERIOD_SEC, STARTPIN, x,
			LaneSample.MAX_SCANS);
		ci.storeSpeed(time, SAMPLE_PERIOD_SEC, STARTPIN, x);
	}

}

