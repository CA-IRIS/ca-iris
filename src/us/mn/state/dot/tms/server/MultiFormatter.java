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

import us.mn.state.dot.tms.*;
import us.mn.state.dot.tms.units.Interval;
import us.mn.state.dot.tms.utils.MultiString;

/**
 * MULTI string formatter for custom tags.
 *
 * @author Douglas Lau
 */
public class MultiFormatter {

	/** DMS for message formatting */
	private final DMSImpl dms;

	/** Travel time estimator */
	private final TravelTimeEstimator travel_est;

	/** Speed advisory calculator */
	private final SpeedAdvisoryCalculator advisory;

	/** Slow warning formatter */
	private final SlowWarningFormatter slow_warn;

	/** Tolling formatter */
	private final TollingFormatter toll_form;

	/** Create a new MULTI formatter */
	public MultiFormatter(DMSImpl d, TollingFormatter tf) {
		dms = d;
		GeoLoc g = d.getGeoLoc();
		travel_est = new TravelTimeEstimator(dms.getName(), g);
		advisory = new SpeedAdvisoryCalculator(g);
		slow_warn = new SlowWarningFormatter(g);
		toll_form = tf;
	}

	/** Create a multi string for a DMS action */
	public String createMulti(DmsAction da) {
		QuickMessage qm = da.getQuickMessage();
		if (qm != null) {
			FeedCallback fc = new FeedCallback(dms,
				da.getSignGroup());
			new MultiString(qm.getMulti()).parse(fc);
			String m = fc.toString();
			MultiString multi = new MultiString(m);
			if (!multi.isBlank())
				return createMulti(m);
		}
		return null;
	}

	/** Create a MULTI string for a message.
	 * @param qm Quick message MULTI string to parse.
	 * @return MULTI string with travel, vsa, and slow warnings resolved;
	 *         additionally a page-on-time field will be added, if needed.
	 */
	private String createMulti(String qm) {
		String rv = travel_est.replaceTravelTimes(qm);
		rv = (rv != null) ? advisory.replaceSpeedAdvisory(rv) : null;
		rv = (rv != null) ? slow_warn.replaceSlowWarning(rv) : null;
		rv = (rv != null) ? toll_form.replaceTolling(rv) : null;
		MultiString ms = new MultiString(rv);
		short protocol = dms.getController().getCommLink().getProtocol();
		short dmsxml = (short) CommProtocol.DMSXML.ordinal();
		if (ms.pageOnInterval().seconds() == 0 && protocol != dmsxml)
			rv = (rv != null) ? QuickMessageHelper.prependPageOnTime(ms) : null;
		return rv;
	}

	/** Check if DMS action is tolling */
	public boolean isTolling(DmsAction da) {
		QuickMessage qm = da.getQuickMessage();
		if (qm != null)
			return new MultiString(qm.getMulti()).isTolling();
		else
			return false;
	}

	/** Clear the current routes */
	public void clear() {
		travel_est.clear();
	}
}
