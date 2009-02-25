/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SystemAttributeHelper;
import us.mn.state.dot.tms.client.toast.FormPanel;

/**
 * A SingleSignTab is a GUI component for displaying the status of a single
 * selected DMS within the DMS dispatcher.
 *
 * @author Douglas Lau
 */
public class SingleSignTab extends FormPanel {

	/** Get the verification camera name */
	static protected String getCameraName(DMS proxy) {
		Camera camera = proxy.getCamera();
		if(camera == null)
			return " ";
		else
			return camera.getName();
	}

	/** Get the controller status */
	static protected String getControllerStatus(DMS proxy) {
		Controller c = proxy.getController();
		if(c == null)
			return "???";
		else
			return c.getStatus();
	}

	/** Formatter for displaying the hour and minute */
	static protected final SimpleDateFormat HOUR_MINUTE =
		new SimpleDateFormat("HH:mm");

	/** Milliseconds per day */
	static protected final long MS_PER_DAY = 24 * 60 * 60 * (long)1000;

	/** Format the message deployed time */
	static protected String formatDeploy(DMS dms) {
		long deploy = dms.getDeployTime();
		if(System.currentTimeMillis() < deploy + MS_PER_DAY)
			return HOUR_MINUTE.format(deploy);
		else
			return "";
	}

	/** Format the message expriation */
	static protected String formatExpires(DMS dms) {
		SignMessage m = dms.getMessageCurrent();
		Integer duration = m.getDuration();
		if(duration == null)
			return "";
		if(duration <= 0 || duration >= 65535)
			return "";
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dms.getDeployTime());
		c.add(Calendar.MINUTE, duration);
		return HOUR_MINUTE.format(c.getTime());
	}

	/** Displays the id of the DMS */
	protected final JTextField nameTxt = createTextField();

	/** Displays the verify camera for the DMS */
	protected final JTextField cameraTxt = createTextField();

	/** Displays the location of the DMS */
	protected final JTextField locationTxt = createTextField();

	/** Displays the brightness of the DMS */
	protected final JTextField brightnessTxt = createTextField();

	/** Displays the current operation of the DMS */
	protected final JTextField operationTxt = createTextField();

	/** Displays the controller status (optional) */
	protected final JTextField statusTxt = createTextField();

	/** Displays the current message deploy time */
	protected final JTextField deployTxt = createTextField();

	/** Displays the current message expiration */
	protected final JTextField expiresTxt = createTextField();

	/** DMS dispatcher */
	protected final DMSDispatcher dispatcher;

	/** Panel for drawing current pixel status */
	protected final SignPixelPanel currentPnl = new SignPixelPanel(true);

	/** Panel for drawing preview pixel status */
	protected final SignPixelPanel previewPnl = new SignPixelPanel(true,
		new Color(0, 0, 0.4f));

	/** Tabbed pane for current/preview panels */
	protected final JTabbedPane tab = new JTabbedPane();

	/** Preview mode */
	protected boolean preview;

	/** Adjusting counter */
	protected int adjusting = 0;

	/** Create a new single sign tab */
	public SingleSignTab(DMSDispatcher d) {
		super(true);
		dispatcher = d;
		currentPnl.setPreferredSize(new Dimension(390, 108));
		previewPnl.setPreferredSize(new Dimension(390, 108));
		add("Name", nameTxt);
		addRow("Camera", cameraTxt);
		add("Location", locationTxt);
		addRow("Brightness", brightnessTxt);
		if(SystemAttributeHelper.isDmsStatusEnabled()) {
			add("Operation", operationTxt);
			addRow("Status", statusTxt);
		} else
			addRow("Operation", operationTxt);
		add("Deployed", deployTxt);
		addRow("Expires", expiresTxt);
		tab.add("Current", currentPnl);
		tab.add("Preview", previewPnl);
		addRow(tab);
		tab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(adjusting == 0)
					togglePreview();
			}
		});
	}

	/** Get the panel for drawing current pixel status */
	public SignPixelPanel getCurrentPanel() {
		return currentPnl;
	}

	/** Get the panel for drawing preview pixel status */
	public SignPixelPanel getPreviewPanel() {
		return previewPnl;
	}

	/** Toggle the preview mode */
	protected void togglePreview() {
		preview = !preview;
		dispatcher.selectPreview(preview);
	}

	/** Select the preview (or current) tab */
	public void selectPreview(boolean p) {
		preview = p;
		if(adjusting == 0) {
			adjusting++;
			if(p)
				tab.setSelectedComponent(previewPnl);
			else
				tab.setSelectedComponent(currentPnl);
			dispatcher.selectPreview(p);
			adjusting--;
		}
	}

	/** Clear the selected DMS */
	public void clearSelected() {
		nameTxt.setText("");
		cameraTxt.setText("");
		locationTxt.setText("");
		brightnessTxt.setText("");
		operationTxt.setText("");
		statusTxt.setText("");
		deployTxt.setText("");
		expiresTxt.setText("");
	}

	/** Update one attribute on the form */
	public void updateAttribute(DMS dms, String a) {
		if(a == null || a.equals("name"))
			nameTxt.setText(dms.getName());
		if(a == null || a.equals("camera"))
			cameraTxt.setText(getCameraName(dms));
		// FIXME: this won't update when geoLoc attributes change
		if(a == null || a.equals("geoLoc")) {
			locationTxt.setText(GeoLocHelper.getDescription(
				dms.getGeoLoc()));
		}
		if(a == null || a.equals("lightOutput")) {
			Integer o = dms.getLightOutput();
			if(o != null)
				brightnessTxt.setText("" + o + "%");
			else
				brightnessTxt.setText("");
		}
		if(a == null || a.equals("operation")) {
			String status = getControllerStatus(dms);
			if("".equals(status)) {
				operationTxt.setForeground(null);
				operationTxt.setBackground(null);
			} else {
				operationTxt.setForeground(Color.WHITE);
				operationTxt.setBackground(Color.GRAY);
			}
			operationTxt.setText(dms.getOperation());
			statusTxt.setText(status);
		}
		if(a == null || a.equals("messageCurrent")) {
			deployTxt.setText(formatDeploy(dms));
			expiresTxt.setText(formatExpires(dms));
		}
	}
}
