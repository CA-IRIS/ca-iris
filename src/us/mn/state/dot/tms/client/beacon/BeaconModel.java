/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.beacon;

import java.util.ArrayList;
import us.mn.state.dot.tms.Beacon;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;
import us.mn.state.dot.tms.client.proxy.SonarObjectForm;

/**
 * Table model for beacons.
 *
 * @author Douglas Lau
 */
public class BeaconModel extends ProxyTableModel2<Beacon> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<Beacon>> createColumns() {
		ArrayList<ProxyColumn<Beacon>> cols =
			new ArrayList<ProxyColumn<Beacon>>(2);
		cols.add(new ProxyColumn<Beacon>("beacon", 200) {
			public Object getValueAt(Beacon b) {
				return b.getName();
			}
		});
		cols.add(new ProxyColumn<Beacon>("location", 300) {
			public Object getValueAt(Beacon b) {
				return GeoLocHelper.getDescription(
					b.getGeoLoc());
			}
		});
		return cols;
	}

	/** Create a new beacon table model */
	public BeaconModel(Session s) {
		super(s, s.getSonarState().getBeacons());
	}

	/** Get the visible row count */
	@Override
	public int getVisibleRowCount() {
		return 12;
	}

	/** Determine if create button is available */
	@Override
	public boolean canCreate() {
		return true;
	}

	/** Determine if a properties form is available */
	@Override
	public boolean hasProperties() {
		return true;
	}

	/** Create a properties form for one proxy */
	@Override
	protected BeaconProperties createPropertiesForm(Beacon proxy) {
		return new BeaconProperties(session, proxy);
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return Beacon.SONAR_TYPE;
	}
}
