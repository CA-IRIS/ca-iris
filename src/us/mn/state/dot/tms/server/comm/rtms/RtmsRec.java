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
package us.mn.state.dot.tms.server.comm.rtms;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.utils.ByteBlob;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * RTMS protocol record.
 * A record has a header, qualifier, length, data, and checksum.
 * See the EIS RTMS protocol documentation.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class RtmsRec {

	/** Header constant */
	static private final int HEADER = 0xff;

	/** Index for start of record data */
	static private final int DATA_IDX = 3;

	/** Fixed number of lanes */
	static protected final int NUM_LANES = 8;

	/** Missing speed indicator */
	static private final int MISSING_SPEED = 0xf0;

	/** Maximum (valid) speed (miles per hour) */
	static private final float MAX_SPEED = 100.0F;

	/** TODO: get rid of this and use us.mn.state.dot.tms.units.Distance */
	static private float MILES_PER_KM = 0.621371192F;

	/** Record data */
	private ByteBlob rec_data = new ByteBlob();

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Assigned station ID or negative if unassigned. */
	private int assigned_station_id = -1;

	/** Time record was created */
	private final long creation_time;

	/** Constructor */
	public RtmsRec(CommLinkImpl cl, ByteBlob bb) {
		comm_link = cl;
		creation_time = TimeSteward.currentTimeMillis();
		rec_data = bb.clone();
		RtmsPoller.log("cl=" + comm_link +
			": created RtmsRec=" + this);
	}

	/** Return time record was created */
	public long getAge() {
		return creation_time;
	}

	/** Return time in MS since the record was created */
	public long getAgeDelta() {
		return STime.calcTimeDeltaMS(creation_time);
	}

	/** Return true if the record starts a new station. */
	public boolean startStation() {
		return getType() == RecType.VOLLONG;
	}

	/**
	 * Get assigned station id.
	 * @return The assigned station id, which is < 0 if unassigned.
	 */
	public int getAssignedStationId() {
		return assigned_station_id;
	}

	/** Set the assigned station id. */
	public void setAssignedStationId(int id) {
		assigned_station_id = id;
	}

	/**
	 * Get the sensor id, which is specified in occupancy records.
	 * @return The sensor id, else -1 if not found.
	 */
	public int getSensorId() {
		if(getType() == RecType.OCCUPANCY)
			return rec_data.getInt(DATA_IDX + NUM_LANES);
		else if(getType() == RecType.OCCUPANCYL)
			return rec_data.getInt(DATA_IDX + 17);
		else
			return -1;
	}

	/** Get the voltage */
	public float getVoltage() {
		if(getType() == RecType.VOLLONG)
			return .1F * rec_data.getInt(DATA_IDX + NUM_LANES);
		else
			return -1F;
	}

	/** Get the sequence number */
	public int getSeqNum() {
		if(getType() == RecType.VOLUME)
			return rec_data.getInt(DATA_IDX + NUM_LANES);
		else
			return -1;
	}

	/** Get the forward looking avg speed */
	public int getFwdSpeed() {
		if(getType() == RecType.SPEED)
			return rec_data.getInt(DATA_IDX + NUM_LANES + 0);
		else
			return -1;
	}

	/** Get the direction */
	public int getDir() {
		if(getType() == RecType.SPEED)
			return rec_data.getInt(DATA_IDX + NUM_LANES + 1);
		else
			return -1;
	}

	/** Get the health */
	public int getHealth() {
		if(getType() == RecType.SPEED)
			return rec_data.getInt(DATA_IDX + NUM_LANES + 2);
		else
			return -1;
	}

	/** To string */
	public String toString() {
		RecType t = getType();
		String r = "";
		r += "(RtmsRec: t=" + t;
		r += ", q=" + getQualifier();
		r += ", dl=" + getDataLength();
		r += ", length=" + length();
		r += ", age ms=" + getAgeDelta();
		r += ", valid=" + valid();
		r += ", " + toStringRec(t);
		r += ")";
		return r;
	}

	/** To string for record data per record type. */
	private String toStringRec(RecType t) {
		StringBuilder r = new StringBuilder();
		if(t == RecType.VOLLONG) {
			r.append("volLong=");
			r.append(SString.toString(getVolumeLong()));
			r.append(", voltage=").append(getVoltage());
		}
		else if(t == RecType.VOLUME) {
			r.append("vol=");
			r.append(SString.toString(getVolume()));
			r.append(", seqNum=").append(getSeqNum());
		}
		else if(t == RecType.OCCUPANCY) {
			r.append("occ=");
			r.append(SString.toString(getOccupancy()));
			r.append(", SensorId=").append(getSensorId());
		}
		else if(t == RecType.OCCUPANCYL) {
			r.append("occLong=");
			r.append(SString.toString(getOccupancy()));
			r.append(", SensorId=").append(getSensorId());
		}
		else if(t == RecType.SPEED) {
			r.append("speed=");
			r.append(SString.toString(getSpeed()));
			r.append(", FwdAvgSpd=").append(getFwdSpeed());
			r.append(", Dir=").append(getDir());
			r.append(", Health=").append(getHealth());
		}
		else if(t == RecType.MIDSIZE) {
			r.append("midsizedVol=");
			r.append(SString.toString(getMidSizeVolume()));
		}
		else if(t == RecType.XLONG) {
			r.append("xlongVol=");
			r.append(SString.toString(getXLongVolume()));
		}
		else if(t == RecType.RTCLOCK) {
			r.append("date=").append(getClock());
		}
		else if(t == RecType.CLOCKTICK) {
			r.append("ignored");
		}
		else if(t == RecType.MSGINFO) {
			r.append("ignored");
		}
		else {
			RtmsPoller.log("cl=" + comm_link +
				": unknown record type=" + t + " ignored");
		}
		return r.toString();
	}

	/**
	 * Return an array containing the bytes at the specified position
	 * converted to ints.
	 */
	private int[] getInts(int i, int n) {
		int[] r = new int[n];
		for(int j = 0; j < n; ++j)
			r[j] = rec_data.getInt(i + j);
		return r;
	}

	/**
	 * Return an array containing the bytes at the specified position
	 * converted to floats.
	 */
	private float[] getFloats(int i, int n) {
		float[] r = new float[n];
		for(int j = 0; j < n; ++j)
			r[j] = (float)rec_data.getInt(i + j);
		return r;
	}

	/**
	 * Return an array containing the the values of pairs of subsequent
	 * bytes at the specified position, converted to floats.
	 */
	private float[] getFloats2(int i, int n) {
		float[] r = new float[n];
		for(int j = 0; j < n; ++j)
			r[j] = (float)rec_data.getInt(i + j*2);
		return r;
	}

	/**
	 * Return an array containing the the values of pairs of subsequent
	 * bytes at the specified position, converted as .1 per 1.
	 */
	private float[] getFloats2P1(int i, int n) {
		float[] r = getFloats2(i, n);
		for(int j = 0; j < n; ++j)
			r[j] *= .1F;
		return r;
	}

	/** Get date from RTCLOCK record */
	private Date getClock() {
		assert getType() == RecType.RTCLOCK;
		final int i = DATA_IDX;
		int sec = rec_data.getBits(i + 0, 6);
		int min = rec_data.getBits(i + 1, 6);
		int hr = rec_data.getBits(i + 2, 5);
		int date = rec_data.getBits(i + 4, 4);
		int month = rec_data.getBits(i + 5, 5);
		int year = 2000 + rec_data.getBits(i + 6, 7);
		RtmsPoller.log("cl=" + comm_link +
			": date=" + month +"/"+date+"/"+year);
		Calendar dt = new GregorianCalendar();
		dt.set(year, month, date, hr, min, sec);
		return dt.getTime();
	}

	/**
	 * Get speed values from speed record.
	 * @return Array of measured speeds (MPH), where -1 is not available,
	 *         and null on error.
	 */
	public int[] getSpeed() {
		if(getType() != RecType.SPEED)
			return null;
		int[] r = getInts(DATA_IDX, NUM_LANES);
		for(int i = 0; i < r.length; ++i)
			r[i] = (r[i] == MISSING_SPEED ? -1 : kphToMph(r[i]));
		return r;
	}

	/**
	 * Get occupancy values from either occupancy record.
	 * @return Array of measured occupancy, else null on error
	 */
	public float[] getOccupancy() {
		if(getType() == RecType.OCCUPANCY)
			return getFloats(DATA_IDX, NUM_LANES);
		else if(getType() == RecType.OCCUPANCYL)
			return getFloats2P1(DATA_IDX, NUM_LANES);
		else
			return null;
	}

	/**
	 * Get volume values.
	 * @return Array of measured volume, else null on error
	 */
	public int[] getVolume() {
		if(getType() != RecType.VOLUME)
			return null;
		return getInts(DATA_IDX, NUM_LANES);
	}

	/**
	 * Get midsized volume values.
	 * @return Array of volume, else null on error
	 */
	public int[] getMidSizeVolume() {
		assert getType() == RecType.MIDSIZE;
		return getInts(DATA_IDX, NUM_LANES);
	}

	/**
	 * Get xlong volume values.
	 * @return Array of volume, else null on error
	 */
	public int[] getXLongVolume() {
		assert getType() == RecType.XLONG;
		return getInts(DATA_IDX, NUM_LANES);
	}

	/**
	 * Get long volume values.
	 * @return Array of measured volume, else null on error
	 */
	public int[] getVolumeLong() {
		if(getType() != RecType.VOLLONG)
			return null;
		return getInts(DATA_IDX, NUM_LANES);
	}

	/* Convert Km/hour to Miles/hour */
	static private int kphToMph(int kph) {
		if(kph == Constants.MISSING_DATA)
			return Constants.MISSING_DATA;
		int mph = Math.round((float)kph * MILES_PER_KM);
		mph = (mph < 1 ? 0 : mph);
		mph = (mph > MAX_SPEED ? 0 : mph);
		return mph;
	}

	/** Is the record valid? */
	public boolean valid() {
		return validLength() && validChecksum();
	}

	/** Is the specified length valid for the specified qualifier? */
	private boolean validLength() {
		return getType() != null;
	}

	/** Does the checksum field match the calculated checksum? */
	private boolean validChecksum() {
		// FIXME: log as checksum error
		int ecs = getChecksum();
		int ccs = calcChecksum();
		if(ecs != ccs) {
			RtmsPoller.log("cl=" + comm_link +
				":  invalid checksum: ccs=" + ccs +
				", ecs=" + ecs + ", data=" +
				rec_data.toString());
		}
		return ecs == ccs;
	}

	/** Return true if a record starts at the argument index */
	static public boolean recStarts(ByteBlob bb, int i) {
		return bb.getInt(i) == HEADER;
	}

	/**
	 * Extract a single record from the byte buffer.
	 * @param bb Byte buffer potentially containing a record.
	 * @param i Index into byte buffer, which is 1st byte in new record.
	 * @param oa Packet origin address
	 * @return Extracted byte buffer containing unverified new record.
	 */
	static public ByteBlob extractRec(CommLinkImpl cl, ByteBlob bb,
		int i, InetAddress oa)
	{
		RtmsPoller.log("cl=" + cl + ": i=" + i +
			", size(i)=" + bb.size(i) + "origin=" + oa);
		if(bb.size(i) < DATA_IDX)
			return null;
		if(!recStarts(bb, i))
			return null;
		int q = getQualifier(bb, i);
		if(!RecType.validQualifier(q)) {
			// FIXME: log as parsing error?
			RtmsPoller.log("cl=" + cl +
				": invalid qualifier read=" + q +
				", origin=" + oa);
			return null;
		}
		int dl = getDataLength(bb, i);
		if(RecType.lookupType(q, dl) == null) {
			RtmsPoller.log("cl=" + cl +
				": invalid data length field=" + dl);
			return null;
		}
		int reclen = calcRecLength(dl);
		if(bb.size(i) < reclen) {
			RtmsPoller.log("cl=" + cl +
				": not enough bytes in buffer " +
				"for record: have=" + bb.size(i) +
				", need=" + reclen);
			return null;
		}
		return bb.getByteBlob(i, i + reclen);
	}

	/**
	 * Get the RTMS record type.
	 * @return The type otherwise null if no type.
	 */
	public RecType getType() {
		int q = getQualifier();
		int dl = getDataLength();
		return RecType.lookupType(q, dl);
	}

	/**
	 * Get field from byte blob
	 * @param bb Byte buffer
	 * @param i Index of first record byte in buffer
	 */
	static private int getQualifier(ByteBlob bb, int i) {
		return bb.getInt(i + 1);
	}

	/** Get the qualifier field value */
	public int getQualifier() {
		return getQualifier(rec_data, 0);
	}

	/**
	 * Get field from byte blob
	 * @param bb Byte buffer
	 * @param i Index of first record byte in buffer
	 */
	static private int getDataLength(ByteBlob bb, int i) {
		return bb.getInt(i + 2);
	}

	/** Get the data length field value */
	public int getDataLength() {
		return getDataLength(rec_data, 0);
	}

	/** Get total record length */
	public int length() {
		return calcRecLength(getDataLength());
	}

	/**
	 * Calculate the total record length using the data length.
	 * @param dl Data length
	 */
	static private int calcRecLength(int dl) {
		return DATA_IDX + dl + 1;
	}

	/** Get checksum field value */
	private int getChecksum() {
		return rec_data.getInt(DATA_IDX + getDataLength());
	}

	/** Calc the record checksum as an unsigned byte */
	private int calcChecksum() {
		return rec_data.calcOneByteChecksum(
			DATA_IDX, DATA_IDX + getDataLength() - 1);
	}

	/**
	 * Add record data to the specified station.
	 * @return True if record added to station, else false.
	 */
	public boolean addToStation(StationSample s) {
		RecType t = getType();
		if(t == RecType.SPEED) {
			s.addSpeed(getSpeed());
			return true;
		}
		else if(t == RecType.OCCUPANCY) {
			s.addOccupancy(getOccupancy());
			return true;
		}
		else if(t == RecType.VOLUME) {
			s.addVolume(getVolume());
			s.addSeqNum(getSeqNum());
			return true;
		}
		return false;
	}

}
