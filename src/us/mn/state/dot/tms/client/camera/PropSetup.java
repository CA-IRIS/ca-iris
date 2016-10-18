/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       California Department of Transportation
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
package us.mn.state.dot.tms.client.camera;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.EncoderType;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.IPanel;

/**
 * Camera properties setup panel.
 *
 * @author Douglas Lau
 * @author Jacob Barde
 */
public class PropSetup extends IPanel {

	/** Video stream encoder host (and port) */
	private final JTextField encoder_txt = new JTextField("", 20);

	/** Model for encoder channel spinner */
	private final SpinnerNumberModel num_model =
		new SpinnerNumberModel(1, 0, 10, 1);

	/** Encoder channel spinner */
	private final JSpinner enc_chn_spn = new JSpinner(num_model);

	/** Encoder type combobox */
	private final JComboBox<EncoderType> enc_type_cbx =
		new JComboBox<EncoderType>(EncoderType.values());

	/** Encoder type action */
	private final IAction enc_type_act = new IAction("camera.encoder.type"){
		protected void doActionPerformed(ActionEvent e) {
		      camera.setEncoderType(enc_type_cbx.getSelectedIndex());
		}
		@Override
		protected void doUpdateSelected() {
			enc_type_cbx.setSelectedIndex(camera.getEncoderType());
		}
	};

	/** Checkbox to allow publishing camera images */
	private final JCheckBox publish_chk = new JCheckBox(new IAction(null) {
		protected void doActionPerformed(ActionEvent e) {
			camera.setPublish(publish_chk.isSelected());
		}
	});

	/** camera shift schedule minutes past the hour */
	private final SpinnerNumberModel shift_schedule_num_model = new SpinnerNumberModel(0, -1, 59, 1);

	/** Encoder channel spinner */
	private final JSpinner shift_schedule_spn = new JSpinner(shift_schedule_num_model);

	/** User session */
	private final Session session;

	/** Camera proxy */
	private final Camera camera;

	/** Create a new camera properties setup panel */
	public PropSetup(Session s, Camera c) {
		session = s;
		camera = c;
	}

	/** Initialize the widgets on the panel */
	@Override
	public void initialize() {
		super.initialize();
		enc_type_cbx.setAction(enc_type_act);
		add("camera.encoder");
		add(encoder_txt, Stretch.LAST);
		add("camera.encoder.channel");
		add(enc_chn_spn, Stretch.LAST);
		add("camera.encoder.type");
		add(enc_type_cbx, Stretch.LAST);
		add("camera.publish");
		add(publish_chk, Stretch.LAST);
		add("camera.shift.schedule");
		add(shift_schedule_spn, Stretch.LAST);
		createJobs();
	}

	/** Create jobs */
	private void createJobs() {
		encoder_txt.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				camera.setEncoder(encoder_txt.getText());
			}
		});
		enc_chn_spn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Number c = (Number) enc_chn_spn.getValue();
				camera.setEncoderChannel(c.intValue());
			}
		});
		shift_schedule_spn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Number c = (Number) shift_schedule_spn.getValue();
				camera.setShiftSchedule( (c.intValue() >= 0) ? c.intValue() : null);
			}
		});
	}

	/** Update the edit mode */
	public void updateEditMode() {
		encoder_txt.setEnabled(canUpdate("encoder"));
		enc_chn_spn.setEnabled(canUpdate("encoderChannel"));
		enc_type_act.setEnabled(canUpdate("encoderType"));
		publish_chk.setEnabled(canUpdate("publish"));
		shift_schedule_spn.setEnabled(canUpdate("shift_schedule"));
	}

	/** Update one attribute on the form tab */
	public void updateAttribute(String a) {
		if (a == null || a.equals("encoder"))
			encoder_txt.setText(camera.getEncoder());
		if (a == null || a.equals("encoderChannel"))
			enc_chn_spn.setValue(camera.getEncoderChannel());
		if (a == null || a.equals("encoderType"))
			enc_type_act.updateSelected();
		if (a == null || a.equals("publish"))
			publish_chk.setSelected(camera.getPublish());
		if (a == null || a.equals("shift_schedule")) {
			Integer v = camera.getShiftSchedule();
			shift_schedule_spn.setValue( (v == null) ? -1 : v);
		}
	}

	/** Check if the user can update an attribute */
	private boolean canUpdate(String aname) {
		return session.canUpdate(camera, aname);
	}
}
