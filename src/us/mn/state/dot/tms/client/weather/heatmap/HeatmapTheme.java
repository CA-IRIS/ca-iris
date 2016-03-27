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

import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * HeatmapTheme is for...
 *
 * @author Jacob Barde
 */
public class HeatmapTheme extends ProxyTheme {

	public HeatmapTheme(ProxyManager m) {
		this(m, new DummyShape());
	}
	public HeatmapTheme(ProxyManager m, Shape s) {
		super(m, s);
	}

	private static class DummyShape extends RectangularShape {

		public DummyShape() {
		}

		@Override
		public double getHeight() {
			return 0;
		}

		@Override
		public double getX() {
			return 0;
		}

		@Override
		public double getY() {
			return 0;
		}

		@Override
		public double getWidth() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public void setFrame(double x, double y, double w, double h) {

		}

		@Override
		public Rectangle2D getBounds2D() {
			return null;
		}

		@Override
		public boolean contains(double x, double y) {
			return false;
		}

		@Override
		public boolean intersects(double x, double y, double w,
			double h) {
			return false;
		}

		@Override
		public boolean contains(double x, double y, double w,
			double h) {
			return false;
		}

		@Override
		public PathIterator getPathIterator(AffineTransform at) {
			return null;
		}
	}
}
