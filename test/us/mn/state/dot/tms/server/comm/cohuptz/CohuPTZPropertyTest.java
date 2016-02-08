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
 */package us.mn.state.dot.tms.server.comm.cohuptz;

import junit.framework.TestCase;
import us.mn.state.dot.tms.SystemAttrEnum;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * CohuPTZProperty test cases
 *
 * @author Jacob Barde
 */
public class CohuPTZPropertyTest extends TestCase {

	public CohuPTZPropertyTest(String name) { super(name); }


	public void testFullPTZ() {
		List<Byte> cmd;
		List<Byte> exp;

		CohuPTZProperty.fixed_speed = true;
		Float v = null;

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.PAN, v, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.TILT, v, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.ZOOM, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		exp.add((byte) 'Z');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.PAN, 0.5f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.TILT, 0.5f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.ZOOM, 0f, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'R');
		exp.add((byte) 'T');
		exp.add((byte) 'U');
		exp.add((byte) 'Z');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.PAN, null, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.TILT, 0f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.ZOOM, 0.5f, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		exp.add((byte) 'Z');
		exp.add((byte) 'I');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		CohuPTZProperty.fixed_speed = false;
		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.PAN, null, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.TILT, 0f, cmd);
		cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.ZOOM, 0.5f, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		exp.add((byte) 'c');
		exp.add((byte) 'Z');
		exp.add((byte) '1');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}

	public void testPanFixed() {
		List<Byte> cmd = new ArrayList<Byte>();
		List<Byte> exp = new ArrayList<Byte>();

		CohuPTZProperty.fixed_speed = true;
		CohuPTZProperty.Command c = CohuPTZProperty.Command.PAN;
		Float v = null;

		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);


		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.0009f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = -(CohuPTZProperty.PTZ_THRESH);
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'L');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 1f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'R');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 100f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'R');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}

	public void testTiltFixed() {
		List<Byte> cmd = new ArrayList<Byte>();
		List<Byte> exp = new ArrayList<Byte>();

		CohuPTZProperty.fixed_speed = true;
		CohuPTZProperty.Command c = CohuPTZProperty.Command.TILT;
		Float v = null;

		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);


		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.0009f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = -(CohuPTZProperty.PTZ_THRESH);
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'D');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 1f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'U');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 100f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'U');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}

	public void testZoomFixed() {
		List<Byte> cmd = new ArrayList<Byte>();
		List<Byte> exp = new ArrayList<Byte>();

		CohuPTZProperty.fixed_speed = true;
		CohuPTZProperty.Command c = CohuPTZProperty.Command.ZOOM;
		Float v = null;

		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);


		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.0009f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = -(CohuPTZProperty.PTZ_THRESH);
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'O');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 1f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'I');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 100f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'I');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}


	public void testPanVariable() {
		List<Byte> cmd = new ArrayList<Byte>();
		List<Byte> exp = new ArrayList<Byte>();

		CohuPTZProperty.fixed_speed = false;
		CohuPTZProperty.Command c = CohuPTZProperty.Command.PAN;
		Float v = null;

		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);


		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.0009f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'P');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = -(CohuPTZProperty.PTZ_THRESH);
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'l');
		exp.add((byte) '1');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 1f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'r');
		exp.add((byte) '?');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.5f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'r');
		exp.add((byte) '8');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}

	public void testTiltVariable() {
		List<Byte> cmd = new ArrayList<Byte>();
		List<Byte> exp = new ArrayList<Byte>();

		CohuPTZProperty.fixed_speed = false;
		CohuPTZProperty.Command c = CohuPTZProperty.Command.TILT;
		Float v = null;

		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);


		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.0009f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'T');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = -(CohuPTZProperty.PTZ_THRESH);
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'd');
		exp.add((byte) '1');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 1f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'u');
		exp.add((byte) '?');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.5f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'u');
		exp.add((byte) '8');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}

	public void testZoomVariable() {
		List<Byte> cmd = new ArrayList<Byte>();
		List<Byte> exp = new ArrayList<Byte>();

		CohuPTZProperty.fixed_speed = false;
		CohuPTZProperty.Command c = CohuPTZProperty.Command.ZOOM;
		Float v = null;

		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);


		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.0009f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'Z');
		exp.add((byte) 'S');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = -(CohuPTZProperty.PTZ_THRESH);
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'c');
		exp.add((byte) 'z');
		exp.add((byte) '0');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 1f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'c');
		exp.add((byte) 'Z');
		exp.add((byte) '2');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);

		cmd = new ArrayList<>();
		exp = new ArrayList<>();
		v = 0.5f;
		cmd = CohuPTZProperty.processPTZInfo(c, v, cmd);
		exp.add((byte) 'c');
		exp.add((byte) 'Z');
		exp.add((byte) '1');
		try {
			System.out.println("commands: " + new String(CohuPTZProperty.list2bytearray(cmd), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		assertEquals(exp, cmd);
	}



}
