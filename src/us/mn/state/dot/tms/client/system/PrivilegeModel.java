/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.system;

import java.util.ArrayList;
import java.util.HashMap;
import us.mn.state.dot.sonar.Capability;
import us.mn.state.dot.sonar.Privilege;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;

/**
 * Table model for IRIS privileges.
 *
 * @author Douglas Lau
 */
public class PrivilegeModel extends ProxyTableModel2<Privilege> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<Privilege>> createColumns() {
		ArrayList<ProxyColumn<Privilege>> cols =
			new ArrayList<ProxyColumn<Privilege>>(5);
		cols.add(new ProxyColumn<Privilege>("privilege.pattern", 300) {
			public Object getValueAt(Privilege p) {
				return p.getPattern();
			}
			public boolean isEditable(Privilege p) {
				return canUpdate(p);
			}
			public void setValueAt(Privilege p, Object value) {
				String v = value.toString().trim();
				p.setPattern(v);
			}
		});
		cols.add(new ProxyColumn<Privilege>("privilege.read", 60,
			Boolean.class)
		{
			public Object getValueAt(Privilege p) {
				return p.getPrivR();
			}
			public boolean isEditable(Privilege p) {
				return canUpdate(p);
			}
			public void setValueAt(Privilege p, Object value) {
				if(value instanceof Boolean)
					p.setPrivR((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<Privilege>("privilege.write", 60,
			Boolean.class)
		{
			public Object getValueAt(Privilege p) {
				return p.getPrivW();
			}
			public boolean isEditable(Privilege p) {
				return canUpdate(p);
			}
			public void setValueAt(Privilege p, Object value) {
				if(value instanceof Boolean)
					p.setPrivW((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<Privilege>("privilege.create", 60,
			Boolean.class)
		{
			public Object getValueAt(Privilege p) {
				return p.getPrivC();
			}
			public boolean isEditable(Privilege p) {
				return canUpdate(p);
			}
			public void setValueAt(Privilege p, Object value) {
				if(value instanceof Boolean)
					p.setPrivC((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<Privilege>("privilege.del", 60,
			Boolean.class)
		{
			public Object getValueAt(Privilege p) {
				return p.getPrivD();
			}
			public boolean isEditable(Privilege p) {
				return canUpdate(p);
			}
			public void setValueAt(Privilege p, Object value) {
				if(value instanceof Boolean)
					p.setPrivD((Boolean)value);
			}
		});
		return cols;
	}

	/** Capability associated with privileges */
	private final Capability capability;

	/** Create a new privilege table model */
	public PrivilegeModel(Session s, Capability c) {
		super(s, s.getSonarState().getPrivileges(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      false);	/* has_name */
		capability = c;
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return Privilege.SONAR_TYPE;
	}

	/** Check if a proxy is included in the list */
	@Override
	protected boolean check(Privilege proxy) {
		return proxy.getCapability() == capability;
	}

	/** Create an object with the given name */
	@Override
	public void createObject(String n) {
		String name = createUniqueName();
		if (name != null) {
			HashMap<String, Object> attrs =
				new HashMap<String, Object>();
			attrs.put("capability", capability);
			attrs.put("pattern", "");
			cache.createObject(name, attrs);
		}
	}

	/** Create a unique privilege name */
	private String createUniqueName() {
		for (int uid = 1; uid <= 9999; uid++) {
			String n = "PRV_" + uid;
			if (cache.lookupObject(n) == null)
				return n;
		}
		assert false;
		return null;
	}
}
