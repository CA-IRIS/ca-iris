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
package us.mn.state.dot.tms.server.comm.pems;

import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.server.DetectorImpl;
import us.mn.state.dot.tms.server.DetectorSet;
import us.mn.state.dot.tms.server.R_NodeImpl;
import us.mn.state.dot.tms.server.StationImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.utils.ByteBlob;
import us.mn.state.dot.tms.utils.SString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A PeMS property represents the data associated with a single
 * traffic station.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class PemsProperty extends ControllerProperty {

	/**
	 * Maximum age of samples to report.
	 * Samples older than this will not be reported.
	 */
	private static final int MAX_AGE_MS = 2 * 60 * 1000;

	/**
	 * Maximum allowed time differential for considering a sample
	 * component (ie volume, speed, scans) to be part of a discrete
	 * sample.
	 */
	private static final int TOLERANCE_MS = 15 * 1000;

	/** Associated traffic station */
	private final StationImpl traf_stat;

	/** Constructor. */
	public PemsProperty(StationImpl ts) {
		traf_stat = ts;
	}

	/** Return a representative string. */
	public String toString() {
		return getStationName();
	}

	/** Perform a GET request.  Called by PemsMessage. */
	public void doGetRequest(OutputStream os, InputStream is, String h)
		throws IOException {
	}

	/**
	 * Perform a SET request. Called by PemsMessage.
	 *
	 * @return Number of bytes written.
	 */
	public int doSetRequest(OutputStream os, InputStream is)
		throws IOException {
		return writeStationData(os);
	}

	/**
	 * Write data associated with a single station to PeMS.
	 *
	 * @param os Output stream to write.
	 * @return Number of bytes written.
	 * @throws IOException
	 */
	private int writeStationData(OutputStream os) throws IOException {
		ByteBlob b = getPemsSample();
		if (b == null || b.size() <= 0)
			return 0;
		os.write(b.toArray());
		PemsPoller.log("wrote to PeMS=" + b.toStringChars());
		return b.size();
	}

	/**
	 * Get sample data for a single station in PeMS format.
	 *
	 * @return Sample data in PeMS format, which has zero
	 * length on error.k
	 */
	private ByteBlob getPemsSample() {
		R_NodeImpl rn = (R_NodeImpl) traf_stat.getR_Node();
		DetectorSet ds = rn.getDetectorSet();
		DetectorImpl[] dets = ds.toArray();

		if (isSampleEmpty(dets))
			return new ByteBlob();
		StringBuilder sb = new StringBuilder();
		String sid = getStationName();
		if (sid == null)
			return new ByteBlob();
		sb.append(sid).append(",");
		long tsLatestSamp = addDetectorData(sid, dets, sb);
		if (getAge(tsLatestSamp) > MAX_AGE_MS) {
			PemsPoller.log("station " + this +
				" ignored for invalid/expired time.");
			return new ByteBlob();
		} else
			PemsPoller.log("time is valid.");
		sb.append(getTime(tsLatestSamp));
		return new ByteBlob(sb.toString().getBytes());
	}

	/** Return true if the sample contains all missing values. */
	private boolean isSampleEmpty(DetectorImpl[] dets) {
		for (int i = 0; i < dets.length; ++i) {
			if (dets[i].getFlowRaw() >= 0.0F)
				return false;
			if (dets[i].getSpeedRaw() >= 0.0F)
				return false;
			if (dets[i].getOccupancy() >= 0.0F)
				return false;
		}
		return true;
	}

	/**
	 * Compute the age of a timestamp.
	 *
	 * @param stamp The timestamp.
	 */
	private static long getAge(long stamp) {
		return new Date().getTime() - stamp;
	}

	/**
	 * Construct the PeMS time field using the specified stamp.
	 *
	 * @param stamp A time stamp or MISSING_DATA.
	 * @return A string in local time as yyyy-MM-dd HH:mm:ss.
	 */
	static private String getTime(long stamp) {
		SimpleDateFormat sdf =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		if (stamp == Constants.MISSING_DATA)
			d = new Date();
		else
			d = new Date(stamp);
		return sdf.format(d);
	}

	/**
	 * Get PeMS station name.
	 *
	 * @return The 7 character PeMS station id or null on errror.
	 */
	private String getStationName() {
		String id = traf_stat.getName();
		if (id.length() != 7 && id.length() != 8) {
			PemsPoller.log("PeMS station='" + id +
				"' has incorrect length (" + id.length() +
				"), should be 7 or 8. It was ignored.");
			return null;
		}
		if (!SString.isNumeric(id)) {
			PemsPoller.log("PeMS station='" + id +
				"' is not numeric. It was ignored.");
			return null;
		}
		return id;
	}

	/**
	 * Add flow, speed, and occupancy for all detectors to the string.
	 *
	 * @param sid  PeMS station id.
	 * @param dets Detectors in the station.
	 * @param sb   String that is appended with lane data.
	 * @return Detector data timestamp, which may be MISSING_DATA.
	 */
	private long addDetectorData(String sid, DetectorImpl[] dets,
		StringBuilder sb) {
		long tsLatestSamp = Constants.MISSING_DATA;
		sb.append(dets.length).append(',');
		for (int i = 0; i < dets.length; ++i) {
			// most recent timestamp (of vol, speed, and scans)
			String fl = getPemsFlow(dets[i].getFlowRaw());
			long tsVol = dets[i].getVolumeStamp();
			String sp = getPemsSpeed(dets[i].getSpeedRaw());
			long tsSpeed = dets[i].getSpeedStamp();
			String oc = getPemsOccupancy(dets[i].getOccupancy());
			long tsScans = dets[i].getScansStamp();
			// latest sample compoment timestamp
			long tsLatestComp = Math.max(Math.max(tsVol, tsSpeed),
				tsScans);
			if (tsLatestComp > tsLatestSamp)
				tsLatestSamp = tsLatestComp;
			if (getAge(tsLatestComp) > MAX_AGE_MS) {
				// samp too old
				sb.append(",,,");
				continue;
			}
			// only include sample components that belong to the
			// latest sample grouping
			if ((tsLatestComp - tsVol) <= TOLERANCE_MS)
				sb.append(fl);        // vehs per 30 secs
			sb.append(',');
			if ((tsLatestComp - tsSpeed) <= TOLERANCE_MS)
				sb.append(sp);        // MPH
			sb.append(',');
			if ((tsLatestComp - tsScans) <= TOLERANCE_MS)
				sb.append(oc);        // 0-1000
			sb.append(',');
		}
		PemsPoller.log("read " + dets.length
			+ " detectors for PeMS data");
		return tsLatestSamp;
	}

	/**
	 * Get the PeMS flow.
	 *
	 * @param fl IRIS flow as vehicles per hour, or MISSING_DATA.
	 * @return PeMS flow, &gt;= 0, as vehicles per 30 secs or empty.
	 */
	static private String getPemsFlow(float fl) {
		if (fl < 0)
			return "";
		return String.valueOf(Math.round(fl / 60F / 2F));
	}

	/**
	 * Get the PeMS occupancy.
	 *
	 * @param oc IRIS occupancy, as a percentage 0-100, or MISSING_DATA.
	 * @return PeMS occupancy, 0 - 1000, or blank.
	 */
	static private String getPemsOccupancy(float oc) {
		if (oc < 0)
			return "";
		oc = (oc > 100 ? 100 : oc);
		return String.valueOf(Math.round(10 * oc));
	}

	/**
	 * Get the PeMS speed.
	 *
	 * @param sp IRIS speed, mph or MISSING_DATA.
	 * @return PeMS speed, mph, &gt;= 0 or empty.
	 */
	static private String getPemsSpeed(float sp) {
		if (sp < 0)
			return "";
		return String.valueOf(Math.round(sp));
	}

}

