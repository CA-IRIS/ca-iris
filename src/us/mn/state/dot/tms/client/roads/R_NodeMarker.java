/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010  Minnesota Department of Transportation
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
import us.mn.state.dot.map.marker.AbstractMarker;

/**
 * Marker used to paint roadway nodes.
 *
 * @author Douglas Lau
 */
public class R_NodeMarker extends AbstractMarker {

	/** Size in pixels to render marker */
	static protected final int MARKER_SIZE_PIX = 12;

	/** Create a new r_node marker */
	public R_NodeMarker() {
		super(4);
		float size = MARKER_SIZE_PIX;
		float half = size / 2;
		float third = size / 3;
		path.moveTo(-third, -third);
		path.lineTo(third, third);
		path.moveTo(third, -third);
		path.lineTo(-third, third);
		path.append(new Arc2D.Float(-half, -half, size, size, 0, 360,
			Arc2D.OPEN), false);
	}
}
