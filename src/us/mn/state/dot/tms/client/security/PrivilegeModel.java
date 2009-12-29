/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.security;

import java.util.HashMap;
import us.mn.state.dot.sonar.Name;
import us.mn.state.dot.sonar.Privilege;
import us.mn.state.dot.sonar.Role;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for IRIS privileges
 *
 * @author Douglas Lau
 */
public class PrivilegeModel extends ProxyTableModel<Privilege> {

	/** Create the columns in the model */
	protected ProxyColumn[] createColumns() {
	    // NOTE: half-indent to declare array
	    return new ProxyColumn[] {
		new ProxyColumn<Privilege>("Pattern", 420) {
			public Object getValueAt(Privilege p) {
				return p.getPattern();
			}
			public boolean isEditable(Privilege p) {
				return (p == null && canAdd()) || canUpdate(p);
			}
			public void setValueAt(Privilege p, Object value) {
				String v = value.toString().trim();
				if(p == null)
					createPrivilege(v);
				else
					p.setPattern(v);
			}
		},
		new ProxyColumn<Privilege>("Read", 80, Boolean.class) {
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
		},
		new ProxyColumn<Privilege>("Write", 80, Boolean.class) {
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
		},
		new ProxyColumn<Privilege>("Create", 80, Boolean.class) {
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
		},
		new ProxyColumn<Privilege>("Delete", 80, Boolean.class) {
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
		}
	    };
	}

	/** Role associated with privileges */
	protected final Role role;

	/** Create a new privilege table model */
	public PrivilegeModel(Session s, Role r) {
		super(s, s.getSonarState().getPrivileges());
		role = r;
	}

	/** Add a new proxy to the table model */
	protected int doProxyAdded(Privilege proxy) {
		if(proxy.getRole() == role)
			return super.doProxyAdded(proxy);
		else
			return -1;
	}

	/** Create a new privilege */
	protected void createPrivilege(String p) {
		String name = createUniqueName();
		if(name != null) {
			HashMap<String, Object> attrs =
				new HashMap<String, Object>();
			attrs.put("role", role);
			attrs.put("pattern", p);
			cache.createObject(name, attrs);
		}
	}

	/** Create a unique privilege name */
	protected String createUniqueName() {
		for(int uid = 1; uid <= 9999; uid++) {
			String n = "PRV_" + uid;
			if(cache.lookupObject(n) == null)
				return n;
		}
		assert false;
		return null;
	}

	/** Check if the user can add a privilege */
	public boolean canAdd() {
		return namespace.canAdd(user, new Name(Privilege.SONAR_TYPE,
			"oname"));
	}
}
