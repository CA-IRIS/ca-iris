/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2011  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import us.mn.state.dot.sonar.server.ServerNamespace;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.SignText;
import us.mn.state.dot.tms.TMSException;

/**
 * Sign text contains the properties of a single line MULTI string for display
 * on a dynamic message sign (DMS).
 *
 * @author Douglas Lau
 */
public class SignTextImpl extends BaseObjectImpl implements SignText {

	/** Sign message text validation regex pattern */
	static protected final Pattern MESS_PATTERN = Pattern.compile(
		"[0-9A-Z !#$%&()*+,-./:;<=>?'@]*");

	/** Validate a message string */
	static protected void validateMessage(String t)
		throws ChangeVetoException
	{
		Matcher m = MESS_PATTERN.matcher(t);
		if(!m.matches())
			throw new ChangeVetoException("Invalid message: " + t);
		if(t.length() > 24)
			throw new ChangeVetoException("Message too wide");
	}

	/** Load all the sign text */
	static protected void loadAll() throws TMSException {
		System.err.println("Loading sign text...");
		namespace.registerType(SONAR_TYPE, SignTextImpl.class);
		store.query("SELECT name, sign_group, line, multi, priority" +
			" FROM iris." + SONAR_TYPE + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new SignTextImpl(namespace,
					row.getString(1),	// name
					row.getString(2),	// sign_group
					row.getShort(3),	// line
					row.getString(4),	// multi
					row.getShort(5)		// priority
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("sign_group", sign_group);
		map.put("line", line);
		map.put("multi", multi);
		map.put("priority", priority);
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

	/** Create a new sign text message */
	public SignTextImpl(String n) {
		super(n);
	}

	/** Create a new sign text message */
	public SignTextImpl(String n, SignGroup g, short l, String m, short p) {
		super(n);
		sign_group = g;
		line = l;
		multi = m;
		priority = p;
	}

	/** Create a new sign text message */
	protected SignTextImpl(ServerNamespace ns, String n, String g, short l,
		String m, short p)
	{
		this(n, (SignGroupImpl)ns.lookupObject("sign_group", g), l,m,p);
	}

	/** Sign group */
	protected SignGroup sign_group;

	/** Get the sign group */
	public SignGroup getSignGroup() {
		return sign_group;
	}

	/** Line number on sign (usually 1-3) */
	protected short line;

	/** Set the line */
	public void setLine(short l) {
		line = l;
	}

	/** Set the line */
	public void doSetLine(short l) throws TMSException {
		if(l == line)
			return;
		store.update(this, "line", l);
		setLine(l);
	}

	/** Get the line */
	public short getLine() {
		return line;
	}

	/** MULTI string */
	protected String multi;

	/** Set the MULTI string */
	public void setMulti(String m) {
		multi = m;
	}

	/** Set the MULTI string */
	public void doSetMessage(String m) throws TMSException {
		if(m.equals(multi))
			return;
		validateMessage(m);
		store.update(this, "multi", m);
		setMulti(m);
	}

	/** Get the MULTI string */
	public String getMulti() {
		return multi;
	}

	/** Message ordering priority */
	protected short priority;

	/** Set the priority */
	public void setPriority(short p) {
		priority = p;
	}

	/** Set the priority */
	public void doSetPriority(short p) throws TMSException {
		if(p == priority)
			return;
		store.update(this, "priority", p);
		setPriority(p);
	}

	/** Get the priority */
	public short getPriority() {
		return priority;
	}
}
