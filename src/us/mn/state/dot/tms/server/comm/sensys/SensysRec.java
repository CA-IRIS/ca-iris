/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012  Iteris Inc.
 * Copyright (C) 2012-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.sensys;

import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ParsingException;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * Sensys protocol record. A line feed (\n, 0x0a) terminates a record.
 * The string read from the field controller follows this format:<p>
 * {@code timestamp,APEG,}
 * {@code
 *lane,occupancy,volume,speed_med,speed_avg,
 *speed_0<=X<19mph,speed_19<=X<25mph,speed_25<=X<31mph,
 *speed_31<=X<37mph,speed_37<=X<44mph,speed_44<=X<50mph,
 *speed_50<=X<56mph,speed_56<=X<62mph,speed_62<=X<71mph,
 *speed_71<=X<81mph,speed_81<=X<90mph,speed_90<=X<99mph,
 *speed_99+mph,length_0<=X<11ft,length_11<=X<22ft,
 *length_22<=X<30ft,length_30<=X<38ft,length38+ft,diag,}
 * {@code
 *lane,occupancy,volume,speed_med,speed_avg,
 *speed_0<=X<19mph,speed_19<=X<25mph,speed_25<=X<31mph,
 *speed_31<=X<37mph,speed_37<=X<44mph,speed_44<=X<50mph,
 *speed_50<=X<56mph,speed_56<=X<62mph,speed_62<=X<71mph,
 *speed_71<=X<81mph,speed_81<=X<90mph,speed_90<=X<99mph,
 *speed_99+mph,length_0<=X<11ft,length_11<=X<22ft,
 *length_22<=X<30ft,length_30<=X<38ft,length38+ft,diag,}
 * {@code
 *lane,occupancy,volume,speed_med,speed_avg,
 *speed_0<=X<19mph,speed_19<=X<25mph,speed_25<=X<31mph,
 *speed_31<=X<37mph,speed_37<=X<44mph,speed_44<=X<50mph,
 *speed_50<=X<56mph,speed_56<=X<62mph,speed_62<=X<71mph,
 *speed_71<=X<81mph,speed_81<=X<90mph,speed_90<=X<99mph,
 *speed_99+mph,length_0<=X<11ft,length_11<=X<22ft,
 *length_22<=X<30ft,length_30<=X<38ft,length38+ft,diag}
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class SensysRec {

	/** Sample period (seconds) */
	static private final int SAMPLE_PERIOD_SEC = 30;

	/** Starting pin for controller I/O */
	static private final int STARTPIN = 1;

	/** CSV column indexes */
	private static final int IDX_TIME = 0;
	private static final int IDX_SID = 1;
	private static final int IDX_LANE1 = 2;
	private static final int IDX_LANE2 = 26;
	private static final int IDX_LANE3 = 50;
	private static final int IDX_OFFSET_LANE = 0;
	private static final int IDX_OFFSET_OCC = 1;
	private static final int IDX_OFFSET_VOL = 2;
	private static final int IDX_OFFSET_SPD = 4;

	/** Maximum number of lanes in sample */
	static private final int MAX_NUM_LANES = 8;

	/** Expected number of fields in record */
	static private final int NUM_FIELDS_REC = 74;

	/** Number of fields per lane */
	static private final int NUM_FIELDS_LANE = 24;

	/** Number of fields in header, i.e. not part of a lane */
	static private final int NUM_FIELDS_HEADER = 2;

	/** Raw record data */
	private String raw_rec = "";

	/** True if record is valid */
	public boolean valid_rec = false;

	/** Sensor ID */
	private String sensor_id = "";

	/** Number of lanes */
	private int num_lanes = 0;

	/** Sample data for all lanes.  The protocol returns 3 lanes of data
	 * and must be configured for imperial. */
	private LaneSamples lane_samples = new LaneSamples(false, 3);

	/** Creation time */
	private long create_time = 0;

	/** Record time, read from record */
	private long rec_time = Constants.MISSING_DATA;

	/** Controller name */
	private String controller_name = "";

	/** Constructor. */
	public SensysRec() {
	}

	/** Return a representative string. */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("(SensysRec: valid_rec=").append(valid_rec);
		s.append(" sid=").append(sensor_id);
		s.append(", create_time=").append(new Date(create_time));
		s.append(", rec_time=").append(new Date(rec_time));
		s.append(", lane_samples=").append(lane_samples);
		s.append(", raw_rec=").append(raw_rec);
		s.append(")");
		return s.toString();
	}

	/**
	 * Parse the byte blob into fields.
	 * @param cname Controller name for logging purposes.
	 * @param sb Blob read from Sensys access point, never null.
	 * @return True if the record is valid, else false.
	 */
	public boolean parse(String cname, SensysBlob sb)
		throws ParsingException
	{
		controller_name = cname;
		valid_rec = false;
		create_time = TimeSteward.currentTimeMillis();
		raw_rec = sb.toStringChars();
		final String[] fs = splitFields();
		SensysPoller.log("numfields=" + fs.length);
		num_lanes = calcNumLanes(fs.length);
		SensysPoller.log("numlanes=" + num_lanes);
		rec_time = getFieldDate(fs, IDX_TIME);
		sensor_id = getFieldString(fs, IDX_SID);
		for (int l = 0; l < num_lanes; ++l)
			parseLane(fs, calcIdx(l, fs.length), l);
		valid_rec = true;
		SensysPoller.log("created rec=" + toString());
		return true;
	}

	/**
	 * Calculate a field index.
	 * @param laneidx Zero based index for lane column.
	 * @param numcols Total number of columns on line read.
	 * @return Zero based field index.
	 */
	private int calcIdx(int laneidx, int numcols) throws ParsingException {
		int i = NUM_FIELDS_HEADER + laneidx * NUM_FIELDS_LANE;
		if (i < 0 || i >= numcols) {
			String m = "controller=" + controller_name +
				", bogus column index=" + i;
			SensysPoller.log(m);
			throw new ParsingException(m);
		}
		return i;
	}

	/** Calculate number of lanes. */
	private int calcNumLanes(int nfields) throws ParsingException {
		 if ((nfields - NUM_FIELDS_HEADER) % NUM_FIELDS_LANE != 0) {
			String m = "controller=" + controller_name +
				", bad num fields=" + nfields +
				". Is field controller configured correctly?";
			SensysPoller.log(m);
			throw new ParsingException(m);
		}
		int nlanes = (nfields - NUM_FIELDS_HEADER) / NUM_FIELDS_LANE;
		if (nlanes < 1 || nlanes > MAX_NUM_LANES) {
			String m = "controller=" + controller_name +
				", bad num lanes=" + nlanes +
				", expected 1 - " + MAX_NUM_LANES;
			SensysPoller.log(m);
			throw new ParsingException(m);
		}
		return nlanes;
	}

	/**
	 * Split raw string record into fields.
	 * @return Array of fields read from device.
	 */
	private String[] splitFields() {
		if (raw_rec == null || raw_rec.length() <= 0)
			return new String[0];
		String[] fs = raw_rec.split(",");
		if (fs == null)
			return new String[0];
		for (int i = 0; i < fs.length; ++i)
			fs[i] = fs[i].trim();
		return fs;
	}

	/**
	 * Parse a single lane.
	 * @param fs Array of fields read from field device.
	 * @param ci Index for first column in lane.
	 * @param li lane index, zero-based
	 */
	private void parseLane(String fs[], int ci, int li)
		throws ParsingException
	{
		int ln = getFieldInt(fs, ci + IDX_OFFSET_LANE);
		String m = "read lane#=" + ln + ", expected=" +
			new Integer(li + 1);
		lane_samples.setOccupancy(li,
			getFieldFloat(fs, ci + IDX_OFFSET_OCC));
		lane_samples.setVolume(li,
			getFieldInt(fs, ci + IDX_OFFSET_VOL));
		lane_samples.setSpeed(li,
			getFieldFloat(fs, ci + IDX_OFFSET_SPD));
	}

	/** Get a field as String. */
	private String getFieldString(String[] fs, int i)
		throws ParsingException
	{
		if (i >= 0 && i < fs.length)
			return fs[i];
		String m = "controller=" + controller_name +
			", bogus idx=" + i + ", fs=" + SString.toString(fs);
		SensysPoller.log(m);
		throw new ParsingException(m);
	}

	/** Get a field as an int. */
	private int getFieldInt(String[] fs, int i)
		throws ParsingException
	{
		return SString.stringToInt(getFieldString(fs, i));
	}

	/** Get a field as a float. */
	private float getFieldFloat(String[] fs, int i)
		throws ParsingException
	{
		return SString.stringToFloat(getFieldString(fs, i));
	}

	/**
	 * Get a timestamp field (e.g. "2012-04-29 13:28:45") as an
	 * epoch value.
	 * @return The date as a long
	 * @throws ParsingException
	 */
	private long getFieldDate(String[] fs, int i)
		throws ParsingException
	{
		long t = STime.parseDate(fs[i]);
		if (t >= 0)
			return t;
		String m = "controller=" + controller_name +
			", could not parse time=" + fs[i];
		SensysPoller.log(m);
		throw new ParsingException(m);
	}

	/** Is the record valid? */
	public boolean getValid() {
		return valid_rec;
	}

	/**
	 * Store the record to the specified controller.
	 * @param ci Controller associated with the record, can be null
	 */
	public void store(ControllerImpl ci) {
		if (ci == null)
			return;
		SensysPoller.log("storing rec");
		LinkedList<String> sids = ControllerHelper.getStationIds(ci);
		if (sids.size() <= 0) {
			SensysPoller.log("No rnode for configured " +
				"for ctrl=" + ci);
		}
		SensysPoller.log("storing rec: " +
			" stations=" + sids +
			", create_time=" + new Date(create_time) +
			", vo=" + SString.toString(lane_samples.getVolumes())+
			", sp=" + SString.toString(lane_samples.getSpeeds()) +
			", sc=" + SString.toString(lane_samples.getScans()) +
			", to ctrl=" + ci);
		ci.storeVolume(create_time, SAMPLE_PERIOD_SEC, STARTPIN,
			lane_samples.getVolumes());
		ci.storeOccupancy(create_time, SAMPLE_PERIOD_SEC, STARTPIN,
			lane_samples.getScans(), LaneSample.MAX_SCANS);
		ci.storeSpeed(create_time, SAMPLE_PERIOD_SEC, STARTPIN,
			lane_samples.getSpeeds());
	}

}
