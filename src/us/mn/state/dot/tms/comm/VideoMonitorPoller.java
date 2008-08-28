/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm;

import us.mn.state.dot.tms.ControllerImpl;
import us.mn.state.dot.tms.VideoMonitor;

/**
 * VideoMonitorPoller is an interface for MessagePoller classes which can send
 * video monitor switching messages.
 *
 * @author Douglas Lau
 */
public interface VideoMonitorPoller {

	/** Set the camera to display on the specified monitor */
	void setMonitorCamera(ControllerImpl c, VideoMonitor m, String cam);
}
