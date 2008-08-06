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
package us.mn.state.dot.tms.client.dms;

import java.util.HashMap;
import java.util.HashSet;
import us.mn.state.dot.sonar.Checker;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.client.TmsConnection;
import us.mn.state.dot.tms.DmsSignGroup;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.SignText;
import us.mn.state.dot.tms.utils.SDMS;

/**
 * Model for sign text messages. This object is instantiated and contained by
 * MessageSelector. One SignMessageModel is associated with a single DMS.
 * It creates and contains SignTextComboBoxModel objects for each combobox 
 * in MessageSelector. This object listens for changes to sign_text and 
 * dms_sign_groups and is responsible for updating its model accordingly. 
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class SignMessageModel implements ProxyListener<DmsSignGroup> {

	/** DMS id associated with this object */
	protected final String dms_id;

	/** DMS sign group type cache, relates dms to sign groups */
	protected final TypeCache<DmsSignGroup> dms_sign_groups;

	/** Sign text type cache, list of all sign text lines */
	protected final TypeCache<SignText> sign_text;

	/** Listener for sign text proxies */
	protected final ProxyListener<SignText> listener;

	/** Tms Connection */
	protected TmsConnection m_tmsConnection;

	/** Create a new sign message model */
	public SignMessageModel(String dms, TypeCache<DmsSignGroup> d,
		TypeCache<SignText> t, TmsConnection tmsConnection)
	{
		dms_id = dms;
		dms_sign_groups = d;
		sign_text = t;
		m_tmsConnection = tmsConnection;
		listener = new ProxyListener<SignText>() {
			public void proxyAdded(SignText proxy) {
				if(isMember(proxy.getSignGroup()))
					addSignText(proxy);
			}
			public void enumerationComplete() { }
			public void proxyRemoved(SignText proxy) {
				if(isMember(proxy.getSignGroup()))
					removeSignText(proxy);
			}
			public void proxyChanged(SignText proxy, String a) {
				if(isMember(proxy.getSignGroup()))
					changeSignText(proxy);
			}
		};
	}

	/** Initialize the sign message model */
	public void initialize() {
		dms_sign_groups.addProxyListener(this);
		sign_text.addProxyListener(listener);
	}

	/** Dispose of the model */
	public void dispose() {
		sign_text.removeProxyListener(listener);
		dms_sign_groups.removeProxyListener(this);
	}

	/** Add a new proxy to the model */
	public void proxyAdded(DmsSignGroup proxy) {
		if(dms_id.equals(proxy.getDms()))
			addGroup(proxy.getSignGroup());
	}

	/** Enumeration of the proxy type is complete */
	public void enumerationComplete() {
		// We're not interested
	}

	/** Remove a proxy from the model */
	public void proxyRemoved(DmsSignGroup proxy) {
		if(dms_id.equals(proxy.getDms()))
			removeGroup(proxy.getSignGroup());
	}

	/** Change a proxy in the model */
	public void proxyChanged(DmsSignGroup proxy, String attrib) {
		// NOTE: this should never happen
	}

	/** Set of DMS member groups */
	protected final HashSet<String> groups = new HashSet<String>();

	/** Is the DMS a member of the specified group? */
	protected boolean isMember(SignGroup g) {
		return g != null && groups.contains(g.getName());
	}

	/** 
	 * Get the DMS SignGroup with the same name as the DMS id.
	 * @return the SignGroup else null if it doesn't exist.
	 */
	protected SignGroup getIdentitySignGroup() {
		if (dms_sign_groups==null || dms_id==null)
			return null;
		// find the DmsSignGroup for the identity SignGroup. The
		// identity sign group has the same name as the dms, e.g. V1.
		DmsSignGroup dsg = dms_sign_groups.find(new Checker() {
			public boolean check(SonarObject o) {
				if(o instanceof DmsSignGroup) {
					DmsSignGroup g = (DmsSignGroup)o;
					if(g.getDms().equals(dms_id) && 
						g.getSignGroup().getName().equals(dms_id) )
						return true;
				}
				return false;
			}
		});
		if (dsg==null)
			return null;
		SignGroup sg = dsg.getSignGroup();
		return sg;
	}

	/** 
	  * Create a new sign text and add to the persistent sign text library.
	  * @param sg SignGroup the new message will be associated with.
	  * @param line Combobox line number.
	  * @param message Line text.
	  * @param priority line priority
	  */
	protected void createSignText(SignGroup sg,short line,String messarg,short priority) {
		//System.err.println("SignMessageModel.createSignText("+line+","+messarg+","+priority+") called. admin="+m_tmsConnection.isAdmin());
		if (sg==null || line<1 || messarg==null || messarg.length()<=0)
			return;

		// FIXME: this is a hack, see comments in method
		if(SDMS.ignoreLineHack(messarg))
			return;

		// validate message
		String mess=SDMS.getValidText(messarg);

		// insert into library
		String name = createUniqueSignTextName(sg);
		if(name == null)
			return;
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("sign_group", sg);
		attrs.put("line", new Short(line));
		attrs.put("message", mess);
		attrs.put("priority", new Short(priority));
		sign_text.createObject(name, attrs);
		//System.err.println("SignMessageModel.createSignText() returning.");
	}

	/** 
	 * Create a HashSet which contains all SignText names for this sign.
	 * @return A HashSet with entries as SignText names, e.g. V1_23
	 */
	private HashSet<String> createSignTextNameSet(SignGroup sg) {
		if (sg==null)
			return null;

		final String sgname=sg.getName();
		final HashSet<String> names=new HashSet<String>();

		// cycle through all SignTexts
		sign_text.find(new Checker() {
			public boolean check(SonarObject o) {
				if(o instanceof SignText) {
					SignText st = (SignText)o;
					if(!st.getSignGroup().getName().equals(sgname))
						return false;
					names.add(st.getName());
				}
				return false;
			}
		});
		return names;
	}

	/** Create a SignText name given a sign group name and unique id */
	private String buildSignTextName(String sign_group_name,int id) {
		if (sign_group_name==null)
			return "";
		return sign_group_name + "_" + id;
	}

	/** 
	 * Create a SignText name, which is in this form: 
	 *    sign_group.name + "_" + uniqueid
	 *    where uniqueid is a sequential integer.
	 * @return A unique string for a new SignText entry, e.g. V1_23
	 */
	private String createUniqueSignTextName(SignGroup sg) {
		if (sg==null || sg.getName()==null)
			return null;
		final HashSet<String> names=createSignTextNameSet(sg);
		for(int i = 0; i<10000; i++) {
			String n = buildSignTextName(sg.getName(),i);
			if(!names.contains(n))
				return n;
		}
		String msg="Warning: something is wrong in SignMessageModel.createUniqueSignTextName().";
		System.err.println(msg);
		assert false : msg;
		return null;
	}

	/** 
	 * Lookup a SignText in the database.
	 * @parm line Message line number (1 based)
	 * @return the matching SignText else null if it doesn't exist.
	 */
	protected SignText lookupSignText(final short line,final String msg,final SignGroup sg) {
		if (sign_text==null || msg==null || sg==null || line<1)
			return null;
		SignText ret = sign_text.find(new Checker() {
			public boolean check(SonarObject o) {
				if(o instanceof SignText) {
					SignText st = (SignText)o;
					if(st.getLine()!=line)
						return false;
					if(!st.getSignGroup().getName().equals(sg.getName()))
						return false;
					if(!st.getMessage().equals(msg))
						return false;
					return true;
				}
				return false;
			}
		});
		return ret;
	}

	/** 
	  * Called when the DMS associated with this object is added to a
	  * new sign group. New SignText lines from the new sign group are
	  * added to each SignTextComboBoxModel.
	  */
	protected void addGroup(final SignGroup g) {
		groups.add(g.getName());
		// add new sign text lines in new group to combobox models
		sign_text.find(new Checker() {
			public boolean check(SonarObject o) {
				if(o instanceof SignText) {
					SignText t = (SignText)o;
					if(t.getSignGroup() == g)
						addSignText(t);
				}
				return false;
			}
		});
	}

	/** 
	  * Called when the DMS associated with this object is removed
	  * from a sign group. 
	  */
	protected void removeGroup(final SignGroup g) {
		groups.remove(g.getName());
		// delete lines from combobox models associated with sign group
		sign_text.find(new Checker() {
			public boolean check(SonarObject o) {
				if(o instanceof SignText) {
					SignText t = (SignText)o;
					if(t.getSignGroup() == g)
						removeSignText(t);
				}
				return false;
			}
		});
	}

	/** Mapping of line numbers to combo box models */
	protected final HashMap<Short, SignTextComboBoxModel> lines =
		new HashMap<Short, SignTextComboBoxModel>();

	/** Get the combobox line model for the specified line */
	public SignTextComboBoxModel getLineModel(short line) {
		if(lines.containsKey(line))
			return lines.get(line);
		else {
			SignTextComboBoxModel m = new SignTextComboBoxModel(line,this,m_tmsConnection);
			lines.put(line, m);
			return m;
		}
	}

	/** Get the maximum line number */
	public short getMaxLine() {
		short m = 0;
		for(short i: lines.keySet())
			m = (short)Math.max(i, m);
		return m;
	}

	/** Add a sign message to the model, called by listener when sign_text changes */
	protected void addSignText(SignText t) {
		//System.err.println("SignMessageModel.addSignText("+t.getMessage()+") called. Line="+t.getLine());
		short line = t.getLine();
		SignTextComboBoxModel m = getLineModel(line);
		m.add(t);
	}

	/** Remove a sign message from the model, called by listener when sign_text changes */
	protected void removeSignText(SignText t) {
		//System.err.println("SignMessageModel.removeSignText("+t.getMessage()+") called. Line="+t.getLine());
		short line = t.getLine();
		SignTextComboBoxModel m = getLineModel(line);
		m.remove(t);
	}

	/** Change a sign message in the model, called by listener when sign_text changes */
	protected void changeSignText(SignText t) {
		//System.err.println("SignMessageModel.changeSignText("+t.getMessage()+") called. Line="+t.getLine());

		// note: this didn't work, the new value wasn't being added back to the cbox
		//       after the old value was deleted.
		//for(SignTextComboBoxModel m: lines.values())
		//	m.change(t);

		// iterate through all combobox models because the line
		// may have changed, moving it between comboboxes
		for(SignTextComboBoxModel m: lines.values())
			m.remove(t);
		// add to associated model
		this.addSignText(t);
	}
}
