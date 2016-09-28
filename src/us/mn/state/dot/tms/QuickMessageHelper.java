/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
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

import java.util.Iterator;
import us.mn.state.dot.tms.utils.MultiString;

/**
 * Helper class for quick messages.
 *
 * @author Douglas Lau
 * @author Jacob Barde
 */
public class QuickMessageHelper extends BaseHelper {

	/** Don't allow instances to be created */
	private QuickMessageHelper() {
		assert false;
	}

	/** Lookup the quick message with the specified name */
	static public QuickMessage lookup(String name) {
		return (QuickMessage) namespace.lookupObject(
			QuickMessage.SONAR_TYPE, name);
	}

	/** Get a quick message iterator */
	static public Iterator<QuickMessage> iterator() {
		return new IteratorWrapper<QuickMessage>(namespace.iterator(
			QuickMessage.SONAR_TYPE));
	}

	/** Find a quick message with the specified MULTI string.
	 * @param ms MULTI string.
	 * @return A matching quick message or null if no match is found. */
	static public QuickMessage find(String ms) {
		if (ms != null) {
			MultiString multi = new MultiString(ms);
			Iterator<QuickMessage> it = iterator();
			while (it.hasNext()) {
				QuickMessage qm = it.next();
				if (multi.equals(qm.getMulti()))
					return qm;
			}
		}
		return null;
	}

	/**
	 * Find a quick message with the specified sign group and MULTI string.
	 * @param sg sign group
	 * @param ms MULTI string.
	 *
	 * @return A matching quick message or null if no match is found.
	 */
	static public QuickMessage find(SignGroup sg, String ms) {
		if (sg != null && ms != null) {
			MultiString multi = new MultiString(ms);
			Iterator<QuickMessage> it = iterator();
			while (it.hasNext()) {
				QuickMessage qm = it.next();
				if (sg.equals(qm.getSignGroup()) && multi.equals(qm.getMulti()))
					return qm;
			}
		}
		return null;
	}

	/**
	 * Is the specified quick message deployed? Equivalence is used
	 * to determine if the specified and deployed message are equal.
	 * @param dms    DMS to check, may be null.
	 * @param qmname Name of quick message, may be null.
	 * @return True if the specifed quick message is equivalent to the
	 *         deployed message on the specified DMS.  */
	static public boolean isQuickMsgDeployed(DMS dms, String qmname) {
		if (dms == null || qmname == null)
			return false;
		QuickMessage newqm = lookup(qmname);
		if (newqm == null)
			return false;
		String newms = newqm.getMulti();
		if (newms == null)
			return false;
		SignMessage dsm = dms.getMessageCurrent();
		if (dsm == null)
			return false;
		return new MultiString(dsm.getMulti()).equals(newms);
	}

	/** determine if quickmessage is a raw/temporary */
	static public boolean isRawQuickMessage(final QuickMessage q) {
		return (q != null && q.getName() != null && q.getName().startsWith(QuickMessage.RAW_PREFIX));
	}
}
