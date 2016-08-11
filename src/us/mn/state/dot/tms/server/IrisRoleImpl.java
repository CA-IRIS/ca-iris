/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2010  Minnesota Department of Transportation
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
import java.util.TreeSet;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.sonar.Capability;
import us.mn.state.dot.sonar.server.ServerNamespace;
import us.mn.state.dot.sonar.server.RoleImpl;

/**
 * IRIS role
 *
 * @author Douglas lau
 */
public class IrisRoleImpl extends RoleImpl implements Comparable<IrisRoleImpl>,
	Storable
{
	/** SQL connection to database */
	static protected SQLConnection store;

	/** Role/Capability table mapping */
	static protected TableMapping mapping;

	/** Lookup all the roles */
	static public void lookup(SQLConnection c, final ServerNamespace ns)
		throws TMSException
	{
		store = c;
		mapping = new TableMapping(store, "iris", "role", "capability");
		store.query("SELECT name, enabled FROM iris.role;",
			new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				ns.addObject(new IrisRoleImpl(ns,
					row.getString(1),	// name
					row.getBoolean(2)	// enabled
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("enabled", enabled);
		return map;
	}

	/** Store an object */
	public void doStore() throws TMSException {
		store.create(this);
	}

	/** Get the database table name */
	public String getTable() {
		return "iris.role";
	}

	/** Create a new IRIS role */
	public IrisRoleImpl(String n) {
		super(n);
	}

	/** Create an IRIS role from database lookup */
	protected IrisRoleImpl(ServerNamespace ns, String n, boolean e)
		throws TMSException
	{
		this(n);
		enabled = e;
		TreeSet<IrisCapabilityImpl> caps =
			new TreeSet<IrisCapabilityImpl>();
		for(Object o: mapping.lookup("role", this)) {
			caps.add((IrisCapabilityImpl)ns.lookupObject(
				"capability", (String)o));
		}
		capabilities = caps.toArray(new IrisCapabilityImpl[0]);
	}

	/** Compare to another role */
	public int compareTo(IrisRoleImpl o) {
		return name.compareTo(o.name);
	}

	/** Test if the role equals another role */
	public boolean equals(Object o) {
		if(o instanceof IrisRoleImpl)
			return name.equals(((IrisRoleImpl)o).name);
		else
			return false;
	}

	/** Calculate a hash code */
	public int hashCode() {
		return name.hashCode();
	}

	/** Get the primary key name */
	public String getKeyName() {
		return "name";
	}

	/** Get the primary key */
	public String getKey() {
		return name;
	}

	/** Get a string representation of the object */
	public String toString() {
		return name;
	}

	/** Destroy an IRIS role */
	public void doDestroy() throws TMSException {
		store.destroy(this);
	}

	/** Set the capabilities assigned to the role */
	public void doSetCapabilities(Capability[] caps) throws TMSException {
		TreeSet<Storable> cset = new TreeSet<Storable>();
		for(Capability c: caps) {
			if(c instanceof IrisCapabilityImpl)
				cset.add((IrisCapabilityImpl)c);
			else
				throw new ChangeVetoException("Bad capability");
		}
		mapping.update("role", this, cset);
		super.setCapabilities(caps);
	}

	/** Set the enabled flag */
	public void doSetEnabled(boolean e) throws TMSException {
		if(e == enabled)
			return;
		store.update(this, "enabled", e);
		super.setEnabled(e);
	}
}
