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
package us.mn.state.dot.tms.client.dms;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import us.mn.state.dot.tms.Beacon;
import us.mn.state.dot.tms.CameraPreset;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.camera.PresetComboRenderer;
import us.mn.state.dot.tms.client.comm.ControllerForm;
import us.mn.state.dot.tms.client.roads.LocationPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.IComboBoxModel;
import us.mn.state.dot.tms.utils.I18N;

/**
 * PropLocation is a GUI panel for displaying and editing locations on a DMS
 * properties form.
 *
 * @author Douglas Lau
 */
public class PropLocation extends LocationPanel {

	/** Notes text area */
	private final JTextArea notes_txt = new JTextArea(3, 24);

	/** External beacon combo box model */
	private final IComboBoxModel<Beacon> beacon_mdl;

	/** External beacon action */
	private final IAction beacon_act = new IAction("dms.beacon.ext") {
		protected void doActionPerformed(ActionEvent e) {
			dms.setBeacon(beacon_mdl.getSelectedProxy());
		}
	};

	/** External beacon combo box */
	private final JComboBox beacon_cbx = new JComboBox();

	/** Camera preset combo box model */
	private final IComboBoxModel<CameraPreset> preset_mdl;

	/** Camera preset action */
	private final IAction preset_act = new IAction("camera.preset") {
		protected void doActionPerformed(ActionEvent e) {
			dms.setPreset(preset_mdl.getSelectedProxy());
		}
	};

	/** Camera preset combo box */
	private final JComboBox preset_cbx = new JComboBox();

	/** Controller action */
	private final IAction controller = new IAction("controller") {
		protected void doActionPerformed(ActionEvent e) {
			controllerPressed();
		}
	};

	/** Controller lookup button pressed */
	private void controllerPressed() {
		Controller c = dms.getController();
		if (c != null) {
			session.getDesktop().show(
				new ControllerForm(session, c));
		}
	}

	/** DMS to display */
	private final DMS dms;

	/** Create a new DMS properties location panel */
	public PropLocation(Session s, DMS sign) {
		super(s);
		dms = sign;
		beacon_mdl = new IComboBoxModel<Beacon>(state.getBeaconModel());
		preset_mdl = new IComboBoxModel<CameraPreset>(
			state.getCamCache().getPresetModel());
	}

	/** Initialize the widgets on the form */
	@Override
	public void initialize() {
		super.initialize();
		beacon_cbx.setModel(beacon_mdl);
		preset_cbx.setModel(preset_mdl);
		preset_cbx.setRenderer(new PresetComboRenderer());
		add("device.notes");
		add(notes_txt, Stretch.FULL);
		add("dms.beacon.ext");
		add(beacon_cbx, Stretch.LAST);
		add("camera.preset");
		add(preset_cbx, Stretch.LAST);
		add(new JButton(controller), Stretch.RIGHT);
		setGeoLoc(dms.getGeoLoc());
	}

	/** Create the widget jobs */
	@Override
	protected void createJobs() {
		super.createJobs();
		notes_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				dms.setNotes(notes_txt.getText());
			}
		});
	}

	/** Update the edit mode */
	@Override
	public void updateEditMode() {
		super.updateEditMode();
		notes_txt.setEnabled(canUpdate("notes"));
		beacon_act.setEnabled(canUpdate("beacon"));
		preset_act.setEnabled(canUpdate("preset"));
	}

	/** Update one attribute on the form tab */
	public void updateAttribute(String a) {
		if (a == null || a.equals("controller"))
			controller.setEnabled(dms.getController() != null);
		if (a == null || a.equals("notes"))
			notes_txt.setText(dms.getNotes());
		if (a == null || a.equals("beacon")) {
			beacon_cbx.setAction(null);
			beacon_mdl.setSelectedItem(dms.getBeacon());
			beacon_cbx.setAction(beacon_act);
		}
		if (a == null || a.equals("preset")) {
			preset_cbx.setAction(null);
			preset_mdl.setSelectedItem(dms.getPreset());
			preset_cbx.setAction(preset_act);
		}
	}

	/** Check if the user can update an attribute */
	private boolean canUpdate(String aname) {
		return session.canUpdate(dms, aname);
	}
}
