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

import java.util.LinkedList;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DetectorHelper;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.QuickMessageHelper;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.server.DetectorImpl;

/**
 * An AWS traffic rule for slow and stopped traffic.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsRuleTrafficSpeed extends AwsRule {

	/** Associated quick message names */
	public static final String QMNAME_SLOW = "aws_slow";
	public static final String QMNAME_STOP = "aws_stopped";

	/** Constructor */
	public AwsRuleTrafficSpeed() {
		super("TrafficSpeed");
	}

	/** Create action for stopped or slow condition */
	AwsAction createAction(boolean stopped) {
		if (stopped)
			return new AwsAction(this, 4, rule_name, QMNAME_STOP,
				DMSMessagePriority.AWS,
				DMSMessagePriority.AWS);
		else
			return new AwsAction(this, 3, rule_name, QMNAME_SLOW,
				DMSMessagePriority.AWS,
				DMSMessagePriority.AWS);
	}

	/**
	 * Apply the rule to current conditions.
	 * @return An AWS action or null if none.
	 */
	public AwsAction apply(long now, LinkedList<Station> vs,
		LinkedList<WeatherSensor> ws)
	{
		// highest priority conditions evaluated first
		if (isTrafficStopped(now, vs)) {
			// traf stopped for at least 1 det @ at least 1 stat
			AwsJob.loginfo("traffic stopped: "
				+ getStationsSpeedsString(vs));
			return createAction(true);
		}
		else if (isTrafficSlow(now, vs)) {
			// traf slow for at least 1 det @ at least 1 stat
			AwsJob.loginfo("traffic slow: "
				+ getStationsSpeedsString(vs));
			return createAction(false);
		}
		else
			return null;
	}

	/** Return true if the deployed message is from a speed rule */
	static protected boolean deployedIsSpeedRule(DMS dms) {
		boolean d = QuickMessageHelper.isQuickMsgDeployed(dms,
			QMNAME_STOP);
		AwsJob.logfinest("deployed is stopped message=" + d);
		if (d)
			return true;
		d = QuickMessageHelper.isQuickMsgDeployed(dms, QMNAME_SLOW);
		AwsJob.logfinest("deployed is slow message=" + d);
		return d;
	}

	/**
	 * Is traffic slow?
	 * @return true if the speed is slow for any station, else false.
	 */
	boolean isTrafficSlow(long now, LinkedList<Station> ts) {
		int stopt = AwsProps.getStoppedSpeedTriggerMph();
		int slowt = AwsProps.getSlowSpeedTriggerMph();
		for (Station s : ts)
			if (speedRange(now, s, stopt, slowt))
				return true;
		return false;
	}

	/**
	 * Is traffic stopped?
	 * @return true if traffic is stopped for any station, else false.
	 */
	public boolean isTrafficStopped(long now, LinkedList<Station> ts) {
		int stopt = AwsProps.getStoppedSpeedTriggerMph();
		for (Station s : ts)
			if (speedRange(now, s, 0, stopt))
				return true;
		return false;
	}

	/**
	 * Is speed in the specified range for any detector?
	 * @return true if the speed is in the specified range for any
	 *         detector in the station, else false.
	 */
	public boolean speedRange(long now, Station s, int limlow,
		int limhigh)
	{
		for (DetectorImpl dti : getDetectors(s))
			if (speedRange(now, dti, limlow, limhigh))
				return true;
		return false;
	}

	/**
	 * Is speed in the specified range?
	 * @param limlow Speed must be &gt;= this value.
	 * @param limhigh Speed must be &lt; this value.
	 * @return True if the current speed is within the specified range.
	 */
	public boolean speedRange(long now, DetectorImpl dti, int limlow,
		int limhigh)
	{
		if (!isSampleRecent(now, dti))
			return false;
		float vol = dti.getVolume();
		float speed = dti.getSpeedRaw();
		float occ = dti.getOccupancy();
		if (ignore(vol, speed, occ))
			return false;
		boolean inrange = (speed >= limlow && speed < limhigh);
		if (inrange) {
			AwsJob.logfine("speed in range for " +
				"sid=" + DetectorHelper.getStationId(dti) +
				", lane=" + dti.getLaneNumber() +
				", sp=" + speed +
				", >= " + limlow + ", < " + limhigh +
				", vol = " + vol + ", occ=" + occ);
		}
		return inrange;
	}

	/** Ignore detector data that is bogus or not relevant */
	static private boolean ignore(float vol, float speed, float occ) {
		return !(vol > 0 && occ > 0 & speed > 0);
	}

	/**
	 * Get the minimum amount of time a rule must be active for the
	 * action to be deployed.
	 * @param dep True for deploying else false for undeploying.
	 * @return Minimum deploying or undeploying time in seconds.
	 */
	protected int getMinDeployingTimeSecs(boolean dep) {
		return AwsProps.getMinVdsDeployingTimeSecs(dep);
	}

}
