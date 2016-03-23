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
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.proxy.ProxyLayer;
import us.mn.state.dot.tms.client.widget.IWorker;
import us.mn.state.dot.tms.utils.I18N;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * HeatmapLayer is for...
 *
 * @author Jacob Barde
 */
public class HeatmapLayer extends ProxyLayer /*implements Iterable<Hotspot>*/ {

	/** Shape used for calculating the layer extent */
	static private final Rectangle2D EXTENT_SHAPE =
		new Rectangle2D.Float(-500, -500, 1000, 1000);



	public HeatmapLayer(Session s, WeatherHeatmapManager m) {
		super(I18N.get("weather.heatmaps"), m);
	}

	@Override
	public LayerState createState(MapBean mb) {
		return new HeatmapLayerState(this, mb);
	}


	/** Update the layer geometry */
	public void updateGeometry() {
		fireLayerChanged(LayerChange.geometry);
	}

	/** Update the layer status */
	public void updateStatus() {
		fireLayerChanged(LayerChange.status);
	}

	/** Update the layer extent */
	public void updateExtent() {
		IWorker<Rectangle2D> worker = new IWorker<Rectangle2D>() {
			@Override
			public Rectangle2D doInBackground() {
				ExtentCalculator calc = new ExtentCalculator();
				getManager().forEach(calc, 1);
				return calc.extent;
			}
			@Override
			public void done() {
				Rectangle2D e = getResult();
				if(e != null)
					setExtent(e);
			}
		};
		worker.execute();
	}

//	/**
//	 * Returns an iterator over elements of type {@code T}.
//	 *
//	 * @return an Iterator.
//	 */
//	@Override
//	public Iterator<Hotspot> iterator() {
//
//		Iterator<WeatherSensor> wi = WeatherSensorHelper.iterator();
//		double x;
//		double y;
//		while(wi.hasNext()) {
//			WeatherSensor ws = wi.next();
//			MapGeoLoc loc = getManager().findGeoLoc(ws);
//			x = loc.getShape().getBounds().getCenterX();
//			y = loc.getShape().getBounds().getCenterY();
//		}
//
//		final Iterator<List<Hotspot>> cors =
//			cor_segs.values().iterator();
//		return new Iterator<Hotspot>() {
//			Iterator<Hotspot> hotspotIterator;
//			public boolean hasNext() {
//				if(hotspotIterator != null && hotspotIterator.hasNext())
//					return true;
//				while(cors.hasNext()) {
//					hotspotIterator = cors.next().iterator();
//					if(hotspotIterator.hasNext())
//						return true;
//				}
//				return false;
//			}
//			public Hotspot next() {
//				if(hotspotIterator != null)
//					return hotspotIterator.next();
//				else
//					throw new NoSuchElementException();
//			}
//			public void remove() {
//				throw new UnsupportedOperationException();
//			}
//		};
//	}

	/** Class to calculate the extent of the layer */
	private class ExtentCalculator implements MapSearcher {
		private Rectangle2D extent = null;

		public boolean next(MapObject o) {
			AffineTransform t = o.getTransform();
			Rectangle2D b = t.createTransformedShape(
				EXTENT_SHAPE).getBounds2D();
			if(extent == null) {
				extent = new Rectangle2D.Double();
				extent.setRect(b);
			} else
				extent.add(b);
			return false;
		}
	}

}
