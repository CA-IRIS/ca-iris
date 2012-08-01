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
package us.mn.state.dot.tms.client.toast;

import java.awt.Color;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.sched.ChangeJob;
import us.mn.state.dot.sched.FocusJob;
import us.mn.state.dot.sonar.Checker;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Cabinet;
import us.mn.state.dot.tms.CabinetStyle;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.SonarObjectForm;
import us.mn.state.dot.tms.client.roads.LocationPanel;
import us.mn.state.dot.tms.client.widget.FormPanel;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;
import us.mn.state.dot.tms.client.widget.ZTable;
import us.mn.state.dot.tms.utils.I18N;

/**
 * ControllerForm is a Swing dialog for editing Controller records
 *
 * @author Douglas Lau
 */
public class ControllerForm extends SonarObjectForm<Controller> {

	/** Table row height */
	static protected final int ROW_HEIGHT = 24;

	/** Frame title */
	static protected final String TITLE = "Controller: ";

	/** Comm link combo box */
	protected final JComboBox comm_link = new JComboBox();

	/** Model for drop address spinner */
	protected DropNumberModel drop_model;

	/** Drop address spinner */
	protected final JSpinner drop_id = new JSpinner();

	/** Controller notes text */
	protected final JTextArea notes = new JTextArea(3, 24);

	/** Access password */
	protected final JPasswordField password = new JPasswordField(16);

	/** Button to clear the access password */
	protected final JButton clear_btn = new JButton("Clear");

	/** Active checkbox */
	protected final JCheckBox active = new JCheckBox();

	/** Location panel */
	private final LocationPanel location;

	/** Mile point text field */
	protected final JTextField mile = new JTextField(10);

	/** Cabinet style combo box */
	protected final JComboBox cab_style = new JComboBox();

	/** Cabinet for controller */
	protected final Cabinet cabinet;

	/** Cabinet cache */
	protected final TypeCache<Cabinet> cabinets;

	/** Cabinet listener */
	protected CabinetListener cab_listener;

	/** Controller IO model */
	protected ControllerIOModel io_model;

	/** Firmware version */
	protected final JLabel version = new JLabel();

	/** Maint status */
	protected final JLabel maint = new JLabel();

	/** Status */
	protected final JLabel status = new JLabel();

	/** Fail time */
	protected final JLabel fail_time_lbl = new JLabel();

	/** Timeout errors label */
	protected final JLabel timeout_lbl = new JLabel();

	/** Checksum errors label */
	protected final JLabel checksum_lbl = new JLabel();

	/** Parsing errors label */
	protected final JLabel parsing_lbl = new JLabel();

	/** Controller errors label */
	protected final JLabel controller_lbl = new JLabel();

	/** Successful operations label */
	protected final JLabel success_lbl = new JLabel();

	/** Failed operations label */
	protected final JLabel failed_lbl = new JLabel();

	/** Clear error status button */
	protected final JButton clearErrorBtn = new JButton("Clear Errors");

	/** Reset button */
	protected final JButton reset =
		new JButton(I18N.get("controller.reset"));

	/** Comm Link list model */
	protected final ProxyListModel<CommLink> link_model;

	/** Cabinet style list model */
	protected final ProxyListModel<CabinetStyle> sty_model;

	/** Create a new controller form */
	public ControllerForm(Session s, Controller c) {
		super(TITLE, s, c);
		ConCache con_cache = state.getConCache();
		link_model = con_cache.getCommLinkModel();
		cabinets = con_cache.getCabinets();
		cabinet = proxy.getCabinet();
		cab_listener = new CabinetListener();
		sty_model = con_cache.getCabinetStyleModel();
		location = new LocationPanel(s);
	}

	/** Get the SONAR type cache */
	protected TypeCache<Controller> getTypeCache() {
		return state.getConCache().getControllers();
	}

	/** Initialize the widgets on the form */
	protected void initialize() {
		super.initialize();
		io_model = new ControllerIOModel(session, proxy);
		io_model.initialize();
		cabinets.addProxyListener(cab_listener);
		comm_link.setModel(new WrapperComboBoxModel(link_model, false));
		cab_style.setModel(new WrapperComboBoxModel(sty_model, true));
		JTabbedPane tab = new JTabbedPane();
		tab.add("Setup", createSetupPanel());
		tab.add("Cabinet", createCabinetPanel());
		tab.add("I/O", createIOPanel());
		tab.add("Status", createStatusPanel());
		add(tab);
		updateAttribute(null);
		if(canUpdate())
			createSetupJobs();
		if(canActivate())
			createActivateJob();
		if(canUpdateCabinet())
			createCabinetJobs();
		if(canRequest())
			createStatusJobs();
		else {
			clearErrorBtn.setEnabled(false);
			reset.setEnabled(false);
		}
		setBackground(Color.LIGHT_GRAY);
	}

	/** Dispose of the form */
	protected void dispose() {
		io_model.dispose();
		cabinets.removeProxyListener(cab_listener);
		location.dispose();
		super.dispose();
	}

	/** Create the controller setup panel */
	protected JPanel createSetupPanel() {
		FormPanel panel = new FormPanel(canUpdate());
		panel.add("Comm Link", comm_link);
		panel.finishRow();
		panel.add("Drop", drop_id);
		panel.finishRow();
		panel.add("Password", password);
		panel.setEast();
		panel.addRow(clear_btn);
		panel.addRow("Notes", notes);
		panel.add("Active", active);
		// Add a third column to the grid bag so the drop spinner
		// does not extend across the whole form
		panel.addRow(new javax.swing.JLabel());
		return panel;
	}

	/** Check if the user can activate a controller */
	protected boolean canActivate() {
		return canUpdate("active");
	}

	/** Check if the user can update the cabinet */
	protected boolean canUpdateCabinet() {
		return session.canUpdate(cabinet);
	}

	/** Can a controller request be made */
	protected boolean canRequest() {
		return canUpdate("counters") && canUpdate("download");
	}

	/** Create the jobs for the setup panel */
	protected void createSetupJobs() {
		new ActionJob(this, comm_link) {
			public void perform() {
				proxy.setCommLink(
					(CommLink)comm_link.getSelectedItem());
			}
		};
		new ChangeJob(this, drop_id) {
			public void perform() {
				Number n = (Number)drop_id.getValue();
				proxy.setDrop(n.shortValue());
			}
		};
		new FocusJob(password) {
			public void perform() {
				if(wasLost()) {
					String pwd = new String(
						password.getPassword()).trim();
					password.setText("");
					if(pwd.length() > 0)
						proxy.setPassword(pwd);
				}
			}
		};
		new ActionJob(this, clear_btn) {
			public void perform() {
				proxy.setPassword(null);
			}
		};
		new FocusJob(notes) {
			public void perform() {
				if(wasLost())
					proxy.setNotes(notes.getText());
			}
		};
	}

	/** Create the job for the activate checkbox */
	protected void createActivateJob() {
		new ActionJob(this, active) {
			public void perform() {
				proxy.setActive(active.isSelected());
			}
		};
	}

	/** Create the cabinet panel */
	protected JPanel createCabinetPanel() {
		location.setGeoLoc(cabinet.getGeoLoc());
		location.initialize();
		location.add("Milepoint", mile);
		location.finishRow();
		location.add("Style", cab_style);
		location.finishRow();
		return location;
	}

	/** Create the jobs for the cabinet panel */
	protected void createCabinetJobs() {
		new FocusJob(mile) {
			public void perform() {
				if(wasLost()) {
					String ms = mile.getText();
					try {
						Float m = Float.valueOf(ms);
						cabinet.setMile(m);
					}
					catch(NumberFormatException e) {
						cabinet.setMile(null);
					}
				}
			}
		};
		new ActionJob(this, cab_style) {
			public void perform() {
				cabinet.setStyle((CabinetStyle)
					cab_style.getSelectedItem());
			}
		};
	}

	/** Listener for cabinet proxy changes */
	protected class CabinetListener implements ProxyListener<Cabinet> {
		public void proxyAdded(Cabinet p) {}
		public void enumerationComplete() { }
		public void proxyRemoved(Cabinet p) {}
		public void proxyChanged(Cabinet p, final String a) {
			if(p == cabinet)
				updateAttribute(a);
		}
	}

	/** Create the I/O panel */
	protected JPanel createIOPanel() {
		ZTable table = new ZTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(io_model);
		table.setColumnModel(io_model.createColumnModel());
		table.setRowHeight(ROW_HEIGHT);
		table.setVisibleRowCount(8);
		FormPanel panel = new FormPanel(true);
		panel.addRow(table);
		return panel;
	}

	/** Create the status panel */
	protected JPanel createStatusPanel() {
		JPanel buttonPnl = new JPanel();
		buttonPnl.add(clearErrorBtn);
		buttonPnl.add(reset);
		FormPanel panel = new FormPanel(canUpdate());
		panel.addRow("Version:", version);
		panel.addRow("Maint Status:", maint);
		panel.addRow("Status:", status);
		panel.addRow("Fail Time:", fail_time_lbl);
		panel.addRow("Timeout Errors:", timeout_lbl);
		panel.addRow("Checksum Errors:", checksum_lbl);
		panel.addRow("Parsing Errors:", parsing_lbl);
		panel.addRow("Controller Errors:", controller_lbl);
		panel.addRow("Successful Operations:", success_lbl);
		panel.addRow("Failed Operations:", failed_lbl);
		panel.addRow(buttonPnl);
		return panel;
	}

	/** Create the jobs for the status panel */
	protected void createStatusJobs() {
		new ActionJob(this, reset) {
			public void perform() {
				proxy.setDownload(true);
			}
		};
		new ActionJob(this, clearErrorBtn) {
			public void perform() {
				proxy.setCounters(true);
			}
		};
	}

	/** Update one attribute on the form */
	protected void doUpdateAttribute(String a) {
		if(a == null || a.equals("commLink")) {
			comm_link.setSelectedItem(proxy.getCommLink());
			drop_model = new DropNumberModel(
				proxy.getCommLink(), getTypeCache(),
				proxy.getDrop());
			drop_id.setModel(drop_model);
		}
		if(a == null || a.equals("drop"))
			drop_id.setValue(proxy.getDrop());
		if(a == null || a.equals("notes"))
			notes.setText(proxy.getNotes());
		if(a == null || a.equals("active"))
			active.setSelected(proxy.getActive());
		if(a == null || a.equals("version"))
			version.setText(proxy.getVersion());
		if(a == null || a.equals("maint"))
			maint.setText(proxy.getMaint());
		if(a == null || a.equals("status"))
			status.setText(proxy.getStatus());
		if(a == null || a.equals("failTime")) {
			Long ft = proxy.getFailTime();
			if(ft != null)
				fail_time_lbl.setText(new Date(ft).toString());
			else
				fail_time_lbl.setText("");
		}
		if(a == null || a.equals("timeoutErr")) {
			timeout_lbl.setText(String.valueOf(
				proxy.getTimeoutErr()));
		}
		if(a == null || a.equals("checksumErr")) {
			checksum_lbl.setText(String.valueOf(
				proxy.getChecksumErr()));
		}
		if(a == null || a.equals("parsingErr")) {
			parsing_lbl.setText(String.valueOf(
				proxy.getParsingErr()));
		}
		if(a == null || a.equals("controllerErr")) {
			controller_lbl.setText(String.valueOf(
				proxy.getControllerErr()));
		}
		if(a == null || a.equals("successOps")) {
			success_lbl.setText(String.valueOf(
				proxy.getSuccessOps()));
		}
		if(a == null || a.equals("failedOps")) {
			failed_lbl.setText(String.valueOf(
				proxy.getFailedOps()));
		}
		if(a == null || a.equals("mile")) {
			Float m = cabinet.getMile();
			if(m == null)
				mile.setText("");
			else
				mile.setText(m.toString());
		}
		if(a == null || a.equals("style"))
			cab_style.setSelectedItem(cabinet.getStyle());
	}
}
