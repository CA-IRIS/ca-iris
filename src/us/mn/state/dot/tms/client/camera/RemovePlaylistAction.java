/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import us.mn.state.dot.sched.AbstractJob;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.utils.I18N;

/**
 * This is an action to remove cameras from the playlist.
 *
 * @author Douglas Lau
 */
public class RemovePlaylistAction extends AbstractAction {

	/** Camera manager */
	protected final CameraManager manager;

	/** Proxy selection model */
	protected final ProxySelectionModel<Camera> s_model;

	/** Create a new remove playlist action */
	public RemovePlaylistAction(CameraManager m,
		ProxySelectionModel<Camera> s)
	{
		manager = m;
		s_model = s;
		putValue(Action.NAME, I18N.get("camera.playlist.remove"));
		putValue(Action.SHORT_DESCRIPTION,
			I18N.get("camera.playlist.remove.short"));
		putValue(Action.LONG_DESCRIPTION,
			I18N.get("camera.playlist.remove.long"));
	}

	/** Schedule the action to be performed */
	public void actionPerformed(ActionEvent e) {
		new AbstractJob() {
			public void perform() {
				do_perform();
			}
		}.addToScheduler();
	}

	/** Remove the selected cameras from the playlist */
	protected void do_perform() {
		for(Camera c: s_model.getSelected())
			manager.removePlaylist(c);
		s_model.clearSelection();
	}
}
