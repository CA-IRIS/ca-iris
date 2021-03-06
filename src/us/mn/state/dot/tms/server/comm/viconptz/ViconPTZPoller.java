/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2014  Minnesota Department of Transportation
 * Copyright (C) 2014  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.viconptz;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.TransientPoller;

/**
 * ViconPoller is a java implementation of the Vicon camera control
 * communication protocol
 *
 * @author Douglas Lau
 * @author Travis Swanston
 */
public class ViconPTZPoller extends TransientPoller<ViconPTZProperty>
	implements CameraPoller
{
	/** Vicon PTZ debug log */
	static private final DebugLog VICON_LOG = new DebugLog("viconptz");

	/** Highest allowed address for Vicon protocol */
	static public final int ADDRESS_MAX = 254;

	/** Create a new Vicon poller */
	public ViconPTZPoller(String n, Messenger m) {
		super(n, m);
	}

	/** Check if a drop address is valid */
	public boolean isAddressValid(int drop) {
		return drop >= 1 && drop <= ADDRESS_MAX;
	}

	/** Send a PTZ camera move command */
	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		addOperation(new OpMoveCamera(c, p, t, z));
	}

	/** Send a store camera preset command */
	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		addOperation(new OpPreset(c, true, preset));
	}

	/** Send a recall camera preset command */
	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		addOperation(new OpPreset(c, false, preset));
	}

	/** Send a device request
	 * @param c The CameraImpl object.
	 * @param r The desired DeviceRequest. */
	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {
		addOperation(new OpDeviceRequest(c, r));
	}

	/** Get the protocol debug log */
	@Override
	protected DebugLog protocolLog() {
		return VICON_LOG;
	}
}
