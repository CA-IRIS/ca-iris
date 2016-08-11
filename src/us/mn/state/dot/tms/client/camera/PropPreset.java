/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraPreset;
import us.mn.state.dot.tms.PresetAliasName;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.client.widget.IPanel.Stretch;
import us.mn.state.dot.tms.client.widget.ZTable;

/**
 * Camera properties preset panel.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 */
public class PropPreset extends IPanel {

	/** Preset model */
	private final PresetModel preset_mdl;

	/** Preset alias model */
	private final PresetAliasMappingModel alias_mdl;

	/** Create a new camera properties preset panel */
	public PropPreset(Session s, Camera c) {
		preset_mdl = new PresetModel(s, c);
		alias_mdl = new PresetAliasMappingModel(s, c);
	}

	/** Initialize the widgets on the panel */
	@Override
	public void initialize() {
		super.initialize();
		preset_mdl.initialize();
		alias_mdl.initialize();

		ZTable preset_table = new ZTable();
		preset_table.setAutoCreateColumnsFromModel(false);
		preset_table.setModel(preset_mdl);
		preset_table.setColumnModel(preset_mdl.createColumnModel());
		preset_table.setVisibleRowCount(CameraPreset.MAX_PRESET + 1);
		add(preset_table, Stretch.FULL);

		ZTable alias_table = new ZTable();
		alias_table.setAutoCreateColumnsFromModel(false);
		alias_table.setModel(alias_mdl);
		alias_table.setColumnModel(alias_mdl.createColumnModel());
		alias_table.setVisibleRowCount(PresetAliasName.size + 1);
		add(alias_table, Stretch.FULL);
	}

	/** Dispose of the preset panel */
	@Override
	public void dispose() {
		preset_mdl.dispose();
		alias_mdl.dispose();
		super.dispose();
	}
}
