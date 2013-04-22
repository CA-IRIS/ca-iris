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
package us.mn.state.dot.tms.client.marking;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.JPopupMenu;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.LaneMarking;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A lane marking manager is a container for SONAR lane marking objects.
 *
 * @author Douglas Lau
 */
public class LaneMarkingManager extends ProxyManager<LaneMarking> {

	/** Lane marking map object marker */
	static protected final LaneMarkingMarker MARKER =
		new LaneMarkingMarker();

	/** User session */
	protected final Session session;

	/** Create a new lane marking manager */
	public LaneMarkingManager(Session s, TypeCache<LaneMarking> c,
		GeoLocManager lm)
	{
		super(c, lm);
		session = s;
		cache.addProxyListener(this);
	}

	/** Get the proxy type name */
	public String getProxyType() {
		return I18N.get("lane.marking");
	}

	/** Get the shape for a given proxy */
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a theme for lane markings */
	protected ProxyTheme<LaneMarking> createTheme() {
		ProxyTheme<LaneMarking> theme = new ProxyTheme<LaneMarking>(
			this, MARKER);
		theme.addStyle(ItemStyle.NO_CONTROLLER,
			ProxyTheme.COLOR_NO_CONTROLLER);
		theme.addStyle(ItemStyle.ALL);
		return theme;
	}

	/** Check the style of the specified proxy */
	public boolean checkStyle(ItemStyle is, LaneMarking proxy) {
		switch(is) {
		case NO_CONTROLLER:
			return proxy.getController() == null;
		case ALL:
			return true;
		default:
			return false;
		}
	}

	/** Show the properties form for the selected proxy */
	public void showPropertiesForm() {
		// FIXME
	}

	/** Create a popup menu for the selected proxy object(s) */
	protected JPopupMenu createPopup() {
		// No popup
		return null;
	}

	/** Find the map geo location for a proxy */
	protected GeoLoc getGeoLoc(LaneMarking proxy) {
		return proxy.getGeoLoc();
	}

	/** Get the layer zoom visibility threshold */
	protected int getZoomThreshold() {
		return 17;
	}
}
