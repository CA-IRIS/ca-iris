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
import java.util.ArrayList;
import java.util.List;

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.utils.NumericAlphaComparator;

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

	/** Debug log */
	static protected final DebugLog DEBUG_LOG = new DebugLog("cohuptz");

	protected static boolean fixed_speed = SystemAttrEnum.CAMERA_PTZ_FIXED_SPEED.getBoolean();

	protected enum Command {
		PAN,
		TILT,
		ZOOM
	}

	private enum Command2 {
		UNKNOWN,
		FIXED_PAN,
		FIXED_TILT,
		FIXED_ZOOM,
		VAR_PAN,
		VAR_TILT,
		VAR_ZOOM
	}

	// fixed speed commands
	private static final byte fcPan = (byte) 'P';
	private static final byte fcTilt = (byte) 'T';
	private static final byte fcZoom = (byte) 'Z';

	// fixed speed arguments                     direction
	private static final byte faPP = (byte) 'R'; // positive
	private static final byte faPN = (byte) 'L'; // negative
	private static final byte faTP = (byte) 'U'; // positive
	private static final byte faTN = (byte) 'D'; // negative
	private static final byte faZP = (byte) 'I'; // positive
	private static final byte faZN = (byte) 'O'; // negative

	// variable speed commands                    direction
	private static final byte vcPP = (byte) 'r';  // positive
	private static final byte vcPN = (byte) 'l';  // negative
	private static final byte vcTP = (byte) 'u';  // positive
	private static final byte vcTN = (byte) 'd';  // negative
	private static final byte vcZB = (byte) 'c';  // both

	// variable speed zoom argument
	private static final byte vaZP = (byte) 'Z'; // positive
	private static final byte vaZN = (byte) 'z'; // negative

	// stop argument
	private static final byte faStop = (byte) 'S';

	/** Log a message to the debug log */
	static public void log(String msg) {
		DEBUG_LOG.log(msg);
	}

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

		if (presetNum <= 47)
			presetByte = (byte) (0x10 + (presetNum - 1));
		else
			presetByte = (byte) (0x60 + (presetNum - 1));

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
	protected static byte getPanTiltSpeedByte(float speed) {

		int range = (0x3f - 0x31) + 1; // excludes 0x00
		int scale = range - 1;

		speed = Math.abs(speed);
		float mapped = (speed * scale);
		int mapInt = Math.round(mapped);

		// sanity check for floating point gotchas
		if (mapInt > scale) mapInt = scale;

		return (byte) (0x31 + mapInt);
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
	protected static byte getZoomSpeedByte(float speed) {

		int range = (0x32 - 0x30) + 1;
		int scale = range - 1;

		speed = Math.abs(speed);
		float mapped = (speed * scale);
		int mapInt = Math.round(mapped);

		// sanity check for floating point gotchas
		if (mapInt > scale) mapInt = scale;

		return (byte) (0x30 + mapInt);
	}

	/** Writes given payload surrounded by proper header and checksum. */
	protected void writePayload(OutputStream os, short drop, byte[] payload)
		throws IOException {

		byte[] msg = new byte[3 + payload.length];
		msg[0] = (byte)0xf8;
		msg[1] = (byte)drop;
		System.arraycopy(payload, 0, msg, 2, payload.length);

		Byte checksum = calculateChecksum(msg, 1, payload.length + 1);
		if (checksum == null)
			return;

		msg[msg.length - 1] = checksum;
		os.write(msg);
		log("wrote command bytes (string): " + ba2hex(msg));
	}

	private static String ba2hex(byte[] msg) {
		StringBuilder rv = new StringBuilder();
		for(byte b : msg) {
			if ((int)b <= 20) {
				rv.append("x").append(String.format("%02X", b));
			} else {
				rv.append((char)b);
			}
			rv.append(" ");
		}
		return rv.toString();
	}
	/** Encode a STORE request */
	@Override
	public void encodeStore(ControllerImpl c, OutputStream os)
		throws IOException {
	}

	/** Decode a STORE response */
	@Override
	public void decodeStore(ControllerImpl c, InputStream is)
		throws IOException {

		try {
			// NOTE: force reading of the ACK/NAK before continuing. This ensures that commands are not lost
			// due to overloading the camera with commands on top of each other.
			// noinspection ResultOfMethodCallIgnored
			int a = is.read();
			if(a == 21) {
				log("NAK encountered.");
				c.setErrorStatus("NAK");
			} else {
				c.setErrorStatus("");
			}
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

	/**
	 * convert a Byte list to a byte array
	 * @param list the Byte list
	 * @return the byte array
	 */
	private static byte[] l2ba(final List<Byte> list) {

		byte[] rv = new byte[list.size()];
		int i = 0;

		for(byte a : list) { rv[i] = a; i++; }

		return rv;
	}

	/**
	 * convert a byte array to Byte list
	 * @param arr the byte array
	 * @return the Byte list
	 */
	private static List<Byte> ba2l(final byte[] arr) {

		List<Byte> rv = new ArrayList<Byte>(arr.length);
		for(byte b : arr)
			rv.add(b);

		return rv;
	}

	/**
	 * processes the params and adds them as byte commands on the command list
	 * @param c Command type [enum]
	 * @param vF float speed value
	 * @param carr current byte array to append to in return value
	 */
	protected static byte[] processPTZInfo(final Command c, final Float vF,
					       final byte[] carr) {

		String error = "ERROR: Unknown PTZ information. ";
		List<Byte> rv = new ArrayList<Byte>();
		rv.addAll(ba2l(carr));

		// assign nulls to 0;
		float v = (vF == null) ? 0f : vF;

		// determine direction
		int dir = NumericAlphaComparator.compareFloats(v, 0f, PTZ_THRESH);

		boolean stopping = (dir == 0);
		boolean posDir = (dir > 0);

		boolean fixed = (fixed_speed || stopping);
		Command2 c2;
		switch(c) {
		case PAN:
			c2 = (fixed) ? Command2.FIXED_PAN : Command2.VAR_PAN;
			break;
		case TILT:
			c2 = (fixed) ? Command2.FIXED_TILT : Command2.VAR_TILT;
			break;
		case ZOOM:
			c2 = (fixed) ? Command2.FIXED_ZOOM : Command2.VAR_ZOOM;
			break;
		default:
			// something went wrong
			log(error + c.toString() + " speed="
				+ ((vF == null) ? "null" : vF));
			return carr;
		}

		// which command to send
		switch(c2) {
		case FIXED_PAN:
			rv.add(fcPan);
			break;
		case VAR_PAN:
			rv.add((posDir ? vcPP : vcPN));
			break;
		case FIXED_TILT:
			rv.add(fcTilt);
			break;
		case VAR_TILT:
			rv.add((posDir ? vcTP : vcTN));
			break;
		case FIXED_ZOOM:
			rv.add(fcZoom);
			break;
		case VAR_ZOOM:
			rv.add(vcZB);
			break;
		default:
			// something went wrong
			log(error + c.toString() + " speed="
				+ ((vF == null) ? "null" : vF));
			return carr;
		}

		// if stopping, just add a stop and exit
		if(stopping) {
			rv.add(faStop);
			return l2ba(rv);
		}

		// first argument (variable zoom takes a second, see below)
		switch(c2) {
		case FIXED_PAN:
			rv.add((posDir ? faPP : faPN));
			break;
		case VAR_PAN:
			rv.add(getPanTiltSpeedByte(v));
			break;
		case FIXED_TILT:
			rv.add((posDir ? faTP : faTN));
			break;
		case VAR_TILT:
			rv.add(getPanTiltSpeedByte(v));
			break;
		case FIXED_ZOOM:
			rv.add((posDir ? faZP : faZN));
			break;
		case VAR_ZOOM:
			rv.add((posDir ? vaZP : vaZN));
			break;
		default:
			// something went wrong
			log(error + c.toString() + " speed="
				+ ((vF == null) ? "null" : vF));
			return carr;
		}

		// variable zoom speed
		if(Command2.VAR_ZOOM.equals(c2))
			rv.add(getZoomSpeedByte(v));

		return l2ba(rv);
	}
}
