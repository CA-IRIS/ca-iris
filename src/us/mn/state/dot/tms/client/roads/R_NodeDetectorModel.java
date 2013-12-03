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
package us.mn.state.dot.tms.client.roads;

import java.util.ArrayList;
import java.util.HashMap;
import us.mn.state.dot.tms.Detector;
import us.mn.state.dot.tms.DetectorHelper;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for r_node detectors
 *
 * @author Douglas Lau
 */
public class R_NodeDetectorModel extends ProxyTableModel<Detector> {

	/** Create the columns in the model */
	protected ArrayList<ProxyColumn<Detector>> createColumns() {
		ArrayList<ProxyColumn<Detector>> cols =
			new ArrayList<ProxyColumn<Detector>>(2);
		cols.add(new ProxyColumn<Detector>("detector", 60) {
			public Object getValueAt(Detector d) {
				return d.getName();
			}
		});
		cols.add(new ProxyColumn<Detector>("detector.label", 150) {
			public Object getValueAt(Detector d) {
				return DetectorHelper.getLabel(d);
			}
		});
		return cols;
	}

	/** R_Node in question */
	protected final R_Node r_node;

	/** Create a new r_node detector table model */
	public R_NodeDetectorModel(Session s, R_Node n) {
		super(s, s.getSonarState().getDetCache().getDetectors());
		r_node = n;
	}

	/** Get the count of rows in the table */
	@Override
	public int getRowCount() {
		synchronized(proxies) {
			return proxies.size();
		}
	}

	/** Add a new proxy to the list model */
	@Override
	protected int doProxyAdded(Detector proxy) {
		if(proxy.getR_Node() == r_node)
			return super.doProxyAdded(proxy);
		else
			return -1;
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return Detector.SONAR_TYPE;
	}

	/** Create a new detector */
	public void create(String name) {
		if(name.length() > 0 && canAdd(name)) {
			HashMap<String, Object> attrs =
				new HashMap<String, Object>();
			attrs.put("r_node", r_node);;
			cache.createObject(name, attrs);
		}
	}

	/** Transfer a detector to the r_node */
	public void transfer(Detector det) {
		det.setR_Node(r_node);
	}
}
