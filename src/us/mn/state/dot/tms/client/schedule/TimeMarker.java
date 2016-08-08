/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.schedule;

import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import us.mn.state.dot.map.AbstractMarker;

/**
 * Marker used to paint time plans.
 *
 * @author Douglas Lau
 */
public class TimeMarker extends AbstractMarker {

	/** Size in pixels to render marker */
	static private final int MARKER_SIZE_PIX = 36;

	/** Arc representing a clock face */
	static private final Arc2D.Float ARC = new Arc2D.Float(0, 0,
		MARKER_SIZE_PIX, MARKER_SIZE_PIX, 0, 360, Arc2D.OPEN);

	/** Create a new time marker */
	public TimeMarker() {
		super(ARC); //FIXME CA-MN-MERGE
		float size = MARKER_SIZE_PIX;
		float half = size / 2;
		float quarter = size / 4;
		float x = half;
		float y = size;
		moveTo(x, y);
		lineTo(x, y -= half);
		moveTo(x, y);
		lineTo(x += quarter, y);
	}
}
