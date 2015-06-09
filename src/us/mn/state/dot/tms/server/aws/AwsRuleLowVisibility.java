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
 * An AWS low-visibility rule.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsRuleLowVisibility extends AwsRule {

	/** Associated quick message name */
	public static final String QMNAME = "aws_low_vis";

	/** Constructor */
	public AwsRuleLowVisibility() {
		super("LowVisibility");
	}

	/** Create action */
	AwsAction createAction() {
		return new AwsAction(this, 2, rule_name, QMNAME,
			DMSMessagePriority.AWS, DMSMessagePriority.AWS);
	}

	/**
	 * Apply the rule to current conditions.
	 * @return An AWS action or null if none.
	 */
	public AwsAction apply(long now, LinkedList<Station> vs,
		LinkedList<WeatherSensor> ws)
	{
		return (getLowVisibility(ws) ? createAction() : null);
	}

	/** Determine if visibility is low for any of the specified sensors */
	private boolean getLowVisibility(LinkedList<WeatherSensor> ws) {
		for (WeatherSensor w : ws) {
			WeatherSensorImpl wi = (WeatherSensorImpl)w;
			if (WeatherSensorHelper.isLowVisibility(wi)) {
				AwsJob.loginfo("low vis for ws=" + w
					+ ": " + wi.getVisibility());
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
