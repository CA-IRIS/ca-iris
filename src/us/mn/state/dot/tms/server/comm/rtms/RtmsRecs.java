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

import java.util.ArrayList;
import java.util.LinkedList;
import java.net.InetAddress;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.utils.ByteBlob;
import us.mn.state.dot.tms.utils.SString;

/**
 * Container for RTMS protocol records.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class RtmsRecs {

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Container of records */
	ArrayList<RtmsRec> rtms_recs = new ArrayList<RtmsRec>();

	/** Constructor */
	public RtmsRecs(CommLinkImpl cl) {
		comm_link = cl;
	}

	/** Constructor */
	public RtmsRecs(CommLinkImpl cl, ByteBlob bb, InetAddress oa) {
		comm_link = cl;
		add(bb, oa);
	}

	/** Get number of records in the container */
	public int size() {
		return rtms_recs.size();
	}

	/** Get the specified record */
	public RtmsRec get(int i) {
		assert i >= 0 && i < size();
		if (i < 0 || i >= size())
			return null;
		return rtms_recs.get(i);
	}

	/** To string */
	public String toString() {
		return "(RtmsRecs size=" + size() + ", recs=" +
			SString.toString(getNames().toArray()) + ")";
	}

	/** Get a list of names in container */
	private LinkedList<String> getNames() {
		LinkedList<String> rs = new LinkedList();
		for(RtmsRec r : rtms_recs)
			rs.add(r.getType().toString());
		return rs;
	}

	/**
	 * Add an RTMS record to container that is contained in a byte blob.
	 * @param bb A ByteBlob containing one or more RTMS records.
	 * @param oa Packet origin address
	 */
	private void add(ByteBlob bb, InetAddress oa) {
		RtmsPoller.log("cl=" + comm_link + ": buffer size=" +
			bb.size());
		int i = 0;
		while(i < bb.size()) {
			RtmsRec r = create(bb, i, oa);
			if (r == null) {
				RtmsPoller.log("cl=" + comm_link + ": " +
					"ignoring dgram, no rec at i=" + i);
				return;
			} else {
				RtmsPoller.log("cl=" + comm_link +
					": created rec, i=" + i);
				add(r);
				i += r.length();
			}
		}
	}

	/** Add a single RTMS record to the container */
	private void add(RtmsRec r) {
		rtms_recs.add(r);
	}

	/**
	 * Create a an RTMS record via data in the byte buffer, starting
	 * at the specified position.
	 * @param bb Byte buffer containing potential records, never null.
	 * @param i Byte buffer index for location of record header.
	 * @param oa Packet origin address.
	 * @return Null if a valid record is not found at the specified
	 *         position, otherwise the new valid record.
	 */
	private RtmsRec create(ByteBlob bb, int i, InetAddress oa) {
		RtmsPoller.log("cl=" + comm_link + ": i=" + i +
			", size(i)=" + bb.size(i));
		if (!RtmsRec.recStarts(bb, i))
			return null;
		ByteBlob rec = RtmsRec.extractRec(comm_link, bb, i, oa);
		if (rec == null)
			return null;
		RtmsRec r = new RtmsRec(comm_link, rec);
		if (r.valid())
			return r;
		return null;
	}

	/**
	 * Find the next record that starts a new station sample.
	 * @return The index of the next record to start a new station,
	 *         or a negative index to indicate no next station.
	 */
	private int findStation(int si) {
		for(int i = si; i < size(); ++i)
			if (get(i).startStation()) {
				RtmsPoller.log("cl=" + comm_link +
					": found station,i=" + i);
				return i;
			}
		RtmsPoller.log("cl=" + comm_link + ": no more stats");
		return -1;
	}

	/** Count the number of station start records in the container. */
	public int countStations() {
		int nsr = 0;
		for(RtmsRec r : rtms_recs)
			if (r.startStation())
				++nsr;
		return nsr;
	}

	/**
	 * Create a station sample from rtms records.
	 * @return The created station sample else null on error.
	 */
	public StationSample createStationSample() {
		if (size() <= 0)
			return null;
		if (!get(0).startStation()) {
			RtmsPoller.log("cl=" + comm_link +
				": 1st rec isn't start rec.");
			return null;
		}
		// look for sensor id in records
		int sid = findSensorId();
		if (sid < 0)
			return null;
		StationSample s = new StationSample(comm_link, sid);
		for(int i = 0; i < size(); ++i) {
			final RtmsRec r = get(i);
			// station age is age of the 1st rtms record
			if (i == 0)
				s.setAge(r.getAge());
			boolean add = r.addToStation(s);
			RtmsPoller.log("cl=" + comm_link +
				": rtms rec[" + i + "] added " +
				"to station=" + add + ", rec=" + r);
		}
		return s;
	}

	/**
	 * Find the sensor id starting with the specified record.
	 * @return The station id if found, else -1
	 */
	private int findSensorId() {
		for(int i = 0; i < size(); ++i) {
			int sid = get(i).getSensorId();
			if (sid >= 0)
				return sid;
		}
		RtmsPoller.log("cl=" + comm_link +
			": didn't find sensorid in recs.");
		return -1;
	}

}
