/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2014  Minnesota Department of Transportation
 * Copyright (C) 2009-2010  AHMCT, University of California
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
package us.mn.state.dot.tms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import us.mn.state.dot.tms.utils.SString;

/**
 * Helper class for DMS. Used on the client and server.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class DMSHelper extends BaseHelper {

	/** don't instantiate */
	private DMSHelper() {
		assert false;
	}

	/** Lookup the DMS with the specified name */
	static public DMS lookup(String name) {
		return (DMS)namespace.lookupObject(DMS.SONAR_TYPE, name);
	}

	/** Get a DMS iterator */
	static public Iterator<DMS> iterator() {
		return new IteratorWrapper<DMS>(namespace.iterator(
			DMS.SONAR_TYPE));
	}

	/** Test if a DMS is active */
	static public boolean isActive(DMS proxy) {
		return !ItemStyle.INACTIVE.checkBit(proxy.getStyles());
	}

	/** Get the maintenance status of a DMS */
	static public String getMaintenance(DMS proxy) {
		return ControllerHelper.getMaintenance(proxy.getController());
	}

	/** Test if a DMS has a critical error */
	static public boolean hasCriticalError(DMS proxy) {
		return !getCriticalError(proxy).isEmpty();
	}

	/** Get the DMS critical error */
	static public String getCriticalError(DMS proxy) {
		Integer h = proxy.getFaceHeight();
		Integer w = proxy.getFaceWidth();
		if(h == null || w == null || h <= 0 || w <= 0)
			return "Invalid dimensions";
		else
			return getStatus(proxy);
	}

	/** Get DMS controller communication status */
	static public String getStatus(DMS proxy) {
		return ControllerHelper.getStatus(proxy.getController());
	}

	/** Test if a DMS is failed */
	static public boolean isFailed(DMS proxy) {
		return ItemStyle.FAILED.checkBit(proxy.getStyles());
	}

	/** Get a string that contains all active DMS styles,
	 *  separated by commas. */
	static public String getAllStyles(DMS proxy) {
		StringBuilder s = new StringBuilder();
		for(ItemStyle style: ItemStyle.toStyles(proxy.getStyles())) {
			s.append(style.toString());
			s.append(", ");
		}
		return SString.removeTail(s.toString(), ", ");
	}

	/** Lookup the camera preset for a DMS */
	static public CameraPreset getPreset(DMS dms) {
		return (dms != null) ? dms.getPreset() : null;
	}

	/** Get the DMS roadway direction from the geo location as a String */
	static public String getRoadDir(DMS proxy) {
		if(proxy != null) {
			GeoLoc loc = proxy.getGeoLoc();
			if(loc != null) {
				short rd = loc.getRoadDir();
				return Direction.fromOrdinal(rd).abbrev;
			}
		}
		return "";
	}

	/** Get the MULTI string currently on the specified dms.
	 * @param dms DMS to lookup. */
	static public String getMultiString(DMS dms) {
		if(dms != null) {
			SignMessage sm = dms.getMessageCurrent();
			if(sm != null)
				return sm.getMulti();
		}
		return "";
	}

	/** Get the default font number for a DMS */
	static public int getDefaultFontNumber(DMS dms) {
		if(dms != null) {
			Font f = dms.getDefaultFont();
			if(f != null)
				return f.getNumber();
		}
		return FontHelper.DEFAULT_FONT_NUM;
	}

	/** Get the number of lines on a DMS.
	 * @param dms DMS to check.
	 * @return Number of text lines on the DMS. */
	static public int getLineCount(DMS dms) {
		if(dms != null) {
			RasterBuilder rb = createRasterBuilder(dms);
			if(rb != null)
				return rb.getLineCount();
		}
		return SystemAttrEnum.DMS_MAX_LINES.getInt();
	}

	/** Create a raster builder for a DMS.
	 * @param dms DMS with proper dimensions for the builder.
	 * @return A pixel map builder, or null is dimensions are invalid. */
	static public RasterBuilder createRasterBuilder(DMS dms) {
		Integer w = dms.getWidthPixels();
		Integer h = dms.getHeightPixels();
		Integer cw = dms.getCharWidthPixels();
		Integer ch = dms.getCharHeightPixels();
		int df = getDefaultFontNumber(dms);
		if(w != null && h != null && cw != null && ch != null)
			return new RasterBuilder(w, h, cw, ch, df);
		else
			return null;
	}

	/** Return a single string which is formated to be readable
	 * by the user and contains all sign message lines on the
	 * specified DMS.
	 * @param dms The DMS containing the message.
	 * @return Text of message on the DMS. */
	static public String buildMsgLine(DMS dms) {
		SignMessage sm = dms.getMessageCurrent();
		if(sm != null) {
			String multi = sm.getMulti();
			if(multi != null)
				return new MultiString(multi).asText();
		}
		return "";
	}

	/** Messages lines that flag no DMS message text available */
	public final static String NOTXT_L1 = "_OTHER_";
	public final static String NOTXT_L2 = "_SYSTEM_";
	public final static String NOTXT_L3 = "_MESSAGE_";

	/** Filter the specified multi. If certain keywords are present then
	 * a blank multi is returned. The keywords indicate no text is
	 * available for the associated bitmap.
	 * @return A blank multi if the argument multi flags no text,
	 *         else the specified multi. */
	static public MultiString ignoreFilter(MultiString ms) {
		String s = ms.toString();
		boolean ignore = s.contains(NOTXT_L1) && s.contains(NOTXT_L2)
			&& s.contains(NOTXT_L3);
		if(ignore)
			ms = new MultiString();
		return ms;
	}

	/**
	 * Return true if the specified message line should be ignored.
	 * By convention, a line begining and ending with an underscore
	 * is to be ignored. IRIS assumes non-blank DMS messages have
	 * both a bitmap and multistring, which is not the case for all
	 * DMS protocols.
	 */
	static public boolean ignoreLineFilter(String line) {
		if(line == null)
			return false;
		return SString.enclosedBy(line, "_");
	}

	/** Get the current bitmap graphic for all pages of the specified DMS.
	 * @param DMS with the graphic.
	 * @return Array of bitmaps, one for each page, or null on error. */
	static public BitmapGraphic[] getBitmaps(DMS dms) {
		if(dms != null) {
			SignMessage sm = dms.getMessageCurrent();
			return SignMessageHelper.getBitmaps(sm, dms);
		} else
			return null;
	}

	/** Create a bitmap graphic for the specified DMS */
	static public BitmapGraphic createBitmapGraphic(DMS dms) {
		Integer wp = dms.getWidthPixels();
		Integer hp = dms.getHeightPixels();
		if(wp != null && hp != null)
			return new BitmapGraphic(wp, hp);
		else
			return null;
	}

	/** Get the current raster graphics for all pages of the specified DMS.
	 * @param dms Sign in question.
	 * @return RasterGraphic array, one for each page, or null on error.
	 */
	static public RasterGraphic[] getRasters(DMS dms) {
		if(dms != null) {
			SignMessage sm = dms.getMessageCurrent();
			if(sm != null)
				return getRasters(dms, sm);
		}
		return null;
	}

	/** Get the current raster graphics for all pages of the specified DMS.
	 * @param dms Sign in question.
	 * @param sm Sign message.
	 * @return RasterGraphic array, one for each page, or null on error.
	 */
	static private RasterGraphic[] getRasters(DMS dms, SignMessage sm) {
		BitmapGraphic[] bitmaps =
			SignMessageHelper.getBitmaps(sm, dms);
		if(bitmaps == null)
			return null;
		RasterGraphic[] rasters = createRasters(dms, sm);
		if(rasters == null)
			return null;
		if(graphicsMatch(rasters, bitmaps) || bitmaps.length == 0)
			return rasters;
		else
			return bitmaps;
	}

	/** Create raster graphics for all pages of the specified DMS.
	 * @param dms Sign in question.
	 * @return RasterGraphic array, one for each page, or null on error.
	 */
	static private RasterGraphic[] createRasters(DMS dms, SignMessage sm) {
		RasterBuilder rb = createRasterBuilder(dms);
		if(rb != null)
			return createRasters(rb, sm.getMulti());
		else
			return null;
	}

	/** Create raster graphics using a raster builder and multi string.
	 * @return RasterGraphic array, one for each page, or null on error.
	 */
	static private RasterGraphic[] createRasters(RasterBuilder rb,
		String multi)
	{
		try {
			return rb.createPixmaps(new MultiString(multi));
		}
		catch(InvalidMessageException e) {
			return null;
		}
	}

	/** Check if an array of raster graphics match another */
	static private boolean graphicsMatch(RasterGraphic[] rg,
		BitmapGraphic[] bm)
	{
		if(rg.length != bm.length)
			return false;
		for(int i = 0; i < rg.length; i++) {
			RasterGraphic r = rg[i];
			BitmapGraphic b = bm[i];
			BitmapGraphic test = b.createBlankCopy();
			test.copy(b);
			try {
				test.difference(r);
			}
			catch(IndexOutOfBoundsException e) {
				return false;
			}
			if(test.getLitCount() > 0)
				return false;
		}
		return true;
	}

	/** Test if a DMS can be controlled by AWS. */
	static public boolean isAwsControlled(DMS proxy) {
		if (proxy == null)
			return false;
		return (proxy.getAwsAllowed() && proxy.getAwsControlled());
	}

	/** Test if a DMS has an AWS message deployed.
	 * @param proxy DMS, may be null.
	 * @return True if the deployed message is an AWS message,
	 *         else false if not or it is a blank. */
	static public boolean isAwsDeployed(DMS proxy) {
		if(proxy == null)
			return false;
		SignMessage m = proxy.getMessageCurrent();
		if(m != null) {
			return m.getRunTimePriority() == DMSMessagePriority.AWS.
				ordinal() && !SignMessageHelper.isBlank(m);
		} else {
			// messageCurrent should never be null, so this means
			// the proxy has just been removed
			return false;
		}
	}

	/** Get an unsorted list of all dms */
	static public List<DMS> getAll() {
		final List<DMS> list = new LinkedList<DMS>();
		Iterator<DMS> it = iterator();
		while(it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

}
