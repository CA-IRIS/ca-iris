/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2012  Minnesota Department of Transportation
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

import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for IRIS users
 *
 * @author Douglas Lau
 */
public class UserModel extends ProxyTableModel<User> {

	/** Create the columns in the model */
	protected ProxyColumn[] createColumns() {
	    // NOTE: half-indent to declare array
	    return new ProxyColumn[] {
		new ProxyColumn<User>("user", 100) {
			public Object getValueAt(User u) {
				return u.getName();
			}
			public boolean isEditable(User u) {
				return u == null && canAdd();
			}
			public void setValueAt(User u, Object value) {
				String v = value.toString().trim();
				if(v.length() > 0)
					cache.createObject(v);
			}
		}
	    };
	}

	/** Create a new user table model */
	public UserModel(Session s) {
		super(s, s.getSonarState().getUsers());
	}

	/** Get the SONAR type name */
	protected String getSonarType() {
		return User.SONAR_TYPE;
	}
}
