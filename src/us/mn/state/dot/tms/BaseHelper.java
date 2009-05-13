/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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

import us.mn.state.dot.sonar.Namespace;

/**
 * Base helper class for client/server interfaces.
 *
 * @author Douglas Lau
 */
abstract public class BaseHelper {

	/** SONAR namespace. For server code this is set in TMSImpl and
	 * for client code this is set in SonarState. */
	static public Namespace namespace;

	/** Prevent object creation */
	protected BaseHelper() {
		assert false;
	}
}
