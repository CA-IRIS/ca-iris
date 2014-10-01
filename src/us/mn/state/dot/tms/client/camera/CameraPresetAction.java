/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014  Minnesota Department of Transportation
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
import javax.swing.Action;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraPreset;
import us.mn.state.dot.tms.client.proxy.ProxyAction;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.utils.I18N;

/**
 * Selects the specified camera preset.
 *
 * @author Douglas Lau
 */
public class CameraPresetAction extends ProxyAction<CameraPreset> {

	/** Camera selection model */
	private final ProxySelectionModel<Camera> sel_model;

	/** Create a new action to select a camera preset */
	public CameraPresetAction(CameraPreset cp,
		ProxySelectionModel<Camera> mdl)
	{
		super("camera.select", cp);
		sel_model = mdl;
		if (cp != null)
			putValue(Action.NAME, cp.getCamera().getName());
		else
			putValue(Action.NAME, I18N.get("camera.none"));
	}

	/** Actually perform the action */
	@Override
	protected void doActionPerformed(ActionEvent e) {
		if (proxy != null) {
			Camera c = proxy.getCamera();
			if (c != null) {
				sel_model.setSelected(c);
				c.setRecallPreset(proxy.getPresetNum());
			}
		}
	}
}
