/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
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

/**
 * DMS message priority levels. This enum is designed so that the ordinal
 * values can be used for NTCIP activation and run-time priority. NTCIP
 * priority values can range from 1 to 255, with higher numbers indicating
 * higher priority. The enum is also ordered from low to high priority.
 *
 * @author Michael Darter
 * @author Douglas Lau
 */
public enum DMSMessagePriority {
	INVALID,	/* 0: invalid priority */
	BLANK,		/* 1: blank message run-time priority */
	PSA,		/* 2: public service announcement */
	TRAVEL_TIME,	/* 3: travel time priority */
	SPEED_LIMIT,	/* 4: variable speed limit priority */
	SCHEDULED,	/* 5: scheduled priority (planned events) */
	OTHER_SYSTEM,	/* 6: other system priority */
	ALERT,		/* 7: alert priority (AMBER alerts, etc.) */
	OPERATOR,	/* 8: operator priority */
	INCIDENT_LOW,	/* 9: low-priority incident */
	INCIDENT_MED,	/* 10: medium-priority incident */
	INCIDENT_HIGH,	/* 11: high-priority incident */
	AWS,		/* 12: automated warning system */
	OVERRIDE;	/* 13: override priority */

	/** Get a DMSMessagePriority from an ordinal value */
	public static DMSMessagePriority fromOrdinal(int o) {
		for(DMSMessagePriority e: values()) {
			if(e.ordinal() == o)
				return e;
		}
		return INVALID;
	}

	/** Test if a run-time priority was "scheduled" */
	static public boolean isScheduled(DMSMessagePriority p) {
		switch(p) {
		case INVALID:
		case BLANK:
		case ALERT:
		case OPERATOR:
		case OVERRIDE:
			return false;
		default:
			return true;
		}
	}
}
