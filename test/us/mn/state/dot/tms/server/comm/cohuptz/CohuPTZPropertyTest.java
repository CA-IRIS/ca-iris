/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016  Southwest Research Institute
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

import junit.framework.TestCase;

import us.mn.state.dot.tms.server.comm.cohuptz.CohuPTZProperty.Command;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertArrayEquals;

/**
 * CohuPTZProperty test cases
 *
 * @author Jacob Barde
 */
public class CohuPTZPropertyTest extends TestCase {

	private static final String ENC = "UTF-8";

	public CohuPTZPropertyTest(String name) {
		super(name);
	}

	public void testFullPTZ() {
		byte[] cmd;
		byte[] exp;

		CohuPTZProperty.fixed_speed = true;

		cmd = new byte[]{};
		exp = new byte[]{(byte) 'P', (byte) 'S', (byte) 'T', (byte) 'S',
			(byte) 'Z', (byte) 'S'};
		cmd = CohuPTZProperty.processPTZInfo(Command.PAN, null, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.TILT, null, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.ZOOM, null, cmd);
		log(cmd);
		assertArrayEquals(exp, cmd);

		cmd = new byte[]{};
		exp = new byte[]{(byte) 'P', (byte) 'R', (byte) 'T', (byte) 'U',
			(byte) 'Z', (byte) 'S'};
		cmd = CohuPTZProperty.processPTZInfo(Command.PAN, 0.5f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.TILT, 0.5f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.ZOOM, 0f, cmd);
		log(cmd);
		assertArrayEquals(exp, cmd);

		cmd = new byte[]{};
		exp = new byte[]{(byte) 'P', (byte) 'S', (byte) 'T', (byte) 'S',
			(byte) 'Z', (byte) 'I'};
		cmd = CohuPTZProperty.processPTZInfo(Command.PAN, null, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.TILT, 0f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.ZOOM, 0.5f, cmd);
		log(cmd);
		assertArrayEquals(exp, cmd);

		CohuPTZProperty.fixed_speed = false;
		cmd = new byte[]{};
		exp = new byte[]{(byte) 'P', (byte) 'S', (byte) 'T', (byte) 'S',
			(byte) 'c', (byte) 'Z', (byte) '1'};
		cmd = CohuPTZProperty.processPTZInfo(Command.PAN, null, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.TILT, 0f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(Command.ZOOM, 0.5f, cmd);
		log(cmd);
		assertArrayEquals(exp, cmd);
	}

	public void testPanFixed() {
		byte[] exp;

		CohuPTZProperty.fixed_speed = true;
		Command c = Command.PAN;

		exp = new byte[]{(byte) 'P', (byte) 'S'};
		chkarr(c, null, exp);

		exp = new byte[]{(byte) 'P', (byte) 'S'};
		chkarr(c, 0.0009f, exp);

		exp = new byte[]{(byte) 'P', (byte) 'L'};
		chkarr(c, -(CohuPTZProperty.PTZ_THRESH), exp);

		exp = new byte[]{(byte) 'P', (byte) 'R'};
		chkarr(c, 1f, exp);

		exp = new byte[]{(byte) 'P', (byte) 'R'};
		chkarr(c, 100f, exp);
	}

	public void testTiltFixed() {
		byte[] exp;

		CohuPTZProperty.fixed_speed = true;
		Command c = Command.TILT;

		exp = new byte[]{(byte) 'T', (byte) 'S'};
		chkarr(c, null, exp);

		exp = new byte[]{(byte) 'T', (byte) 'S'};
		chkarr(c, 0.0009f, exp);

		exp = new byte[]{(byte) 'T', (byte) 'D'};
		chkarr(c, -(CohuPTZProperty.PTZ_THRESH), exp);

		exp = new byte[]{(byte) 'T', (byte) 'U'};
		chkarr(c, 1f, exp);

		exp = new byte[]{(byte) 'T', (byte) 'U'};
		chkarr(c, 100f, exp);

	}

	public void testZoomFixed() {
		byte[] exp;

		CohuPTZProperty.fixed_speed = true;
		Command c = Command.ZOOM;

		exp = new byte[]{(byte) 'Z', (byte) 'S'};
		chkarr(c, null, exp);

		exp = new byte[]{(byte) 'Z', (byte) 'S'};
		chkarr(c, 0.0009f, exp);

		exp = new byte[]{(byte) 'Z', (byte) 'O'};
		chkarr(c, -(CohuPTZProperty.PTZ_THRESH), exp);

		exp = new byte[]{(byte) 'Z', (byte) 'I'};
		chkarr(c, 1f, exp);

		exp = new byte[]{(byte) 'Z', (byte) 'I'};
		chkarr(c, 100f, exp);
	}


	public void testPanVariable() {
		byte[] exp;

		CohuPTZProperty.fixed_speed = false;
		Command c = Command.PAN;

		exp = new byte[]{(byte) 'P', (byte) 'S'};
		chkarr(c, null, exp);

		exp = new byte[]{(byte) 'P', (byte) 'S'};
		chkarr(c, 0.0009f, exp);

		exp = new byte[]{(byte) 'l', (byte) '1'};
		chkarr(c, -(CohuPTZProperty.PTZ_THRESH), exp);

		exp = new byte[]{(byte) 'r', (byte) '?'};
		chkarr(c, 1f, exp);

		exp = new byte[]{(byte) 'r', (byte) '8'};
		chkarr(c, 0.5f, exp);
	}

	public void testTiltVariable() {
		byte[] exp;

		CohuPTZProperty.fixed_speed = false;
		Command c = Command.TILT;

		exp = new byte[]{(byte) 'T', (byte) 'S'};
		chkarr(c, null, exp);

		exp = new byte[]{(byte) 'T', (byte) 'S'};
		chkarr(c, 0.0009f, exp);

		exp = new byte[]{(byte) 'd', (byte) '1'};
		chkarr(c,  -(CohuPTZProperty.PTZ_THRESH), exp);

		exp = new byte[]{(byte) 'u', (byte) '?'};
		chkarr(c, 1f, exp);

		exp = new byte[]{(byte) 'u', (byte) '8'};
		chkarr(c, 0.5f, exp);
	}

	public void testZoomVariable() {
		byte[] exp;

		CohuPTZProperty.fixed_speed = false;
		Command c = Command.ZOOM;

		exp = new byte[]{(byte) 'Z', (byte) 'S'};
		chkarr(c, null, exp);

		exp = new byte[]{(byte) 'Z', (byte) 'S'};
		chkarr(c, 0.0009f, exp);

		exp = new byte[]{(byte) 'c', (byte) 'z', (byte) '0'};
		chkarr(c, -(CohuPTZProperty.PTZ_THRESH), exp);

		exp = new byte[]{(byte) 'c', (byte) 'Z', (byte) '2'};
		chkarr(c, 1f, exp);

		exp = new byte[]{(byte) 'c', (byte) 'Z', (byte) '1'};
		chkarr(c, 0.5f, exp);
	}


	private static void log(byte[] cmd) {

//		try {
//			System.out.println("commands: " + new String(cmd, ENC));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

	}

	private static void chkarr(Command c, Float v, byte[] exp) {
		byte[] cmd = new byte[]{};
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		log(cmd);
		assertArrayEquals(exp, cmd);
	}

}
