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
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.ControllerImpl;

/**
 * This class creates a Cohu PTZ request to initiate an iris movement.
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class MoveIrisProperty extends CohuPTZProperty {

	protected final DeviceRequest devReq;

	/** Create the property. */
	public MoveIrisProperty(DeviceRequest dr) {
		devReq = dr;
	}

	/** Encode a STORE request */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
		byte[] payload = null;
		switch (devReq) {
			case CAMERA_IRIS_STOP:
				payload = new byte[]{ 'I', 'S' };
				break;
			case CAMERA_IRIS_CLOSE:
				payload = new byte[]{ 'I', 'C' };
				break;
			case CAMERA_IRIS_OPEN:
				payload = new byte[]{ 'I', 'O' };
				break;
		}

		if (payload != null) {
			writePayload(os, c.getDrop(), payload);
		}
	}

}
