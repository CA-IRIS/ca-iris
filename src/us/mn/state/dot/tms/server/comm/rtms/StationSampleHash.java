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

import java.util.HashMap;
import us.mn.state.dot.tms.server.CommLinkImpl;

/**
 * Hash of station sample ids to most recent station sample.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class StationSampleHash {

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/**
	 * Station sample container, which hashes the RTMS id to a
	 * station sample.
	 */
	HashMap<Integer, StationSample> sample_hash =
		new HashMap<Integer, StationSample>();

	/** Constructor */
	public StationSampleHash(CommLinkImpl cl) {
		comm_link = cl;
	}

	/**
	 * Add a new station sample to the container.
	 * @param ss New station sample to add, ignored if null.
	 * @return Previous station sample or null if not replaced.
	 */
	public StationSample add(StationSample ss) {
		if(ss == null)
			return null;
		Integer id = ss.getRtmsSensorId();
		StationSample oss = sample_hash.put(id, ss);
		return oss;
	}

	/** Get number of samples in container */
	public int size() {
		return sample_hash.size();
	}

	/** To string */
	public String toString() {
		return "(StationSampleHash size=" + size() + ")";
	}

	/** Validity check
	 * @param ns Station sample, may be null. */
	public void validityCheck(StationSample ns) {
		if(ns == null)
			return;
		StationSample ps = getStoredSample(ns);
		if(ps != null)
			validSeqNum(ns, ps);
	}

	/** Get stored sample for station identified in new sample */
	private StationSample getStoredSample(StationSample ns) {
		if(ns == null)
			return null;
		Integer id = ns.getRtmsSensorId();
		return sample_hash.get(id);
	}

	/** Check if the sequence numbers are valid. */
	static private void validSeqNum(StationSample ns, StationSample ps) {
		int nsn = ns.getSeqNum();
		int psn = ps.getSeqNum();
		boolean v = (nsn == psn + 1) || (nsn == 0 && psn == 255);
		if(!v)
			RtmsPoller.log("Missed seqnum, new seqnum=" +
				nsn + ", prior=" + psn + ", for ss=" + ns);
	}

}
