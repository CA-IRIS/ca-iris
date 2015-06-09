/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.aws;

import java.lang.Math;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.DetectorHelper;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.StationHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.server.DetectorImpl;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.WeatherSensorImpl;
import us.mn.state.dot.tms.utils.SCsv;

/**
 * Job to periodically generate AWS messages.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsJob extends Job {

	// complex logging logic due to extra detail for debugging/testing;
	// refactor me.

	/** Debug log */
	static protected final DebugLog AWSJOB_LOG
		= new DebugLog("awsjob");

	/** Noisy debug log.  To be removed in future. */
	static protected final DebugLog AWSJOB_LOG_DEBUG
		= new DebugLog("awsjobdebug");

	/** Log a msg */
	static protected void log(String msg) {
		if (AWSJOB_LOG.isOpen())
			AWSJOB_LOG.log(msg);
	}

	/** Log a msg to noisy debug log */
	static protected void logDebug(String msg) {
		if (AWSJOB_LOG_DEBUG.isOpen())
			AWSJOB_LOG_DEBUG.log(msg);
	}

	// leftover log stubs from 10.0.0 merge (old logging subsystem);
	static protected void logfine(String s) {
		logDebug("FINE: " + s);
		//log("FINE: " + s);
	}
	static protected void logfinest(String s) {
		logDebug("FINEST: " + s);
		//log("FINEST: " + s);
	}
	static protected void logconfig(String s) {
		logDebug("CONFIG: " + s);
		log("CONFIG: " + s);
	}
	static protected void logsevere(String s) {
		logDebug("SEVERE: " + s);
		log("SEVERE: " + s);
	}
	// for useful decision/action info
	static protected void loginfo(String s) {
		logDebug("INFO: " + s);
		log("INFO: " + s);
	}

	/** Time of 1st job start or -1 if not set */
	private long start_time = -1;

	/** Constructor */
	public AwsJob() {
		super(Calendar.SECOND, AwsProps.getJobPeriod(),
			Calendar.SECOND, AwsProps.getJobOffset());
	}

	/**
	 * Perform the job, called periodically.  The state of all
	 * weather sensors are updated regardless of if the AWS is
	 * activated or not.  After leaving the initial start-up mode,
	 * individual DMS are processed.
	 */
	public void perform() {
		loginfo("BEGIN AWS JOB ==================================");
		if (start_time < 0)
			start_time = TimeSteward.currentTimeMillis();
		long now = TimeSteward.currentTimeMillis();
		updateStateSensors(now);
		MsgFile.clean();
		if (!AwsProps.readProps())
			return;
		logMessages();
		for (DMS d : AwsProps.getAwsDms())
			processDms((DMSImpl)d, now);
		loginfo("END AWS JOB.");
	}

	/** Log miscellaneous messages */
	private void logMessages() {
		if (WeatherSensorHelper.getObsAgeLimitSecs() <= 0)
			AwsJob.logfine("RWIS observations never expire");
	}

	/**
	 * Calculate the amount of time (secs) during which the AWS
	 * is idle, before starting.  This is important because the AWS
	 * should not act on incomplete information -- for example, on
	 * start-up, blanking a previously deployed AWS message and
	 * resending it a few seconds later.
	 */
	static int calcStartupIdleTimeSecs() {
		final int ESTIMATED_MAX_VDS_AND_RWIS_READ_PERIOD = 30;
		int max = ESTIMATED_MAX_VDS_AND_RWIS_READ_PERIOD +
			Math.max(AwsProps.getMinVdsDeployingTimeSecs(true),
			AwsProps.getMinRwisDeployingTimeSecs(true)) +
			AwsProps.getJobPeriod();
		final int MIN = 20;
		return (max < MIN ? MIN : max);
	}

	/** Update the state of sensors upon which the DMS are dependent. */
	// merge remnant: all this does now is log.  shouldn't be needed.
	private void updateStateSensors(final long now) {
		//logfinest("updating state of all WeatherSensors");
		Iterator<WeatherSensor> it = WeatherSensorHelper.iterator();
		while(it.hasNext()) {
			WeatherSensor ws = it.next();
			if (ws instanceof WeatherSensorImpl) {
				WeatherSensorImpl wsi = (WeatherSensorImpl)ws;
				//wsi.updateState(now);
				logfine("ws=" + wsi +
					", isExpired=" + WeatherSensorHelper.isSampleExpired(wsi) +
					", highwind=" + WeatherSensorHelper.isHighWind(wsi) +
					", wspeed_kph=" + wsi.getWindSpeed() +
					", lowvis=" + WeatherSensorHelper.isLowVisibility(wsi) +
					", vis_m=" + wsi.getVisibility());
			}
		}
	}

	/**
	 * For the specified DMS, generate potential AWS messages, update
	 * the AWS state for the specified DMS, select the best message, and
	 * send it to the specified DMS.
	 * @param di DMS to process for AWS messages (may be null)
	 */
	private void processDms(DMSImpl di, long now) {
		if (di == null)
			return;
		boolean penabled = AwsProps.getDmsPropsEnabled(di);
		loginfo ("processing d=" + di + ", dms." +
			di.getName() + ".enable=" + penabled);
		if (!penabled)
			return;

		// get new actions
		LinkedList<Station> vds = getStations(di);
		LinkedList<WeatherSensor> rwis = getRwis(di);
		logSensorData(di, vds, rwis);
		LinkedList<AwsAction> actions = getActions(di, now, vds, rwis);

		// update history
		di.aws_action_history.update(now, actions);

		// choose and execute best action
		AwsAction best = di.aws_action_history.getBestAction(di, now);
		loginfo("d=" + di + ", best action=" + best);
		boolean starting = startState(now);
		if (starting) {
			loginfo("Still in startup mode; inhibiting actions.");
		}
		else {
			if (best != null)
				best.perform(di);
		}
		di.aws_action_history.updateUndeploying(di, now);
	}

	/**
	 * Is the job in the start-up state?  The AWS functionality does
	 * nothing until both weather sensor and VDS max observation times
	 * have passed.
	 * @return True if AWS is in start-up mode, else false.
	 */
	private boolean startState(long now) {
		long dsecs = (now - start_time) / 1000L;
		long lim = calcStartupIdleTimeSecs();
		return (dsecs <= lim);
	}

	/**
	 * Get actions for the specified DMS.
	 * @return List of potential actions generated from sensor data.
	 */
	private LinkedList<AwsAction> getActions(DMSImpl di, long now,
		LinkedList<Station> vds, LinkedList<WeatherSensor> rwis)
	{
		logfinest("d=" + di + ", getting all actions");
		LinkedList<AwsAction> actions = new LinkedList<AwsAction>();
		for (AwsRule r : AwsRule.getAll()) {
			AwsAction aa = r.apply(now, vds, rwis);
			if (aa != null) {
				actions.addFirst(aa);
				logfinest("d=" + di + ", added act=" + aa);
			}
		}
		loginfo("d=" + di + ", potential actions=" + actions);
		return actions;
	}

	/** Log current sensor data */
	private void logSensorData(DMSImpl di, LinkedList<Station> vds,
		LinkedList<WeatherSensor> rwis)
	{
		logfinest("d=" + di + ", all vds=" + vds);
		for (Station v : vds) {
			for (DetectorImpl dti : AwsRule.getDetectors(v)) {
				String msg = ", det=" + dti.getName() +
					", sid=" +
					DetectorHelper.getStationId(dti) +
					", lane=" + dti.getLaneNumber() +
					", speed=" + dti.getSpeed() +
					", vol=" + dti.getVolume() +
					", occ=" + dti.getOccupancy() +
					", stamp(s,v,o)=" +
					timestamp(dti.getSpeedStamp()) + "," +
					timestamp(dti.getVolumeStamp()) + "," +
					timestamp(dti.getScansStamp());
				logfinest("d=" + di + msg);
			}
		}
		logfinest("d=" + di + ", all rwis=" + rwis);
		for (WeatherSensor w : rwis) {
			WeatherSensorImpl wi = (WeatherSensorImpl)w;
			logfinest("d=" + di + ", wi=" + wi +
				", vis_m=" + wi.getVisibility() +
				", wspeed_kph=" + wi.getWindSpeed() +
				", stamp=" + timestamp(wi.getStamp()));
		}
	}

	/** Return timestamp string for an epoch timestamp. */
	static private String timestamp(long t) {
		if (t == Constants.MISSING_DATA)
			return "(missing)";
		else
			return new Date(t).toString();
	}

	/** Return a list of stations for the specified dms */
	private LinkedList<Station> getStations(DMSImpl di) {
		LinkedList<Station> vds = new LinkedList<Station>();
		LinkedList<String> lost = new LinkedList<String>();
		String[] names = SCsv.separate(
			AwsProps.getDmsPropString(di, "vds"));
		for (String name : names) {
			if (name == null || name.length() <= 0)
				continue;
			Station v = StationHelper.lookupWithStationId(name);
			if (v == null)
				lost.addFirst(name);
			else
				vds.addFirst(v);
		}
		if (lost.size() > 0)
			logconfig("d=" + di + ", vds in props file not " +
				"found=" + lost);
		return vds;
	}

	/** Return a list of rwis for the specified dms */
	private LinkedList<WeatherSensor> getRwis(DMSImpl di) {
		LinkedList<WeatherSensor> rwis =
			new LinkedList<WeatherSensor>();
		LinkedList<String> lost = new LinkedList<String>();
		String[] names = SCsv.separate(
			AwsProps.getDmsPropString(di, "rwis"));
		for (String name : names) {
			if (name == null || name.length() <= 0)
				continue;
			WeatherSensor ws = WeatherSensorHelper.lookup(name);
			if (ws == null)
				lost.addFirst(name);
			else
				rwis.addFirst(ws);
		}
		if (lost.size() > 0)
			logconfig("dms=" + di + ", rwis in props file not " +
				"found=" + lost);
		return rwis;
	}

}
