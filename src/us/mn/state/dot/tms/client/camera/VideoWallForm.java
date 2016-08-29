/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2015 AHMCT, University of California
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumn;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;
import us.mn.state.dot.tms.PresetAliasHelper;
import us.mn.state.dot.tms.PresetAliasName;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.widget.AbstractForm;

/**
 * Form that allows management of a video wall.
 * NOTE: THIS IS PURE PROTOTYPE CODE THAT UNFORTUNATELY, DO TO SCHEDULE
 * CONSTRAINTS, BECAME PRODUCTION CODE.  IT NEEDS A FULL REDESIGN AND
 * REWRITE.
 */
public class VideoWallForm extends AbstractForm {

	static private final String[] map_cols = {
		"Decoder",
		"Current Source",
		"Select Source"
	};
	static private final int MAP_TABLE_DEC_COL_IDX = 0;
	static private final int[] map_col_widths = {140, 200, 200};

	static private final String[] util_cols = {
		"Connection Group",
		"In Use / Limit"
	};
	static private final int[] util_col_widths = {140, 160};

	private final VideoWallManager vw_manager;
	private JPanel panel;
	private JTable mapping_table;
	private boolean inhibit_updates = false;	// kludge

	static private final int REFRESH_PERIOD_MS = 2000;

	/** Timer listener for refresh job */
	private class Refresher implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// performed on event dispatch thread
			updatePanel();
		}
	};

	/** Refresh timer */
	private final Timer refresh_timer = new Timer(REFRESH_PERIOD_MS,
		new Refresher());


	/** Create a new form */
	public VideoWallForm(VideoWallManager vwm) {
		super("Video Manager");
		vw_manager = vwm;
		setPreferredSize(new Dimension(600, 600));
	}


	/**
	 * Initialize form.
	 * Called from SmartDesktop.
	 */
	protected void initialize() {
		add(createFormPanel());
		refresh_timer.start();
	}


	protected void dispose() {
		refresh_timer.stop();
		super.dispose();
	}


	/**
	 * Create form panel with terrible layout.
	 */
	private JPanel createFormPanel() {
		Map<String, String> decmap = vw_manager.getDecoderMap();
		Map<String, String> utilmap = vw_manager.getGroupUtilMap();
		List<String> cams = vw_manager.getCameraList();
		mapping_table = createMappingTable(decmap, cams);
		JTable util_table = createUtilTable(utilmap);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalGlue());

		JScrollPane scrollPane = new JScrollPane(mapping_table);
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(scrollPane, BorderLayout.CENTER);

		panel.add(Box.createVerticalGlue());

		JScrollPane scrollPane2 = new JScrollPane(util_table);
		scrollPane2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(scrollPane2, BorderLayout.CENTER);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		return panel;
	}


	/** Create utilization table */
	private JTable createUtilTable(Map<String, String> map) {
		if (map == null) {
			return new JTable(new String[0][2], util_cols);
		}

		String[][] data = new String[map.size()][2];

		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		int row = 0;
		for (String k : keys) {
			data[row][0] = k;
			data[row][1] = map.get(k);
			++row;
		}

		JTable table = new JTable(data, util_cols);
		setUtilTableColWidths(table);

	return table;

	}


	/** Create mapping table */
	private JTable createMappingTable(Map<String, String> map,
		List<String> cams)
	{
		if (map == null) {
			return new JTable(new String[0][3], map_cols);
		}
		String[][] data = new String[map.size()][3];

		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		int row = 0;
		for (String did : keys) {
			String cid = map.get(did);
			String sn = SiteDataHelper.getSiteName(cid);
			String cam = (sn != null ? sn : cid);
			data[row][0] = did;
			data[row][1] = cam;
			data[row][2] = null;
			++row;
		}

		JTable table = new JTable(data, map_cols);
		setMapTableColWidths(table);

		TableColumn testColumn = table.getColumnModel().getColumn(2);
		JComboBox cameraBox = new JComboBox();

		// populate cameraBox
		cameraBox.addItem("");
		for (String cid : cams) {
			String sn = SiteDataHelper.getSiteName(cid);
			String cam = (sn != null ? sn : cid);
			cameraBox.addItem(cam);
		}
		testColumn.setCellEditor(new DefaultCellEditor(cameraBox));

		cameraBox.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(
				PopupMenuEvent e)
			{
				inhibit_updates = true;
			}
			public void popupMenuWillBecomeInvisible(
				PopupMenuEvent e)
			{
				inhibit_updates = false;
			}
			public void popupMenuCanceled(
				PopupMenuEvent e)
			{
				inhibit_updates = false;
			}
		});
		cameraBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String camDesc = (String)cb.getSelectedItem();
				int row = mapping_table.getSelectedRow();
				if (row < 0)
					return;
				String did = (String)(mapping_table.getValueAt(
					row, MAP_TABLE_DEC_COL_IDX));
				// disconnect decoder if connected
				String curCid = vw_manager.getCameraByDecoder(did);
				if (curCid != null) {
					vw_manager.disconnectDec(did);
					TimeSteward.sleep_well(250);	// kludge to avoid race
					int numConns = vw_manager.getNumConns(curCid);
					if (numConns == 0)
						maybeReturnHome(curCid);
				}
				// connect decoder to new camera, if one selected
				if ((camDesc != null)
					&& (!("".equals(camDesc.trim()))))
				{
					String cid = SiteDataHelper
						.getGeoLocNameBySiteName(camDesc);
					if (cid == null)
						cid = camDesc;
					vw_manager.connect(did, cid);
				}
			}
		});
		return table;
	}

	// refactor, merge with similar routine in CameraDispatcher; move elsewhere.
	private void maybeReturnHome(String cid) {
		if (cid == null)
			return;
		Camera c = CameraHelper.lookup(cid);
		if (c == null)
			return;
		if (!SystemAttrEnum.CAMERA_PTZ_RETURN_HOME.getBoolean())
			return;
		Integer p = PresetAliasHelper.getPreset(c,
			PresetAliasName.HOME);
		if (p == null)
			return;
		c.setRecallPreset(p.intValue());
	}

	// kludge
	private void updatePanel() {
		if (inhibit_updates)
			return;
		if (panel != null)
			panel.setEnabled(false);
		JPanel jp = createFormPanel();
		removeAll();
		add(jp);
		panel = jp;
		revalidate();
	}


	/** Set table column widths */
	private static void setUtilTableColWidths(JTable jt) {
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		assert util_col_widths.length == util_cols.length;
		for(int i = 0; i < util_col_widths.length; ++i)
			setTableColWidth(jt, i, util_col_widths[i]);
	}


	/** Set map table column widths */
	private static void setMapTableColWidths(JTable jt) {
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		assert map_col_widths.length == map_cols.length;
		for(int i = 0; i < map_col_widths.length; ++i)
			setTableColWidth(jt, i, map_col_widths[i]);
	}


	/** Set table column width */
	private static void setTableColWidth(JTable jt, int col, int wid) {
		jt.getColumnModel().getColumn(col).setPreferredWidth(wid);
	}

}

