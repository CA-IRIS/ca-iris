/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import us.mn.state.dot.tms.Base64;
import us.mn.state.dot.tms.Font;
import us.mn.state.dot.tms.FontHelper;
import us.mn.state.dot.tms.Glyph;
import us.mn.state.dot.tms.Graphic;
import us.mn.state.dot.tms.server.comm.ntcip.CRC16;

/**
 * FontVersionByteStream is used to calculate a FontVersionID.  It is encoded
 * using OER (NTCIP 1102).
 *
 * @author Douglas Lau
 */
public class FontVersionByteStream extends CRC16 {

	/** Create a new FontVersionByteStream */
	public FontVersionByteStream(Font font) throws IOException {
		Collection<Glyph> glyphs = FontHelper.lookupGlyphs(font);
		DataOutputStream dos = new DataOutputStream(this);
		dos.writeByte(font.getNumber());
		dos.writeByte(font.getHeight());
		dos.writeByte(font.getCharSpacing());
		dos.writeByte(font.getLineSpacing());
		dos.writeByte(1); // number of subsequent length octets
		dos.writeByte(glyphs.size());
		for(Glyph glyph: glyphs) {
			Graphic graphic = glyph.getGraphic();
			byte[] bitmap = Base64.decode(graphic.getPixels());
			dos.writeShort(glyph.getCodePoint());
			dos.writeByte(graphic.getWidth());
			dos.writeByte(bitmap.length);
			dos.write(bitmap);
		}
	}
}
