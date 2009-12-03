/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms;

import java.util.HashMap;
import us.mn.state.dot.tms.utils.I18N;

/**
 * This enum defines all system attributes.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public enum SystemAttrEnum {
	ACTIONPLAN_TOOLBAR_ENABLE(true, Change.RESTART_CLIENT),
	CAMERA_NUM_PRESET_BTNS(3, 0, 20, Change.RESTART_CLIENT),
	CAMERA_NUM_VIDEO_FRAMES(900, 0),
	CAMERA_PTZ_PANEL_ENABLE(false, Change.RESTART_CLIENT),
	DATABASE_VERSION(String.class, Change.RESTART_SERVER),
	DETECTOR_AUTO_FAIL_ENABLE(true),
	DMS_AWS_ENABLE(false),
	DMS_AWS_LOG_ENABLE(false),
	DMS_AWS_LOG_FILENAME("/var/www/html/awsreport.txt"),
	DMS_AWS_RETRY_THRESHOLD(6, 1, 20),
	DMS_BRIGHTNESS_ENABLE(true, Change.RESTART_CLIENT),
	DMS_DEFAULT_JUSTIFICATION_LINE(3, 2, 5),
	DMS_DEFAULT_JUSTIFICATION_PAGE(2, 2, 4),
	DMS_DURATION_ENABLE(true, Change.RESTART_CLIENT),
	DMS_FONT_SELECTION_ENABLE(false, Change.RESTART_CLIENT),
	DMS_COMPOSER_EDIT_MODE(1, 0, 2, Change.RESTART_CLIENT), 
	DMS_PGONTIME_SELECTION_ENABLE(false, Change.RESTART_CLIENT),
	DMS_FORM(1, 1, 2), 
	DMS_HIGH_TEMP_CUTOFF(60, 35, 100),
	DMS_INTERMEDIATE_STATUS_ENABLE(false, Change.RESTART_CLIENT),
	DMS_LAMP_TEST_TIMEOUT_SECS(30, 5, 90),
	DMS_MANUFACTURER_ENABLE(true, Change.RESTART_CLIENT),
	DMS_MAX_LINES(3, 1, 12),
	DMS_MESSAGE_BLANK_LINE_ENABLE(true),
	DMS_MESSAGE_MIN_PAGES(1, 1, 6, Change.RESTART_CLIENT),
	DMS_PAGE_ON_SECS(2f, 0f, 60f),
	DMS_PAGE_OFF_SECS(0f, 0f, 60f),
	DMS_PIXEL_OFF_LIMIT(2, 1),
	DMS_PIXEL_ON_LIMIT(1, 1),
	DMS_PIXEL_STATUS_ENABLE(true, Change.RESTART_CLIENT),
	DMS_PIXEL_TEST_TIMEOUT_SECS(30, 5, 90),
	DMS_POLL_FREQ_SECS(30, 0, Change.RESTART_SERVER),
	DMS_QLIB_ENABLE(false, Change.RESTART_CLIENT),
	DMS_QUERYMSG_ENABLE(false, Change.RESTART_CLIENT),
	DMS_RENDER_SIZE(0, 0, 1, Change.RESTART_CLIENT),
	DMS_RESET_ENABLE(false, Change.RESTART_CLIENT),
	DMS_SEND_CONFIRMATION_ENABLE(false, Change.RESTART_CLIENT),
	DMSLITE_MODEM_OP_TIMEOUT_SECS(5 * 60 + 5, 5),
	DMSLITE_OP_TIMEOUT_SECS(60 + 5, 5),
	EMAIL_SENDER_CLIENT(String.class),
	EMAIL_SENDER_SERVER(String.class),
	EMAIL_SMTP_HOST(String.class),
	EMAIL_RECIPIENT_AWS(String.class),
	EMAIL_RECIPIENT_BUGS(String.class),
	HELP_TROUBLE_TICKET_ENABLE(false),
	HELP_TROUBLE_TICKET_URL(String.class),
	INCIDENT_CLEAR_SECS(600, 0, 3600),
	INCIDENT_RING_1_MILES(0, 0, 50),
	INCIDENT_RING_2_MILES(0, 0, 50),
	INCIDENT_RING_3_MILES(0, 0, 50),
	INCIDENT_RING_4_MILES(0, 0, 50),
	KML_FILE_ENABLE(false),
	KML_FILENAME("/var/www/html/iris-client/iris.kmz"),
	MAP_NORTHERN_HEMISPHERE(true),
	MAP_UTM_ZONE(15, 1, 60),
	MAP_SEGMENT_MAX_METERS(2000, 100),
	MAP_TOOLBAR_COORDS("WGS84", Change.RESTART_CLIENT),
	METER_GREEN_SECS(1.3f, 0.1f, 10f),
	METER_MAX_RED_SECS(13f, 5f, 30f),
	METER_MIN_RED_SECS(0.1f, 0.1f, 10f),
	METER_YELLOW_SECS(0.7f, 0.1f, 10f),
	OPERATION_RETRY_THRESHOLD(3, 1, 20),
	TEMP_FAHRENHEIT_ENABLE(true, Change.RESTART_CLIENT),
	TESLA_HOST(String.class),
	TRAVEL_TIME_MAX_LEGS(8, 1, 20),
	TRAVEL_TIME_MAX_MILES(16, 1, 30),
	TRAVEL_TIME_MIN_MPH(15, 1, 50),
	UPTIME_LOG_ENABLE(false),
	UPTIME_LOG_FILENAME("/var/www/html/irisuptimelog.csv"),
	WINDOW_TITLE("IRIS: ", Change.RESTART_CLIENT),
	XML_OUTPUT_DIRECTORY("/var/www/html/dds/");

	/** Change action, which indicates what action the admin must
	 *  take after changing a system attribute. */
	enum Change {
		RESTART_SERVER("Restart the server after changing."), 
		RESTART_CLIENT("Restart the client after changing."), 
		NONE("A change takes effect immediately.");

		/** Change message for user. */
		String m_msg = "";

		/** Constructor */
		Change(String msg) {
			m_msg = msg;
		}

		/** Get the restart message. */
		public String getMessage() {
			return m_msg;
		}
	}

	/** System attribute class */
	protected final Class atype;

	/** Default value */
	protected final Object def_value;

	/** Change action */
	protected final Change change_action;

	/** Minimum value for number attributes */
	protected final Number min_value;

	/** Maximum value for number attributes */
	protected final Number max_value;

	/** Create a String attribute with the given default value */
	private SystemAttrEnum(String d) {
		this(String.class, d, null, null, Change.NONE);
	}

	/** Create a String attribute with the given default value */
	private SystemAttrEnum(String d, Change ca) {
		this(String.class, d, null, null, ca);
	}

	/** Create a Boolean attribute with the given default value */
	private SystemAttrEnum(boolean d) {
		this(Boolean.class, d, null, null, Change.NONE);
	}

	/** Create a Boolean attribute with the given default value */
	private SystemAttrEnum(boolean d, Change ca) {
		this(Boolean.class, d, null, null, ca);
	}

	/** Create an Integer attribute with default, min and max values */
	private SystemAttrEnum(int d, int mn, int mx) {
		this(Integer.class, d, mn, mx, Change.NONE);
	}

	/** Create an Integer attribute with default, min and max values */
	private SystemAttrEnum(int d, int mn, int mx, Change ca) {
		this(Integer.class, d, mn, mx, ca);
	}

	/** Create an Integer attribute with default and min values */
	private SystemAttrEnum(int d, int mn) {
		this(Integer.class, d, mn, null, Change.NONE);
	}

	/** Create an Integer attribute with default and min values */
	private SystemAttrEnum(int d, int mn, Change ca) {
		this(Integer.class, d, mn, null, ca);
	}

	/** Create a Float attribute with default, min and max values */
	private SystemAttrEnum(float d, float mn, float mx) {
		this(Float.class, d, mn, mx, Change.NONE);
	}

	/** Create a system attribute with a null default value */
	private SystemAttrEnum(Class c) {
		this(c, null, null, null, Change.NONE);
	}

	/** Create a system attribute with a null default value */
	private SystemAttrEnum(Class c, Change ca) {
		this(c, null, null, null, ca);
	}

	/** Create a system attribute */
	private SystemAttrEnum(Class c, Object d, Number mn, Number mx, 
		Change ca) 
	{
		atype = c;
		def_value = d;
		min_value = mn;
		max_value = mx;
		change_action = ca;
		assert isValidBoolean() || isValidFloat() ||
		       isValidInteger() || isValidString();
	}

	/** Get a description of the system attribute enum. */
	public static String getDesc(String aname) {
		String ret = I18N.get(aname);
		SystemAttrEnum sae = lookup(aname);
		if(sae !=null)
			ret += " " + sae.change_action.getMessage();
		return ret;
	}

	/** Test if the attribute is a valid boolean */
	private boolean isValidBoolean() {
		return (atype == Boolean.class) &&
		       (def_value instanceof Boolean) &&
		       min_value == null && max_value == null;
	}

	/** Test if the attribute is a valid float */
	private boolean isValidFloat() {
		return (atype == Float.class) &&
		       (def_value instanceof Float) &&
		       (min_value == null || min_value instanceof Float) &&
		       (max_value == null || max_value instanceof Float);
	}

	/** Test if the attribute is a valid integer */
	private boolean isValidInteger() {
		return (atype == Integer.class) &&
		       (def_value instanceof Integer) &&
		       (min_value == null || min_value instanceof Integer) &&
		       (max_value == null || max_value instanceof Integer);
	}

	/** Test if the attribute is a valid string */
	private boolean isValidString() {
		return (atype == String.class) &&
		       (def_value == null || def_value instanceof String) &&
		       min_value == null && max_value == null;
	}

	/** Get the attribute name */
	public String aname() {
		return toString().toLowerCase();
	}

	/** Set of all system attributes */
	static protected final HashMap<String, SystemAttrEnum> ALL_ATTRIBUTES =
		new HashMap<String, SystemAttrEnum>();
	static {
		for(SystemAttrEnum sa: SystemAttrEnum.values())
			ALL_ATTRIBUTES.put(sa.aname(), sa);
	}

	/** Lookup an attribute by name */
	static public SystemAttrEnum lookup(String aname) {
		return ALL_ATTRIBUTES.get(aname);
	}

	/** Get the value of the attribute as a string */
	public String getString() {
		assert atype == String.class;
		return (String)get();
	}

	/** Get the default value as a String. */
	public String getDefault() {
		if(def_value != null)
			return def_value.toString();
		else
			return "";
	}

	/** Get the value of the attribute as a boolean */
	public boolean getBoolean() {
		assert atype == Boolean.class;
		return (Boolean)get();
	}

	/** Get the value of the attribute as an int */
	public int getInt() {
		assert atype == Integer.class;
		return (Integer)get();
	}

	/** Get the value of the attribute as a float */
	public float getFloat() {
		assert atype == Float.class;
		return (Float)get();
	}

	/** Get the value of the attribute */
	protected Object get() {
		return getValue(SystemAttributeHelper.get(aname()));
	}

	/** Get the value of a system attribute */
	protected Object getValue(SystemAttribute attr) {
		if(attr == null) {
			System.err.println(warningDefault());
			return def_value;
		}
		return parseValue(attr.getValue());
	}

	/** Get the value of a system attribute */
	public Object parseValue(String v) {
		Object value = parse(v);
		if(value == null) {
			System.err.println(warningParse());
			return def_value;
		}
		return value;
	}

	/** Parse an attribute value */
	protected Object parse(String v) {
		if(atype == String.class)
			return v;
		if(atype == Boolean.class)
			return parseBoolean(v);
		if(atype == Integer.class)
			return parseInteger(v);
		if(atype == Float.class)
			return parseFloat(v);
		assert false;
		return null;
	}

	/** Parse a boolean attribute value */
	protected Boolean parseBoolean(String v) {
		try {
			return Boolean.parseBoolean(v);
		}
		catch(NumberFormatException e) {
			return null;
		}
	}

	/** Parse an integer attribute value */
	protected Integer parseInteger(String v) {
		int i;
		try {
			i = Integer.parseInt(v);
		}
		catch(NumberFormatException e) {
			return null;
		}
		if(min_value != null) {
			int m = min_value.intValue();
			if(i < m) {
				System.err.println(warningMinimum());
				return m;
			}
		}
		if(max_value != null) {
			int m = max_value.intValue();
			if(i > m) {
				System.err.println(warningMaximum());
				return m;
			}
		}
		return i;
	}

	/** Parse a float attribute value */
	protected Float parseFloat(String v) {
		float f;
		try {
			f = Float.parseFloat(v);
		}
		catch(NumberFormatException e) {
			return null;
		}
		if(min_value != null) {
			float m = min_value.floatValue();
			if(f < m) {
				System.err.println(warningMinimum());
				return m;
			}
		}
		if(max_value != null) {
			float m = max_value.floatValue();
			if(f > m) {
				System.err.println(warningMaximum());
				return m;
			}
		}
		return f;
	}

	/** Create a 'missing system attribute' warning message */
	protected String warningDefault() {
		return "Warning: " + toString() + " system attribute was not " +
		       "found; using a default value (" + def_value + ").";
	}

	/** Create a parsing warning message */
	protected String warningParse() {
		return "Warning: " + toString() + " system attribute could " +
		       "be parsed; using a default value (" + def_value + ").";
	}

	/** Create a minimum value warning message */
	protected String warningMinimum() {
		return "Warning: " + toString() + " system attribute was too " +
		       "low; using a minimum value (" + min_value + ").";
	}

	/** Create a maximum value warning message */
	protected String warningMaximum() {
		return "Warning: " + toString() + " system attribute was too " +
		       "high; using a maximum value (" + max_value + ").";
	}
}
