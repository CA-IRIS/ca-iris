/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2015  AHMCT, University of California
 * Copyright (C) 2016       California Department of Transportation
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.DatagramMessenger;
import us.mn.state.dot.tms.utils.ByteBlob;

/**
 * RTMS Property, which reads a traffic sample in a datagram from a field
 * sensor and stores the sample.
 *
 * @author Michael Darter
 * @author Travis Swanston
 * @author Jacob Barde
 *
 * FIXME may need to rewrite to properly utilize controller
 */
public class RtmsProperty extends ControllerProperty {

	/** Traffic sample period */
	static protected final long SAMPLE_PERIOD_MS = 30 * 1000;

	/** List of active field controllers */
	private final LinkedList<Controller> active_ctrls;

	/** Hash of latest samples */
	private StationSampleHash sample_hash;

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/**
	 * Constructor
	 * @param acs List of active controllers
	 */
	public RtmsProperty(CommLinkImpl cl,
		LinkedList<Controller> acs, StationSampleHash sh)
	{
		comm_link = cl;
		active_ctrls = acs;
		sample_hash = sh;
	}

	/** Perform a "SET" request */
	public void doSetRequest(OutputStream os, InputStream is)
		throws IOException {}

	/**
	 * Perform a get request, which consists of reading sensor data,
	 * parsing into a record and storing.  Called by RtmsMessage.
	 * @throws IOException, e.g. on timeout
	 */
	public void doGetRequest(OutputStream os, InputStream is)
		throws IOException
	{
		ByteBlob bb = new ByteBlob();
		// throws SocketTimeoutException:
		InetAddress oa = read(comm_link, is, bb);
		RtmsPoller.log("cl=" + comm_link + ": returned from "
			+ "read(), #bytes=" + bb.size());
		StationSample s = new RtmsRecs(comm_link, bb, oa).
			createStationSample();
		if(s != null) {
			sample_hash.validityCheck(s);
			sample_hash.add(s);
			s.store(active_ctrls);
		}
	}

	/**
	 * Read a datagram from a field device into a byte buffer, blocking
	 * until the read times-out or a datagram is read.
	 * @param ais Input stream to read from
	 * @param bb Returned containing the bytes read from the device
	 * @return Internet address of datagram origin, may be null
	 * @throws IOException, e.g. on timeout
	 */
	static private InetAddress read(CommLinkImpl cl, InputStream ais,
		ByteBlob bb) throws IOException
	{
		RtmsPoller.log("cl=" + cl + ": reading datagram");
		DatagramMessenger.DatagramInputStream is =
			(DatagramMessenger.DatagramInputStream)ais;
		InetAddress oa = null;
		bb.clear();
		bb.add(is.readDatagram());	// throws SocketTimeoutException
		oa = is.getOriginAddr();
		RtmsPoller.log("cl=" + cl + ": read dgram=" + bb);
		RtmsPoller.log("cl=" + cl + ": dg origin=" + oa);
		return oa;
	}

	/**
	 * Update the status of active controllers that have not received
	 * a sample within the sample period.
	 */
	public void updateControllers()
	{
		final long now = TimeSteward.currentTimeMillis();
		RtmsPoller.log("cl=" + comm_link + ": updating " +
			"controller states for " + active_ctrls.size() +
			" active controllers, now=" + new Date(now));
		for(Controller c : active_ctrls)
			updateController((ControllerImpl)c, now);
	}

	/**
	 * Update the status of a controller that has not received a
	 * sample within the sample period, regardless of if the controller
	 * is already failed.  The controller's last fail time is
	 * incremented by the period (30 secs).
	 */
	private void updateController(ControllerImpl ci, long now) {
		boolean f = ci.isFailed();
		long lt = getLastTime(ci, f);
		long delta = now - lt;
		boolean ms = (delta > SAMPLE_PERIOD_MS + getReadMarginMs());
		RtmsPoller.log("cl=" + comm_link + ": c=" + ci +
			", isFailed=" + f + ", was updated " + delta +
			" ms ago.  Missed sample=" + ms);
		if(ms) {
			long st = nextStorageTime(comm_link, lt, now);
			RtmsPoller.log("cl=" + comm_link +
				": storing missing sample, failing " +
				"controller " + ci + " at time=" + new Date(st));
			StationSample.storeMissingSample(comm_link, ci, st);
			failController(comm_link, ci, st);
		}
	}

	/**
	 * Calculate the next storage time.
	 * @param lt Last storage time.
	 * @param now The time now.
	 */
	static private long nextStorageTime(CommLinkImpl cl, long lt,
		long now)
	{
		long nt = lt + SAMPLE_PERIOD_MS;
		RtmsPoller.log("cl=" + cl + ": now - nt=" +
			new Long(now - nt));
		if(now - nt > 2 * getReadMarginMs()) {
			RtmsPoller.log("cl=" + cl + ": delta " +
				"is larger than 2x margin, using now for " +
				"store time.");
			nt = now;
		}
		return nt;
	}

	/** Get the last storage or fail time. */
	static private long getLastTime(ControllerImpl ci, boolean failed) {
		return failed ? ci.getFailTime() : ci.getLastStoreTime();
	}

	/**
	 * Fail the specified controller.
	 * @param ci Associated controller.
	 * @param now Current time.
	 */
	static private void failController(CommLinkImpl cl,
		ControllerImpl ci, long now)
	{
		RtmsPoller.log("cl=" + cl + ": failing c=" + ci);
		LinkedList<String> sids = ControllerHelper.getStationIds(ci);
		String m = "No data received in sample period.";
		RtmsPoller.log("cl=" + cl + ": VDS c=" + ci +
			", stationids=" + sids + ", msg=" + m);
		ci.setErrorStatus(m);
		ci.setFailTimeCA(now); //FIXME bandaid for D10 and Trac #562
		ci.setFailed(true);
		ci.completeOperation(ci.toString(), false);
	}

	static protected int getReadMarginMs() {
		return SystemAttrEnum.RTMS_READ_MARGIN_SEC.getInt() * 1000;
	}

}
