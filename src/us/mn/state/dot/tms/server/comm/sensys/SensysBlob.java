/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012  Iteris Inc.
 * Copyright (C) 2012-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.sensys;

import us.mn.state.dot.tms.utils.ByteBlob;

/**
 * This is an ordered list of bytes received from a Sensys access point.
 * Methods interpret this data for parsing and constructing Sensys records.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class SensysBlob extends ByteBlob {

	/** Record terminator */
	private final static int RECORD_TERMINATOR = 0x0a;

	/** Constructor */
	public SensysBlob() {
		super();
	}

	/** Constructor */
	public SensysBlob(ByteBlob bb) {
		super(bb);
	}

	/**
	 * Is the message read complete?
	 * This method is called during the message read process to determine,
	 * on a byte-by-byte basis, if the entire message has been received
	 * from the field controller.
	 * @param b Last byte read
	 * @return True when the complete message was read, else false
	 */
	static protected boolean readComplete(int b) {
		return b == RECORD_TERMINATOR;
	}

}
