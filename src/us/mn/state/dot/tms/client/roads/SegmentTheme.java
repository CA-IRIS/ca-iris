/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2010  Minnesota Department of Transportation
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Style;
import us.mn.state.dot.map.StyledTheme;

/**
 * A simple theme which uses one symbol to draw all segment objects.
 *
 * @author Douglas Lau
 */
abstract public class SegmentTheme extends StyledTheme {

	/** Color for rendering gray stations */
	static public final Color GRAY = Color.GRAY;

	/** Color for rendering green stations */
	static public final Color GREEN = new Color(48, 160, 48);

	/** Color for rendering yellow stations */
	static public final Color YELLOW = new Color(240, 240, 0);

	/** Color for rendering orange stations */
	static public final Color ORANGE = new Color(255, 192, 0);

	/** Color for rendering red stations */
	static public final Color RED = new Color(208, 0, 0);

	/** Color for rendering violet stations */
	static public final Color VIOLET = new Color(192, 0, 240);

	/** Transparent black outline */
	static protected final Outline OUTLINE = Outline.createSolid(
		new Color(0, 0, 0, 128), 0.6f);

	/** Default segment style theme */
	static protected final Style DEFAULT_STYLE = new Style("No Data",
		OUTLINE, GRAY);

	/** Create a new segment theme */
	protected SegmentTheme(String name) {
		super(name, new Rectangle(0, 0, 200, 200));
		addStyle(DEFAULT_STYLE);
	}

	/** Draw the specified map object */
	public void draw(Graphics2D g, MapObject mo, float scale) {
		// don't apply transform
		getSymbol(mo).draw(g, mo.getShape(), mo.getOutlineShape(),
			scale);
	}

	/** Draw a selected map object */
	public void drawSelected(Graphics2D g, MapObject mo, float scale) {
		Shape shape = mo.getShape();
		Outline outline = Outline.createDashed(Color.WHITE, 2);
		g.setColor(outline.color);
		g.setStroke(outline.getStroke(scale));
		g.draw(shape);
		outline = Outline.createSolid(Color.WHITE, 4);
		Shape ellipse = createEllipse(shape);
		g.setStroke(outline.getStroke(scale));
		g.draw(ellipse);
	}

	/** Get the style to draw a given map object */
	public Style getStyle(MapObject mo) {
		MapSegment ms = (MapSegment)mo;
		return getStyle(ms);
	}

	/** Get the style to draw a given segment */
	abstract protected Style getStyle(MapSegment ms);

	/** Get the tooltip text for a given segment */
	public String getTip(MapObject mo) {
		if(mo instanceof MapSegment)
			return ((MapSegment)mo).getTip();
		else
			return null;
	}
}
