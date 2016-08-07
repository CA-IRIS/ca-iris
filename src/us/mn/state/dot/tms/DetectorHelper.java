/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
 * Copyright (C) 2010-2015  AHMCT, University of California
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
package us.mn.state.dot.tms;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Helper class for detectors.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class DetectorHelper extends BaseHelper {

	/** Don't allow instances to be created */
	private DetectorHelper() {
		assert false;
	}

	/** Lookup the detector with the specified name */
	static public Detector lookup(String name) {
		return (Detector)namespace.lookupObject(Detector.SONAR_TYPE,
			name);
	}

	/** Get a detector iterator */
	static public Iterator<Detector> iterator() {
		return new IteratorWrapper<>(namespace.iterator(
			Detector.SONAR_TYPE));
	}

	/** Get the geo location of a detector */
	static public GeoLoc getGeoLoc(Detector d) {
		R_Node n = d.getR_Node();
		return (n != null) ? n.getGeoLoc() : null;
	}

	/** Get the detector label */
	static public String getLabel(Detector det) {
		StringBuilder b = new StringBuilder();
		b.append(GeoLocHelper.getRootLabel(getGeoLoc(det)));
		if (b.toString().equals(GeoLocHelper.FUTURE))
			return b.toString();
		LaneType lt = LaneType.fromOrdinal(det.getLaneType());
		b.append(lt.suffix);
		int l_num = det.getLaneNumber();
		if (l_num > 0)
			b.append(l_num);
		if (det.getAbandoned())
			b.append("-ABND");
		return b.toString();
	}

	/** Test if a detector is active */
	static public boolean isActive(Detector d) {
		return ControllerHelper.isActive(d.getController())
		    && !d.getAbandoned();
	}

	//FIXME CA-MN-MERGE needed?
	/**
	 * Return all the detectors for the specified controller.
	 * @param c Controller (may be null)
	 */
	static public Detector[] getDetectors(Controller c) {
		final String cname = c.getName();
		final ArrayList<Detector> dets = new ArrayList<>();
		Iterator<Detector> it = iterator();
		while(it.hasNext()) {
			Detector det = it.next();
			Controller dc = det.getController();
			if (dc != null) {
				String cn = dc.getName();
				if(cn != null && cn.equals(cname))
					dets.add(det);
			}
		}
		return dets.toArray(new Detector[dets.size()]);
	}

	/**
	 * Return a detector's associated r_node station id.
	 * @param d Detector (may be null)
	 * @return The r_node's station ID, or null if not found.
	 */
	static public String getStationId(Detector d) {
		R_Node rn = null;
		if(d != null)
			rn = d.getR_Node();
		return (rn != null) ? rn.getStationID() : null;
	}

}
