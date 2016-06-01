/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2015  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.VideoMonitor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.widget.IComboBoxModel;
import us.mn.state.dot.tms.client.widget.ILabel;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.utils.I18N;

/**
 * GUI for viewing camera images.
 *
 * @author Douglas Lau
 * @author Tim Johnson
 * @author Travis Swanston
 */
public class CameraDispatcher extends JPanel {

	/** Number of joystick preset buttons */
	static private final int NUM_JOY_PRESET_BTNS = 6;

	/** Button number to select previous camera */
	static private final int BUTTON_PREVIOUS = 10;

	/** Button number to select next camera */
	static private final int BUTTON_NEXT = 11;

	/** Video size */
	static private final VideoRequest.Size SIZE = VideoRequest.Size.MEDIUM;

	/** User session */
	private final Session session;

	/** Proxy manager for camera devices */
	private final CameraManager manager;

	/** Client properties */
	private final Properties client_props;

	/** VideoRequest object */
	private final VideoRequest video_req;

	/** Selection model */
	private final ProxySelectionModel<Camera> sel_model;

	/** Selection listener */
	private final ProxySelectionListener sel_listener =
		new ProxySelectionListener()
	{
		public void selectionChanged() {
			selectCamera(sel_model.getSingleSelection());
		}
	};

	/** Stream status listener */
	private final StreamStatusListener ss_listener;

	/** Camera list model */
	private final ProxyListModel<Camera> model;

	/** Camera name label */
	private final JLabel name_lbl = IPanel.createValueLabel();

	/** Camera location label */
	private final JLabel location_lbl = IPanel.createValueLabel();

	/** Video output selection ComboBox */
	private final JComboBox output_cbx;

	/** Selected video monitor output */
	private VideoMonitor video_monitor;

	/** Camera PTZ control */
	private final CameraPTZ cam_ptz;

	/** Camera information panel */
	private final JPanel info_pnl;

	/** Streaming video panel */
	private final StreamPanel stream_pnl;

	/** Camera control panel */
	private final CamControlPanel control_pnl;

	/** Currently selected camera */
	private Camera selected = null;

	/** Joystick PTZ handler */
	private final JoystickPTZ joy_ptz;

	/** Create a new camera dispatcher */
	public CameraDispatcher(Session s, CameraManager man) {
		session = s;
		manager = man;
		client_props = session.getProperties();
		video_req = new VideoRequest(client_props, SIZE);
		video_req.setSonarSessionId(session.getSessionId());
		video_req.setRate(30);
		setLayout(new BorderLayout());
		sel_model = manager.getSelectionModel();
		model = session.getSonarState().getCamCache().getCameraModel();
		cam_ptz = new CameraPTZ(s);
		joy_ptz = new JoystickPTZ(cam_ptz);
		output_cbx = createOutputCombo();
		info_pnl = createInfoPanel();
		stream_pnl = createStreamPanel();
		control_pnl = new CamControlPanel(cam_ptz);
		ss_listener = createStreamStatusListener();
	}

	/** Create camera information panel */
	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		p.add(new ILabel("device.name"), gbc);
		gbc.gridx = 1;
		p.add(name_lbl, gbc);
		gbc.gridx = 2;

		gbc.weightx = 0.1;
		p.add(Box.createHorizontalGlue(), gbc);

		gbc.gridx = 3;
		gbc.weightx = 0.0;
		p.add(new ILabel("camera.output"), gbc);
		gbc.gridx = 4;
		p.add(output_cbx, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		p.add(new ILabel("location"), gbc);
		gbc.gridx = 1;
		p.add(location_lbl, gbc);

		return p;
	}

	/** Create the stream panel */
	private StreamPanel createStreamPanel() {
		boolean controls = SystemAttrEnum.CAMERA_STREAM_CONTROLS_ENABLE
			.getBoolean();
		boolean autoplay = SystemAttrEnum.CAMERA_AUTOPLAY
			.getBoolean();
		return new StreamPanel(video_req, cam_ptz, session, controls,
			autoplay);
	}

	/** Create the StreamStatusListener */
	private StreamStatusListener createStreamStatusListener() {
		StreamStatusListener ssl = new StreamStatusListener() {
			@Override
			public void onStreamStarted(Camera c) {
				updateCamControls();
			}
			@Override
			public void onStreamFinished(Camera c) {
				updateCamControls();
			}
		};
		return ssl;
	}

	/**
	 * Update the enable/disable status of the camera controls.
	 * The criteria used to determine what is enabled/disabled include:
	 * <ul>
	 *   <li> if a camera is currently selected
	 *   <li> if the camera has a controller
	 *   <li> if the user has any camera control permissions
	 *   <li> the value of the CAMERA_PTZ_BLIND system attribute
	 *   <li> if a stream is currently active
	 * </ul>
	 *
	 * Note: if the selected camera's EncoderType requires an external
	 * viewer, the PTZ controls are enabled as long as the user has
	 * permissions and the camera has a controller.  This is because
	 * there is currently no practical way for IRIS to determine whether
	 * an external stream is currently active.
	 */
	private void updateCamControls() {
		if (selected == null) {
			enablePTZ(false);
			return;
		}
		boolean hasCtrl = (selected.getController() != null);
		boolean hasPerms = (cam_ptz.canControlPtz() ||
			cam_ptz.canRequestDevice() ||
			cam_ptz.canRecallPreset() ||
			cam_ptz.canStorePreset()
		);
		boolean streaming = stream_pnl.isStreaming();
		boolean extOnly = !video_req.hasMJPEG(selected);
		boolean blindOk = SystemAttrEnum.CAMERA_PTZ_BLIND.getBoolean();
		boolean enable = (hasCtrl && hasPerms &&
			(streaming || extOnly || blindOk));
		enablePTZ(enable);
	}

	/**
	 * Enable or disable PTZ controls.
	 * Enables/disables control via CameraPTZ (applies to buttons, mouse,
	 * and joystick), and enables/disables the CamControlPanel.
	 *
	 * @param enable true to enable, false to disable
	 */
	private void enablePTZ(boolean enable) {
		cam_ptz.enableControl(enable);
		control_pnl.setEnabled(enable);
	}

	/** Create the video output selection combo box */
	private JComboBox createOutputCombo() {
		JComboBox box = new JComboBox();
		FilteredMonitorModel m = new FilteredMonitorModel(session);
		box.setModel(new IComboBoxModel(m));
		if (m.getSize() > 1)
			box.setSelectedIndex(1);
		return box;
	}

	/** Initialize the widgets on the panel */
	public void initialize() {
		stream_pnl.bindStreamStatusListener(ss_listener);
		output_cbx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				monitorSelected();
			}
		});
		joy_ptz.addJoystickListener(new JoystickListener() {
			public void buttonChanged(JoystickButtonEvent ev) {
				if (ev.pressed)
					doJoyButton(ev);
			}
		});
		setTitle(I18N.get("camera.selected"));
		add(info_pnl, BorderLayout.NORTH);
		add(stream_pnl, BorderLayout.CENTER);
		add(control_pnl, BorderLayout.SOUTH);
		clear();
		sel_model.addProxySelectionListener(sel_listener);
	}

	/** Set the title */
	public void setTitle(String t) {
		setBorder(BorderFactory.createTitledBorder(t));
	}

	/** Process a joystick button event */
	private void doJoyButton(JoystickButtonEvent ev) {
		if (ev.button == BUTTON_NEXT)
			selectNextCamera();
		else if (ev.button == BUTTON_PREVIOUS)
			selectPreviousCamera();
		else if (ev.button >= 0 && ev.button < NUM_JOY_PRESET_BTNS)
			cam_ptz.recallPreset(ev.button + 1);
	}

	/** Select the next camera */
	private void selectNextCamera() {
		int i = model.getIndex(selected);
		if (i >= 0 && i < model.getSize() - 1) {
			Camera cam = model.getProxy(i + 1);
			if (cam != null)
				sel_model.setSelected(cam);
		}
	}

	/** Select the previous camera */
	private void selectPreviousCamera() {
		int i = model.getIndex(selected);
		if (i > 0) {
			Camera cam = model.getProxy(i - 1);
			if (cam != null)
				sel_model.setSelected(cam);
		}
	}

	/** Dispose of the camera viewer */
	public void dispose() {
		stream_pnl.unbindStreamStatusListener(ss_listener);
		sel_model.removeProxySelectionListener(sel_listener);
		joy_ptz.dispose();
		cam_ptz.setCamera(null);
		stream_pnl.dispose();
		selected = null;
		removeAll();
		control_pnl.dispose();
	}

	/** Set the selected camera */
	private void selectCamera(final Camera camera) {
		if (camera == selected)
			return;
		cam_ptz.setCamera(camera);
		selected = camera;
		if (camera != null) {
			name_lbl.setText(camera.getName());
			location_lbl.setText(GeoLocHelper.getDescription(
				camera.getGeoLoc()));
			stream_pnl.setCamera(camera);
			selectMonitorCamera();
			updateCamControls();
		} else
			clear();
	}

	/** Called when a video monitor is selected */
	private void monitorSelected() {
		Object o = output_cbx.getSelectedItem();
		if (o instanceof VideoMonitor) {
			video_monitor = (VideoMonitor)o;
			selectMonitorCamera();
		} else
			video_monitor = null;
	}

	/** Select a camera on a video monitor */
	private void selectMonitorCamera() {
		VideoMonitor vm = video_monitor;
		if (vm != null)
			vm.setCamera(selected);
	}

	/** Clear all of the fields */
	private void clear() {
		name_lbl.setText("");
		location_lbl.setText("");
		stream_pnl.setCamera(null);
		control_pnl.setEnabled(false);
	}
}
