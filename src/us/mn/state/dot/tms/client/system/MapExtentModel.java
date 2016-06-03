/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.system;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import us.mn.state.dot.geokit.Position;
import us.mn.state.dot.geokit.SphericalMercatorPosition;
import us.mn.state.dot.geokit.ZoomLevel;
import us.mn.state.dot.tms.MapExtent;
import us.mn.state.dot.tms.client.IrisClient;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for map extents.
 *
 * @author Douglas Lau
 */
public class MapExtentModel extends ProxyTableModel<MapExtent> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<MapExtent>> createColumns() {
		ArrayList<ProxyColumn<MapExtent>> cols =
			new ArrayList<ProxyColumn<MapExtent>>(4);
		cols.add(new ProxyColumn<MapExtent>("location.map.extent", 160){
			public Object getValueAt(MapExtent me) {
				return me.getName();
			}
		});
		cols.add(new ProxyColumn<MapExtent>("location.longitude", 80,
			Float.class)
		{
			public Object getValueAt(MapExtent me) {
				return me.getLon();
			}
			public boolean isEditable(MapExtent me) {
				return canUpdate(me);
			}
			public void setValueAt(MapExtent me, Object value) {
				if(value instanceof Float)
					me.setLon((Float)value);
			}
		});
		cols.add(new ProxyColumn<MapExtent>("location.latitude", 80,
			Float.class)
		{
			public Object getValueAt(MapExtent me) {
				return me.getLat();
			}
			public boolean isEditable(MapExtent me) {
				return canUpdate(me);
			}
			public void setValueAt(MapExtent me, Object value) {
				if(value instanceof Float)
					me.setLat((Float)value);
			}
		});
		cols.add(new ProxyColumn<MapExtent>("location.map.zoom", 80,
			Integer.class)
		{
			public Object getValueAt(MapExtent me) {
				return me.getZoom();
			}
			public boolean isEditable(MapExtent me) {
				return canUpdate(me);
			}
			public void setValueAt(MapExtent me, Object value) {
				if(value instanceof Integer)
					me.setZoom((Integer)value);
			}
		});
		return cols;
	}

	/** Iris client */
	private final IrisClient client;

	/** Create a new map extent table model */
	public MapExtentModel(Session s, IrisClient ic) {
		super(s, s.getSonarState().getMapExtents(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      true);	/* has_name */
		client = ic;
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return MapExtent.SONAR_TYPE;
	}

	/** Get the visible row count */
	@Override
	public int getVisibleRowCount() {
		return 20;
	}

	/** Create an object with the given name */
	@Override
	public void createObject(String name) {
		String n = name.trim();
		if (n.length() > 0)
			cache.createObject(n, createAttrs());
	}

	/** Create attrs for a new map extent */
	private HashMap<String, Object> createAttrs() {
		Point2D c = client.getMapModel().getCenter();
		SphericalMercatorPosition smp = new SphericalMercatorPosition(
			c.getX(), c.getY());
		Position pos = smp.getPosition();
		ZoomLevel zoom = client.getMapModel().getZoomLevel();
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("lat", pos.getLatitude());
		attrs.put("lon", pos.getLongitude());
		attrs.put("zoom", zoom.ordinal());
		attrs.put("position", cache.size());
		return attrs;
	}

	/** If hasManualSort this sets the manual sort value. */
	public void setManualSort(MapExtent proxy, int sort) {
		proxy.setPosition(sort);
		proxyChangedSwing(proxy);
	}

	/** If hasManualSort this gets the manual sort value. */
	protected int getManualSort(MapExtent proxy) {
		return proxy.getPosition();
	}
}
