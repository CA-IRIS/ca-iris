/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.roads;

import java.awt.geom.Arc2D;
import us.mn.state.dot.map.AbstractMarker;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * Marker used for rectangles.
 *
 * @author Douglas Lau
 */
public class RectMarker extends AbstractMarker {

	/** Size in pixels */
	static private final int MARKER_SIZE_PIX = UI.scaled(16);

	/** Create a new rect marker */
	public RectMarker() {
		super(4);
		float size = MARKER_SIZE_PIX;
		moveTo(0, 0);
		lineTo(size, 0);
		lineTo(size, size);
		lineTo(0, size);
		closePath();
	}
}
