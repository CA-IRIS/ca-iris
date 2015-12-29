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

package us.mn.state.dot.tms.server.comm.cohuptz;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

/**
 * Cohu PTZ Property
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
abstract public class CohuPTZProperty extends ControllerProperty {

	/**
	 * Absolute value of PTZ movement threshold.
	 * PTZ vectors below this value will be considered as stop commands.
	 */
	static public final float PTZ_THRESH = 0.001F;

	/**
	 * Calculate the XOR-based checksum of the given Cohu message.
	 *
	 * @param message The message for which to calculate the checksum.
	 * @param first The index of the first byte in the checksum range.
	 * @param last The index of the last byte in the checksum range.
	 * @return A Byte representing the checksum for the message,
	 *         or null on error.
	 */
	private Byte calculateChecksum(byte[] message, int first, int last) {
		if (message.length < 1) return null;
		if (first < 0) return null;
		if (last < 0) return null;
		if (first > last) return null;
		if (last >= message.length) return null;

		byte runningXor = 0;
		for(int i = first; i <= last; ++i)
			runningXor ^= message[i];

		return (byte) (0x80 + ((runningXor & (byte)0x0f)));
	}

	/**
	 * Calculate the "preset byte" that corresponds to the given preset
	 * number.
	 * Presets [1..47] correspond to preset bytes [0x10..0x3e], and
	 * presets [48..64] correspond to preset bytes [0x60..0x70].
	 *
	 * @param presetNum The preset number, [1..64].
	 * @return The preset byte corresponding to the given preset number,
	 *         or null if the given preset number is invalid.
	 */
	protected Byte getPresetByte(int presetNum) {
		if (presetNum < 1) return null;
		if (presetNum > 64) return null;

		byte presetByte;

		if (presetNum <= 47) {
			presetByte = (byte) (0x10 + (presetNum-1));
		}
		else {
			presetByte = (byte) (0x60 + (presetNum-1));
		}
		return presetByte;
	}

	/**
	 * Calculate the pan/tilt "speed byte" that corresponds to the given
	 * speed value [-1..1].
	 *
	 * @param speed The speed value [-1..1].  Values outside this range
	 *              will be remapped.
	 * @return The pan/tilt speed byte [0x31..0x3f] corresponding to the
	 *         given speed value.  Note that 0x00 will not be returned, as
	 *         it seems to be a special, undocumented case (as of v6.8 of
	 *         the Cohu PTZ protocol specs) that appears to correspond to
	 *         some sort of "default" speed mode.
	 */
	protected byte getPanTiltSpeedByte(float speed) {
		int range = (0x3f - 0x31) + 1;		// excludes 0x00
		int scale = range - 1;

		speed = Math.abs(speed);
		float mapped = (speed * scale);
		int mapInt = Math.round(mapped);

		// sanity check for floating point gotchas
		if (mapInt > scale) mapInt = scale;

		return (byte) (0x31 + mapInt);
	}

	/** Writes given payload surrounded by proper header and checksum. */
	protected void writePayload(OutputStream os, short drop, byte[] payload)
		throws IOException
	{
		byte[] msg = new byte[3 + payload.length];
		msg[0] = (byte)0xf8;
		msg[1] = (byte)drop;
		System.arraycopy(payload, 0, msg, 2, payload.length);

		Byte checksum = calculateChecksum(msg, 1, payload.length + 1);
		if (checksum == null)
			return;

		msg[msg.length - 1] = checksum;
		os.write(msg);
	}

	/** Encode a STORE request */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException
	{
	}

	/** Decode a STORE response */
	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException
	{
		try {
			// NOTE: force reading of the ACK/NAK before continuing. This ensures that commands are not lost
			// due to overloading the camera with commands on top of each other.
			// noinspection ResultOfMethodCallIgnored
			is.read();
			c.setErrorStatus("");
		} catch ( IOException s ) {
			if ( s.getMessage() == null )
				c.setErrorStatus("Unknown IOException Error");
			else if ( s.getMessage().equals( "Connection attempt timed out" ) )
				c.setErrorStatus("Exceeded Read Timeout");
			else if ( s.getMessage().equals( "Connection refused: connect" ) )
				c.setErrorStatus("Field Site Blocked Connection Attempt");
			else if ( s.getMessage().equals( "No route to host: connect" ) )
				c.setErrorStatus("Network Problem With This Computer");
			else
				c.setErrorStatus(s.getMessage());
		}
	}

}
