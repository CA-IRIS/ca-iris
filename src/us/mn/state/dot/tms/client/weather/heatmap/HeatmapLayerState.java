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
import us.mn.state.dot.map.LayerChange;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Theme;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;
import us.mn.state.dot.tms.client.weather.WeatherSensorMarker;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * HeatmapLayerState is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayerState<T extends SonarObject> extends LayerState {
	public static final String THEME_AIRTEMP = "airTemp";
	public static final String THEME_WINDSPEED = "windSpeed";
	public static final String THEME_PRECIP = "precipitation";
	public static final String THEME_VISIBILITY = "visibility";

	private final HeatmapLayer heatmapLayer;

	private final WeatherHeatmapManager manager;

	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 */
	public HeatmapLayerState(HeatmapLayer layer, MapBean mb) {
		super(layer, mb);
		addDefaultThemes();
		heatmapLayer = layer;
		manager = layer.getManager();
	}
	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 * @param theme
	 */
	public HeatmapLayerState(HeatmapLayer layer, MapBean mb, Theme theme) {
		super(layer, mb, theme);
		addDefaultThemes();
		heatmapLayer = layer;
		manager = layer.getManager();
	}

	private void addDefaultThemes() {
		addTheme(new WindHeatmapTheme(THEME_WINDSPEED, WeatherSensorManager.MARKER));
		addTheme(new TempHeatmapTheme(THEME_AIRTEMP, WeatherSensorManager.MARKER));
		//FIXME
		addTheme(new WindHeatmapTheme(THEME_PRECIP, WeatherSensorManager.MARKER));
		addTheme(new WindHeatmapTheme(THEME_VISIBILITY, WeatherSensorManager.MARKER));
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
		Composite oc = g.getComposite();
//		BufferedImage bufferedImage = new BufferedImage(data.length, data[0].length,
//								BufferedImage.TYPE_INT_ARGB);
//		g.drawImage(); // buffered image
	}

	private void paintHeatmap(Graphics2D g) {
		String tn = getTheme().getName();

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

	/** Flag to indicate the tab is selected */
	private boolean tab_selected = false;

	/** Set the tab selected flag */
	public void setTabSelected(boolean ts) {
		tab_selected = ts;
		if(getVisible() == null)
			fireLayerChanged(LayerChange.visibility);
	}

	/** Get the visibility flag */
	@Override
	public boolean isVisible() {
		Boolean v = getVisible();
		return v != null ? v : tab_selected || isZoomVisible();
	}

	/** Is the layer visible at the current zoom level? */
	private boolean isZoomVisible() {
		return manager.isVisible(
			map.getModel().getZoomLevel().ordinal());
	}
}
