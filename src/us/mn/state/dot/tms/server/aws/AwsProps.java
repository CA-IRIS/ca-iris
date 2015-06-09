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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.NumericAlphaComparator;
import us.mn.state.dot.tms.utils.PropertyLoader;

/**
 * Java property convenience class.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsProps {

	/** Java property file name */
	static final String PROP_FILE_NAME = "/etc/iris/aws.properties";

	/** java property file */
	static Properties aws_props = _readProps();

	/** Read the AWS properties.
	 * @return True if properties read, else false. */
	static boolean readProps() {
		aws_props = _readProps();
		return aws_props != null;
	}

	/** Read the AWS properties */
	static private Properties _readProps() {
		try {
			Properties p = PropertyLoader.load(PROP_FILE_NAME);
			AwsJob.logconfig("Read props=" + p);
			return p;
		}
		catch(Exception ex) {
			AwsJob.logconfig("Could not open property file " +
				PROP_FILE_NAME + ", ex=" + ex);
			return null;
		}
	}

	/** Get a property as a string.
	 * @return The property value or null if property was not found. */
	static private String getPropString(String pname) {
		if (aws_props == null) {
			AwsJob.logconfig("unable to read the aws " +
				"properties file: " + PROP_FILE_NAME);
			return null;
		}
		return aws_props.getProperty(pname);
	}

	/** Get a property as an integer.
	 * @param dvalue Default value if property not found. */
	static private int getPropInt(String pname, int dvalue) {
		String v = getPropString(pname);
		if (v == null || v.isEmpty()) {
			AwsJob.logconfig("property=" + pname +
				" not defined, using default=" + dvalue);
			return dvalue;
		}
		else
			return SString.stringToInt(v.trim());
	}

	/** Get a property as an integer.
	 * @param dv Default value if property not found.
	 * @param min Minimum value
	 * @param max Maximum value */
	static private int getPropInt(String pname, int dv,
		int min, int max)
	{
		int i = getPropInt(pname, dv);
		i = (i < min ? min : i);
		i = (i > max ? max : i);
		return i;
	}

	/** Get a property as an integer.
	 * @param dv Default value if property not found. */
	static private boolean getPropBoolean(String pname, boolean dv) {
		String v = getPropString(pname);
		if (v == null || v.isEmpty()) {
			AwsJob.logconfig("property=" + pname +
				" not defined, using default=" + dv);
			return dv;
		}
		else
			return SString.stringToBoolean(v.trim());
	}

	/** Get the AWS job period.
	 * @return AWS job period in seconds &gt;= 5 and &lt;= 60. */
	static int getJobPeriod() {
		int i = getPropInt("awsjob.period.secs", 15, 5, 60);
		AwsJob.logfine("job period=" + i);
		return i;
	}

	/** Get the AWS job offset.
	 * @return AWS job offset in seconds &gt;= 0 and &lt; 60. */
	static int getJobOffset() {
		int i = getPropInt("awsjob.offset.secs", 0, 0, 59);
		AwsJob.logfine("job offset=" + i);
		return i;
	}

	/** Get the specified DMS property.
	 * @return The value of the specified property or the empty
	 *         string on error. */
	static String getDmsPropString(DMSImpl di, String pname) {
		String pn = dmsPropName(di, pname);
		String v = getPropString(pn);
		if (v == null) {
			AwsJob.logconfig("property=" + pn + " not defined");
			v = "";
		}
		return v;
	}

	/** Get full property name for the specified dms */
	static String dmsPropName(DMSImpl di, String pname) {
		return "dms." + di.getName() + "." + pname;
	}

	/** Is the DMS enabled in the props file? */
	static boolean getDmsPropsEnabled(DMSImpl di) {
		return getPropBoolean(dmsPropName(di, "enable"), false);
	}

	/** Is the DMS specified as enabled or disabled in the props file?
	 * @return True if the enable property string is specified for the
	 *         specified dms, else false. */
	static boolean getDmsPropsSpecified(DMSImpl di) {
		String pn = dmsPropName(di, "enable");
		String v = getPropString(pn);
		return v != null;
	}

	/** Get the minimum time in seconds an action must be DEPLOYING
	 * before it is DEPLOYED or UNDEPLOYING before it is UNDEPLOYED. */
	static int getMinRwisDeployingTimeSecs(boolean dep) {
		if (dep)
			return getPropInt("rwis.min.deploying.secs", 90);
		else
			return getPropInt("rwis.min.undeploying.secs", 90);
	}

	/** Get the minimum time in seconds an action must be DEPLOYING
	 * before it is DEPLOYED or UNDEPLOYING before it is UNDEPLOYED. */
	static int getMinVdsDeployingTimeSecs(boolean dep) {
		if (dep)
			return getPropInt("vds.min.deploying.secs", 90);
		else
			return getPropInt("vds.min.undeploying.secs", 90);
	}

	/** Get the slow speed trigger in mph */
	static int getSlowSpeedTriggerMph() {
		return getPropInt("trigger.traffic.slow.mph", 35);
	}

	/** Get the stopped speed trigger in mph */
	static int getStoppedSpeedTriggerMph() {
		return getPropInt("trigger.traffic.stopped.mph", 11);
	}

	/** Get the sensor observation age limit in seconds */
	static int getVdsSensorObsAgeLimitSecs() {
		return getPropInt("vds.observation.age.limit.secs", 150);
	}

	/** Get a sorted list of all dms specified in the props file that are
	 * also defined IRIS DMS. */
	static LinkedList<DMS> getAwsDms() {
		LinkedList<DMS> list = new LinkedList<DMS>();
		for (DMS d : DMSHelper.getAll())
			if (AwsProps.getDmsPropsSpecified((DMSImpl)d))
				list.addFirst(d);
		Collections.sort(list, new NumericAlphaComparator<DMS>());
		AwsJob.logfinest("will process dms specified in " +
			PROP_FILE_NAME + ": " + list);
		return list;
	}

}

