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
package us.mn.state.dot.tms.client.lcs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.LCS;
import us.mn.state.dot.tms.LCSArray;
import us.mn.state.dot.tms.LCSArrayHelper;
import us.mn.state.dot.tms.LCSArrayLock;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.camera.CameraSelectAction;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.proxy.ProxyView;
import us.mn.state.dot.tms.client.proxy.ProxyWatcher;
import us.mn.state.dot.tms.client.roads.LaneConfigurationPanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.IPanel;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;
import us.mn.state.dot.tms.utils.I18N;

/**
 * GUI for dispatching LCS arrays.
 *
 * @author Douglas Lau
 */
public class LcsDispatcher extends IPanel implements ProxyView<LCSArray> {

	/** Size in pixels for each LCS in array */
	static private final int LCS_SIZE = UI.scaled(44);

	/** Current session */
	private final Session session;

	/** LCS Array manager */
	private final LCSArrayManager manager;

	/** Selection model */
	private final ProxySelectionModel<LCSArray> sel_model;

	/** Selection listener */
	private final ProxySelectionListener<LCSArray> sel_listener =
		new ProxySelectionListener<LCSArray>()
	{
		public void selectionAdded(LCSArray la) {
			setSelected(getSelected());
		}
		public void selectionRemoved(LCSArray la) {
			setSelected(getSelected());
		}
	};

	/** Name of the selected LCS array */
	private final JLabel name_lbl = createValueLabel();

	/** Verify camera button */
	private final JButton camera_btn = new JButton();

	/** Location of LCS array */
	private final JLabel location_lbl = createValueLabel();

	/** Status of selected LCS array */
	private final JLabel status_lbl = createValueLabel();

	/** Operation of selected LCS array */
	private final JLabel operation_lbl = createValueLabel();

	/** LCS lock combo box component */
	private final JComboBox lock_cmb = new JComboBox(
		LCSArrayLock.getDescriptions());

	/** Lane configuration panel */
	private final LaneConfigurationPanel lane_config =
		new LaneConfigurationPanel(LCS_SIZE, true);

	/** Panel for drawing an LCS array */
	private final LCSArrayPanel lcs_pnl = new LCSArrayPanel(LCS_SIZE);

	/** LCS indicaiton selector */
	private final IndicationSelector ind_selector =
		new IndicationSelector(LCS_SIZE);

	/** Action to send new indications to the LCS array */
	private final IAction send = new IAction("lcs.send") {
		protected void doActionPerformed(ActionEvent e) {
			sendIndications();
		}
	};

	/** Button to blank the LCS array indications */
	private final JButton blank_btn = new JButton();

	/** Action to blank selected LCS */
	private final BlankLcsAction blank;

	/** Currently logged in user */
	private final User user;

	/** Proxy watcher */
	private final ProxyWatcher<LCSArray> watcher;

	/** Currently selected LCS array */
	private LCSArray lcs_array;

	/** Create a new LCS dispatcher */
	public LcsDispatcher(Session s, LCSArrayManager m) {
		session = s;
		manager = m;
		user = session.getUser();
		sel_model = manager.getSelectionModel();
		blank = new BlankLcsAction(sel_model, user);
		blank_btn.setAction(blank);
		manager.setBlankAction(blank);
		TypeCache<LCSArray> cache =
			session.getSonarState().getLcsCache().getLCSArrays();
		watcher = new ProxyWatcher<LCSArray>(cache, this, true);
	}

	/** Initialize the widgets on the panel */
	public void initialize() {
		camera_btn.setBorder(BorderFactory.createEtchedBorder(
			EtchedBorder.LOWERED));
		// Make label opaque so that we can set the background color
		status_lbl.setOpaque(true);
		setTitle(I18N.get("lcs.selected"));
		add("device.name");
		add(name_lbl);
		add("camera");
		add(camera_btn, Stretch.LAST);
		add("location");
		add(location_lbl, Stretch.LAST);
		add("device.status");
		add(status_lbl, Stretch.LAST);
		add("device.operation");
		add(operation_lbl, Stretch.LAST);
//		add("lcs.lock");
//		add(lock_cmb, Stretch.LAST);
		add(buildSelectorBox(), Stretch.FULL);
		add(createButtonPanel(), Stretch.RIGHT);
		watcher.initialize();
		clear();
		sel_model.addProxySelectionListener(sel_listener);
	}

	/** Build the indication selector */
	private JPanel buildSelectorBox() {
		lane_config.add(Box.createVerticalStrut(UI.vgap));
		lane_config.add(lcs_pnl);
		lane_config.add(Box.createVerticalStrut(UI.vgap));
		lane_config.add(ind_selector);
		lane_config.add(Box.createVerticalStrut(UI.vgap));
		return lane_config;
	}

	/** Create the button panel */
	private Box createButtonPanel() {
		Box box = Box.createHorizontalBox();
		box.add(new JButton(send));
		box.add(Box.createHorizontalStrut(UI.hgap));
		box.add(blank_btn);
		return box;
	}

	/** Dispose of the LCS dispatcher */
	@Override
	public void dispose() {
		watcher.dispose();
		sel_model.removeProxySelectionListener(sel_listener);
		ind_selector.dispose();
		clear();
		super.dispose();
	}

	/** Get the selected LCS array */
	private LCSArray getSelected() {
		if(sel_model.getSelectedCount() == 1) {
			for(LCSArray la: sel_model.getSelected())
				return la;
		}
		return null;
	}

	/** Set the selected LCS array */
	public void setSelected(LCSArray la) {
		watcher.setProxy(la);
	}

	/** Update the proxy view */
	@Override
	public void update(final LCSArray la, String a) {
		lcs_array = la;
		if(a == null)
			updateConfig(la);
		if(a == null || a.equals("name"))
			name_lbl.setText(la.getName());
		if(a == null || a.equals("camera"))
			setCameraAction(la);
		// FIXME: this won't update when geoLoc attributes change
		//        plus, geoLoc is not an LCSArray attribute
		if(a == null || a.equals("geoLoc"))
			location_lbl.setText(LCSArrayHelper.lookupLocation(la));
		if(a == null || a.equals("operation")) {
			updateStatus(la);
			String op = la.getOperation();
			operation_lbl.setText(op);
			// These operations can be very slow -- discourage
			// users from sending multiple operations at once
			// RE: None -- see server.DeviceImpl.getOperation()
			send.setEnabled(isUpdatePermitted(la) &&
				op.equals("None"));
		}
		if(a == null || a.equals("lcsLock")) {
			Integer lk = la.getLcsLock();
			if(lk != null)
				lock_cmb.setSelectedIndex(lk);
			else
				lock_cmb.setSelectedIndex(0);
		}
		if(a == null || a.equals("indicationsCurrent")) {
			Integer[] ind = la.getIndicationsCurrent();
			lcs_pnl.setIndications(ind, la.getShift());
			lcs_pnl.setClickHandler(
				new LCSArrayPanel.ClickHandler()
			{
				public void handleClick(int lane) {
					selectDMS(la, lane);
				}
			});
			ind_selector.setIndications(ind);
		}
	}

	/** Update the LCS array config */
	private void updateConfig(LCSArray la) {
		boolean update = isUpdatePermitted(la);
		lane_config.setConfiguration(manager.laneConfiguration(la));
		ind_selector.setLCSArray(la);
		ind_selector.setEnabled(update);
		if(update)
			lock_cmb.setAction(new LockLcsAction(la, lock_cmb));
		else
			lock_cmb.setAction(null);
		lock_cmb.setEnabled(update);
		send.setEnabled(update);
		blank_btn.setEnabled(update);
	}

	/** Set the camera action */
	private void setCameraAction(LCSArray la) {
		Camera cam = LCSArrayHelper.getCamera(la);
		camera_btn.setAction(new CameraSelectAction(cam,
			session.getCameraManager().getSelectionModel()));
	}

	/** Update the status widgets */
	private void updateStatus(LCSArray la) {
		if(LCSArrayHelper.isFailed(la)) {
			status_lbl.setForeground(Color.WHITE);
			status_lbl.setBackground(Color.GRAY);
			status_lbl.setText(LCSArrayHelper.getStatus(la));
		} else
			updateCritical(la);
	}

	/** Update the critical error status */
	private void updateCritical(LCSArray la) {
		String critical = LCSArrayHelper.getCriticalError(la);
		if(critical.isEmpty())
			updateMaintenance(la);
		else {
			status_lbl.setForeground(Color.WHITE);
			status_lbl.setBackground(Color.BLACK);
			status_lbl.setText(critical);
		}
	}

	/** Update the maintenance error status */
	private void updateMaintenance(LCSArray la) {
		String maintenance = LCSArrayHelper.getMaintenance(la);
		if(maintenance.isEmpty()) {
			status_lbl.setForeground(null);
			status_lbl.setBackground(null);
		} else {
			status_lbl.setForeground(Color.BLACK);
			status_lbl.setBackground(Color.YELLOW);
		}
		status_lbl.setText(maintenance);
	}

	/** Select the DMS for the specified lane */
	private void selectDMS(LCSArray la, int lane) {
		LCS lcs = LCSArrayHelper.lookupLCS(la, lane);
		if(lcs != null) {
			DMS dms = DMSHelper.lookup(lcs.getName());
			if(dms != null) {
				session.getDMSManager().getSelectionModel().
					setSelected(dms);
			}
		}
	}

	/** Clear the proxy view. */
	@Override
	public void clear() {
		lcs_array = null;
		name_lbl.setText("");
		setCameraAction(null);
		location_lbl.setText("");
		status_lbl.setText("");
		status_lbl.setForeground(null);
		status_lbl.setBackground(null);
		operation_lbl.setText("");
		lock_cmb.setEnabled(false);
		lock_cmb.setSelectedItem(null);
		ind_selector.setEnabled(false);
		send.setEnabled(false);
		blank_btn.setEnabled(false);
		lcs_pnl.clear();
		lane_config.clear();
	}

	/** Send new indications to the selected LCS array */
	private void sendIndications() {
		LCSArray la = lcs_array;
		if(la != null)
			sendIndications(la);
	}

	/** Send new indications to the specified LCS array */
	private void sendIndications(LCSArray la) {
		Integer[] indications = ind_selector.getIndications();
		if(indications != null) {
			la.setOwnerNext(user);
			la.setIndicationsNext(indications);
		}
	}

	/** Check if the user is permitted to update the given LCS array */
	private boolean isUpdatePermitted(LCSArray la) {
		return isUpdatePermitted(la, "indicationsNext") &&
		       isUpdatePermitted(la, "ownerNext");
	}

	/** Check if the user is permitted to update a given LCS attribute */
	private boolean isUpdatePermitted(LCSArray la, String aname) {
		return session.isUpdatePermitted(la, aname);
	}
}
