/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
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

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import us.mn.state.dot.sched.ListSelectionJob;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.DmsAction;
import us.mn.state.dot.tms.LaneAction;
import us.mn.state.dot.tms.MeterAction;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.TimeAction;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.FormPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.ZTable;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A form for displaying and editing action plans.
 *
 * @author Douglas Lau
 */
public class ActionPlanPanel extends JPanel {

	/** Check if the user is permitted to use the form */
	static public boolean isPermitted(Session s) {
		return s.canRead(ActionPlan.SONAR_TYPE) &&
		       s.canRead(DmsAction.SONAR_TYPE) &&
		       s.canRead(LaneAction.SONAR_TYPE) &&
		       s.canRead(MeterAction.SONAR_TYPE) &&
		       s.canRead(PlanPhase.SONAR_TYPE) &&
		       s.canRead(TimeAction.SONAR_TYPE);
	}

	/** Table row height */
	static protected final int ROW_HEIGHT = 22;

	/** Table model for action plans */
	protected final ActionPlanModel p_model;

	/** Table to hold the action plans */
	protected final ZTable p_table = new ZTable();

	/** Action to delete the selected action plan */
	private final IAction del_plan = new IAction("action.plan.delete") {
		protected void do_perform() {
			ListSelectionModel sm = p_table.getSelectionModel();
			int row = sm.getMinSelectionIndex();
			if(row >= 0)
				p_model.deleteRow(row);
		}
	};

	/** Time action table panel */
	private final PlanTablePanel<TimeAction> t_panel;

	/** DMS action table panel */
	private final PlanTablePanel<DmsAction> d_panel;

	/** Lane action table panel */
	private final PlanTablePanel<LaneAction> l_panel;

	/** Meter action table panel */
	private final PlanTablePanel<MeterAction> m_panel;

	/** User session */
	protected final Session session;

	/** Create a new action plan panel */
	public ActionPlanPanel(Session s) {
		super(new BorderLayout());
		session = s;
		p_model = new ActionPlanModel(s);
		t_panel = new PlanTablePanel<TimeAction>();
		d_panel = new PlanTablePanel<DmsAction>();
		l_panel = new PlanTablePanel<LaneAction>();
		m_panel = new PlanTablePanel<MeterAction>();
	}

	/** Initializze the widgets on the panel */
	protected void initialize() {
		p_model.initialize();
		addActionPlanJobs();
		add(createActionPlanPanel(), BorderLayout.NORTH);
		JTabbedPane tab = new JTabbedPane();
		tab.setBorder(new EmptyBorder(0, 12, 12, 12));
		tab.add(I18N.get("action.plan.schedule"), t_panel);
		tab.add(I18N.get("action.plan.dms"), d_panel);
		tab.add(I18N.get("action.plan.lane"), l_panel);
		tab.add(I18N.get("action.plan.meter"), m_panel);
		add(tab, BorderLayout.SOUTH);
	}

	/** Create the main action plan panel */
	private JPanel createActionPlanPanel() {
		FormPanel p_panel = new FormPanel(true);
		p_panel.setBorder();
		p_table.setModel(p_model);
		p_table.setAutoCreateColumnsFromModel(false);
		p_table.setColumnModel(p_model.createColumnModel());
		p_table.setRowHeight(ROW_HEIGHT);
		p_table.setVisibleRowCount(10);
		p_panel.addRow(p_table);
		p_panel.addRow(new JButton(del_plan));
		del_plan.setEnabled(false);
		return p_panel;
	}

	/** Add jobs for action plan table */
	protected void addActionPlanJobs() {
		ListSelectionModel sm = p_table.getSelectionModel();
		sm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new ListSelectionJob(this, sm) {
			public void perform() {
				if(!event.getValueIsAdjusting())
					selectActionPlan();
			}
		};
	}

	/** Dispose of the form */
	public void dispose() {
		p_model.dispose();
		t_panel.dispose();
		d_panel.dispose();
		l_panel.dispose();
		m_panel.dispose();
	}

	/** Change the selected action plan */
	protected void selectActionPlan() {
		ActionPlan ap = p_model.getProxy(p_table.getSelectedRow());
		del_plan.setEnabled(p_model.canRemove(ap));
		t_panel.setTableModel(new TimeActionModel(session, ap));
		d_panel.setTableModel(new DmsActionModel(session, ap));
		l_panel.setTableModel(new LaneActionModel(session, ap));
		m_panel.setTableModel(new MeterActionModel(session, ap));
		// We need to revalidate here, because the PlanTablePanels
		// don't resize properly on their own.  I think it has something
		// to do with JTables inside of JScrollPanes, and empty models
		// versus models with actual data in them.
		revalidate();
	}
}
