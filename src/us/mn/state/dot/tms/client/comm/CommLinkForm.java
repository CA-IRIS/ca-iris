/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.comm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import us.mn.state.dot.sched.ListSelectionJob;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.AbstractForm;
import us.mn.state.dot.tms.client.widget.FormPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.ILabel;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;
import us.mn.state.dot.tms.client.widget.ZTable;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A form for displaying and editing comm links
 *
 * @author Douglas Lau
 */
public class CommLinkForm extends AbstractForm {

	/** Check if the user is permitted to use the form */
	static public boolean isPermitted(Session s) {
		return s.canRead(CommLink.SONAR_TYPE) &&
		       s.canRead(Controller.SONAR_TYPE);
	}

	/** Comm link table row height */
	static protected final int ROW_HEIGHT = 24;

	/** Tabbed pane */
	protected final JTabbedPane tab = new JTabbedPane();

	/** Table model for comm links */
	protected final CommLinkModel model;

	/** Table to hold the comm link list */
	protected final ZTable table = new ZTable();

	/** Table model for controllers */
	protected ControllerModel cmodel;

	/** Table to hold controllers */
	protected final ZTable ctable = new ZTable();

	/** Comm link status */
	protected final JLabel link_status = new JLabel();

	/** Action to delete the selected comm link */
	private final IAction delete_link = new IAction("comm.link.delete") {
		protected void do_perform() {
			ListSelectionModel s = table.getSelectionModel();
			int row = s.getMinSelectionIndex();
			if(row >= 0)
				model.deleteRow(row);
		}
	};

	/** Table to hold failed controllers */
	protected final ZTable ftable = new ZTable();

	/** Failed controller table model */
	protected final FailedControllerModel fmodel;

	/** Table row sorter */
	protected final TableRowSorter<FailedControllerModel> sorter;

	/** Table row filter */
	protected final RowFilter<FailedControllerModel, Integer> filter;

	/** Action to show controller properties */
	private final IAction controller = new IAction("controller") {
		protected void do_perform() {
			doPropertiesAction();
		}
	};

	/** Action to delete the selected controller */
	private final IAction del_ctr = new IAction("controller.delete") {
		protected void do_perform() {
			ListSelectionModel cs = ctable.getSelectionModel();
			int row = cs.getMinSelectionIndex();
			if(row >= 0)
				cmodel.deleteRow(row);
		}
	};

	/** Action to go to a failed controller */
	private final IAction go_ctrl = new IAction("controller.go") {
		protected void do_perform() {
			goFailedController();
		}
	};

	/** User session */
	protected final Session session;

	/** Create a new comm link form */
	public CommLinkForm(Session s) {
		super(I18N.get("comm.links"));
		session = s;
		model = new CommLinkModel(s);
		fmodel = new FailedControllerModel(s);
		sorter = new TableRowSorter<FailedControllerModel>(fmodel);
		sorter.setSortsOnUpdates(true);
		LinkedList<RowSorter.SortKey> keys =
			new LinkedList<RowSorter.SortKey>();
		keys.add(new RowSorter.SortKey(4, SortOrder.DESCENDING));
		sorter.setSortKeys(keys);
		filter = new RowFilter<FailedControllerModel, Integer>() {
			public boolean include(Entry<? extends
				FailedControllerModel, ? extends Integer> entry)
			{
				Controller ctrl = fmodel.getRowProxy(
					entry.getIdentifier());
				return ControllerHelper.isFailed(ctrl);
			}
		};
		sorter.setRowFilter(filter);
	}

	/** Initializze the widgets in the form */
	protected void initialize() {
		model.initialize();
		fmodel.initialize();
		setLayout(new BorderLayout());
		tab.add(I18N.get("comm.link.all"), createCommLinkPanel());
		tab.add(I18N.get("controller.failed"),
			createFailedControllerPanel());
		add(tab);
		setBackground(Color.LIGHT_GRAY);
	}

	/** Dispose of the form */
	protected void dispose() {
		model.dispose();
		fmodel.dispose();
		if(cmodel != null)
			cmodel.dispose();
	}

	/** Create comm link panel */
	protected JPanel createCommLinkPanel() {
		final JButton ctr_props = new JButton(controller);
		ListSelectionModel s = table.getSelectionModel();
		s.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new ListSelectionJob(this, s) {
			public void perform() {
				if(!event.getValueIsAdjusting())
					selectCommLink();
			}
		};
		ListSelectionModel cs = ctable.getSelectionModel();
		cs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new ListSelectionJob(this, cs) {
			public void perform() {
				if(!event.getValueIsAdjusting())
					selectController();
			}
		};
		ctable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1)
					ctr_props.doClick();
			}
		});
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints bag = new GridBagConstraints();
		bag.insets.left = UI.hgap;
		bag.insets.right = UI.hgap;
		bag.insets.top = UI.vgap;
		bag.insets.bottom = UI.vgap;
		bag.gridwidth = 3;
		bag.fill = GridBagConstraints.BOTH;
		bag.weightx = 1;
		bag.weighty = 0.6f;
		table.setModel(model);
		table.setAutoCreateColumnsFromModel(false);
		table.setColumnModel(model.createColumnModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(ROW_HEIGHT);
		table.setVisibleRowCount(5);
		JScrollPane pane = new JScrollPane(table);
		panel.add(pane, bag);
		bag.gridwidth = 1;
		bag.gridx = 0;
		bag.gridy = 1;
		bag.anchor = GridBagConstraints.WEST;
		bag.fill = GridBagConstraints.NONE;
		bag.weightx = 0;
		bag.weighty = 0;
		panel.add(new ILabel("comm.link.selected"), bag);
		bag.gridx = 1;
		panel.add(link_status, bag);
		bag.gridx = 2;
		bag.anchor = GridBagConstraints.EAST;
		delete_link.setEnabled(false);
		panel.add(new JButton(delete_link), bag);
		bag.gridx = 0;
		bag.gridy = 2;
		bag.gridwidth = 3;
		bag.anchor = GridBagConstraints.CENTER;
		bag.fill = GridBagConstraints.BOTH;
		bag.weightx = 1;
		bag.weighty = 1;
		ctable.setAutoCreateColumnsFromModel(false);
		ctable.setRowHeight(ROW_HEIGHT);
		// NOTE: the width of the controller table is the same as the
		// comm link table on purpose.
		ctable.setPreferredScrollableViewportSize(new Dimension(
			table.getPreferredSize().width, ROW_HEIGHT * 12));
		pane = new JScrollPane(ctable);
		panel.add(pane, bag);
		bag.gridwidth = 1;
		bag.gridx = 0;
		bag.gridy = 3;
		bag.anchor = GridBagConstraints.WEST;
		bag.fill = GridBagConstraints.NONE;
		bag.weightx = 0;
		bag.weighty = 0;
		panel.add(new ILabel("controller.selected"), bag);
		bag.gridx = 1;
		bag.anchor = GridBagConstraints.EAST;
		controller.setEnabled(false);
		panel.add(ctr_props, bag);
		bag.gridx = 2;
		del_ctr.setEnabled(false);
		panel.add(new JButton(del_ctr), bag);
		return panel;
	}

	/** Change the selected comm link */
	protected void selectCommLink() {
		int row = table.getSelectedRow();
		CommLink cl = model.getProxy(row);
		if(cl != null)
			link_status.setText(cl.getStatus());
		else
			link_status.setText("");
		delete_link.setEnabled(model.canRemove(cl));
		del_ctr.setEnabled(false);
		ControllerModel old_model = cmodel;
		cmodel = new ControllerModel(session, cl);
		cmodel.initialize();
		ctable.setModel(cmodel);
		ctable.setColumnModel(cmodel.createColumnModel());
		if(old_model != null)
			old_model.dispose();
	}

	/** Change the selected controller */
	protected void selectController() {
		int row = ctable.getSelectedRow();
		Controller c = cmodel.getProxy(row);
		controller.setEnabled(c != null);
		del_ctr.setEnabled(cmodel.canRemove(c));
	}

	/** Do the action for controller properties button */
	protected void doPropertiesAction() {
		ListSelectionModel cs = ctable.getSelectionModel();
		int row = cs.getMinSelectionIndex();
		if(row >= 0) {
			Controller c = cmodel.getProxy(row);
			session.getDesktop().show(new ControllerForm(
				session, c));
		}
	}

	/** Create the failed controller panel */
	protected JPanel createFailedControllerPanel() {
		final JButton go_btn = new JButton(go_ctrl);
		ListSelectionModel s = ftable.getSelectionModel();
		s.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ftable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1)
					go_btn.doClick();
			}
		});
		FormPanel panel = new FormPanel(true);
		ftable.setModel(fmodel);
		ftable.setAutoCreateColumnsFromModel(false);
		ftable.setColumnModel(fmodel.createColumnModel());
		ftable.setRowHeight(ROW_HEIGHT);
		ftable.setVisibleRowCount(16);
		ftable.setRowSorter(sorter);
		panel.addRow(ftable);
		panel.add(go_btn);
		return panel;
	}

	/** Go to the failed controller (on the main tab) */
	protected void goFailedController() {
		int row = ftable.getSelectedRow();
		if(row >= 0) {
			int mrow = ftable.convertRowIndexToModel(row);
			if(mrow >= 0) {
				Controller c = fmodel.getRowProxy(mrow);
				if(c != null)
					goController(c);
			}
		}
	}

	/** Go to the specified controller (on the main tab) */
	protected void goController(Controller c) {
		CommLink l = c.getCommLink();
		int row = model.getRow(l);
		if(row >= 0) {
			ListSelectionModel s = table.getSelectionModel();
			s.setSelectionInterval(row, row);
			table.scrollRectToVisible(
				table.getCellRect(row, 0, true));
			tab.setSelectedIndex(0);
		}
	}
}