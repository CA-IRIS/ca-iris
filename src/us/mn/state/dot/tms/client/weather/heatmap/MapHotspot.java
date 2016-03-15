/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       Southwest Research Institute
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
package us.mn.state.dot.tms.client.weather.heatmap;

import us.mn.state.dot.map.MapObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Jacob Barde
 */
public class MapHotspot implements MapObject {

	/** Identity transform */
	static protected final AffineTransform IDENTITY_TRANSFORM =
		new AffineTransform();

	/** Shape to render */
	protected final Shape shape;

	/** Shape to draw outline */
	protected final Shape outline;

	protected final Hotspot hotspot;

	public MapHotspot(Hotspot hs, float scale) {
		//FIXME
		shape = null;
		outline = null;
		hotspot = hs;
	}

	/** Get the coordinate transform */
	public AffineTransform getTransform() {

		return IDENTITY_TRANSFORM;
	}

	/** Get the inverse coordinate transform */
	public AffineTransform getInverseTransform() {

		return IDENTITY_TRANSFORM;
	}

	/** Get the shape to draw the map object */
	@Override
	public Shape getShape() {

		return shape;
	}

	/** Get the shape to draw the outline */
	@Override
	public Shape getOutlineShape() {

		return outline;
	}

	/** Get the map segment tool tip */
	public String getTip() {

		//FIXME
		return "Hi There!";
	}
}
