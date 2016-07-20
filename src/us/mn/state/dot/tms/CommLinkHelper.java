/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
 * Copyright (C) 2009-2015  AHMCT, University of California
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

/**
 * Helper for comm links.
 *
 * @author Michael Darter
 * @author Douglas Lau
 * @author Travis Swanston
 */
public class CommLinkHelper extends BaseHelper {

	/** Disallow instantiation */
	private CommLinkHelper() {
		assert false;
	}

	/** Get a comm link iterator */
	static public Iterator<CommLink> iterator() {
		return new IteratorWrapper<CommLink>(namespace.iterator(
			CommLink.SONAR_TYPE));
	}

	/** Lookup the comm link with the specified name */
	static public CommLink lookup(String name) {
		return (CommLink)namespace.lookupObject(
			CommLink.SONAR_TYPE, name);
	}

}

