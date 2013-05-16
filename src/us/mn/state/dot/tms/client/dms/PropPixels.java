/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
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

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import us.mn.state.dot.tms.Base64;
import us.mn.state.dot.tms.BitmapGraphic;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.FormPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.utils.I18N;

/**
 * PropPixels is a GUI panel for displaying pixel data on a DMS properties
 * form.
 *
 * @author Douglas Lau
 */
public class PropPixels extends FormPanel {

	/** Unknown value string */
	static private final String UNKNOWN = "???";

	/** Bad pixel count label */
	private final JLabel bad_pixels_lbl = createValueLabel();

	/** Stuck off pixel panel */
	private final SignPixelPanel stuck_off_pnl = new SignPixelPanel(100,
		400, true);

	/** Stuck on pixel panel */
	private final SignPixelPanel stuck_on_pnl = new SignPixelPanel(100,
		400, true);

	/** Action to query pixel failures */
	private final IAction query_pixels = new IAction("dms.query.pixels") {
		@Override protected void do_perform() {
			dms.setDeviceRequest(DeviceRequest.
				QUERY_PIXEL_FAILURES.ordinal());
		}
	};

	/** Action to test pixel failures */
	private final IAction test_pixels = new IAction("dms.test.pixels") {
		@Override protected void do_perform() {
			dms.setDeviceRequest(
				DeviceRequest.TEST_PIXELS.ordinal());
		}
	};

	/** User session */
	private final Session session;

	/** DMS to display */
	private final DMS dms;

	/** Create a new DMS properties pixels panel */
	public PropPixels(Session s, DMS sign) {
		super(true);
		session = s;
		dms = sign;
	}

	/** Initialize the widgets on the panel */
	public void initialize() {
		JPanel b_pnl = new JPanel();
		b_pnl.add(new JButton(query_pixels));
		b_pnl.add(new JButton(test_pixels));
		addRow(I18N.get("dms.pixel.errors"), bad_pixels_lbl);
		setFill();
		addRow(createTitledPanel("dms.pixel.errors.off",stuck_off_pnl));
		setFill();
		addRow(createTitledPanel("dms.pixel.errors.on", stuck_on_pnl));
		setCenter();
		add(b_pnl);
		updateAttribute(null);
	}

	/** Create a panel with a titled border */
	private JPanel createTitledPanel(String text_id, JPanel p) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(I18N.get(
			text_id)));
		panel.add(p, BorderLayout.CENTER);
		return panel;
	}

	/** Update one attribute on the panel */
	public void updateAttribute(String a) {
		// NOTE: messageCurrent attribute changes after all sign
		//       dimension attributes are updated.
		if(a == null || a.equals("pixelStatus") ||
		   a.equals("messageCurrent"))
			updatePixelStatus();
		if(a == null) {
			boolean r = canRequest();
			query_pixels.setEnabled(r);
			test_pixels.setEnabled(r);
		}
	}

	/** Update the pixel status */
	private void updatePixelStatus() {
		updatePixelPanel(stuck_off_pnl);
		updatePixelPanel(stuck_on_pnl);
		String[] pixels = dms.getPixelStatus();
		if(pixels != null && pixels.length == 2) {
			try {
				updatePixelStatus(pixels);
				return;
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		stuck_off_pnl.setGraphic(null);
		stuck_on_pnl.setGraphic(null);
		bad_pixels_lbl.setText(UNKNOWN);
	}

	/** Update the pixel status */
	private void updatePixelStatus(String[] pixels) throws IOException {
		BitmapGraphic stuckOff = createBlankBitmap();
		BitmapGraphic stuckOn = createBlankBitmap();
		if(stuckOff == null || stuckOn == null)
			return;
		byte[] b_off = Base64.decode(pixels[DMS.STUCK_OFF_BITMAP]);
		if(b_off.length == stuckOff.length())
			stuckOff.setPixels(b_off);
		stuck_off_pnl.setGraphic(stuckOff);
		byte[] b_on = Base64.decode(pixels[DMS.STUCK_ON_BITMAP]);
		if(b_on.length == stuckOn.length())
			stuckOn.setPixels(b_on);
		stuck_on_pnl.setGraphic(stuckOn);
		int n_off = stuckOff.getLitCount();
		int n_on = stuckOn.getLitCount();
		bad_pixels_lbl.setText(String.valueOf(n_off + n_on));
	}

	/** Create a blank bitmap */
	private BitmapGraphic createBlankBitmap() {
		Integer w = dms.getWidthPixels();	// Avoid race
		Integer h = dms.getHeightPixels();	// Avoid race
		if(w != null && h != null)
			return new BitmapGraphic(w, h);
		else
			return null;
	}

	/** Update the dimensions of a sign pixel panel */
	private void updatePixelPanel(SignPixelPanel p) {
		p.setDimensions(dms);
		p.repaint();
	}

	/** Check if the user can update an attribute */
	private boolean canUpdate(String aname) {
		return session.canUpdate(dms, aname);
	}

	/** Check if the user can make device requests */
	private boolean canRequest() {
		return canUpdate("deviceRequest");
	}
}
