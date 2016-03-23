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

import us.mn.state.dot.map.LayerChange;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Theme;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

	private final ProxyManager<WeatherSensor> manager;

	private double[][] heatmapData;

	private Color[] colors;

	private int[][] heatmapDataColorIndicies;

	private BufferedImage bufferedImage;
	private Graphics2D bufferedGraphics;

	int posX = 0;
	int posY = 0;

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
		int w = (int)layer.getExtent().getWidth();
		int h = (int)layer.getExtent().getHeight();
		System.out.println(new StringBuilder("init. w=").append(w)
			.append(", h=").append(h)
			.append(". total=").append((w*h))
			.toString());
		//heatmapData = new double[(int)layer.getExtent().getWidth()][(int)layer.getExtent().getHeight()];
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
		super.paint(g);
		if(!isVisible()) {
			return;
		}

//		Composite oc = g.getComposite();
//		BufferedImage bufferedImage = new BufferedImage(data.length, data[0].length,
//								BufferedImage.TYPE_INT_ARGB);
//		g.drawImage(); // buffered image

		paintHeatmap(g);
	}

	private void paintHeatmap(Graphics2D g) {
		String tn = getTheme().getName();

//		int w = (int)getLayer().getExtent().getWidth();
//		int h = (int)getLayer().getExtent().getHeight();
//		System.out.println(new StringBuilder("w=").append(w)
//			.append(", h=").append(h)
//			.append(". total=").append((w*h))
//			.toString());

//		heatmapData = new double[1000][1000];
		// render heatmap image
		// draw the heat map

//		if (bufferedImage == null) {
//			// Ideally, we only call drawData in the constructor, or if we
//			// change the data or gradients. We include this just to be safe.
//			drawData();
//		}

		heatmapData = populateHeatmapData(this.getTheme().getName(), g);
		updateData(heatmapData, true);
		//updateDataColors();
		//drawData();
//		// add image to graphics object
//		g.drawImage(bufferedImage, g.getTransform(), null);
		AffineTransform at = new AffineTransform();
		at.scale(getScale(), getScale());
		AffineTransformOp atOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage after = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		after = atOp.filter(bufferedImage, after);
		g.drawImage(bufferedImage, at, null);
//		g.drawImage(bufferedImage, this.posX, this.posY, null);
		// add bitmap to map with applied transparency


//		double[][] data = HeatMap.generateRampTestData();
//		boolean useGraphicsYAxis = true;
//		JPanel panel = new HeatMap(data, useGraphicsYAxis,
//			Gradient.GRADIENT_BLUE_TO_RED);
//		Rectangle r = new Rectangle(0, 0, 100, 100);
//
//		g.draw(r);
//		panel.getGraphics();

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

	protected double[][] populateHeatmapData(String themeName, Graphics2D g) {
		Iterator<WeatherSensor> wi = WeatherSensorHelper.iterator();
		DataHolder dh = new DataHolder();
		forEach(new MapSearcher() {
			public boolean next(MapObject mo) {
//				theme.draw(g, mo, scale);
//				g.setTransform(t);
				double x = mo.getShape().getBounds().getX();
				double y = mo.getShape().getBounds().getY();
				System.out.println("mo.x=" + x + ", mo.y=" + y);
				return false;
			}
		});

		while(wi.hasNext()) {
			WeatherSensor ws = wi.next();
			MapGeoLoc mloc = getManager().findGeoLoc(ws);
			int x = (int)mloc.getShape().getBounds().getX();
			int y = (int)mloc.getShape().getBounds().getY();
			Integer val = getMeasurement(ws, themeName);
			if(val == null)
				val = 0;
			//heatmapData[x][y] = val;
			//dh.add(new GraphData(x, y, val));
		}
		int j = 0;
		int i = 0;
		dh.add(new GraphData(i, j, 0d));

		int dim = 100;
		for(i = 0; i<dim; i++) {
			j = (int) (Math.random()*dim);
			dh.add(new GraphData(i, j, (10 + Math.random()*21)));
		}

		this.posX = dh.minX;
		this.posY = dh.minY;

		return dh.generate2dArray();
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
		boolean rv = (tab_selected || (v==null?Boolean.FALSE : v)) && isZoomVisible();
		return rv;
	}

	/** Is the layer visible at the current zoom level? */
	private boolean isZoomVisible() {
		return manager.isVisible(
			map.getModel().getZoomLevel().ordinal());
	}

	private class DataHolder {
		private int minX = Integer.MAX_VALUE;
		private int minY = Integer.MAX_VALUE;
		private int maxX = Integer.MIN_VALUE;
		private int maxY = Integer.MIN_VALUE;
		private double minValue = Double.MAX_VALUE;
		private double maxValue = Double.MIN_VALUE;

		private java.util.List<GraphData> data = null;

		public DataHolder() {
			data = new ArrayList<GraphData>();
		}

		public void add(GraphData g) {
			this.minX = Math.min(minX, g.x);
			this.minY = Math.min(minY, g.y);
			this.maxX = Math.max(maxX, g.x);
			this.maxY = Math.max(maxY, g.y);

			this.minValue = Math.min(minValue, g.value);
			this.maxValue = Math.max(maxValue, g.value);

			this.data.add(g);
		}

		public final double[][] generate2dArray() {
			int w = maxX - minX;
			int h = maxY - minY;
			int xoff = 0 - minX;
			int yoff = 0 - minY;
			int dim = Math.max(w, h) + 10;

			double[][] d = new double[dim][dim];

			//initialize whole array to 0.0 values
			for (int ix = 0; ix < d.length; ix++) {
				for (int iy = 0; iy < d[0].length; iy++) {
					d[ix][iy] = 0.0d;
				}
			}

			StringBuilder sb = new StringBuilder("\ngenerate2dArray: ");
			sb.append("\nminX=").append(minX).append(" minY=").append(minY);
			sb.append("\nmaxX=").append(maxX).append(" maxY=").append(maxY);
			sb.append("\nxoff=").append(xoff).append(" yoff=").append(yoff);
			sb.append("\ndimension=").append(dim);

			System.out.println(sb.toString());
			for (GraphData g : this.data) {
//				if(xoff != 0)
//					g.x += xoff;
//				if(yoff != 0)
//					g.y += yoff;
				d[g.x][g.y] = g.value;
			}

			return d;
		}
	}

	private class GraphData {
		private int x = 0;
		private int y = 0;
		private double value = 0d;

		public GraphData(int x, int y, double v) {
			this.x = x;
			this.y = y;
			this.value = v;
			System.out.println("data["+x+"]["+y+"]="+v);
		}
	}
}
