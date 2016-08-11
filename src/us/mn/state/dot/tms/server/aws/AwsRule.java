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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.server.Constants;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.server.DetectorImpl;
import us.mn.state.dot.tms.server.DetectorSet;
import us.mn.state.dot.tms.server.R_NodeImpl;
import us.mn.state.dot.tms.server.WeatherSensorImpl;

/**
 * An AWS rule, combined with current sensor values, is used to determine
 * if an AWS message should be generated and sent to a sign.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public abstract class AwsRule {

	/** Name */
	final String rule_name;

	/** Constructor */
	public AwsRule(String n) {
		rule_name = n;
	}

	/** Apply the rule to current sensor data and generate a rule */
	abstract AwsAction apply(long now, LinkedList<Station> vs,
		LinkedList<WeatherSensor> ws);

	/**
	 * Get the minimum amount of time a rule must be active for the
	 * action to be deployed.
	 * @param dep True for deploying, else false for undeploying.
	 * @return Minimum deploying or undeploying time in seconds.
	 */
	protected abstract int getMinDeployingTimeSecs(boolean dep);

	/** To string */
	public String toString() {
		return rule_name;
	}

	/**
	 * Are the speed, volume, and occupancy timestamps of the sample all
	 * recent?
	 */
	boolean isSampleRecent(long now, DetectorImpl deti) {
		long tsOldest = Math.min(Math.min(deti.getSpeedStamp(),
			deti.getVolumeStamp()), deti.getScansStamp());
		if (tsOldest == Constants.MISSING_DATA)
			return false;
		long deltams = now - tsOldest;
		long limitms = AwsProps.getVdsSensorObsAgeLimitSecs() * 1000;
		AwsJob.logfinest("delta_ms=" + deltams + ", deti=" +
			deti + ", limit_ms=" + limitms);
		if (deltams > limitms) {
			AwsJob.logconfig("disregarding observation for " + deti
				+ " that contains an expired s/v/o timestamp: "
				+ new Date(tsOldest) + ".  delta_ms=" + deltams
				+ ", limit_ms=" + limitms);
			return false;
		}
		return true;
	}

	/** Get a detector set for the specified station */
	static public DetectorImpl[] getDetectors(Station s) {
		R_NodeImpl rni = (R_NodeImpl)(s.getR_Node());
		return rni.getDetectorSet().toArray();
	}

	/**
	 * Get a string listing all detectors and their speeds for each
	 * station in the specified list.
	 */
	static protected String getStationsSpeedsString(List<Station> vs) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Station s : vs) {
			if (!first)
				sb.append(",");
			sb.append(s.toString());
			sb.append(":");
			sb.append(getStationSpeedsString(s));
			first = false;
		}
		return sb.toString();
	}

	/**
	 * Get a string listing all detectors and their speeds for the
	 * specified station.
	 */
	static protected String getStationSpeedsString(Station s) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		DetectorImpl[] dtis = getDetectors(s);
		boolean first = true;
		for (DetectorImpl dti : dtis) {
			if (!first)
				sb.append(",");
			sb.append(dti.toString());
			sb.append(":");
			sb.append(dti.getSpeedRaw());
			first = false;
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Return true if this rule is a speed rule and the deployed
	 * message is from a speed rule.
	 */
	protected boolean bothSpeedRules(DMS dms) {
		if (!(this instanceof AwsRuleTrafficSpeed)) {
			AwsJob.logfinest("d=" + dms + ", rule=" + this +
				" is not a speed rule");
			return false;
		}
		AwsJob.logfinest("d=" + dms + ", rule=" + this +
			" is a speed rule");
		return AwsRuleTrafficSpeed.deployedIsSpeedRule(dms);
	}

	/** Get all AWS rules */
	static public LinkedList<AwsRule> getAll() {
		LinkedList<AwsRule> rs = new LinkedList<AwsRule>();
		rs.add(new AwsRuleTrafficSpeed());
		rs.add(new AwsRuleLowVisibility());
		rs.add(new AwsRuleHighWind());
		AwsJob.logfinest("all rules=" + rs);
		return rs;
	}

}
