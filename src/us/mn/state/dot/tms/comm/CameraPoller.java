/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2008  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.CameraImpl;

/**
 * CameraPoller is an interface for MessagePoller classes which can send camera
 * control messages.
 *
 * @author Douglas Lau
 */
public interface CameraPoller {

	/** Send a PTZ camera move command */
	void sendPTZ(CameraImpl c, float p, float t, float z);
}
