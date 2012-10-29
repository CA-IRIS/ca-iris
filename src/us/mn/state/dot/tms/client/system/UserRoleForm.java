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

import javax.swing.JTabbedPane;
import us.mn.state.dot.sonar.Capability;
import us.mn.state.dot.sonar.Privilege;
import us.mn.state.dot.sonar.Role;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.AbstractForm;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A form for displaying and editing the users and roles
 *
 * @author Douglas Lau
 */
public class UserRoleForm extends AbstractForm {

	/** Check if the user is permitted to use the form */
	static public boolean isPermitted(Session s) {
		return s.canUpdate(User.SONAR_TYPE) ||
		       s.canUpdate(Role.SONAR_TYPE) ||
		       s.canUpdate(Capability.SONAR_TYPE) ||
		       s.canUpdate(Privilege.SONAR_TYPE);
	}

	/** Tabbed pane */
	protected final JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);

	/** User tab panel */
	protected final UserTabPanel u_panel;

	/** Role panel */
	protected final RolePanel r_panel;

	/** Capability panel */
	protected final CapabilityPanel cap_panel;

	/** Connection panel */
	protected final ConnectionPanel c_panel;

	/** Create a new user role form */
	public UserRoleForm(Session s) {
		super(I18N.get("user.menu"));
		setHelpPageName("help.userroleform");
		u_panel = new UserTabPanel(s);
		r_panel = new RolePanel(s);
		cap_panel = new CapabilityPanel(s);
		c_panel = new ConnectionPanel(s);
	}

	/** Initializze the widgets in the form */
	protected void initialize() {
		u_panel.initialize();
		r_panel.initialize();
		cap_panel.initialize();
		c_panel.initialize();
		tab.add(I18N.get("user.plural"), u_panel);
		tab.add(I18N.get("role.plural"), r_panel);
		tab.add(I18N.get("capability.plural"), cap_panel);
		tab.add(I18N.get("connection.plural"), c_panel);
		add(tab);
	}

	/** Close the form */
	protected void close() {
		super.close();
		u_panel.dispose();
		r_panel.dispose();
		cap_panel.dispose();
		c_panel.dispose();
	}
}
