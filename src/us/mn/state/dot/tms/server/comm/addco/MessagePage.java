/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.addco;

import us.mn.state.dot.tms.BitmapGraphic;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.server.DMSImpl;

/**
 * A message page contains the MULTI string and bitmap for one page of an
 * Addco sign message.
 *
 * @author Douglas Lau
 */
public class MessagePage {

	/** MULTI string for the page */
	private final String multi;

	/** Bitmap graphic for the page */
	private final BitmapGraphic bitmap;

	/** Create a new message page.
	 * @param dms DMS for message.
	 * @param sm Sign message.
	 * @param p Page number (0-relative). */
	public MessagePage(DMSImpl dms, SignMessage sm, int p) {
		multi = lookupMulti(sm.getMulti(), p);
		bitmap = lookupBitmap(dms, sm, p);
	}

	/** Lookup the MULTI string for the page */
	private String lookupMulti(String multi, int p) {
		return new MultiString(multi).getPage(p);
	}

	/** Lookup a bitmap for the page */
	private BitmapGraphic lookupBitmap(DMSImpl dms, SignMessage sm, int p) {
		BitmapGraphic[] bmaps = SignMessageHelper.getBitmaps(sm, dms);
		if (bmaps != null && bmaps.length > p)
			return bmaps[p];
		else
			return null;
	}

	/** Create a new message page.
	 * @param dms DMS for message.
	 * @param ms MULTI string. */
	public MessagePage(DMSImpl dms, String ms) {
		multi = new MultiString(ms).getPage(0);
		bitmap = createBitmap(dms, ms);
	}

	/** Create a bitmap for a DMS */
	private BitmapGraphic createBitmap(DMSImpl dms, String ms) {
		try {
			BitmapGraphic[] bmaps = DMSHelper.createBitmaps(dms,ms);
			if (bmaps.length > 0)
				return bmaps[0];
		}
		catch (InvalidMessageException e) {
			// fall thru
		}
		return DMSHelper.createBitmapGraphic(dms);
	}

	/** Get the MULTI string for the page */
	public String getMulti() {
		return multi;
	}

	/** Get the bitmap graphic for the page */
	public BitmapGraphic getBitmap() {
		return bitmap;
	}
}
