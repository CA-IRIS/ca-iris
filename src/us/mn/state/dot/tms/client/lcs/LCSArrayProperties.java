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
package us.mn.state.dot.tms.client.lcs;

import java.awt.Color;
import java.util.LinkedList;
import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.sched.ChangeJob;
import us.mn.state.dot.sched.FocusJob;
import us.mn.state.dot.sched.ListSelectionJob;
import us.mn.state.dot.sonar.Checker;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.LaneUseIndication;
import us.mn.state.dot.tms.LCS;
import us.mn.state.dot.tms.LCSArray;
import us.mn.state.dot.tms.LCSArrayLock;
import us.mn.state.dot.tms.LCSHelper;
import us.mn.state.dot.tms.LCSIndication;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.toast.FormPanel;
import us.mn.state.dot.tms.client.toast.SonarObjectForm;
import us.mn.state.dot.tms.client.widget.IButton;
import us.mn.state.dot.tms.client.widget.ZTable;
import us.mn.state.dot.tms.utils.I18N;

/**
 * LCSArrayProperties is a dialog for editing the properties of an LCS array.
 *
 * @author Douglas Lau
 */
public class LCSArrayProperties extends SonarObjectForm<LCSArray> {

	/** SONAR state */
	protected final SonarState state;

	/** LCS Indication creator */
	protected final LCSIndicationCreator creator;

	/** LCS table model */
	protected final LCSTableModel table_model;

	/** LCS table */
	protected final ZTable lcs_table = new ZTable();

	/** Button to edit the selected LCS */
	private final IButton edit_btn = new IButton("lcs.edit");

	/** Button to delete the selected LCS */
	private final IButton delete_btn = new IButton("lcs.delete");

	/** Spinner for lane shift */
	protected final JSpinner shift_spn = new JSpinner(
		new SpinnerNumberModel(0, 0, 12, 1));

	/** Notes text area */
	protected final JTextArea notes = new JTextArea(3, 24);

	/** List of indication buttons */
	protected final LinkedList<JCheckBox> indications =
		new LinkedList<JCheckBox>();

	/** LCS lock combo box component */
	protected final JComboBox lcs_lock = new JComboBox(
		LCSArrayLock.getDescriptions());

	/** Operation description label */
	protected final JLabel operation = new JLabel();

	/** Button to send settings */
	private final IButton settings_btn =new IButton("device.send.settings");

	/** Create a new lane control signal properties form */
	public LCSArrayProperties(Session s, LCSArray proxy) {
		super(I18N.get("lcs.array") + ": ", s, proxy);
		state = s.getSonarState();
		User user = s.getUser();
		creator = new LCSIndicationCreator(state.getNamespace(),
			state.getLcsCache().getLCSIndications(), user);
		table_model = new LCSTableModel(s, proxy);
		table_model.initialize();
	}

	/** Get the SONAR type cache */
	protected TypeCache<LCSArray> getTypeCache() {
		return state.getLcsCache().getLCSArrays();
	}

	/** Initialize the widgets on the form */
	protected void initialize() {
		super.initialize();
		JTabbedPane tab = new JTabbedPane();
		tab.add(I18N.get("device.setup"), createSetupPanel());
		tab.add(I18N.get("device.status"), createStatusPanel());
		add(tab);
		updateAttribute(null);
		if(canUpdate())
			createUpdateJobs();
		if(canUpdate("lcsLock"))
			createLockJob();
		if(canUpdate("deviceRequest"))
			createRequestJobs();
		setBackground(Color.LIGHT_GRAY);
	}

	/** Dispose of the form */
	protected void dispose() {
		table_model.dispose();
		super.dispose();
	}

	/** Create setup panel */
	protected JPanel createSetupPanel() {
		FormPanel panel = new FormPanel(canUpdate());
		initTable();
		FormPanel tpnl = new FormPanel(canUpdate());
		tpnl.addRow(lcs_table);
		lcs_table.setEnabled(true);
		tpnl.add(edit_btn);
		edit_btn.setEnabled(false);
		tpnl.addRow(delete_btn);
		delete_btn.setEnabled(false);
		tpnl.addRow(I18N.get("lcs.lane.shift"), shift_spn);
		// this panel is needed to make the widgets line up
		panel.add(new JPanel());
		panel.add(tpnl);
		panel.addRow(createIndicationPanel());
		panel.addRow(I18N.get("device.notes"), notes);
		return panel;
	}

	/** Create jobs for updating widgets */
	protected void createUpdateJobs() {
		new ChangeJob(this, shift_spn) {
			public void perform() {
				Number n = (Number)shift_spn.getValue();
				proxy.setShift(n.intValue());
			}
		};
		new FocusJob(notes) {
			public void perform() {
				proxy.setNotes(notes.getText());
			}
		};
	}

	/** Initialize the table */
	protected void initTable() {
		final ListSelectionModel s = lcs_table.getSelectionModel();
		s.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new ListSelectionJob(this, s) {
			public void perform() {
				selectLCS();
			}
		};
		lcs_table.setAutoCreateColumnsFromModel(false);
		lcs_table.setColumnModel(table_model.createColumnModel());
		lcs_table.setModel(table_model);
		lcs_table.setVisibleRowCount(12);
		new ActionJob(this, edit_btn) {
			public void perform() {
				editPressed();
			}
		};
		new ActionJob(this, delete_btn) {
			public void perform() throws Exception {
				int row = s.getMinSelectionIndex();
				if(row >= 0)
					table_model.deleteRow(row);
			}
		};
	}

	/** Edit button pressed */
	protected void editPressed() {
		LCS lcs = getSelectedLCS();
		if(lcs != null) {
			DMS dms = DMSHelper.lookup(lcs.getName());
			if(dms != null)
				session.getDMSManager().showPropertiesForm(dms);
		}
	}

	/** Get the selected LCS */
	protected LCS getSelectedLCS() {
		ListSelectionModel s = lcs_table.getSelectionModel();
		return table_model.getProxy(s.getMinSelectionIndex());
	}

	/** Create the indication panel */
	protected JPanel createIndicationPanel() {
		FormPanel panel = new FormPanel(true);
		for(LaneUseIndication i: LaneUseIndication.values()) {
			final int ind = i.ordinal();
			JCheckBox btn = new JCheckBox();
			new ActionJob(btn) {
				public void perform() {
					toggleIndication(ind);
				}
			};
			indications.add(btn);
			panel.add(new JLabel(IndicationIcon.create(18, i)));
			panel.addRow(btn, new JLabel(i.description));
			btn.setEnabled(false);
		}
		return panel;
	}

	/** Toggle one LCS indication checkbox */
	protected void toggleIndication(int ind) {
		LCS lcs = getSelectedLCS();
		if(lcs != null) {
			JCheckBox btn = indications.get(ind);
			if(btn.isSelected())
				creator.create(lcs, ind);
			else
				destroyLCSIndication(lcs, ind);
		}
	}

	/** Destroy the specified LCS indication */
	protected void destroyLCSIndication(LCS lcs, final int ind) {
		LCSIndication li = LCSHelper.lookupIndication(lcs,
			new Checker<LCSIndication>()
		{
			public boolean check(LCSIndication li) {
				return li.getIndication() == ind;
			}
		});
		if(li != null)
			li.destroy();
	}

	/** Select an LCS in the table */
	protected void selectLCS() {
		LCS lcs = getSelectedLCS();
		if(lcs != null)
			selectLCS(lcs);
		else {
			edit_btn.setEnabled(false);
			delete_btn.setEnabled(false);
			for(JCheckBox btn: indications) {
				btn.setEnabled(false);
				btn.setSelected(false);
			}
		}
	}

	/** Select an LCS in the table */
	protected void selectLCS(LCS lcs) {
		edit_btn.setEnabled(true);
		final HashMap<Integer, LCSIndication> ind =
			new HashMap<Integer, LCSIndication>();
		LCSHelper.lookupIndication(lcs, new Checker<LCSIndication>() {
			public boolean check(LCSIndication li) {
				ind.put(li.getIndication(), li);
				return false;
			}
		});
		delete_btn.setEnabled(ind.isEmpty());
		String name = lcs.getName();
		boolean can_add = creator.canAdd(name);
		boolean can_remove = creator.canRemove(name);
		for(LaneUseIndication i: LaneUseIndication.values()) {
			JCheckBox btn = indications.get(i.ordinal());
			if(ind.containsKey(i.ordinal())) {
				LCSIndication li = ind.get(i.ordinal());
				boolean no_c = li.getController() == null;
				btn.setEnabled(can_remove && no_c);
				btn.setSelected(true);
			} else {
				btn.setEnabled(can_add);
				btn.setSelected(false);
			}
		}
	}

	/** Create status panel */
	protected JPanel createStatusPanel() {
		FormPanel panel = new FormPanel(false);
		panel.addRow(I18N.get("lcs.lock"), lcs_lock);
		panel.addRow(I18N.get("device.operation"), operation);
		panel.addRow(settings_btn);
		return panel;
	}

	/** Create lock job */
	protected void createLockJob() {
		lcs_lock.setAction(new LockLcsAction(proxy, lcs_lock));
		lcs_lock.setEnabled(true);
	}

	/** Create request jobs */
	protected void createRequestJobs() {
		new ActionJob(this, settings_btn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					SEND_SETTINGS.ordinal());
			}
		};
		settings_btn.setEnabled(true);
	}

	/** Update one attribute on the form */
	protected void doUpdateAttribute(String a) {
		if(a == null || a.equals("shift"))
			shift_spn.setValue(proxy.getShift());
		if(a == null || a.equals("notes"))
			notes.setText(proxy.getNotes());
		if(a == null || a.equals("lcsLock")) {
			Integer lk = proxy.getLcsLock();
			if(lk != null)
				lcs_lock.setSelectedIndex(lk);
			else
				lcs_lock.setSelectedIndex(0);
		}
		if(a == null || a.equals("operation"))
			operation.setText(proxy.getOperation());
	}
}
