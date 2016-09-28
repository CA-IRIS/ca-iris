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

import us.mn.state.dot.sonar.SonarObject;

/**
 * A quick message is a sign message which consists of a MULTI string.
 *
 * @author Michael Darter
 * @author Douglas Lau
 * @author Jacob Barde
 */
public interface QuickMessage extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "quick_message";

	/** prefix for temporary quick messages */
	String RAW_PREFIX = "~raw";

	/** Get the sign group associated with the quick message.
	 * @return Sign group for quick message; null for no group. */
	SignGroup getSignGroup();

	/** Set the sign group associated with the quick message.
	 * @param sg Sign group to associate; null for no group. */
	void setSignGroup(SignGroup sg);

	/** Get the message MULTI string.
	 * @return Message text in MULTI markup.
	 * @see us.mn.state.dot.tms.utils.MultiString */
	String getMulti();

	/** Set the message MULTI string.
	 * @multi Message text in MULTI markup.
	 * @see us.mn.state.dot.tms.utils.MultiString */
	void setMulti(String multi);
}
