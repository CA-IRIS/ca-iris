/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2013  Minnesota Department of Transportation
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

/**
 * Abstract MULTI string state adapter.
 *
 * Note: Fields in the class use the
 * "ms_" prefix to make it easier to distinguish their origin in subclasses.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class MultiAdapter implements Multi {

	/** Page number, zero based */
	protected int ms_page;

	/** Add a page */
	public void addPage() {
		ms_page++;
		ms_line = 0;
	}

	/** Page on time (tenths of a second) */
	protected Integer ms_pt_on;

	/** Page off time (tenths of a second) */
	protected Integer ms_pt_off;

	/** Set the page times.
	 * @param pt_on Page on time (deciseconds; null means default)
	 * @param pt_off Page off time (deciseconds; null means default) */
	public void setPageTimes(Integer pt_on, Integer pt_off) {
		ms_pt_on = pt_on;
		ms_pt_off = pt_off;
	}

	/** Page justification */
	protected JustificationPage ms_justp;

	/** Set the page justification */
	public void setJustificationPage(JustificationPage jp) {
		ms_justp = jp;
	}

	/** Line number on page, zero based */
	protected int ms_line;

	/** Add a new line */
	public void addLine(Integer spacing) {
		ms_line++;
	}

	/** Line justification */
	protected JustificationLine ms_justl;

	/** Set the line justification */
	public void setJustificationLine(JustificationLine jl) {
		ms_justl = jl;
	}

	/** Page background color */
	protected DmsColor ms_background = DmsColor.BLACK;

	/** Set the page background color */
	public void setPageBackground(int r, int g, int b) {
		ms_background = new DmsColor(r, g, b);
	}

	/** Foreground color */
	protected DmsColor ms_foreground = DmsColor.AMBER;

	/** Set the foreground color for monochrome1bit, monochrome8bit, and
	 * colorClassic color schemes.
	 * @param x Foreground color (0-1 for monochrome1bit),
	 *                           (0-255 for monochrome8bit),
	 *                           (0-9 for colorClassic). */
	public void setColorForeground(int x) {
		// FIXME: add support for monochrome color schemes
		ColorClassic cc = ColorClassic.fromOrdinal(x);
		if(cc != null)
			ms_foreground = cc.clr;
	}

	/** Set the foreground color for color24bit color scheme.
	 * @param r Red component (0-255).
	 * @param g Green component (0-255).
	 * @param b Blue component (0-255). */
	public void setColorForeground(int r, int g, int b) {
		ms_foreground = new DmsColor(r, g, b);
	}

	/** Add a color rectangle */
	public void addColorRectangle(int x, int y, int w, int h, int r, int g,
		int b)
	{
		// subclass must handle
	}

	/** Set the text rectangle */
	public void setTextRectangle(int x, int y, int w, int h) {
		// subclass must handle
	}

	/** Font number */
	protected int ms_fnum;

	/** Set the font number.
	 * @param f_num Font number (1 to 255)
	 * @param f_id Font version ID (4-digit hex) */
	public void setFont(int f_num, String f_id) {
		ms_fnum = f_num;
	}

	/** Set the character spacing.
	 * @param sc Character spacing (null means use font spacing) */
	public void setCharSpacing(Integer sc) {
		// subclass must handle
	}

	/** Create a new MULTI string adapter */
	public MultiAdapter() {
		ms_page = 0;
		ms_justp = JustificationPage.DEFAULT;
		ms_line = 0;
		ms_justl = JustificationLine.DEFAULT;
		ms_fnum = 1;
	}

	/** Add a span of text */
	public void addSpan(String span) {
		// subclass must handle
	}

	/** Add a graphic */
	public void addGraphic(int g_num, Integer x, Integer y, String g_id) {
		// subclass must handle
	}

	/** Add a travel time destination */
	public void addTravelTime(String sid) {
		// subclass must handle
	}

	/** Add a speed advisory */
	public void addSpeedAdvisory() {
		// subclass must handle
	}

	/** Add a slow traffic warning.
	 * @param spd Highest speed to activate warning.
	 * @param b Distance to end of backup (negative indicates upstream).
	 * @param units Units for speed (mph or kph).
	 * @param dist If true, replace tag with distance to slow station. */
	public void addSlowWarning(int spd, int b, String units, boolean dist) {
		// subclass must handle
	}

	/** Add a feed message */
	public void addFeed(String fid) {
		// subclass must handle
	}
}
