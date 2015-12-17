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
import us.mn.state.dot.tms.server.ControllerImpl;

/**
 * A property to zoom a camera
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class ZoomProperty extends CohuPTZProperty {

	/** Create the property */
	public ZoomProperty(float v) {
		super(v);
	}

	/** Encode a STORE request */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		byte[] cmd;
		if (Math.abs(value) < PTZ_THRESH)
			cmd = new byte[]{ 'Z', 'S' };

		else if (value < 0)
			cmd = new byte[]{ 'c', 'z', getZoomSpeedByte(value) };

		else /* if (value > 0) */
			cmd = new byte[]{ 'c', 'z', getZoomSpeedByte(value) };

		writePayload(os, c.getDrop(), cmd);
	}

	/**
	 * Calculate the zoom "speed byte" that corresponds to the given
	 * speed value [-1..1].
	 *
	 * @param speed The speed value [-1..1].  Values outside this range
	 *              will be remapped.
	 * @return The zoom speed byte [0x30..0x32] corresponding to the
	 *         given speed value.
	 */
	private byte getZoomSpeedByte(float speed) {
		int range = (0x32 - 0x30) + 1;
		int scale = range - 1;

		speed = Math.abs(speed);
		float mapped = (speed * scale);
		int mapInt = Math.round(mapped);

		// sanity check for floating point gotchas
		if (mapInt > scale) mapInt = scale;

		return (byte) (0x30 + mapInt);
	}
}
