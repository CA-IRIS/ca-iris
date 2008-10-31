/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.tms.utils.Agency;

/**
 * A system attribute is a name mapped to a string value.
 * @see SystemAttributeHelper, SystemAttribute
 * @author Douglas Lau
 * @author Michael Darter
 */
public class SystemAttributeImpl extends BaseObjectImpl 
	implements SystemAttribute 
{
	/** Lookup a SystemAttribute in the SONAR namespace. 
	 *  @return Null if the specified attribute does not exist else the 
	 *  attribute value.
	 */
	static protected SystemAttribute lookup(String att) {
		if(att == null || att.length() <= 0) {
			assert false;
			return null;
		}
		return (SystemAttribute)namespace.lookupObject(
			SystemAttribute.SONAR_TYPE, att);
	}

	/** Load all */
	static protected void loadAll() throws TMSException {
		System.err.println("Loading system attributes...");
		namespace.registerType(SONAR_TYPE, 
			SystemAttributeImpl.class);
		store.query("SELECT name, value FROM system_attribute;",
			new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.add(new SystemAttributeImpl(
					row.getString(1), // name
					row.getString(2)  // value
				));
			}
		});

		// validiate essential system attributes, shutdown if invalid
		SystemAttributeHelper.validateDatabaseVersion();
		SystemAttributeHelper.validateAgencyId();
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("value", value);
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new attribute */
	public SystemAttributeImpl(String n) {
		super(n);
	}

	/** Create a new attribute */
	protected SystemAttributeImpl(String att_name, String arg_value) {
		super(att_name);
		value = arg_value;
	}

	/** attribute value */
	protected String value;

	/** Set the attribute value */
	public void setValue(String arg_value) {
		value = arg_value;
	}

	/** Set the attribute value, doSet is required for 
	 *  database backed sonar objects
	 */
	public void doSetValue(String arg_value) throws TMSException {
		//System.err.println("SystemAttributeImpl.doSetValue("+arg_value+") called.");
		if(arg_value == null)
			return;
		if(value.equals(arg_value))
			return;
		store.update(this, "value", arg_value);
		setValue(arg_value);
	}

	/** Get the attribute value */
	public String getValue() {
		return value;
	}
}
