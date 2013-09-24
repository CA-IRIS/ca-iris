/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.proxy;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerChange;
import us.mn.state.dot.map.LayerChangedEvent;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.sched.Job;
import static us.mn.state.dot.sched.SwingRunner.runSwing;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.utils.I18N;
import static us.mn.state.dot.tms.client.IrisClient.WORKER;

/**
 * Layer for drawing SONAR proxy objects on the map.
 *
 * @author Douglas Lau
 */
public class ProxyLayer<T extends SonarObject> extends Layer
	implements ProxyListener<T>
{
	/** Shape used for calculating the layer extent */
	static protected final Rectangle2D EXTENT_SHAPE =
		new Rectangle2D.Float(-500, -500, 1000, 1000);

	/** Proxy manager for the layer */
	protected final ProxyManager<T> manager;

	/** Proxy type cache */
	protected final TypeCache<T> cache;

	/** Get the proxy manager for the layer */
	public ProxyManager<T> getManager() {
		return manager;
	}

	/** Create a new SONAR map layer */
	public ProxyLayer(ProxyManager<T> m) {
		super(I18N.get(m.getProxyType()));
		manager = m;
		cache = manager.getCache();
	}

	/** Enumeration complete flag */
	protected boolean complete;

	/** Initialize the layer. This cannot be done in the constructor
	 * because subclasses may not be fully constructed. */
	public void initialize() {
		complete = false;
		cache.addProxyListener(this);
	}

	/** Dispose of the layer */
	public void dispose() {
		cache.removeProxyListener(this);
	}

	/** Add a new proxy to the layer */
	@Override public void proxyAdded(T proxy) {
		// Don't hog the SONAR TaskProcessor thread
		if(complete) {
			// NOTE: this also gets called when we "watch" an
			//       object after it is selected.
			WORKER.addJob(new Job() {
				public void perform() {
					notifyLayerChanged(
						LayerChange.geometry);
				}
			});
		}
	}

	/** Enumeration of all proxies is complete */
	@Override public void enumerationComplete() {
		complete = true;
		// Don't hog the SONAR TaskProcessor thread
		WORKER.addJob(new Job() {
			public void perform() {
				updateExtent();
			}
		});
	}

	/** Remove a proxy from the model */
	@Override public void proxyRemoved(T proxy) {
		// Don't hog the SONAR TaskProcessor thread
		WORKER.addJob(new Job() {
			public void perform() {
				notifyLayerChanged(
					LayerChange.geometry);
			}
		});
	}

	/** Change a proxy in the model */
	@Override public void proxyChanged(T proxy, String attrib) {
		// Don't hog the SONAR TaskProcessor thread
		WORKER.addJob(new Job() {
			public void perform() {
				// Can an attribute change affect the layer?
				notifyLayerChanged(LayerChange.status);
			}
		});
	}

	/** Update the layer extent */
	public void updateExtent() {
		ExtentCalculator calc = new ExtentCalculator();
		manager.forEach(calc, 1);
		if(calc.extent != null)
			extent.setRect(calc.extent);
		notifyLayerChanged(LayerChange.extent);
	}

	/** Class to calculate the extent of the layer */
	protected class ExtentCalculator implements MapSearcher {
		protected Rectangle2D extent = null;

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

	/** Notify listeners that the layer has changed */
	protected void notifyLayerChanged(LayerChange reason) {
		final LayerChangedEvent e = new LayerChangedEvent(this, reason);
		runSwing(new Runnable() {
			public void run() {
				notifyLayerChangedListeners(e);
			}
		});
	}

	/** Create a new layer state */
	public LayerState createState(MapBean mb) {
		LayerState s = new ProxyLayerState(this, mb);
		s.addTheme(manager.getTheme());
		s.setTheme(manager.getTheme());
		return s;
	}
}
