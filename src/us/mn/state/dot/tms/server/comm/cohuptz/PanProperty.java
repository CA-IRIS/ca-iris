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
 * A property to pan a camera
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class PanProperty extends CohuPTZProperty {

	/** Requested vector [-1..1] */
	private final float value;

	/** Create the property */
	public PanProperty(float v) {
		value = v;
	}

	/** Encode a STORE request */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		byte[] cmd;
		if (Math.abs(value) < PTZ_THRESH)
			cmd = new byte[]{ 'P', 'S' };

		else if (value < 0)
			cmd = new byte[]{ 'l', getPanTiltSpeedByte(value) };

		else /* if (value > 0) */
			cmd = new byte[]{ 'r', getPanTiltSpeedByte(value) };

		writePayload(os, c.getDrop(), cmd);
	}
}
