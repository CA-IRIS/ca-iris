/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.dms;

import java.util.ArrayList;
import java.util.HashMap;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DmsSignGroup;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for sign groups.
 *
 * @author Douglas Lau
 */
public class SignGroupTableModel extends ProxyTableModel<SignGroup> {

	/** Create the columns in the model */
	protected ArrayList<ProxyColumn<SignGroup>> createColumns() {
		ArrayList<ProxyColumn<SignGroup>> cols =
			new ArrayList<ProxyColumn<SignGroup>>(2);
		cols.add(new ProxyColumn<SignGroup>("dms.group", 120) {
			public Object getValueAt(SignGroup sg) {
				return sg.getName();
			}
			public boolean isEditable(SignGroup sg) {
				return (sg == null) && canAdd();
			}
			public void setValueAt(SignGroup sg, Object value) {
				String v = value.toString().trim();
				if(v.length() > 0)
					createSignGroup(v);
			}
		});
		cols.add(new ProxyColumn<SignGroup>("dms.group.member", 50,
			Boolean.class)
		{
			public Object getValueAt(SignGroup sg) {
				return isSignGroupMember(sg);
			}
			public boolean isEditable(SignGroup sg) {
				return canEditDmsSignGroup(sg);
			}
			public void setValueAt(SignGroup sg, Object value) {
				if(value instanceof Boolean) {
					Boolean b = (Boolean)value;
					if(b.booleanValue())
						createDmsSignGroup(sg);
					else
						destroyDmsSignGroup(sg);
				}
			}
		});
		return cols;
	}

	/** Lookup a DMS sign group */
	private DmsSignGroup lookupDmsSignGroup(SignGroup group) {
		for(DmsSignGroup g: dms_sign_groups) {
			if(g.getSignGroup() == group && g.getDms() == dms)
				return g;
		}
		return null;
	}

	/** Test if the DMS is a member of a sign group */
	protected boolean isSignGroupMember(SignGroup group) {
		return lookupDmsSignGroup(group) != null;
	}

	/** Check if a sign group should be listed */
	protected boolean isListed(SignGroup group) {
		if(!group.getLocal())
			return true;
		else
			return dms.getName().equals(group.getName());
	}

	/** Add a new proxy to the table model */
	protected int doProxyAdded(SignGroup proxy) {
		if(isListed(proxy))
			return super.doProxyAdded(proxy);
		else
			return -1;
	}

	/** DMS identifier */
	protected final DMS dms;

	/** DMS sign group type cache */
	private final TypeCache<DmsSignGroup> dms_sign_groups;

	/** Listener for DMS sign group proxies */
	protected final ProxyListener<DmsSignGroup> listener;

	/** 
	 * Create a new sign group table model.
	 * @param s Session
	 * @param dms DMS proxy object.
	 */
	public SignGroupTableModel(Session s, DMS proxy) {
		super(s, s.getSonarState().getDmsCache().getSignGroups());
		dms = proxy;
		dms_sign_groups =
			s.getSonarState().getDmsCache().getDmsSignGroups();
		final SignGroupTableModel model = this;
		listener = new ProxyListener<DmsSignGroup>() {
			public void proxyAdded(DmsSignGroup proxy) {
				model.proxyChanged(proxy.getSignGroup(),
					"member");
			}
			public void enumerationComplete() { }
			public void proxyRemoved(DmsSignGroup proxy) {
				model.proxyChanged(proxy.getSignGroup(),
					"member");
			}
			public void proxyChanged(DmsSignGroup proxy, String a) {
				// NOTE: this should never happen
				model.proxyChanged(proxy.getSignGroup(), a);
			}
		};
		dms_sign_groups.addProxyListener(listener);
	}

	/** Dispose of the proxy table model */
	public void dispose() {
		dms_sign_groups.removeProxyListener(listener);
		super.dispose();
	}

	/** Check if the user is allowed to add / destroy a DMS sign group */
	protected boolean canEditDmsSignGroup(SignGroup g) {
		return g != null && canAddAndRemove(createDmsSignGroupName(g));
	}

	/** Create a DMS sign group name */
	protected String createDmsSignGroupName(SignGroup sg) {
		return sg.getName() + "_" + dms.getName();
	}

	/** Check if the user can add and remove the specified name */
	protected boolean canAddAndRemove(String oname) {
		return session.canAdd(DmsSignGroup.SONAR_TYPE, oname) &&
		       session.canRemove(DmsSignGroup.SONAR_TYPE, oname);
	}

	/** Get the SONAR type name */
	protected String getSonarType() {
		return SignGroup.SONAR_TYPE;
	}

	/** Create a new sign group */
	protected void createSignGroup(String name) {
		boolean local = name.equals(dms.getName());
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("local", local);
		cache.createObject(name, attrs);
	}

	/** Create a new DMS sign group */
	protected void createDmsSignGroup(SignGroup g) {
		String oname = createDmsSignGroupName(g);
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("dms", dms);
		attrs.put("sign_group", g);
		dms_sign_groups.createObject(oname, attrs);
	}

	/** Destroy a DMS sign group */
	protected void destroyDmsSignGroup(SignGroup g) {
		DmsSignGroup dsg = lookupDmsSignGroup(g);
		if(dsg != null)
			dsg.destroy();
	}
}
