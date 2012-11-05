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

import java.util.ArrayList;
import us.mn.state.dot.sonar.Capability;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for IRIS capabilities
 *
 * @author Douglas Lau
 */
public class CapabilityModel extends ProxyTableModel<Capability> {

	/** Create the columns in the model */
	protected ArrayList<ProxyColumn<Capability>> createColumns() {
		ArrayList<ProxyColumn<Capability>> cols =
			new ArrayList<ProxyColumn<Capability>>(2);
		cols.add(new ProxyColumn<Capability>("capability.name", 120) {
			public Object getValueAt(Capability c) {
				return c.getName();
			}
			public boolean isEditable(Capability c) {
				return c == null && canAdd();
			}
			public void setValueAt(Capability c, Object value) {
				String v = value.toString().trim();
				if(v.length() > 0)
					cache.createObject(v);
			}
		});
		cols.add(new ProxyColumn<Capability>("capability.enabled", 60,
			Boolean.class)
		{
			public Object getValueAt(Capability c) {
				return c.getEnabled();
			}
			public boolean isEditable(Capability c) {
				return canUpdate(c);
			}
			public void setValueAt(Capability c, Object value) {
				if(value instanceof Boolean)
					c.setEnabled((Boolean)value);
			}
		});
		return cols;
	}

	/** Create a new capability table model */
	public CapabilityModel(Session s) {
		super(s, s.getSonarState().getCapabilities());
	}

	/** Get the SONAR type name */
	protected String getSonarType() {
		return Capability.SONAR_TYPE;
	}
}
