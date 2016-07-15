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
package us.mn.state.dot.tms.server.comm.axisptz;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.CameraPoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.TransientPoller;
import us.mn.state.dot.tms.utils.HexString;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Axis VAPIX PTZ poller.
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class AxisPTZPoller extends TransientPoller<AxisPTZProperty>
	implements CameraPoller {

	/** Debug log */
	static private final DebugLog DEBUG_LOG = new DebugLog("axisptz");

	/** Log a message to the debug log */
	static public void log(String msg) {
		DEBUG_LOG.log(msg);
	}

	private final CommLink comm_link;
	private String host = null;
	private Integer port = null;

	public long getLastCmdTime() {
		return lastCmdTime;
	}

	public void setLastCmdTime(long lastCmdTime) {
		this.lastCmdTime = lastCmdTime;
	}

	/** Timestamp of most recent transaction with the device. */
	protected long lastCmdTime = 0;

	/** Current unmapped pan value */
	private float cur_p = 0.0F;

	/** Current unmapped tilt value */
	private float cur_t = 0.0F;

	/** Current unmapped zoom value */
	private float cur_z = 0.0F;

	/**
	 * Create a new Axis PTZ poller.
	 *
	 * @param n CommLink name
	 * @param m the Messenger
	 */
	public AxisPTZPoller(String n, Messenger m) {

		super(n, m);
		log("AxisPTZPoller instantiated.");

		comm_link = CommLinkHelper.lookup(n);
		if (comm_link == null) {
			log("Failed to find CommLink.");
			return;
		}

		int to = comm_link.getTimeout();
		try {
			m.setTimeout(to);
			log("Set Messenger timeout to " + to + ".");
		} catch (IOException e) {
			log("Failed to set Messenger timeout.");
		}

		parseUri();
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	/** Check if a drop address is valid */
	@Override
	public boolean isAddressValid(int drop) {
		return (drop > 1);
	}

	/** Get the protocol debug log */
	@Override
	protected DebugLog protocolLog() {
		return DEBUG_LOG;
	}

	/** Map a [-1.0,1.0] float value to an [-100,100] integer value. */
	static protected int mapPTZ(float v) {

		int mapped = Math.round(v * 100.0F);
		// sanity:
		if (mapped < -100)
			mapped = -100;
		if (mapped > 100)
			mapped = 100;

		return mapped;
	}

	/** Send a PTZ camera move command */
	@Override
	public void sendPTZ(CameraImpl c, float p, float t, float z) {

		if ((p != cur_p) || (t != cur_t)) {
			AxisPTZProperty prop = new PanTiltProperty(mapPTZ(p),
				mapPTZ(t));
			addOperation(new OpAxisPTZ(c, prop, this));
			cur_p = p;
			cur_t = t;
		}

		if (z != cur_z) {
			AxisPTZProperty prop = new ZoomProperty(mapPTZ(z));
			addOperation(new OpAxisPTZ(c, prop, this));
			cur_z = z;
		}
	}

	/** Send a store camera preset command */
	@Override
	public void sendStorePreset(CameraImpl c, int preset) {
		AxisPTZProperty prop = new PresetProperty(true, preset);
		addOperation(new OpAxisPTZ(c, prop, this));
	}

	/** Send a recall camera preset command */
	@Override
	public void sendRecallPreset(CameraImpl c, int preset) {
		AxisPTZProperty prop = new PresetProperty(false, preset);
		addOperation(new OpAxisPTZ(c, prop, this));
	}

	/**
	 * Send a device request
	 *
	 * @param c The CameraImpl object.
	 * @param r The desired DeviceRequest.
	 */
	@Override
	public void sendRequest(CameraImpl c, DeviceRequest r) {

		AxisPTZProperty prop = null;

		if (r == DeviceRequest.CAMERA_FOCUS_NEAR)
			prop = new FocusProperty(mapPTZ(-1.0F));
		else if (r == DeviceRequest.CAMERA_FOCUS_FAR)
			prop = new FocusProperty(mapPTZ(1.0F));
		else if (r == DeviceRequest.CAMERA_FOCUS_STOP)
			prop = new FocusProperty(mapPTZ(0.0F));
		else if (r == DeviceRequest.CAMERA_IRIS_CLOSE)
			prop = new IrisProperty(mapPTZ(-1.0F));
		else if (r == DeviceRequest.CAMERA_IRIS_OPEN)
			prop = new IrisProperty(mapPTZ(1.0F));
		else if (r == DeviceRequest.CAMERA_IRIS_STOP)
			prop = new IrisProperty(mapPTZ(0.0F));
		else if (r == DeviceRequest.CAMERA_FOCUS_MANUAL)
			prop = new NullaryProperty(r);
		else if (r == DeviceRequest.CAMERA_FOCUS_AUTO)
			prop = new NullaryProperty(r);
		else if (r == DeviceRequest.CAMERA_IRIS_MANUAL)
			prop = new NullaryProperty(r);
		else if (r == DeviceRequest.CAMERA_IRIS_AUTO)
			prop = new NullaryProperty(r);
		else if (r == DeviceRequest.RESET_DEVICE)
			prop = getSerialWriteProperty(r);
		else if (r == DeviceRequest.CAMERA_WIPER_ONESHOT)
			prop = getSerialWriteProperty(r);

		if (prop != null)
			addOperation(new OpAxisPTZ(c, prop, this));
	}

	/**
	 * Get a SerialWriteProperty, given a DeviceRequest.
	 *
	 * @param r Device request.
	 * @return the SerialWriteProperty
	 */
	static private AxisPTZProperty getSerialWriteProperty(DeviceRequest r) {

		int port = SystemAttrEnum.CAMERA_PTZ_AXIS_COMPORT.getInt();
		String byteString = null;

		switch (r) {
		case RESET_DEVICE:
			byteString = SystemAttrEnum.CAMERA_PTZ_AXIS_RESET
				.getString();
			break;
		case CAMERA_WIPER_ONESHOT:
			byteString = SystemAttrEnum.CAMERA_PTZ_AXIS_WIPE
				.getString();
			break;
		default:
			byteString = "";
			break;
		}

		byte[] data = null;
		try {
			data = HexString.parse(byteString);
		} catch (Exception e) {
			data = new byte[0];
		}

		return new SerialWriteProperty(port, data);
	}

	private void parseUri() {

		if (comm_link == null)
			return;

		String sUri = comm_link.getUri();
		if (sUri == null) {
			log("unable to determine URI");
			return;
		}

		URI uri = null;
		try {
			uri = new URI(sUri);
		} catch (URISyntaxException e) {
			// NOP
		}

		if (uri == null) {
			try {
				uri = new URI("//" + sUri);
			} catch (URISyntaxException e) {
				log("unable to parse URI");
				return;
			}
		}

		String h = uri.getHost();
		int p = uri.getPort();
		if ((h == null) || (p < 1)) {
			log("unable to parse URI");
			return;
		}

		host = h;
		port = p;
	}

}

