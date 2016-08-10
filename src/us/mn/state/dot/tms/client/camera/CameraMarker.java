/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2016  Minnesota Department of Transportation
 * Copyright (C) 2010  AHMCT, University of California
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

import us.mn.state.dot.tms.client.map.AbstractMarker;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * Marker used to paint cameras.
 *
 * @author Douglas Lau
 */
public class CameraMarker extends AbstractMarker {

	/** Size in pixels to render marker */
	static private final int MARKER_SIZE_PIX = UI.scaled(24);

	/** Create a new camera marker */
	public CameraMarker() {
		super(11);
		float size = MARKER_SIZE_PIX;
		float tenth = size / 10;
		float quarter = size / 4;
		float third = size / 3;
		moveTo(0, third);
		lineTo(quarter, tenth);
		lineTo(third, tenth);
		lineTo(third, quarter);
		lineTo(size, quarter);
		lineTo(size, -quarter);
		lineTo(third, -quarter);
		lineTo(third, -tenth);
		lineTo(quarter, -tenth);
		lineTo(0, -third);
		closePath();
	}
}
