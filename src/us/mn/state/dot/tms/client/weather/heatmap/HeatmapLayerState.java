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
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerChange;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Theme;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;
import us.mn.state.dot.tms.client.weather.WeatherSensorMarker;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.util.Iterator;

/**
 * HeatmapLayerState is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayerState extends LayerState {
	public static final String THEME_AIRTEMP = "airTemp";
	public static final String THEME_WINDSPEED = "windSpeed";
	public static final String THEME_PRECIP = "precipitation";
	public static final String THEME_VISIBILITY = "visibility";

	private static final int DEG_KELVIN = 273;

	private final HeatmapLayer heatmapLayer;

	private final WeatherHeatmapManager manager;

	private double[][] heatmapData;

	private Color[] colors;

	private int[][] heatmapDataColorIndicies;

	private BufferedImage bufferedImage;
	private Graphics2D bufferedGraphics;


	/**
	 * Create a new Heatmap Layer
	 * @param layer
	 * @param mb
	 */
	public HeatmapLayerState(HeatmapLayer layer, MapBean mb) {
		super(layer, mb, new WindHeatmapTheme(THEME_WINDSPEED, WeatherSensorManager.MARKER));
		addDefaultThemes();
		heatmapLayer = layer;
		manager = layer.getManager();
		heatmapData = new double[(int)layer.getExtent().getWidth()][(int)layer.getExtent().getHeight()];
		colors = Gradient.GRADIENT_GREEN_YELLOW_ORANGE_RED;
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
	public void paint(final Graphics2D g) {
		//super.paint(g);
		if(isVisible()) {
			final AffineTransform t = g.getTransform();
			final float scale = getScale();
			forEach(new MapSearcher() {
				public boolean next(MapObject mo) {

					getTheme().draw(g, mo, scale);
					g.setTransform(t);
					return false;
				}
			});
		}

		Composite oc = g.getComposite();
//		BufferedImage bufferedImage = new BufferedImage(data.length, data[0].length,
//								BufferedImage.TYPE_INT_ARGB);
//		g.drawImage(); // buffered image

		paintHeatmap(g);
	}

	private void paintHeatmap(Graphics2D g) {
		String tn = getTheme().getName();
		heatmapData = new double[(int)getLayer().getExtent().getWidth()][(int)getLayer().getExtent().getHeight()];
		// render heatmap image
		// draw the heat map

//		if (bufferedImage == null) {
//			// Ideally, we only call drawData in the constructor, or if we
//			// change the data or gradients. We include this just to be safe.
//			drawData();
//		}

		populateHeatmapData(this.getTheme().getName());
		updateData(heatmapData, true);
		updateDataColors();
		drawData();
		// add image to graphics object
		g.drawImage(bufferedImage, MapHotspot.IDENTITY_TRANSFORM, null);
		// add bitmap to map with applied transparency
	}

	/**
	 * Updates the data display, calls drawData() to do the expensive re-drawing
	 * of the data plot, and then calls repaint().
	 *
	 * @param data             The data to display, must be a complete array (non-ragged)
	 * @param useGraphicsYAxis If true, the data will be displayed with the y=0 row at the top of the screen. If false, the data will be displayed with the y=0 row at the bottom of the screen.
	 */
	public void updateData(double[][] data, boolean useGraphicsYAxis) {

		this.heatmapData = new double[data.length][data[0].length];
		for (int ix = 0; ix < data.length; ix++) {
			for (int iy = 0; iy < data[0].length; iy++) {
				// we use the graphics Y-axis internally
				if (useGraphicsYAxis) {
					this.heatmapData[ix][iy] = data[ix][iy];
				} else {
					this.heatmapData[ix][iy] = data[ix][
						data[0].length - iy - 1];
				}
			}
		}

		updateDataColors();

		drawData();

//		repaint();
	}

	private static AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return AlphaComposite.getInstance(type, alpha);
	}

	protected double[][] populateHeatmapData(String themeName) {
		Iterator<WeatherSensor> wi = WeatherSensorHelper.iterator();
		while(wi.hasNext()) {
			WeatherSensor ws = wi.next();
			MapGeoLoc mloc = getManager().findGeoLoc(ws);
			int x = (int)mloc.getShape().getBounds().getCenterX();
			int y = (int)mloc.getShape().getBounds().getCenterY();
			Integer val = getMeasurement(ws, themeName);
			if(val == null)
				val = 0;
			heatmapData[x][y] = val;
		}

		return heatmapData;
	}

	/**
	 * This uses the current array of colors that make up the gradient, and
	 * assigns a color index to each data point, stored in the dataColorIndices
	 * array, which is used by the drawData() method to plot the points.
	 */
	private void updateDataColors() {
		//We need to find the range of the data values,
		// in order to assign proper colors.
		double largest = Double.MIN_VALUE;
		double smallest = Double.MAX_VALUE;
		for (int x = 0; x < heatmapData.length; x++) {
			for (int y = 0; y < heatmapData[0].length; y++) {
				double val = heatmapData[x][y];
				largest = Math.max(val, largest);
				smallest = Math.min(val, smallest);
			}
		}
		double range = largest - smallest;

		// dataColorIndices is the same size as the data array
		// It stores an int index into the color array
		heatmapDataColorIndicies = new int[heatmapData.length][heatmapData[0].length];

		//assign a Color to each data point
		for (int x = 0; x < heatmapData.length; x++) {
			for (int y = 0; y < heatmapData[0].length; y++) {
				double norm = (heatmapData[x][y] - smallest)
					/ range; // 0 < norm < 1
				int colorIndex = (int) Math
					.floor(norm * (colors.length - 1));
				heatmapDataColorIndicies[x][y] = colorIndex;
			}
		}
	}

	/**
	 * Creates a BufferedImage of the actual data plot.
	 * <p>
	 * After doing some profiling, it was discovered that 90% of the drawing
	 * time was spend drawing the actual data (not on the axes or tick marks).
	 * Since the Graphics2D has a drawImage method that can do scaling, we are
	 * using that instead of scaling it ourselves. We only need to draw the
	 * data into the bufferedImage on startup, or if the data or gradient
	 * changes. This saves us an enormous amount of time. Thanks to
	 * Josh Hayes-Sheen (grey@grevian.org) for the suggestion and initial code
	 * to use the BufferedImage technique.
	 * <p>
	 * Since the scaling of the data plot will be handled by the drawImage in
	 * paintComponent, we take the easy way out and draw our bufferedImage with
	 * 1 pixel per data point. Too bad there isn't a setPixel method in the
	 * Graphics2D class, it seems a bit silly to fill a rectangle just to set a
	 * single pixel...
	 * <p>
	 * This function should be called whenever the data or the gradient changes.
	 */
	private void drawData() {

		bufferedImage = new BufferedImage(heatmapData.length, heatmapData[0].length,
			BufferedImage.TYPE_INT_ARGB);
		bufferedGraphics = bufferedImage.createGraphics();

		for (int x = 0; x < heatmapData.length; x++) {
			for (int y = 0; y < heatmapData[0].length; y++) {
				bufferedGraphics.setColor(
					colors[heatmapDataColorIndicies[x][y]]);
				bufferedGraphics.fillRect(x, y, 1, 1);
			}
		}
	}

	private static Integer getMeasurement(WeatherSensor ws, String themeName) {

		// figure the data is bad after an hour
//		if((TimeSteward.currentTimeMillis() - ws.getObsTime()) > 3600000)
//			return null;

		// all values return a minimum of 0. so temperature is returned
		// as Kelvin
		if(THEME_AIRTEMP.equals(themeName))
			return ws.getAirTemp() + DEG_KELVIN;
		else if(THEME_WINDSPEED.equals(themeName))
			return ws.getWindSpeed();
		else if(THEME_PRECIP.equals(themeName))
			return ws.getPrecipRate();
		else if(THEME_VISIBILITY.equals(themeName))
			return ws.getVisibility();


		return null;
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
