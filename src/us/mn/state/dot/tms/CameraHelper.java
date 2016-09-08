/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import us.mn.state.dot.tms.geo.Position;
import us.mn.state.dot.tms.units.Distance;
import us.mn.state.dot.tms.utils.GPSutil;

import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_CONCUR_MOVE;
import static us.mn.state.dot.tms.SystemAttrEnum.CAMERA_SHIFT_MOVE_PAUSE;

/**
 * Helper class for cameras.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 * @uahtor Jacob Barde
 */
public class CameraHelper extends BaseHelper {

	/** Don't allow instances to be created */
	private CameraHelper() {
		assert false;
	}

	/** Lookup the camera with the specified name */
	static public Camera lookup(String name) {
		return (Camera)namespace.lookupObject(Camera.SONAR_TYPE,
			name);
	}

	/** Get a camera iterator */
	static public Iterator<Camera> iterator() {
		return new IteratorWrapper<Camera>(namespace.iterator(
			Camera.SONAR_TYPE));
	}

	/** Get the encoder type for a camera */
	static public EncoderType getEncoderType(Camera cam) {
		return EncoderType.fromOrdinal(cam.getEncoderType());
	}

	/** Get the host ip for the camera's encoder */
	static public String parseEncoderIp(Camera cam) {
		String enc = cam.getEncoder();
		if(enc != null && enc.indexOf(':') >= 0)
			return enc.substring(0, enc.indexOf(':'));
		else
			return enc.trim();
	}

	/** Find the nearest cameras to a position */
	static public Collection<Camera> findNearest(Position pos, int n_count){
		TreeMap<Double, Camera> cams = new TreeMap<Double, Camera>();
		Iterator<Camera> it = iterator();
		while(it.hasNext()) {
			Camera cam = it.next();
			GeoLoc loc = cam.getGeoLoc();
			Distance d = GeoLocHelper.distanceTo(loc, pos);
			if(d != null) {
				cams.put(d.m(), cam);
				while(cams.size() > n_count)
					cams.pollLastEntry();
			}
		}
		return cams.values();
	}

	/** Find a camera with the specific UID */
	static public Camera findUID(String uid) {
		Integer id = parseUID(uid);
		if (id != null) {
			Iterator<Camera> it = iterator();
			while (it.hasNext()) {
				Camera cam = it.next();
				Integer cid = parseUID(cam.getName());
				if (id.equals(cid))
					return cam;
			}
		}
		return null;
	}

	/** Parse the integer ID of a camera */
	static public Integer parseUID(String uid) {
		String id = stripNonDigitPrefix(uid);
		try {
			return Integer.parseInt(id);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	/** Strip non-digit prefix from a string */
	static private String stripNonDigitPrefix(String v) {
		int i = 0;
		for (i = 0; i < v.length(); i++) {
			if (Character.isDigit(v.charAt(i)))
				break;
		}
		return v.substring(i);
	}

	/**
	 * Retrieve a list of cameras with night-shift
	 * change time
	 * @return
	 */
	static public List<Camera> getCamerasByShift(PresetAliasName pan) {
		List<Camera> rv = new ArrayList<>();
		Iterator<Camera> it = iterator();
		while (it.hasNext()) {
			Camera cam = it.next();
			if (PresetAliasHelper.hasShiftPreset(cam, pan))
				rv.add(cam);
		}
		return rv;
	}

	/** return the geographical center of all cameras */
	static public Position getGeographicCenter() {
		List<Position> pl = new ArrayList<>();
		double lat = 0.0;
		double lon = 0.0;

		Iterator<Camera> it = iterator();
		while (it.hasNext()) {
			Camera cam = it.next();
			lat = cam.getGeoLoc().getLat();
			lon = cam.getGeoLoc().getLon();
			pl.add(new Position(lat, lon));
		}

		return GPSutil.getGeographicCenter(pl);
	}

	/** get the shift pause system attribute */
	static public int getShiftPause() {
		int delay = 0;

		if (null != CAMERA_SHIFT_MOVE_PAUSE)
			delay = CAMERA_SHIFT_MOVE_PAUSE.getInt();

		return delay;
	}

	/** get the shift concurrent movements system attribute */
	static public int getConcurrentMovements() {
		int concurrent = 1;

		if (null != CAMERA_SHIFT_CONCUR_MOVE)
			concurrent = CAMERA_SHIFT_CONCUR_MOVE.getInt();

		return concurrent;
	}
}
