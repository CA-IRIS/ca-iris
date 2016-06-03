/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.client;

import us.mn.state.dot.tms.utils.SString;

/**
 * A tool tip builder, which builds tool tips in a uniform style.
 *
 * @author Michael Darter
 */
public class ToolTipBuilder {

	/** Tool tip text */
	private StringBuilder tool_tip = new StringBuilder();

	/** Last line flag */
	private boolean last_line = false;

	/** Get the tool tip as a string */
	public String get() {
		return tool_tip.toString();
	}

	/** Set the last line indicator */
	public ToolTipBuilder setLast() {
		last_line = true;
		return this;
	}

	/** Conditionally add a tag and float value, ignoring a missing value.
	 * @param t Tag to append, e.g. "Precip rate".
	 * @param v Value to append, e.g. "9.33" or null if missing.
	 * @return Tooltip with line conditionally added. */
	public ToolTipBuilder addLine(String t, Float v) {
		return addLineMaybe(t, String.valueOf(v), v != null);
	}

	/** Conditionally add a tag and float value with the specified
	 * precision, ignoring a missing value.
	 * @param t Tag to append, e.g. "Precip rate".
	 * @param v Value to append, e.g. "9.33" or null if missing.
	 * @param n Number of digits to the right of decimal.
	 * @return Tooltip with line conditionally added. */
	public ToolTipBuilder addLine(String t, Float v, int n) {
		if(v == null)
			return this;
		return addLine(t, SString.doubleToString((double)v, n));
	}

	/** Conditionally add a tag and value, ignoring a missing value.
	 * @param t Tag to append, e.g. "Precip rate".
	 * @param v Value to append or null if missing.
	 * @return Tooltip with line conditionally added. */
	public ToolTipBuilder addLine(String t, Integer v) {
		return addLineMaybe(t, String.valueOf(v), v != null);
	}

	/** Conditionally add a tag and value, ignoring a missing value.
	 * @param t Tag to append, e.g. "Precip rate".
	 * @param v Value to append or null if missing.
	 * @return Tooltip with line conditionally added. */
	public ToolTipBuilder addLine(String t, Boolean v) {
		return addLineMaybe(t, String.valueOf(v), v != null);
	}

	/** Conditionally add a tag and string, ignoring a missing value.
	 * @param t Tag to append, e.g. "Precip rate".
	 * @param v Value to append or null if missing.
	 * @return Tooltip with line conditionally added. */
	public ToolTipBuilder addLine(String t, String v) {
		return addLineMaybe(t, String.valueOf(v),
			v != null && !v.isEmpty());
	}

	/** Conditionally add a tag and integer value */
	public ToolTipBuilder addLineMaybe(String t, Integer v, boolean add) {
		return addLineMaybe(t, String.valueOf(v), add);
	}

	/** Conditionally add a tag and boolean value */
	public ToolTipBuilder addLineMaybe(String t, Boolean v, boolean add) {
		return addLineMaybe(t, String.valueOf(v), add);
	}

	/** Conditionally add a tag and non-empty string value */
	public ToolTipBuilder addLineMaybe(String t, String v, boolean add) {
		if(add && v != null && !v.isEmpty()) {
			StringBuilder line = new StringBuilder(t).
				append(" = ").append(v);
			return addLineMaybe(line.toString(), add);
		}
		return this;
	}

	/** Conditionally add a non-empty string */
	public ToolTipBuilder addLineMaybe(String line, boolean add) {
		if(add)
			addLine(line);
		return this;
	}

	/** Add a non-empty line and conditionally a newline */
	public ToolTipBuilder addLine(String line) {
		if(line != null && !line.isEmpty())
			tool_tip.append(line).append(last_line ? "" : "\n");
		return this;
	}

}
