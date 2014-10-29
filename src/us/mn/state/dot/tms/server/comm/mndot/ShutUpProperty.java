/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.mndot;

import java.io.IOException;
import us.mn.state.dot.tms.server.comm.ProtocolException;

/**
 * Shut Up Property
 *
 * @author Douglas Lau
 */
public class ShutUpProperty extends MndotProperty {

	/** Get the expected number of octets in response to a GET request */
	protected int expectedGetOctets() {
		return 0;
	}

	/** Format a basic "SET" request */
	protected byte[] formatPayloadSet(Message m) throws IOException {
		byte[] req = new byte[3];
		req[OFF_DROP_CAT] = m.dropCat(SHUT_UP);
		req[OFF_LENGTH] = 0;
		req[req.length - 1] = checksum(req);
		return req;
	}

	/** Get the expected number of octets in response to a SET request */
	protected int expectedSetOctets() {
		return 0;
	}
}
