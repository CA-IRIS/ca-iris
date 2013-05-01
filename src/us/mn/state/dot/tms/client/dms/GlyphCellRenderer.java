/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.dms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import us.mn.state.dot.tms.BitmapGraphic;

/**
 * Renderer for font glyphs in a Jlist
 *
 * @author Douglas Lau
 */
public class GlyphCellRenderer extends DefaultListCellRenderer {

	/** Margin reserved for default renderer */
	static private final int MARGIN = 16;

	/** Hash of characters to bitmap graphics */
	private final HashMap<String, FontForm.GlyphData> gmap;

	/** Create a new glyph cell renderer */
	public GlyphCellRenderer(HashMap<String, FontForm.GlyphData> gm) {
		gmap = gm;
		setBackground(Color.BLACK);
	}

	/** Lookup the glyph data */
	private FontForm.GlyphData lookupGlyphData(String v) {
		synchronized(gmap) {
			return gmap.get(v);
		}
	}

	/** Get a renderer for the specified list value */
	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus)
	{
		FontForm.GlyphData gdata = lookupGlyphData(value.toString());
		if(gdata != null)
			bitmap = gdata.bmap;
		else
			bitmap = null;
		return super.getListCellRendererComponent(list, value,
			index, isSelected, cellHasFocus);
	}

	/** Bitmap for currently configured glyph */
	private BitmapGraphic bitmap;

	/** Pitch for currently configured glyph */
	private float pitch;

	/** Left margin for currently configured glyph */
	private int left;

	/** Top margin for currently configured glyph */
	private int top;

	/** Paint the currently configured glyph */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		BitmapGraphic bmap = bitmap;
		if(bmap != null) {
			configureRenderer(bmap);
			paintPixels((Graphics2D)g, bmap);
		}
	}

	/** Configure the list cell renderer */
	private void configureRenderer(BitmapGraphic bmap) {
		pitch = calculatePitch(bmap);
		left = calculateLeft(bmap);
		top = calculateTop(bmap);
	}

	/** Calculate the pitch for the current glyph */
	private float calculatePitch(BitmapGraphic bmap) {
		float w = 0;
		if(bmap.getWidth() > 0)
 			w = getWidth() / bmap.getWidth();
		float h = 0;
		if(bmap.getHeight() > 0)
			h = getHeight() / bmap.getHeight();
		return Math.min(w, h);
	}

	/** Calculate the left side of the current glyph */
	private int calculateLeft(BitmapGraphic bmap) {
		return MARGIN + (int)(getWidth() - MARGIN -
			bmap.getWidth() * pitch) / 2;
	}

	/** Calculate the top of the current glyph */
	private int calculateTop(BitmapGraphic bmap) {
		return (int)(getHeight() - bmap.getHeight() * pitch) / 2;
	}

	/** Paint the pixels for the current glyph */
	private void paintPixels(Graphics2D g, BitmapGraphic bmap) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		Ellipse2D pixel = new Ellipse2D.Float();
		float yy = top;
		for(int y = 0; y < bmap.getHeight(); y++, yy += pitch) {
			float xx = left;
			for(int x = 0; x < bmap.getWidth(); x++, xx += pitch){
				if(bmap.getPixel(x, y).isLit())
					g.setColor(Color.YELLOW);
				else
					g.setColor(Color.GRAY);
				pixel.setFrame(xx, yy, pitch, pitch);
				g.fill(pixel);
			}
		}
	}
}
