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

import us.mn.state.dot.geokit.SphericalMercatorPosition;
import us.mn.state.dot.map.LayerChange;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.proxy.ProxyManager;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

/**
 * HeatmapLayerState is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayerState extends LayerState {
	private static final double RADIUS_METERS = 16093.44d;

	private final HeatmapLayer heatmapLayer;

	private final ProxyManager<WeatherSensor> manager;

	private final WeatherMeasurementDataSet dataSet;

	/** Listener to handle the style selection changing */
	private final ActionListener style_listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ItemStyle s = ItemStyle.lookupStyle(e.getActionCommand());

			refreshDataSet(s);
		}
	};

	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 */
	public HeatmapLayerState(HeatmapLayer layer, MapBean mb) {
		super(layer, mb, new HeatmapTheme(layer.getManager()));
		heatmapLayer = layer;
		manager = layer.getManager();
		dataSet = new WeatherMeasurementDataSet();
	}

	public void refreshDataSet(ItemStyle s) {
		dataSet.changeDataType(s);

		Iterator<WeatherSensor> wi = WeatherSensorHelper.iterator();
		while(wi.hasNext()) {
			WeatherSensor ws = wi.next();
			dataSet.add(ws);
		}
	}

	/**
	 * Paint the layer
	 *
	 * @param g
	 */
	@Override
	public void paint(final Graphics2D g) {
		super.paint(g);
		if(isVisible()) {
			refreshDataSet(manager.getStyleSummary().getStyle());
			paintRadii(g);
		}
	}

	private void paintRadii(final Graphics2D g) {
		Composite origComposite = g.getComposite();
		for (Color c : new Color[] {WeatherMeasurementDataSet.LOCOLOR,
			WeatherMeasurementDataSet.MOCOLOR,
			WeatherMeasurementDataSet.HOCOLOR}) {

			//FIXME fix compositing so later drawings disregard any overlap from previous drawing so as not to affect transparency nor color.
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
			g.setComposite(ac);
			//FIXME temporary until listener is added.
			refreshRadii(g, c);
		}
		g.setComposite(origComposite);
	}

	private void refreshRadii(final Graphics2D g, Color c) {
		g.setColor(c);
		MapGeoLoc mloc;
		int r = (int) RADIUS_METERS;
		int x;
		int y;
		List<WeatherMeasurementSample> samples = dataSet.getList(c);
		for(WeatherMeasurementSample sample : samples) {
			WeatherSensor ws = sample.getWeatherSensor();
			mloc = getManager().findGeoLoc(ws);
			SphericalMercatorPosition pos = GeoLocHelper
				.getPosition(mloc.getGeoLoc());

			x = (int) pos.getX() - r;
			y = (int) pos.getY() - r;

			g.fillOval(x, y, 2*r, 2*r);
		}

	}
	public HeatmapLayer getHeatmapLayer() {

		return heatmapLayer;
	}

	public ProxyManager<WeatherSensor> getManager() {

		return manager;
	}

	@Override
	public MapObject forEach(MapSearcher s) {
		return manager.forEach(s, getScale());
	}

	/** Get the visibility flag */
	@Override
	public boolean isVisible() {
		Boolean v = getVisible();
		boolean rv = (v != null ? v : Boolean.FALSE) && isZoomVisible();
		return rv;
	}

	/** Is the layer visible at the current zoom level? */
	private boolean isZoomVisible() {
		int curZoom = map.getModel().getZoomLevel().ordinal();
		return manager.isVisible(curZoom);
	}
}
