/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2014  Minnesota Department of Transportation
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.client.weather;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Angle;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.SonarObjectForm;
import static us.mn.state.dot.tms.client.widget.SwingRunner.runSwing;

/**
 * A weather sensor manager is a container for SONAR weather sensor objects.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WeatherSensorManager extends ProxyManager<WeatherSensor> {

	/** Lane marking map object marker */
	static protected final WeatherSensorMarker MARKER =
		new WeatherSensorMarker();

	/** Create a new weather sensor manager */
	public WeatherSensorManager(Session s, GeoLocManager lm) {

		super(s, lm, ItemStyle.ALL);
	}

	/** Get the sonar type name */
	@Override
	public String getSonarType() {
		return WeatherSensor.SONAR_TYPE;
	}

	/** Get the weather sensor cache */
	@Override
	public TypeCache<WeatherSensor> getCache() {
		return session.getSonarState().getWeatherSensors();
	}

	/** Get the shape for a given proxy */
	@Override
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a map tab for the managed proxies */
	@Override
	public WeatherSensorTab createTab() {

		return new WeatherSensorTab(session, this);
	}

	/** Create a theme for weather sensors */
	@Override
	protected ProxyTheme<WeatherSensor> createTheme() {
		return new WeatherSensorTheme(this, MARKER);
	}

	/**
	 * Does the proxy match the specified style?
	 * @param is The ItemStyle; may be null.
	 * @param proxy WeatherSensor proxy; never null.
	 * @return True if the proxy matches the style, else false.
	 */
	@Override
	public boolean checkStyle(ItemStyle is, WeatherSensor proxy) {
		return WeatherSensorHelper.checkStyle(is, proxy);
	}

	// BEGIN FIXME
	// The below is a workaround.  Figure out why proxyChangedSwing
	// isn't getting called via ProxyManager.listener, even when
	// notifies are clearing being sent by WeatherSensorImpl.
	// Fix the problem, then get rid of this listener, as well as the
	// initialize() method below.
	private final ProxyListener<WeatherSensor> tempListener = new
		ProxyListener<WeatherSensor>()
	{
		public void proxyAdded(WeatherSensor proxy) {}
		public void enumerationComplete() {}
		public void proxyRemoved(WeatherSensor proxy) {}
		public void proxyChanged(final WeatherSensor proxy, final String a) {
			runSwing(new Runnable() {
					public void run() {
						proxyChangedSwing(proxy, a);
					}
				});
		}
	};
	@Override
	public void initialize() {
		super.initialize();
		getCache().addProxyListener(tempListener);
	}
	// END FIXME

	/**
	 * Listener for calls from ProxyManager's SwingProxyAdapter.
	 * This is overridden in order to update the marker so that it
	 * reflects the average wind direction.
	 */
	@Override
	protected void proxyAddedSwing(WeatherSensor proxy) {
		super.proxyAddedSwing(proxy);
		updateMarker(proxy);
	}

	/**
	 * Listener for calls from ProxyManager's SwingProxyAdapter.
	 * This is overridden in order to update the marker so that it
	 * reflects the average wind direction.
	 */
	/** Called when a proxy attribute has been changed */
	@Override
	protected void proxyChangedSwing(WeatherSensor proxy, String attr) {
		super.proxyChangedSwing(proxy, attr);
		if("windDir".equals(attr))
			updateMarker(proxy);
	}

	/** Update a proxy marker (e.g., to reflect orientation change) */
	private void updateMarker(WeatherSensor p) {
		MapGeoLoc loc = findGeoLoc(p);
		if (loc == null)
			return;
		loc.doUpdate();
	}

	/** Get the tangent angle for the given location */
	@Override
	public Double getTangentAngle(MapGeoLoc loc) {
		if (loc == null)
			return null;
		Iterator<WeatherSensor> i = WeatherSensorHelper.iterator();
		WeatherSensor ws = null;
		while (i.hasNext()) {
			WeatherSensor wsi = i.next();
			if (findGeoLoc(wsi) == loc) {
				ws = wsi;
				break;
			}
		}
		double a = new Angle(ws.getWindDir()).invert().toRads();
		return Double.valueOf(a);
	}

	/** Return true if the weather sensor is in a normal state.
	 * @param p Proxy, never null. */
	private boolean isNormal(WeatherSensor p) {
		return !(WeatherSensorHelper.isAwsState(p)
			|| WeatherSensorHelper.isSampleExpired(p));
	}

	/** Create a properties form for the specified proxy */
	@Override
	protected SonarObjectForm<WeatherSensor> createPropertiesForm(
		WeatherSensor proxy)
	{
		return new WeatherSensorProperties(session, proxy);
	}

//	/** Create a popup menu for the selected proxy object(s) */
//	@Override
//	protected JPopupMenu createPopup() {
//		int n_selected = s_model.getSelectedCount();
//		if(n_selected < 1)
//			return null;
//		if(n_selected == 1) {
//			for(WeatherSensor ws: s_model.getSelected())
//				return createSinglePopup(ws);
//		}
//		JPopupMenu p = new JPopupMenu();
//		p.add(new JLabel("" + n_selected + " Weather Sensors"));
//		p.addSeparator();
//		return p;
//	}

//	/** Create a popup menu for a single proxy selection */
//	private JPopupMenu createSinglePopup(WeatherSensor proxy) {
//		JPopupMenu p = new JPopupMenu();
//		p.add(makeMenuLabel(getDescription(proxy)));
//		p.addSeparator();
//		p.add(new PropertiesAction<WeatherSensor>(this, proxy) {
//			protected void do_perform() {
//				showPropertiesForm();
//			}
//		});
//		return p;
//	}

	/** Find the map geo location for a proxy */
	@Override
	protected GeoLoc getGeoLoc(WeatherSensor proxy) {
		return proxy.getGeoLoc();
	}

	/** Get the layer zoom visibility threshold */
	protected int getZoomThreshold() {
		return 4;
	}

	/** Get the description of a proxy */
	@Override
	public String getDescription(WeatherSensor proxy) {
		String pn = proxy.getName();
		String sn = SiteDataHelper.getSiteName(pn);
		return ( (sn != null) ? sn : pn );
	}

}
