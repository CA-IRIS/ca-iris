/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.units.Distance;
import us.mn.state.dot.tms.units.Speed;
import us.mn.state.dot.tms.utils.MultiBuilder;
import us.mn.state.dot.tms.utils.MultiString;

/**
 * Slow Warning Formatter
 *
 * @author Douglas Lau
 */
public class SlowWarningFormatter {

	/** SLOW debug log */
	static private final DebugLog SLOW_LOG = new DebugLog("slow");

	/** Round speed to the nearest 5 mph */
	static private int nearest5Mph(Speed s) {
		float sf = s.asFloat(Speed.Units.MPH);
		return Math.round(sf / 5) * 5;
	}

	/** Check if debug log is open */
	private boolean isLogging() {
		return SLOW_LOG.isOpen();
	}

	/** Log a debug message */
	public void log(String msg) {
		SLOW_LOG.log(loc.getName() + ": " + msg);
	}

	/** Location for warning */
	private final GeoLoc loc;

	/** Create a new slow warning formatter.
	 * @param l Location of sign. */
	public SlowWarningFormatter(GeoLoc l) {
		loc = l;
	}

	/** Replace slow warning tags in a MULTI string.
	 * @param multi MULTI string to parse.
	 * @return MULTI string with slow warning tags replaced. */
	public String replaceSlowWarning(String multi) {
		MultiCallback cb = new MultiCallback();
		new MultiString(multi).parse(cb);
		if (cb.valid)
			return cb.toString();
		else
			return null;
	}

	/** MultiBuilder for replacing slow warning tags */
	private class MultiCallback extends MultiBuilder {

		protected boolean valid = true;

		/** Add a slow traffic warning.
		 * @param spd Highest speed to activate warning.
		 * @param dist Distance to search for slow traffic (1/10 mile).
		 * @param mode Tag replacement mode (none, dist or speed). */
		public void addSlowWarning(int spd, int dist, String mode) {
			String span = slowWarningSpan(createSpeed(spd),
				createDist(dist), mode);
			if (span != null)
				addSpan(span);
			else
				valid = false;
		}
	}

	/** Create a speed.
	 * @param v Speed value.
	 * @return Matching speed. */
	private Speed createSpeed(int v) {
		// FIXME: use system attribute for units
			return new Speed(v, Speed.Units.MPH);
	}

	/** Create a distance.
	 * @param v Distance value.
	 * @return Matching distance. */
	private Distance createDist(int v) {
		// FIXME: use system attribute for units
		int m = Math.max(v, 0);
		return new Distance(m * 0.1f, Distance.Units.MILES);
	}

	/** Calculate slow warning text span.
	 * @param spd Highest speed to activate warning.
	 * @param dist Distance to search for slow traffic.
	 * @param mode Tag replacement mode (none, dist or speed).
	 * @return Tag replacement span, or null for no warning. */
	private String slowWarningSpan(Speed spd, Distance dist, String mode) {
		if ("dist".equals(mode))
			return slowWarningSpanDist(spd, dist);
		else if ("speed".equals(mode))
			return slowWarningSpanSpeed(spd, dist);
		else {
			Distance d = slowWarningDistance(spd, dist);
			return (d != null) ? "" : null;
		}
	}

	/** Calculate slow warning text span as distance.
	 * @param spd Highest speed to activate warning.
	 * @param dist Distance to search for slow traffic.
	 * @return Span of distance to slow traffic (in miles) or null. */
	private String slowWarningSpanDist(Speed spd, Distance dist) {
		Distance d = slowWarningDistance(spd, dist);
		if (d != null) {
			int di = d.round(d.units);
			if (di > 0)
				return String.valueOf(di);
		}
		return null;
	}

	/** Calculate the slow warning distance.
	 * @param as Speed to activate slow warning.
	 * @param bd Distance limit to backup.
	 * @return Distance to backup or null for no backup. */
	private Distance slowWarningDistance(Speed as, Distance bd) {
		BackupFinder bf = slowWarningBackup(as, bd);
		return (bf != null) ? bf.distance() : null;
	}

	/** Calculate slow warning text span as speed.
	 * @param spd Highest speed to activate warning.
	 * @param dist Distance to search for slow traffic.
	 * @return Span of speed of slow traffic (in mph) or null. */
	private String slowWarningSpanSpeed(Speed spd, Distance dist) {
		Speed s = slowWarningSpeed(spd, dist);
		if (s != null) {
			int si = nearest5Mph(s);
			if (si > 0)
				return String.valueOf(si);
		}
		return null;
	}

	/** Calculate the slow warning speed.
	 * @param as Speed to activate slow warning.
	 * @param bd Distance limit to backup.
	 * @return Speed at backup or null for no backup. */
	private Speed slowWarningSpeed(Speed as, Distance bd) {
		BackupFinder bf = slowWarningBackup(as, bd);
		return (bf != null) ? bf.speed() : null;
	}

	/** Create a slow warning backup finder.
	 * @param as Speed to activate slow warning.
	 * @param bd Distance limit to backup.
	 * @return Backup finder or null for no backup. */
	private BackupFinder slowWarningBackup(Speed as, Distance bd) {
		Corridor cor = lookupCorridor();
		return (cor != null) ? slowWarningBackup(cor, as, bd) : null;
	}

	/** Lookup the corridor for the given location.
	 * @return Freeway corridor. */
	private Corridor lookupCorridor() {
		return BaseObjectImpl.corridors.getCorridor(loc);
	}

	/** Create a slow warning backup finder.
	 * @param cor Freeway corridor.
	 * @param as Speed to activate slow warning.
	 * @param bd Distance limit to backup.
	 * @return Backup finder or null for no backup. */
	private BackupFinder slowWarningBackup(Corridor cor, Speed as,
		Distance bd)
	{
		Float m = cor.calculateMilePoint(loc);
		if (isLogging())
			log("mp " + m);
		if (m != null) {
			BackupFinder bf = new BackupFinder(as, bd, m);
			cor.findStation(bf);
			if (isLogging())
				bf.debug(SLOW_LOG);
			return bf;
		} else
			return null;
	}
}
