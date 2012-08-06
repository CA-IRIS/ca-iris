/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.detector;

import javax.swing.JButton;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyTableForm;
import us.mn.state.dot.tms.client.widget.FormPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A form for displaying and editing stations
 *
 * @author Douglas Lau
 */
public class StationForm extends ProxyTableForm<Station> {

	/** Check if the user is permitted to use the form */
	static public boolean isPermitted(Session s) {
		return s.canRead(Station.SONAR_TYPE);
	}

	/** User session */
	protected final Session session;

	/** Action to display the r_node */
	private final IAction r_node = new IAction("r_node") {
		protected void do_perform() {
			showRNode();
		}
	};

	/** Create a new station form */
	public StationForm(Session s) {
		super(I18N.get("detector.station.plural"), new StationModel(s));
		session = s;
	}

	/** Show the r_node for the selected station */
	protected void showRNode() {
		Station s = getSelectedProxy();
		if(s == null)
			return;
		R_Node n = s.getR_Node();
		session.getR_NodeManager().getSelectionModel().setSelected(n);
	}

	/** Add the table to the panel */
	protected void addTable(FormPanel panel) {
		panel.addRow(table);
		panel.addRow(new JButton(r_node));
	}

	/** Get the row height */
	protected int getRowHeight() {
		return 20;
	}

	/** Select a new proxy */
	protected void selectProxy() {
		r_node.setEnabled(getSelectedProxy() != null);
	}
}
