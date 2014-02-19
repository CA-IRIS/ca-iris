/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ntcip.mib1203;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import us.mn.state.dot.tms.server.comm.ntcip.ASN1OctetString;
import us.mn.state.dot.tms.server.comm.ntcip.BrightnessLevel;

/**
 * DmsIllumBrightnessValues is a sign brightness table.  It is encoded using
 * OER (NTCIP 1102).
 *
 * @author Douglas Lau
 */
public class DmsIllumBrightnessValues extends ASN1OctetString {

	/** Create a new DmsIllumBrightnessValues object */
	public DmsIllumBrightnessValues() {
		super(MIB1203.illum.create(new int[] {7, 0}));
	}

	/** Set the brightness table */
	public void setTable(BrightnessLevel[] table) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(table.length);
			for(BrightnessLevel lvl: table) {
				dos.writeShort(lvl.output);
				dos.writeShort(lvl.pc_down);
				dos.writeShort(lvl.pc_up);
			}
			value = bos.toByteArray();
		}
		finally {
			dos.close();
			bos.close();
		}
	}

	/** Get the brightness table */
	public BrightnessLevel[] getTable() throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		DataInputStream dis = new DataInputStream(bis);
		try {
			int n_lvls = dis.readByte();
			BrightnessLevel[] table = new BrightnessLevel[n_lvls];
			for(BrightnessLevel lvl: table) {
				lvl.output = dis.readUnsignedShort();
				lvl.pc_down = dis.readUnsignedShort();
				lvl.pc_up = dis.readUnsignedShort();
			}
			return table;
		}
		finally {
			dis.close();
			bis.close();
		}
	}

	/** Get the object value */
	public String getValue() {
		StringBuilder b = new StringBuilder();
		try {
			BrightnessLevel[] table = getTable();
			b.append(table.length);
			for(BrightnessLevel lvl: table) {
				b.append(", (");
				b.append(lvl.output);
				b.append(",");
				b.append(lvl.pc_down);
				b.append(",");
				b.append(lvl.pc_up);
				b.append(")");
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return b.toString();
	}

	/** Check if the brightness table is valid */
	public boolean isValid() throws IOException {
		BrightnessLevel[] table = getTable();
		int output = 0;
		int down = 0;
		int up = 0;
		for(BrightnessLevel lvl: table) {
			if(lvl.pc_up < lvl.pc_down)
				return false;
			if(lvl.output < output)
				return false;
			if(lvl.pc_down < down)
				return false;
			if(lvl.pc_up < up)
				return false;
			output = lvl.output;
			down = lvl.pc_down;
			up = lvl.pc_up;
		}
		return true;
	}
}
