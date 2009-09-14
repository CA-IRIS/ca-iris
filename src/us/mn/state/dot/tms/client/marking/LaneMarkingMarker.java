/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.marking;

import us.mn.state.dot.map.marker.AbstractMarker;

/**
 * Marker used to paint lane markings.
 *
 * @author Douglas Lau
 */
public class LaneMarkingMarker extends AbstractMarker {

	/** Maximum size (in user coordinates) to render marker */
	static protected final int MARKER_SIZE = 600;

	/** Create a new lane marking marker */
	public LaneMarkingMarker() {
		this(MARKER_SIZE);
	}

	/** Create a new lane marking marker */
	public LaneMarkingMarker(float size) {
		super(3);
		size = Math.min(size, MARKER_SIZE);
		path.moveTo(0, 0);
		path.lineTo(size, size);
		path.closePath();
	}
}
