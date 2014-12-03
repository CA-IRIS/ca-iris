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
import us.mn.state.dot.tms.DayPlan;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.TimeAction;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;

/**
 * Table model for time actions assigned to action plans.
 *
 * @author Douglas Lau
 */
public class TimeActionModel extends ProxyTableModel2<TimeAction> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<TimeAction>> createColumns() {
		ArrayList<ProxyColumn<TimeAction>> cols =
			new ArrayList<ProxyColumn<TimeAction>>(4);
		cols.add(new ProxyColumn<TimeAction>("action.plan.day", 100) {
			public Object getValueAt(TimeAction ta) {
				return ta.getDayPlan();
			}
		});
		cols.add(new ProxyColumn<TimeAction>("action.plan.date", 100) {
			public Object getValueAt(TimeAction ta) {
				return ta.getSchedDate();
			}
		});
		cols.add(new ProxyColumn<TimeAction>("action.plan.time", 80) {
			public Object getValueAt(TimeAction ta) {
				return ta.getTimeOfDay();
			}
		});
		cols.add(new ProxyColumn<TimeAction>("action.plan.phase", 100) {
			public Object getValueAt(TimeAction ta) {
				return ta.getPhase();
			}
			public boolean isEditable(TimeAction ta) {
				return canUpdate(ta);
			}
			public void setValueAt(TimeAction ta, Object value) {
				if (value instanceof PlanPhase)
					ta.setPhase((PlanPhase)value);
			}
			protected TableCellEditor createCellEditor() {
				JComboBox combo = new JComboBox();
				combo.setModel(new WrapperComboBoxModel(
					phase_mdl));
				return new DefaultCellEditor(combo);
			}
		});
		return cols;
	}

	/** Currently selected action plan */
	private final ActionPlan action_plan;

	/** Plan phase model */
	private final ProxyListModel<PlanPhase> phase_mdl;

	/** Create a new time action table model */
	public TimeActionModel(Session s, ActionPlan ap) {
		super(s, s.getSonarState().getTimeActions(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      false);	/* has_name */
		action_plan = ap;
		phase_mdl = s.getSonarState().getPhaseModel();
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return TimeAction.SONAR_TYPE;
	}

	/** Check if a proxy is included in the list */
	@Override
	protected boolean check(TimeAction proxy) {
		return proxy.getActionPlan() == action_plan;
	}

	/** Check if the user can add a proxy */
	@Override
	public boolean canAdd() {
		return action_plan != null && super.canAdd();
	}

	/** Create a new time action */
	public void createObject(DayPlan dp, String sd, String tod) {
		if (action_plan != null && isOneSpecified(dp, sd)) {
			String name = createUniqueName();
			if (name != null)
				create(name, dp, sd, tod);
		}
	}

	/** Check that a day plan or date has been specified */
	static private boolean isOneSpecified(DayPlan dp, String sd) {
		return (dp != null || sd != null) && (dp == null || sd == null);
	}

	/** Create a unique time action name */
	private String createUniqueName() {
		for (int uid = 1; uid <= 999; uid++) {
			String n = action_plan.getName() + "_" + uid;
			if (cache.lookupObject(n) == null)
				return n;
		}
		return null;
	}

	/** Create a new time action */
	private void create(String name, DayPlan dp, String sd, String tod) {
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("action_plan", action_plan);
		if (dp != null)
			attrs.put("day_plan", dp);
		if (sd != null)
			attrs.put("sched_date", sd);
		attrs.put("time_of_day", tod);
		attrs.put("phase", action_plan.getDefaultPhase());
		cache.createObject(name, attrs);
	}
}
