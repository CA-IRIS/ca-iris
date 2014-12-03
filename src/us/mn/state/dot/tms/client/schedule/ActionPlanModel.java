/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2014  Minnesota Department of Transportation
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
import javax.swing.table.TableCellEditor;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;

/**
 * Table model for action plans.
 *
 * @author Douglas Lau
 */
public class ActionPlanModel extends ProxyTableModel2<ActionPlan> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<ActionPlan>> createColumns() {
		ArrayList<ProxyColumn<ActionPlan>> cols =
			new ArrayList<ProxyColumn<ActionPlan>>(6);
		cols.add(new ProxyColumn<ActionPlan>("action.plan.name", 120) {
			public Object getValueAt(ActionPlan ap) {
				return ap.getName();
			}
		});
		cols.add(new ProxyColumn<ActionPlan>("device.description", 380){
			public Object getValueAt(ActionPlan ap) {
				return ap.getDescription();
			}
			public boolean isEditable(ActionPlan ap) {
				return canUpdate(ap);
			}
			public void setValueAt(ActionPlan ap, Object value) {
				String v = value.toString().trim();
				ap.setDescription(v);
			}
		});
		cols.add(new ProxyColumn<ActionPlan>("action.plan.sync.actions",
			80, Boolean.class)
		{
			public Object getValueAt(ActionPlan ap) {
				return ap.getSyncActions();
			}
			public boolean isEditable(ActionPlan ap) {
				return canUpdate(ap);
			}
			public void setValueAt(ActionPlan ap, Object value) {
				if (value instanceof Boolean)
					ap.setSyncActions((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<ActionPlan>("action.plan.sticky", 80,
			Boolean.class)
		{
			public Object getValueAt(ActionPlan ap) {
				return ap.getSticky();
			}
			public boolean isEditable(ActionPlan ap) {
				return canUpdate(ap);
			}
			public void setValueAt(ActionPlan ap, Object value) {
				if (value instanceof Boolean)
					ap.setSticky((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<ActionPlan>("action.plan.active", 80,
			Boolean.class)
		{
			public Object getValueAt(ActionPlan ap) {
				return ap.getActive();
			}
			public boolean isEditable(ActionPlan ap) {
				return canUpdate(ap);
			}
			public void setValueAt(ActionPlan ap, Object value) {
				if (value instanceof Boolean)
					ap.setActive((Boolean)value);
			}
		});
		cols.add(new ProxyColumn<ActionPlan>(
			"action.plan.default.phase", 100)
		{
			public Object getValueAt(ActionPlan ap) {
				return ap.getDefaultPhase();
			}
			public boolean isEditable(ActionPlan ap) {
				return canUpdate(ap);
			}
			public void setValueAt(ActionPlan ap, Object value) {
				if (value instanceof PlanPhase) {
					PlanPhase p = (PlanPhase)value;
					ap.setDefaultPhase(p);
				}
			}
			protected TableCellEditor createCellEditor() {
				JComboBox combo = new JComboBox();
				combo.setModel(new WrapperComboBoxModel(
					phase_model));
				return new DefaultCellEditor(combo);
			}
		});
		return cols;
	}

	/** Plan phase model */
	private final ProxyListModel<PlanPhase> phase_model;

	/** Create a new action plan table model */
	public ActionPlanModel(Session s) {
		super(s, s.getSonarState().getActionPlans(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      true);	/* has_name */
		phase_model = s.getSonarState().getPhaseModel();
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return ActionPlan.SONAR_TYPE;
	}

	/** Get the visible row count */
	@Override
	public int getVisibleRowCount() {
		return 8;
	}

	/** Create a new action plan */
	public void create(String name, PlanPhase p) {
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("default_phase", p);
		attrs.put("phase", p);
		cache.createObject(name, attrs);
	}
}
