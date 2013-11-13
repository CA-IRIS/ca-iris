/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2013  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.VideoMonitor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;

/**
 * A FilteredMonitorModel is a ProxyListModel filtered to just the
 * VideoMonitors for which the user can set the camera attribute.
 *
 * @author Tim Johnson
 * @author Douglas Lau
 */
public class FilteredMonitorModel extends ProxyListModel<VideoMonitor> {

	/** User Session */
	private final Session session;

	/** Create a new filtered monitor model */
	public FilteredMonitorModel(Session s) {
		super(s.getSonarState().getCamCache().getVideoMonitors());
		session = s;
		initialize();
	}

	/** Add a new proxy to the list model */
	protected int doProxyAdded(VideoMonitor proxy) {
		if(session.isUpdatePermitted(proxy, "camera"))
			return super.doProxyAdded(proxy);
		else
			return -1;
	}
}
