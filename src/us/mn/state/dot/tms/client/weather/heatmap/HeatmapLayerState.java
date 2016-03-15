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

import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Theme;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * HeatmapLayerState is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayerState extends LayerState {

	private final HeatmapLayer heatmapLayer;

	private final WeatherHeatmapManager manager;

	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 * @param theme
	 */
	public HeatmapLayerState(HeatmapLayer layer, MapBean mb) {
		super(layer, mb);
		//add themes?
		heatmapLayer = layer;
		manager = layer.getManager();
	}
	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 * @param theme
	 */
	public HeatmapLayerState(HeatmapLayer layer,
		MapBean mb, Theme theme) {
		super(layer, mb, theme);
		//add themes?
		heatmapLayer = layer;
		manager = layer.getManager();
	}

	/** Is the zoom level past the "individual lane" threshold? */
	private boolean isPastHeatmapZoomThreshold() {
		return false;
		/* return map.getModel().getZoomLevel().ordinal() >= 14
			|| map.getModel().getZoomLevel().ordinal() <= 2; */
	}

	/**
	 * Paint the layer
	 *
	 * @param g
	 */
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g.create();
		Composite oc = g2d.getComposite();
//		BufferedImage bufferedImage = new BufferedImage(data.length, data[0].length,
//			BufferedImage.TYPE_INT_ARGB);
		Rectangle r = new Rectangle(100, 100, 100, 100);

		g2d.setPaint(Color.GREEN);
		g2d.setComposite(makeComposite(0.5f));
		g2d.fill(r);
		g2d.setComposite(oc);
	}

	private static AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return AlphaComposite.getInstance(type, alpha);
	}
	/**
	 * Search the layer for a map object containing the given point
	 *
	 * @param p
	 */
	@Override
	public MapObject search(final Point2D p) {

		return forEach(new MapSearcher() {
			public boolean next(MapObject mo) {
				return mo.getShape().contains(p);
			}
		});
	}

	/**
	 * Get the appropriate tool tip text for the specified point
	 *
	 * @param p
	 */
	@Override
	public String getTip(Point2D p) {

		return super.getTip(p);
	}

	public HeatmapLayer getHeatmapLayer() {

		return heatmapLayer;
	}

	public WeatherHeatmapManager getManager() {

		return manager;
	}

	@Override
	public MapObject forEach(MapSearcher s) {
		return manager.forEach(s, getScale());

	}
}
