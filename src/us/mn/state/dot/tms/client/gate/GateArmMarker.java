/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.gate;

import us.mn.state.dot.tms.client.map.Marker;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * Marker used to paint gate arms.
 *
 * @author Douglas Lau
 */
public class GateArmMarker extends Marker {

	/** Size in pixels to render marker */
	static private final int MARKER_SIZE_PIX = UI.scaled(24);

	/** Create a new gate arm marker */
	public GateArmMarker() {
		super(10);
		float size = MARKER_SIZE_PIX;
		float fifth = size / 5;
		float quarter = size / 4;
		float third = size / 3;
		float half = size / 2;
		moveTo(fifth, half);
		lineTo(size, size - quarter);
		lineTo(size, size - third);
		lineTo(quarter, third);
		lineTo(quarter, half);
		moveTo(0, half);
		lineTo(quarter, half);
		lineTo(quarter, 0);
		lineTo(0, 0);
		closePath();
	}
}
