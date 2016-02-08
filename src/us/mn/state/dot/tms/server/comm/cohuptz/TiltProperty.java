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
import java.util.ArrayList;

import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.ControllerImpl;

/**
 * A property to tilt a camera
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class TiltProperty extends CohuPTZProperty {

	/** Requested vector [-1..1] */
	private final float value;

	/** Create the property */
	public TiltProperty(float v) {
		value = v;
	}

	/** Encode a STORE request */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException {

		byte[] cmd = processPTZInfo(Command.TILT, value, new byte[] {});

		writePayload(os, c.getDrop(), cmd);
	}
}
