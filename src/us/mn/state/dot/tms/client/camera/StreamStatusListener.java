/*
 * IRIS -- Intelligent Roadway Information System
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


/**
 * A stream status listener receives events related to changes in video
 * stream status.
 *
 * @author Travis Swanston
 */
public interface StreamStatusListener {

	/** Streaming has begun with Camera c. */
	public void onStreamStarted(Camera c);

	/** Streaming has ended with Camera c. */
	public void onStreamFinished(Camera c);

}

