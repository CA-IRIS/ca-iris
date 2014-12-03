/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2014  Minnesota Department of Transportation
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
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;

/**
 * Table model for plan phases.
 *
 * @author Douglas Lau
 */
public class PlanPhaseModel extends ProxyTableModel2<PlanPhase> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<PlanPhase>> createColumns() {
		ArrayList<ProxyColumn<PlanPhase>> cols =
			new ArrayList<ProxyColumn<PlanPhase>>(3);
		cols.add(new ProxyColumn<PlanPhase>("action.plan.phase.name",
			120)
		{
			public Object getValueAt(PlanPhase p) {
				return p.getName();
			}
		});
		cols.add(new ProxyColumn<PlanPhase>("action.plan.phase.hold",
			120, Integer.class)
		{
			public Object getValueAt(PlanPhase p) {
				return p.getHoldTime();
			}
			public boolean isEditable(PlanPhase p) {
				return canUpdate(p);
			}
			public void setValueAt(PlanPhase p, Object value) {
				if (value instanceof Integer)
					p.setHoldTime((Integer)value);
			}
		});
		cols.add(new ProxyColumn<PlanPhase>("action.plan.phase.next",
			120)
		{
			public Object getValueAt(PlanPhase p) {
				return p.getNextPhase();
			}
			public boolean isEditable(PlanPhase p) {
				return canUpdate(p);
			}
			public void setValueAt(PlanPhase p, Object value) {
				if (value instanceof PlanPhase)
					p.setNextPhase((PlanPhase)value);
				else
					p.setNextPhase(null);
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

	/** Create a new plan phase table model */
	public PlanPhaseModel(Session s) {
		super(s, s.getSonarState().getPlanPhases(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      true);	/* has_name */
		phase_model = s.getSonarState().getPhaseModel();
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return PlanPhase.SONAR_TYPE;
	}
}
