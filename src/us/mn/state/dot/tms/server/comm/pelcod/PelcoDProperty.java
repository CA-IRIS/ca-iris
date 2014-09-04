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
package us.mn.state.dot.tms.server.comm.pelcod;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

/**
 * Pelco D Property
 *
 * @author Douglas Lau
 */
abstract public class PelcoDProperty extends ControllerProperty {

	/** Get the command bits (in the 2 LSBs) */
	abstract protected int getCommand();

	/** Get command parameter 1 */
	abstract protected int getParam1();

	/** Get command parameter 2 */
	abstract protected int getParam2();

	/** Create Pelco D packet */
	private byte[] createPacket(int drop) {
		int cmd = getCommand();
		byte[] pkt = new byte[7];
		pkt[0] = (byte)0xFF;
		pkt[1] = (byte)drop;
		pkt[2] = (byte)(((cmd & 0xff00) >>> 8) & 0xff);
		pkt[3] = (byte)(((cmd & 0x00ff) >>> 0) & 0xff);
		pkt[4] = (byte)getParam2();
		pkt[5] = (byte)getParam1();
		pkt[6] = calculateChecksum(pkt);
		return pkt;
	}

	/** Calculate the checksum */
	private byte calculateChecksum(byte[] pkt) {
		byte checksum = 0;
		for(int i = 1; i < 6; i++)
			checksum += pkt[i];
		return checksum;
	}

	/** Encode a STORE request */
	@Override
	public void encodeStore(OutputStream os, int drop) throws IOException {
		os.write(createPacket(drop));
	}

	/** Decode a STORE response */
	@Override
	public void decodeStore(InputStream is, int drop) {
		// do not expect any response
	}
}
