/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010  Minnesota Department of Transportation
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.client.weather.markers;


import us.mn.state.dot.tms.client.map.AbstractMarker;

/**
 * Marker used to paint weather sensors.
 * The style is that of a windsock, and points in the direction of the wind.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class DirectionMarker extends AbstractMarker {

	/** Size, in pixels, to render markers */
	static protected final int MARKER_SIZE_PIX = 20;

	/** Create a new weather sensor markers, pointing north by default. */
	public DirectionMarker() {
		super(8);
		final float size = MARKER_SIZE_PIX;
		final float size12 = size / 2F;
		final float size16 = size / 6F;
		moveTo(-size12 + size16, -size12);	// 1
		lineTo(size12 - size16, -size12);	// 2
		lineTo(size16, size12);		// 3
		lineTo(0, size12 - size16);	// 4
		lineTo(-size16, size12);		// 5
		closePath();
	}

}

