/*
 * IRIS -- Intelligent Roadway Information System
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
package us.mn.state.dot.tms.server.comm.cohuptz;

import java.io.EOFException;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;

/**
 * Poller for the Cohu PTZ protocol
 *
 * @author Travis Swanston
 */
public class CohuPTZPoller extends MessagePoller implements CameraPoller {

	/** Cohu camera address range constants */
	static public final int ADDR_MIN = 1;
	static public final int ADDR_MAX = 223;

	/** Timestamp of most recent transaction with the device. */
	protected long lastCmdTime = 0;

	/** Current pan value */
	protected float curPan  = 0.0F;

	/** Current tilt value */
	protected float curTilt = 0.0F;

	/** Current zoom value */
	protected float curZoom = 0.0F;

	/** Create a new Cohu PTZ poller */
	public CohuPTZPoller(String n, Messenger m) {
		super(n, m);
	}

	/** Create a new message for the specified drop address */
	@Override
	public CommMessage createMessage(ControllerImpl c)
		throws EOFException
	{
		return new CohuPTZMessage(messenger.getOutputStream(c),
			c.getDrop());
	}

	/** Check drop address validity */
	@Override
	public boolean isAddressValid(int drop) {
		return ((drop >= ADDR_MIN) && (drop <= ADDR_MAX));
	}

	/** Send a "PTZ camera move" command */
	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		Float pan  = null;
		Float tilt = null;
		Float zoom = null;

		if (p != curPan) {
			pan = Float.valueOf(p);
			curPan = p;
		}
		if (t != curTilt) {
			tilt = Float.valueOf(t);
			curTilt = t;
		}
		if (z != curZoom) {
			zoom = Float.valueOf(z);
			curZoom = z;
		}

		addOperation(new OpPTZCamera(c, this, pan, tilt, zoom));
	}

	/** Send a "store camera preset" command */
	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		addOperation(new OpStorePreset(c, this, preset));
	}

	/** Send a "recall camera preset" command */
	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		addOperation(new OpRecallPreset(c, this, preset));
	}

	/**
	 * Get the timestamp of the last command issued to the device.
	 * This value, stored in CohuPTZPoller, is updated by OpCohuPTZ
	 * operations via the CohuPTZPoller.setLastCmdTime method.
	 * @return The timestamp of the last command issued to the device,
	 *         or 0 if no commands have yet been issued.
	 */
	protected long getLastCmdTime() {
		return lastCmdTime;
	}

	/**
	 * Set the timestamp of the last command issued to the device.
	 * This value, stored in CohuPTZPoller, is updated by OpCohuPTZ
	 * operations.
	 * @param time The desired timestamp value to set.
	 */
	protected void setLastCmdTime(long time) {
		lastCmdTime = time;
	}

	/** Send a device request
	 * @param c The CameraImpl object.
	 * @param r The desired DeviceRequest. */
	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {
		switch (r) {
		case CAMERA_FOCUS_STOP:
		case CAMERA_FOCUS_NEAR:
		case CAMERA_FOCUS_FAR:
			addOperation(new OpMoveFocus(c, this, r));
			break;
		case CAMERA_FOCUS_MANUAL:
		case CAMERA_FOCUS_AUTO:
			addOperation(new OpSetAFMode(c, this, r));
			break;
		case CAMERA_IRIS_STOP:
		case CAMERA_IRIS_CLOSE:
		case CAMERA_IRIS_OPEN:
			addOperation(new OpMoveIris(c, this, r));
			break;
		case CAMERA_IRIS_MANUAL:
		case CAMERA_IRIS_AUTO:
			addOperation(new OpSetAIMode(c, this, r));
			break;
		case CAMERA_WIPER_ONESHOT:
			// FIXME: not yet implemented
			break;
		case RESET_DEVICE:
			addOperation(new OpResetCamera(c, this));
			break;
		default:
			break;
		}
	}
}
