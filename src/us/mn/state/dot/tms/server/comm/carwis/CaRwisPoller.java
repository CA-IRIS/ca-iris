/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.carwis;

import java.util.HashMap;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.WeatherSensorImpl;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.WeatherPoller;

/**
 * CA RWIS poller, which periodically reads an XML feed via HTTP.
 * Based on Doug Lau's SsiPoller.
 *
 * @author Travis Swanston
 */
public class CaRwisPoller extends MessagePoller implements WeatherPoller {

	/** Debug log */
	static public final DebugLog LOG = new DebugLog("carwis");

	/** Log a message. */
	static public void log(String msg) {
		LOG.log(msg);
	}

	/** Mapping of site_id to most recent RWIS records */
	private final HashMap<String, RwisRec> records =
		new HashMap<String, RwisRec>();

	/** Constructor. */
	public CaRwisPoller(String n, Messenger m) {
		super(n, m);
	}

	/** Consider all drop addresses to be valid. */
	@Override
	public boolean isAddressValid(int drop) {
		return true;
	}

	/** Send a device request. */
	@Override
	public void sendRequest(WeatherSensorImpl ws, DeviceRequest r) {
		switch(r) {
		case QUERY_STATUS:
			addOperation(new OpRead(ws, records));
			break;
		default:
			// NOP
			break;
		}
	}

	/** Send settings to a weather sensor. */
	@Override
	public void sendSettings(WeatherSensorImpl ws) {
		// NOP
	}

}
