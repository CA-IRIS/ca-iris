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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A property to zoom a camera
 *
 * @author Travis Swanston
 */
public class ZoomProperty extends CohuPTZProperty {

	/** Requested vector [-1..1] */
	protected final float value;

	/** Create the property */
	public ZoomProperty(float v) {
		value = v;
	}

	/**
	 * Encode a STORE request
	 */
	@Override
	public void encodeStore(OutputStream os, int drop) throws IOException {

		byte[] cmd = new byte[0];

		if (Math.abs(value) < PTZ_THRESH) {
			cmd = new byte[2];
			cmd[0] = (byte)0x5a;
			cmd[1] = (byte)0x53;
		}
		else if (value < 0) {
			cmd = new byte[3];
			cmd[0] = (byte)0x63;
			cmd[1] = (byte)0x7a;
			cmd[2] = getZoomSpeedByte(value);
		}
		else if (value > 0) {
			cmd = new byte[3];
			cmd[0] = (byte)0x63;
			cmd[1] = (byte)0x5a;
			cmd[2] = getZoomSpeedByte(value);
		}

		byte[] msg = new byte[3 + cmd.length];
		msg[0] = (byte)0xf8;
		msg[1] = (byte)drop;
		int i=2;
		for (byte b : cmd) msg[i++] = b;
		msg[i] = calculateChecksum(msg, 1, cmd.length+1);

		os.write(msg);
	}

}
