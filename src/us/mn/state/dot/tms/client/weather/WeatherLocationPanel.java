/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016 California Department of Transportation
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
package us.mn.state.dot.tms.client.weather;

import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.roads.LocationPanel;
import us.mn.state.dot.tms.utils.I18N;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A panel for viewing and editing weather sensor parameters.
 *
 * @author Dan Rossiter
 */
public class WeatherLocationPanel extends LocationPanel {

	/** Component for editing notes */
	private final JTextField notes_txt = new JTextField(20);

	/** Label for r_node name */
	private final JLabel name_lbl = createValueLabel();

	/** Node being edited */
	private WeatherSensor sensor;

	/** Create a new weather sensor location panel */
	public WeatherLocationPanel(Session s) {
		super(s);
	}

	/** Initialize the widgets on the panel */
	@Override
	public void initialize() {
		super.initialize();
		add("device.notes");
		add(notes_txt, Stretch.FULL);
		add(name_lbl, Stretch.CENTER);
		clear();
	}

	/** Create the jobs */
	@Override
	protected void createJobs() {
		super.createJobs();
		notes_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setNotes(notes_txt.getText());
			}
		});
	}

	/** Set the sensor notes */
	private void setNotes(String nt) {
		WeatherSensor n = sensor;
		if (n != null)
			n.setNotes(nt);
	}

	/** Update one attribute */
	public void update(WeatherSensor n, String a) {
		if (a == null) {
			sensor = n;
			name_lbl.setText(n.getName());
		}
		if (a == null || a.equals("notes")) {
			notes_txt.setEnabled(canUpdate(n, "notes"));
			notes_txt.setText(n.getNotes());
		}
	}

	/** Clear all attributes */
	@Override
	public void clear() {
		super.clear();
		sensor = null;
		name_lbl.setText(I18N.get("weather.name.none"));
		notes_txt.setEnabled(false);
		notes_txt.setText("");
	}

	/** Test if the user can update an attribute */
	private boolean canUpdate(WeatherSensor n, String a) {
		return session.canUpdate(n, a);
	}
}
