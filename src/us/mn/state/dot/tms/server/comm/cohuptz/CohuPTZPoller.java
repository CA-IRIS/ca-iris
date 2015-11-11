/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.cohuptz;

import java.io.IOException;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.utils.NumericAlphaComparator;

/**
 * Poller for the Cohu PTZ protocol
 *
 * @author Travis Swanston
 * @author Jacob Barde
 * @author Dan Rossiter
 */
public class CohuPTZPoller extends MessagePoller implements CameraPoller {

	/** Debug log */
	static protected final DebugLog DEBUG_LOG = new DebugLog("cohuptz");

	/** Log a message to the debug log */
	static public void log(String msg) {
		DEBUG_LOG.log(msg);
	}

	/** Cohu camera address range constants */
	static public final int ADDR_MIN = 1;
	static public final int ADDR_MAX = 223;

	/** Timestamp of most recent transaction with the device. */
	protected long lastCmdTime = 0;

	/** Current pan value */
	protected float curPan  = 0F;

	/** Current tilt value */
	protected float curTilt = 0F;

	/** Current zoom value */
	protected float curZoom = 0F;

	/**
	 * Create a new Cohu PTZ poller with auto connection mode.
	 * @param n CommLink name
	 * @param m the Messenger
	 */
	public CohuPTZPoller(String n, Messenger m) {
		super(n, m);
		log("CohuPTZPoller instantiated.");
		CommLink cl = CommLinkHelper.lookup(n);
		if (cl == null) {
			log("Failed to find CommLink.");
			return;
		}
		int to = cl.getTimeout();
		try {
			m.setTimeout(to);
			log("Set Messenger timeout to " + to + ".");
		}
		catch (IOException e) {
			log("Failed to set Messenger timeout.");
		}
	}

	/** Check drop address validity */
	@Override
	public boolean isAddressValid(int drop) {
		return ((drop >= ADDR_MIN) && (drop <= ADDR_MAX));
	}

	/** Send a "PTZ camera move" command */
	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {
		// compareFloats does a "proper" comparing of values
		boolean do_pan = NumericAlphaComparator.compareFloats(p, curPan, CohuPTZProperty.PTZ_THRESH) != 0;
		boolean do_tilt = NumericAlphaComparator.compareFloats(t, curTilt, CohuPTZProperty.PTZ_THRESH) != 0;
		boolean do_zoom = NumericAlphaComparator.compareFloats(z, curZoom, CohuPTZProperty.PTZ_THRESH) != 0;

		boolean stop_pan = NumericAlphaComparator.compareFloats(p, 0F, CohuPTZProperty.PTZ_THRESH) == 0;
		boolean stop_tilt = NumericAlphaComparator.compareFloats(t, 0F, CohuPTZProperty.PTZ_THRESH) == 0;
		boolean stop_zoom = NumericAlphaComparator.compareFloats(z, 0F, CohuPTZProperty.PTZ_THRESH) == 0;

		boolean full_stop = stop_pan && stop_tilt && stop_zoom;

		// if either panning or tilting, the last value sent
		// must be initialized for the other operation.
		Float pan = (do_tilt) ? curPan : null;
		Float tilt = (do_pan) ? curTilt : null;
		Float zoom = null;

		log(new StringBuilder().append("curPan=").append(curPan)
		                       .append(" arg p=").append(p)
		                       .append(" prep pan=")
		                       .append(pan).append(" do_pan=")
		                       .append(do_pan)
		                       .toString());
		log(new StringBuilder().append("curTilt=").append(curTilt)
		                       .append(" arg t=").append(t)
		                       .append(" prep tilt=")
		                       .append(tilt).append(" do_tilt=")
		                       .append(do_tilt)
		                       .toString());
		log(new StringBuilder().append("curZoom=").append(curZoom)
		                       .append(" arg z=").append(z)
		                       .append(" prep zoom=")
		                       .append(zoom).append(" do_zoom=")
		                       .append(do_zoom)
		                       .toString());

		if (do_pan) {
			curPan = p;
			pan = p;
		}
		if (do_tilt) {
			curTilt = t;
			tilt = t;
		}
		if (do_zoom) {
			curZoom = z;
			zoom = z;
		}

		if(full_stop) {
			log("Full Stop");
			curPan = 0F;
			pan = 0F;
			curTilt = 0F;
			tilt = 0F;
			curZoom = 0F;
			zoom = 0F;
		}

		log(new StringBuilder().append("sending pan=").append(pan)
		                       .toString());
		log(new StringBuilder().append("sending tilt=").append(tilt)
		                       .toString());
		log(new StringBuilder().append("sending zoom=").append(zoom)
		                       .toString());

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

	/**
	 * Send a device request
	 *
	 * @param c The CameraImpl object.
	 * @param r The desired DeviceRequest.
	 */
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
		case CAMERA_PTZ_FULL_STOP:
			addOperation(new OpPTZCamera(c, this, 0F, 0F, 0F));
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
