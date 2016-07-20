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
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.server.WeatherSensorImpl;

/**
 * An AWS high-wind rule.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsRuleHighWind extends AwsRule {

	/** Associated quick message name */
	public static final String QMNAME = "aws_high_wind";

	/** Constructor */
	public AwsRuleHighWind() {
		super("HighWind");
	}

	/** Create action */
	AwsAction createAction() {
		return new AwsAction(this, 1, rule_name, QMNAME,
			DMSMessagePriority.AWS, DMSMessagePriority.AWS);
	}

	/**
	 * Apply the rule to current conditions.
	 * @return An AWS action or null if none.
	 */
	public AwsAction apply(long now, LinkedList<Station> vs,
		LinkedList<WeatherSensor> ws)
	{
		return (isHighWind(now, ws) ? createAction() : null);
	}

	/** Is wind speed high? */
	private boolean isHighWind(long now, LinkedList<WeatherSensor> ws) {
		for (WeatherSensor w : ws) {
			WeatherSensorImpl wi = (WeatherSensorImpl)w;
			if (WeatherSensorHelper.isHighWind(wi)) {
				AwsJob.loginfo("high wind for ws=" + w
					+ ": " + wi.getWindSpeed());
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the minimum amount of time a rule must be active for the
	 * action to be deployed.
	 * @param dep True for deploying else false for undeploying.
	 * @return Minimum deploying or undeploying time in seconds.
	 */
	protected int getMinDeployingTimeSecs(boolean dep) {
		return AwsProps.getMinRwisDeployingTimeSecs(dep);
	}

}
