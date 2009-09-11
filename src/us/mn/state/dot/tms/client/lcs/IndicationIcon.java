/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.lcs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import us.mn.state.dot.tms.LaneUseIndication;

/**
 * Renderer for LaneUseIndication
 *
 * @author Douglas Lau
 */
abstract public class IndicationIcon implements Icon {

	/** Border around LCS shapes */
	static protected final float SHAPE_BORDER = 0.1f;

	/** Margin on left and right of HOV diamond */
	static protected final float HOV_MARGIN = 0.2f;

	/** Margin on top and bottom of chevron */
	static protected final float CHEVRON_MARGIN = 0.25f;

	/** String to display for error status */
	static protected final String ERROR_STRING = "?";

	/** Shape to use for representing an error condition */
	static protected final Shape ERROR_SHAPE;
	static {
		Font font = new Font("Serif", Font.PLAIN, 24);
		FontRenderContext frc = new FontRenderContext(
			new AffineTransform(), false, false);
		GlyphVector vec = font.createGlyphVector(frc, ERROR_STRING);
		Shape s = vec.getGlyphOutline(0);
		Rectangle2D rect = s.getBounds2D();
		AffineTransform a = new AffineTransform();
		a.translate(SHAPE_BORDER, SHAPE_BORDER);
		a.scale((1 - 2 * SHAPE_BORDER) / rect.getWidth(),
			(1 - 2 * SHAPE_BORDER) / rect.getHeight());
		a.translate(-rect.getX(), -rect.getY());
		ERROR_SHAPE = a.createTransformedShape(s);
	}

	/** Shape to draw an arrow */
	static protected final Shape ARROW_SHAPE;
	static {
		GeneralPath path = new GeneralPath();
		path.moveTo(0.5f, SHAPE_BORDER);
		path.lineTo(0.5f, 1 - SHAPE_BORDER);
		path.moveTo(SHAPE_BORDER, 0.5f);
		path.lineTo(0.5f, 1 - SHAPE_BORDER);
		path.lineTo(1 - SHAPE_BORDER, 0.5f);
		ARROW_SHAPE = path;
	}

	/** Shape to draw an X */
	static protected final Shape CROSS_SHAPE;
	static {
		GeneralPath path = new GeneralPath();
		path.moveTo(SHAPE_BORDER, SHAPE_BORDER);
		path.lineTo(1 - SHAPE_BORDER, 1 - SHAPE_BORDER);
		path.moveTo(SHAPE_BORDER, 1 - SHAPE_BORDER);
		path.lineTo(1 - SHAPE_BORDER, SHAPE_BORDER);
		CROSS_SHAPE = path;
	}

	/** Shape to draw a diamond */
	static protected final Shape DIAMOND_SHAPE;
	static {
		GeneralPath path = new GeneralPath();
		path.moveTo(0.5f, SHAPE_BORDER);
		path.lineTo(HOV_MARGIN, 0.5f);
		path.lineTo(0.5f, 1 - SHAPE_BORDER);
		path.lineTo(1 - HOV_MARGIN, 0.5f);
		path.closePath();
		DIAMOND_SHAPE = path;
	}

	/** Shape to draw a chevron */
	static protected final Shape CHEVRON_SHAPE;
	static {
		GeneralPath path = new GeneralPath();
		path.moveTo(SHAPE_BORDER, CHEVRON_MARGIN);
		path.lineTo(SHAPE_BORDER + CHEVRON_MARGIN, 0.5f);
		path.lineTo(SHAPE_BORDER, 1 - CHEVRON_MARGIN);
		CHEVRON_SHAPE = path;
	}

	/** Create a new indication icon */
	static public IndicationIcon create(int p, LaneUseIndication i) {
		if(i == null)
			return new UnknownIndicationIcon(p);
		switch(i) {
		case DARK:
			return new DarkIndicationIcon(p);
		case LANE_OPEN:
			return new LaneOpenIndicationIcon(p);
		case USE_CAUTION:
			return new UseCautionIndicationIcon(p);
		case LANE_CLOSED_AHEAD:
			return new LaneClosedAheadIndicationIcon(p);
		case LANE_CLOSED:
			return new LaneClosedIndicationIcon(p);
		case HOV:
			return new HovIndicationIcon(p);
		case MERGE_RIGHT:
			return new MergeRightIndicationIcon(p);
		case MERGE_LEFT:
			return new MergeLeftIndicationIcon(p);
		case MERGE_BOTH:
			return new MergeBothIndicationIcon(p);
		default:
			return new UnknownIndicationIcon(p);
		}
	}

	/** Pixel size */
	protected final int pixels;

	/** Stroke for drawing symbol shadows */
	protected final BasicStroke shadow;

	/** Stroke for drawing symbols */
	protected final BasicStroke stroke;

	/** Stroke for thin symbols */
	protected final BasicStroke thin;

	/**
	 * Create a new IndicationIcon.
	 *
	 * @param p Pixel size (height and width)
	 */
	private IndicationIcon(int p) {
		pixels = p;
		shadow = new BasicStroke(5f / pixels, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER);
		stroke = new BasicStroke(3f / pixels, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER);
		thin = new BasicStroke(1f / pixels, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER);
	}

	/** Get the icon height */
	public int getIconHeight() {
		return pixels;
	}

	/** Get the icon width */
	public int getIconWidth() {
		return pixels;
	}

	/**
	 * Paint the icon at the specified location.
	 *
	 * @param component  The component to paint on.
	 * @param g          The Graphics object to paint with.
	 * @param x          The x coordinate at which to paint.
	 * @param y          The y coordinate at which to paint.
	 */
	public void paintIcon(Component component, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(x, y);
		g2.scale(pixels, pixels);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		paintIcon(g2);
	}

	/** Paint the indication icon */
	abstract protected void paintIcon(Graphics2D g2);

	/** Icon for DARK lane-use indication */
	static protected class DarkIndicationIcon extends IndicationIcon {
		protected DarkIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			// Leave background alone
		}
	}

	/** Icon for LANE_OPEN lane-use indication */
	static protected class LaneOpenIndicationIcon extends IndicationIcon {
		protected LaneOpenIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setStroke(shadow);
			g2.draw(ARROW_SHAPE);
			g2.setColor(Color.GREEN);
			g2.setStroke(stroke);
			g2.draw(ARROW_SHAPE);
		}
	}

	/** Icon for USE_CAUTION lane-use indication */
	static protected class UseCautionIndicationIcon extends IndicationIcon {
		protected UseCautionIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setStroke(shadow);
			g2.draw(ARROW_SHAPE);
			g2.setColor(Color.YELLOW);
			g2.setStroke(stroke);
			g2.draw(ARROW_SHAPE);
		}
	}

	/** Icon for LANE_CLOSED_AHEAD lane-use indication */
	static protected class LaneClosedAheadIndicationIcon
		extends IndicationIcon
	{
		protected LaneClosedAheadIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setStroke(shadow);
			g2.draw(CROSS_SHAPE);
			g2.setColor(Color.YELLOW);
			g2.setStroke(stroke);
			g2.draw(CROSS_SHAPE);
		}
	}

	/** Icon for LANE_CLOSED lane-use indication */
	static protected class LaneClosedIndicationIcon extends IndicationIcon {
		protected LaneClosedIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setStroke(shadow);
			g2.draw(CROSS_SHAPE);
			g2.setColor(Color.RED);
			g2.setStroke(stroke);
			g2.draw(CROSS_SHAPE);
		}
	}

	/** Icon for HOV lane-use indication */
	static protected class HovIndicationIcon extends IndicationIcon {
		protected HovIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setStroke(shadow);
			g2.draw(DIAMOND_SHAPE);
			g2.setColor(Color.WHITE);
			g2.setStroke(stroke);
			g2.draw(DIAMOND_SHAPE);
		}
	}

	/** Icon for merge right lane-use indication */
	static protected class MergeRightIndicationIcon extends IndicationIcon {
		protected MergeRightIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			for(int i = 0; i < 3; i++) {
				g2.setColor(Color.BLACK);
				g2.setStroke(shadow);
				g2.draw(CHEVRON_SHAPE);
				g2.setColor(Color.YELLOW);
				g2.setStroke(stroke);
				g2.draw(CHEVRON_SHAPE);
				g2.translate(0.25f, 0);
			}
		}
	}

	/** Icon for merge left lane-use indication */
	static protected class MergeLeftIndicationIcon extends IndicationIcon {
		protected MergeLeftIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.scale(-1, 1);
			g2.translate(-1, 0);
			for(int i = 0; i < 3; i++) {
				g2.setColor(Color.BLACK);
				g2.setStroke(shadow);
				g2.draw(CHEVRON_SHAPE);
				g2.setColor(Color.YELLOW);
				g2.setStroke(stroke);
				g2.draw(CHEVRON_SHAPE);
				g2.translate(0.25f, 0);
			}
		}
	}

	/** Icon for merge both lane-use indication */
	static protected class MergeBothIndicationIcon extends IndicationIcon {
		protected MergeBothIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			AffineTransform at = g2.getTransform();
			g2.translate(0.40f, 0);
			for(int i = 0; i < 2; i++) {
				g2.setColor(Color.BLACK);
				g2.setStroke(stroke);
				g2.draw(CHEVRON_SHAPE);
				g2.setColor(Color.YELLOW);
				g2.setStroke(thin);
				g2.draw(CHEVRON_SHAPE);
				g2.translate(0.15f, 0);
			}
			g2.setTransform(at);
			g2.scale(-1, 1);
			g2.translate(-1, 0);
			g2.translate(0.40f, 0);
			for(int i = 0; i < 2; i++) {
				g2.setColor(Color.BLACK);
				g2.setStroke(stroke);
				g2.draw(CHEVRON_SHAPE);
				g2.setColor(Color.YELLOW);
				g2.setStroke(thin);
				g2.draw(CHEVRON_SHAPE);
				g2.translate(0.15f, 0);
			}
		}
	}

	/** Icon for unknown lane-use indication */
	static protected class UnknownIndicationIcon extends IndicationIcon {
		protected UnknownIndicationIcon(int p) {
			super(p);
		}
		protected void paintIcon(Graphics2D g2) {
			g2.setColor(Color.GRAY);
			g2.fill(ERROR_SHAPE);
		}
	}
}
