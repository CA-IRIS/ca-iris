/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2012  Minnesota Department of Transportation
 * Copyright (C) 2012  Iteris Inc.
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
package us.mn.state.dot.tms.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.TMSException;

/**
 * A sign message represents a message which can be displayed on a dynamic
 * message sign (DMS). It contains the text associated with the message and a
 * bitmap for each page of the message.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class SignMessageImpl extends BaseObjectImpl implements SignMessage {

	/** Last allocated system message ID */
	static protected int last_id = 0;

	/** Create a unique sign message name */
	static protected synchronized String createUniqueName() {
		String n = createNextName();
		while(namespace.lookupObject(SONAR_TYPE, n) != null)
			n = createNextName();
		return n;
	}

	/** Create the next system message name */
	static protected String createNextName() {
		last_id++;
		// Check if the ID has rolled over to negative numbers
		if(last_id < 0)
			last_id = 0;
		return "system_" + last_id;
	}

	/** Load all the sign messages */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, SignMessageImpl.class);
		store.query("SELECT name, multi, bitmaps, a_priority, " +
			"r_priority, scheduled, duration FROM " +
			"iris.sign_message;", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new SignMessageImpl(
					row.getString(1),	// name
					row.getString(2),	// multi
					row.getString(3),	// bitmaps
					row.getInt(4),		// a_priority
					row.getInt(5),		// r_priority
					row.getBoolean(6),	// scheduled
					(Integer)row.getObject(7) // duration
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("multi", multi);
		map.put("bitmaps", bitmaps);
		map.put("a_priority", activationPriority);
		map.put("r_priority", runTimePriority);
		map.put("scheduled", scheduled);
		map.put("duration", duration);
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new sign message (by SONAR clients) */
	public SignMessageImpl(String n) {
		super(n);
	}

	/** Create a sign message */
	protected SignMessageImpl(String n, String m, String b, int ap, int rp,
		boolean s, Integer d)
	{
		super(n);
		multi = m;
		bitmaps = b;
		activationPriority = ap;
		runTimePriority = rp;
		scheduled = s;
		duration = d;		
	}

	/** Create a new sign message (by IRIS) */
	public SignMessageImpl(String m, String b, DMSMessagePriority ap,
		DMSMessagePriority rp, boolean s, Integer d)
	{
		super(createUniqueName());
		multi = m;
		bitmaps = b;
		activationPriority = ap.ordinal();
		runTimePriority = rp.ordinal();
		scheduled = s;
		duration = d;
	}

	/** Message MULTI string, contains message text for all pages */
	protected String multi;

	/** Get the message MULTI string.
	 * @return Message text in MULTI markup.
	 * @see us.mn.state.dot.tms.MultiString */
	public String getMulti() {
		return multi;
	}

	/** Bitmap data for each page (Base64-encoded) */
	protected String bitmaps;

	/** Get the bitmaps for all pages of the message.
	 * @return Base64-encoded bitmap data.
	 * @see us.mn.state.dot.tms.Base64 */
	public String getBitmaps() {
		return bitmaps;
	}

	/** Message activation priority */
	protected int activationPriority;

	/** Get the activation priority.
	 * @return Priority ranging from 1 (low) to 255 (high).
	 * @see us.mn.state.dot.tms.DMSMessagePriority */
	public int getActivationPriority() {
		return activationPriority;
	}

	/** Run-time priority */
	protected int runTimePriority;

	/** Get the run-time priority.
	 * @return Run-time priority ranging from 1 (low) to 255 (high).
	 * @see us.mn.state.dot.tms.DMSMessagePriority */
	public int getRunTimePriority() {
		return runTimePriority;
	}

	/** Scheduled flag */
	protected boolean scheduled;

	/** Get the scheduled flag.
	 * @return True if the message was scheduled. */
	public boolean getScheduled() {
		return scheduled;
	}

	/** Duration of this message (minutes) */
	protected Integer duration;

	/** Get the message duration.
	 * @return Duration in minutes; null means indefinite. */
	public Integer getDuration() {
		return duration;
	}

	/** Render the SignMessage object as xml */
	public void printXml(PrintWriter out, DMSImpl dms) {
		out.print("<sign_message");
		out.print(XmlWriter.createAttribute("dms", dms.getName()));
		out.print(XmlWriter.createAttribute("status",
			DMSHelper.getAllStyles(dms)));
		out.print(XmlWriter.createAttribute("run_priority", 
			getRunTimePriority()));
		out.print(XmlWriter.createAttribute("act_priority", 
			getActivationPriority()));
		out.print(XmlWriter.createAttribute("scheduled", 
			getScheduled()));
		out.print(XmlWriter.createAttribute("duration", 
			getDuration()));
		out.print(XmlWriter.createAttribute("multi", multi));
		out.print(XmlWriter.createAttribute("bitmaps", 
			getBitmaps()));
		out.println("/>");
	}
}
