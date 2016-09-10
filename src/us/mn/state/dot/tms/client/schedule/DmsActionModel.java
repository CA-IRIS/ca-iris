/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;

import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.DmsAction;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.DmsSignGroup;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.PlanPhaseHelper;
import us.mn.state.dot.tms.QuickMessageHelper;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.SignGroupHelper;
import us.mn.state.dot.tms.utils.I18N;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;
import us.mn.state.dot.tms.client.widget.IComboBoxModel;

/**
 * Table model for DMS actions assigned to action plans
 *
 * @author Douglas Lau
 */
public class DmsActionModel extends ProxyTableModel<DmsAction> {

	/** Allowed activation priorities */
	static private final DMSMessagePriority[] A_PRIORITIES = {
		DMSMessagePriority.PREFIX_PAGE,
		DMSMessagePriority.PSA,
		DMSMessagePriority.TRAVEL_TIME,
		DMSMessagePriority.SPEED_LIMIT,
		DMSMessagePriority.SCHEDULED,
		DMSMessagePriority.OTHER_SYSTEM,
		DMSMessagePriority.INCIDENT_LOW,
		DMSMessagePriority.INCIDENT_MED,
		DMSMessagePriority.INCIDENT_HIGH
	};

	/** Allowed run-time priorities */
	static private final DMSMessagePriority[] R_PRIORITIES = {
		DMSMessagePriority.PSA,
		DMSMessagePriority.TRAVEL_TIME,
		DMSMessagePriority.SPEED_LIMIT,
		DMSMessagePriority.SCHEDULED,
		DMSMessagePriority.OTHER_SYSTEM,
		DMSMessagePriority.INCIDENT_LOW,
		DMSMessagePriority.INCIDENT_MED,
		DMSMessagePriority.INCIDENT_HIGH
	};

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<DmsAction>> createColumns() {
		ArrayList<ProxyColumn<DmsAction>> cols =
			new ArrayList<ProxyColumn<DmsAction>>(6);
		cols.add(new ProxyColumn<DmsAction>("action.plan.dms.group",
			120)
		{
			public Object getValueAt(DmsAction da) {
				return da.getSignGroup();
			}
		});
		cols.add(new ProxyColumn<DmsAction>("action.plan.phase", 100) {
			public Object getValueAt(DmsAction da) {
				return da.getPhase();
			}
			public boolean isEditable(DmsAction da) {
				return canUpdate(da);
			}
			public void setValueAt(DmsAction da, Object value) {
				if (value instanceof PlanPhase)
					da.setPhase((PlanPhase) value);
			}
			protected TableCellEditor createCellEditor() {
				JComboBox<PlanPhase> cbx = new JComboBox
					<PlanPhase>();
				cbx.setModel(new IComboBoxModel<PlanPhase>(
					phase_mdl));
				return new DefaultCellEditor(cbx);
			}
		});
		cols.add(new ProxyColumn<DmsAction>("quick.message", 160) {
			public Object getValueAt(DmsAction da) {
				return da.getQuickMessage();
			}
			public boolean isEditable(DmsAction da) {
				return canUpdate(da);
			}
			public void setValueAt(DmsAction da, Object value) {
				String v = value.toString().trim();
				da.setQuickMessage(
					QuickMessageHelper.lookup(v));
			}
		});
		cols.add(new ProxyColumn<DmsAction>("dms.beacon.enabled", 100,
			Boolean.class)
		{
			public Object getValueAt(DmsAction da) {
				return da.getBeaconEnabled();
			}
			public boolean isEditable(DmsAction da) {
				return canUpdate(da);
			}
			public void setValueAt(DmsAction da, Object value) {
				if (value instanceof Boolean)
					da.setBeaconEnabled((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<DmsAction>("dms.priority.activation",
			120)
		{
			public Object getValueAt(DmsAction da) {
				return DMSMessagePriority.fromOrdinal(
				       da.getActivationPriority());
			}
			public boolean isEditable(DmsAction da) {
				return canUpdate(da);
			}
			public void setValueAt(DmsAction da, Object value) {
				if (value instanceof DMSMessagePriority) {
					DMSMessagePriority p =
						(DMSMessagePriority)value;
					da.setActivationPriority(p.ordinal());
				}
			}
			protected TableCellEditor createCellEditor() {
				JComboBox<DMSMessagePriority> cbx =new JComboBox
					<DMSMessagePriority>(A_PRIORITIES);
				return new DefaultCellEditor(cbx);
			}
		});
		cols.add(new ProxyColumn<DmsAction>("dms.priority.run.time",
			120)
		{
			public Object getValueAt(DmsAction da) {
				return DMSMessagePriority.fromOrdinal(
				       da.getRunTimePriority());
			}
			public boolean isEditable(DmsAction da) {
				return canUpdate(da);
			}
			public void setValueAt(DmsAction da, Object value) {
				if (value instanceof DMSMessagePriority) {
					DMSMessagePriority p =
						(DMSMessagePriority)value;
					da.setRunTimePriority(p.ordinal());
				}
			}
			protected TableCellEditor createCellEditor() {
				JComboBox<DMSMessagePriority> cbx =new JComboBox
					<DMSMessagePriority>(R_PRIORITIES);
				return new DefaultCellEditor(cbx);
			}
		});
		return cols;
	}

	/** Currently selected action plan */
	private final ActionPlan action_plan;

	/** Plan phase model */
	private final ProxyListModel<PlanPhase> phase_mdl;

    /** Sign group that is in the process of being created. */
    private String pending_sign_group = null;

    /** DMS for which a sign group is being created. */
    private DMS pending_dms = null;

	/** DMS sign group type cache */
	private final TypeCache<DmsSignGroup> dms_sign_groups;

    /** Sign group type cache */
	private final TypeCache<SignGroup> sign_groups;

	/** Async handling of case where SignGroup must be created */
	private final ProxyListener<SignGroup> sign_group_listener = new ProxyListener<SignGroup>() {

		@Override
		public void proxyAdded(SignGroup g) {
			if (pending_dms != null && g.getName().equals(pending_sign_group)) {
				String oname = createDmsSignGroupName(pending_dms, g);
				HashMap<String, Object> attrs = new HashMap<>();
				attrs.put("dms", pending_dms);
				attrs.put("sign_group", g);
				dms_sign_groups.createObject(oname, attrs);

                pending_dms = null;
                pending_sign_group = null;
				createObjectCallback(g);
			}
		}

		@Override
		public void enumerationComplete() { }

		@Override
		public void proxyRemoved(SignGroup proxy) { }

		@Override
		public void proxyChanged(SignGroup proxy, String a) { }

	};

	/** Create a new DMS action table model */
	public DmsActionModel(Session s, ActionPlan ap) {
		super(s, s.getSonarState().getDmsActions(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      true);	/* has_name */
		action_plan = ap;
		phase_mdl = s.getSonarState().getPhaseModel();
		dms_sign_groups = s.getSonarState().getDmsCache().getDmsSignGroups();
		sign_groups = s.getSonarState().getDmsCache().getSignGroups();
		sign_groups.addProxyListener(sign_group_listener);
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return DmsAction.SONAR_TYPE;
	}

	/** Check if a proxy is included in the list */
	@Override
	protected boolean check(DmsAction proxy) {
		return proxy.getActionPlan() == action_plan;
	}

	/** Check if the user can add a proxy */
	@Override
	public boolean canAdd() {
		return action_plan != null && super.canAdd();
	}

	/** Create an object with the name */
	@Override
	public void createObject(String name) {
		SignGroup sg = SignGroupHelper.lookup(name.trim());
		if (sg == null && action_plan != null) {
			DMS dms = DMSHelper.lookup(name.trim());
			if (dms != null) {
				String sg_name = dms.getName();

				// to handle proxy added event later
				pending_sign_group = sg_name;
				pending_dms = dms;

				HashMap<String, Object> attrs = new HashMap<String, Object>();
				attrs.put("local", true);
				sign_groups.createObject(sg_name, attrs);
				return;
			}
		}

		createObjectCallback(sg);
	}

	private void createObjectCallback(SignGroup sg) {
		if (sg != null && action_plan != null)
			create(sg);
		else {
			JOptionPane.showMessageDialog(null,
				I18N.get("action.plan.dms.hint"));
		}
	}

	/** Create a new DMS action */
	private void create(SignGroup sg) {
		String name = createUniqueName();
		if (name != null) {
			HashMap<String, Object> attrs =
				new HashMap<String, Object>();
			attrs.put("action_plan", action_plan);
			attrs.put("sign_group", sg);
			attrs.put("phase", lookupPlanPhase());
			attrs.put("a_priority",
				DMSMessagePriority.SCHEDULED.ordinal());
			attrs.put("r_priority",
				DMSMessagePriority.SCHEDULED.ordinal());
			cache.createObject(name, attrs);
		}
	}

	/** Create a unique DMS action name */
	private String createUniqueName() {
		for (int uid = 1; uid <= 999; uid++) {
			String n = action_plan.getName() + "_" + uid;
			if (cache.lookupObject(n) == null)
				return n;
		}
		return null;
	}

	/** Lookup the appropriate plan phase for a DMS action */
	private PlanPhase lookupPlanPhase() {
		PlanPhase phase = PlanPhaseHelper.lookup("deployed");
		return (phase != null) ? phase : action_plan.getDefaultPhase();
	}

	/** Create a DMS sign group name */
	private static String createDmsSignGroupName(DMS dms, SignGroup sg) {
		return sg.getName() + "_" + dms.getName();
	}

	/** Dispose of the DMS action model */
	@Override
	public void dispose() {
		super.dispose();
		sign_groups.removeProxyListener(sign_group_listener);
	}
}
