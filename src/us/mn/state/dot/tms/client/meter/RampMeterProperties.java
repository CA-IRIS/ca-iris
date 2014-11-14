/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.meter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Beacon;
import us.mn.state.dot.tms.CameraPreset;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.MeterAlgorithm;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.RampMeterHelper;
import us.mn.state.dot.tms.RampMeterLock;
import us.mn.state.dot.tms.RampMeterQueue;
import us.mn.state.dot.tms.RampMeterType;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.camera.PresetComboRenderer;
import us.mn.state.dot.tms.client.comm.ControllerForm;
import us.mn.state.dot.tms.client.proxy.SonarObjectForm;
import us.mn.state.dot.tms.client.roads.LocationPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.client.widget.IPanel.Stretch;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;
import us.mn.state.dot.tms.utils.I18N;

/**
 * This is a form for viewing and editing the properties of a ramp meter.
 *
 * @author Douglas Lau
 */
public class RampMeterProperties extends SonarObjectForm<RampMeter> {

	/** Location panel */
	private final LocationPanel loc_pnl;

	/** Notes text area */
	private final JTextArea notes_txt = new JTextArea(3, 24);

	/** Camera preset action */
	private final IAction preset = new IAction("camera.preset") {
		protected void doActionPerformed(ActionEvent e) {
			Object o = preset_mdl.getSelectedItem();
			if (o instanceof CameraPreset)
				proxy.setPreset((CameraPreset)o);
			else
				proxy.setPreset(null);
		}
	};

	/** Camera preset combo box */
	private final JComboBox preset_cbx = new JComboBox();

	/** Camera preset combo box model */
	private final WrapperComboBoxModel preset_mdl;

	/** Controller action */
	private final IAction controller = new IAction("controller") {
		protected void doActionPerformed(ActionEvent e) {
			Controller c = proxy.getController();
			if(c != null) {
				SmartDesktop sd = session.getDesktop();
				sd.show(new ControllerForm(session, c));
			}
		}
	};

	/** Meter type action */
	private final IAction meter_type = new IAction("ramp.meter.type") {
		protected void doActionPerformed(ActionEvent e) {
			int t = meter_type_cbx.getSelectedIndex();
			if(t >= 0)
				proxy.setMeterType(t);
		}
	};

	/** Meter type combo box component */
	private final JComboBox meter_type_cbx = new JComboBox(
		RampMeterType.getDescriptions());

	/** Field for Storage length (feet) */
	private final JTextField storage_txt = new JTextField(6);

	/** Field for Maximum wait time (seconds) */
	private final JTextField max_wait_txt = new JTextField(5);

	/** Metering algorithm action */
	private final IAction algorithm = new IAction("ramp.meter.algorithm") {
		protected void doActionPerformed(ActionEvent e) {
			int a = algorithm_cbx.getSelectedIndex();
			if(a >= 0)
				proxy.setAlgorithm(a);
		}
	};

	/** Combo box for metering algorithm */
	private final JComboBox algorithm_cbx = new JComboBox(
		MeterAlgorithm.getDescriptions());

	/** Field for AM target rate */
	private final JTextField am_target_txt = new JTextField(6);

	/** Field for PM target rate */
	private final JTextField pm_target_txt = new JTextField(6);

	/** Beacon action */
	private final IAction beacon = new IAction("ramp.meter.beacon") {
		protected void doActionPerformed(ActionEvent e) {
			Object o = beacon_mdl.getSelectedItem();
			if (o instanceof Beacon)
				proxy.setBeacon((Beacon)o);
			else
				proxy.setBeacon(null);
		}
	};

	/** Advance warning beacon combo box */
	private final JComboBox beacon_cbx = new JComboBox();

	/** Advance warning beacon combo box model */
	private final WrapperComboBoxModel beacon_mdl;

	/** Release rate component */
	private final JLabel release_lbl = new JLabel();

	/** Cycle time component */
	private final JLabel cycle_lbl = new JLabel();

	/** Queue label component */
	private final JLabel queue_lbl = new JLabel();

	/** Meter lock combo box component */
	private final JComboBox lock_cmb = new JComboBox(
		RampMeterLock.getDescriptions());

	/** Lock meter action */
	private final LockMeterAction lock_action;

	/** Operation description label */
	private final JLabel op_lbl = new JLabel();

	/** Status component */
	private final JLabel status_lbl = new JLabel();

	/** Send settings action */
	private final IAction settings = new IAction("device.send.settings") {
		protected void doActionPerformed(ActionEvent e) {
			proxy.setDeviceRequest(DeviceRequest.
				SEND_SETTINGS.ordinal());
		}
	};

	/** Create a new ramp meter properties form */
	public RampMeterProperties(Session s, RampMeter meter) {
		super(I18N.get("ramp.meter.long") + ": ", s, meter);
		loc_pnl = new LocationPanel(s);
		lock_action = new LockMeterAction(meter, lock_cmb,
			isUpdatePermitted("mLock"));
		preset_mdl = new WrapperComboBoxModel(
			state.getCamCache().getPresetModel());
		beacon_mdl = new WrapperComboBoxModel(
			state.getBeaconModel());
	}

	/** Get the SONAR type cache */
	@Override
	protected TypeCache<RampMeter> getTypeCache() {
		return state.getRampMeters();
	}

	/** Initialize the widgets on the form */
	@Override
	protected void initialize() {
		JTabbedPane tab = new JTabbedPane();
		tab.add(I18N.get("location"), createLocationPanel());
		tab.add(I18N.get("device.setup"), createSetupPanel());
		tab.add(I18N.get("device.status"), createStatusPanel());
		add(tab);
		if(canUpdate())
			createUpdateJobs();
		settings.setEnabled(isUpdatePermitted("deviceRequest"));
		super.initialize();
	}

	/** Create the location panel */
	private JPanel createLocationPanel() {
		preset_cbx.setModel(preset_mdl);
		preset_cbx.setRenderer(new PresetComboRenderer());
		loc_pnl.initialize();
		loc_pnl.add("device.notes");
		loc_pnl.add(notes_txt, Stretch.FULL);
		loc_pnl.add("camera.preset");
		loc_pnl.add(preset_cbx, Stretch.LAST);
		loc_pnl.add(new JButton(controller), Stretch.RIGHT);
		loc_pnl.setGeoLoc(proxy.getGeoLoc());
		return loc_pnl;
	}

	/** Create the widget jobs */
	private void createUpdateJobs() {
		notes_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				proxy.setNotes(notes_txt.getText());
			}
		});
		storage_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				proxy.setStorage(Integer.parseInt(
					storage_txt.getText()));
			}
		});
		max_wait_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				proxy.setMaxWait(Integer.parseInt(
					max_wait_txt.getText()));
			}
		});
		am_target_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				proxy.setAmTarget(Integer.parseInt(
					am_target_txt.getText()));
			}
		});
		pm_target_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				proxy.setPmTarget(Integer.parseInt(
					pm_target_txt.getText()));
			}
		});
	}

	/** Create ramp meter setup panel */
	private JPanel createSetupPanel() {
		beacon_cbx.setModel(beacon_mdl);
		IPanel p = new IPanel();
		p.add("ramp.meter.type");
		p.add(meter_type_cbx, Stretch.LAST);
		p.add("ramp.meter.storage");
		p.add(storage_txt, Stretch.LAST);
		p.add("ramp.meter.max.wait");
		p.add(max_wait_txt, Stretch.LAST);
		p.add("ramp.meter.algorithm");
		p.add(algorithm_cbx, Stretch.LAST);
		p.add("ramp.meter.target.am");
		p.add(am_target_txt, Stretch.LAST);
		p.add("ramp.meter.target.pm");
		p.add(pm_target_txt, Stretch.LAST);
		p.add("ramp.meter.beacon");
		p.add(beacon_cbx, Stretch.LAST);
		return p;
	}

	/** Create ramp meter status panel */
	private JPanel createStatusPanel() {
		IPanel p = new IPanel();
		p.add("ramp.meter.rate");
		p.add(release_lbl, Stretch.LAST);
		p.add("ramp.meter.cycle");
		p.add(cycle_lbl, Stretch.LAST);
		p.add("ramp.meter.queue");
		p.add(queue_lbl, Stretch.LAST);
		p.add("ramp.meter.lock");
		p.add(lock_cmb, Stretch.LAST);
		p.add("device.operation");
		p.add(op_lbl, Stretch.LAST);
		// Make label opaque so that we can set the background color
		op_lbl.setOpaque(true);
		p.add("device.status");
		p.add(status_lbl, Stretch.LAST);
		p.add(new JButton(settings), Stretch.RIGHT);
		return p;
	}

	/** Update one attribute on the form */
	@Override
	protected void doUpdateAttribute(String a) {
		if(a == null || a.equals("controller"))
			controller.setEnabled(proxy.getController() != null);
		if(a == null || a.equals("notes")) {
			notes_txt.setEnabled(canUpdate("notes"));
			notes_txt.setText(proxy.getNotes());
		}
		if (a == null || a.equals("preset")) {
			preset_cbx.setAction(null);
			preset_mdl.setSelectedItem(proxy.getPreset());
			preset.setEnabled(canUpdate("preset"));
			preset_cbx.setAction(preset);
		}
		updateComboBox(a, "meterType", meter_type_cbx,
			proxy.getMeterType(), meter_type);
		if(a == null || a.equals("storage")) {
			storage_txt.setEnabled(canUpdate("storage"));
			storage_txt.setText("" + proxy.getStorage());
		}
		if(a == null || a.equals("maxWait")) {
			max_wait_txt.setEnabled(canUpdate("maxWait"));
			max_wait_txt.setText("" + proxy.getMaxWait());
		}
		updateComboBox(a, "algorithm", algorithm_cbx,
			proxy.getAlgorithm(), algorithm);
		if(a == null || a.equals("amTarget")) {
			am_target_txt.setEnabled(canUpdate("amTarget"));
			am_target_txt.setText("" + proxy.getAmTarget());
		}
		if(a == null || a.equals("pmTarget")) {
			pm_target_txt.setEnabled(canUpdate("pmTarget"));
			pm_target_txt.setText("" + proxy.getPmTarget());
		}
		if (a == null || a.equals("beacon")) {
			beacon_cbx.setAction(null);
			beacon_mdl.setSelectedItem(proxy.getBeacon());
			beacon.setEnabled(canUpdate("beacon"));
			beacon_cbx.setAction(beacon);
		}
		if(a == null || a.equals("rate")) {
			Integer rt = proxy.getRate();
			cycle_lbl.setText(RampMeterHelper.formatCycle(rt));
			release_lbl.setText(RampMeterHelper.formatRelease(rt));
		}
		if(a == null || a.equals("queue")) {
			RampMeterQueue q = RampMeterQueue.fromOrdinal(
				proxy.getQueue());
			queue_lbl.setText(q.description);
		}
		updateComboBox(a, "mLock", lock_cmb, getMLock(), lock_action);
		if(a == null || a.equals("operation"))
			op_lbl.setText(proxy.getOperation());
		if(a == null || a.equals("styles")) {
			if(ItemStyle.FAILED.checkBit(proxy.getStyles())) {
				op_lbl.setForeground(Color.WHITE);
				op_lbl.setBackground(Color.GRAY);
			} else {
				op_lbl.setForeground(null);
				op_lbl.setBackground(null);
			}
			status_lbl.setText(ControllerHelper.getStatus(
				proxy.getController()));
		}
	}

	/** Get meter lock index */
	private int getMLock() {
		Integer ml = proxy.getMLock();
		return ml != null ? ml : 0;
	}
}
