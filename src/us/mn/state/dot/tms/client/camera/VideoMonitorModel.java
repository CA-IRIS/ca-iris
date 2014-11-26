/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2014  Minnesota Department of Transportation
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

import java.util.ArrayList;
import us.mn.state.dot.tms.VideoMonitor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;

/**
 * Table model for video monitors
 *
 * @author Douglas Lau
 */
public class VideoMonitorModel extends ProxyTableModel2<VideoMonitor> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<VideoMonitor>> createColumns() {
		ArrayList<ProxyColumn<VideoMonitor>> cols =
			new ArrayList<ProxyColumn<VideoMonitor>>(3);
		cols.add(new ProxyColumn<VideoMonitor>("video.monitor", 160) {
			public Object getValueAt(VideoMonitor vm) {
				return vm.getName();
			}
		});
		cols.add(new ProxyColumn<VideoMonitor>("device.description",
			300)
		{
			public Object getValueAt(VideoMonitor vm) {
				return vm.getDescription();
			}
			public boolean isEditable(VideoMonitor vm) {
				return canUpdate(vm);
			}
			public void setValueAt(VideoMonitor vm, Object value) {
				vm.setDescription(value.toString());
			}
		});
		cols.add(new ProxyColumn<VideoMonitor>("video.restricted", 120,
			Boolean.class)
		{
			public Object getValueAt(VideoMonitor vm) {
				return vm.getRestricted();
			}
			public boolean isEditable(VideoMonitor vm) {
				return canUpdate(vm);
			}
			public void setValueAt(VideoMonitor vm, Object value) {
				if (value instanceof Boolean)
					vm.setRestricted((Boolean)value);
			}
		});
		return cols;
	}

	/** Create a new video monitor table model */
	public VideoMonitorModel(Session s) {
		super(s, s.getSonarState().getCamCache().getVideoMonitors(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      true);	/* has_name */
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return VideoMonitor.SONAR_TYPE;
	}
}
