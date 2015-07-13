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

package us.mn.state.dot.tms.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


/**
 * A Swing DocumentFilter that converts all insert and replace ops to
 * uppercase.
 *
 * @author Travis Swanston
 */
public class UppercaseDocumentFilter extends DocumentFilter {

	public void insertString(DocumentFilter.FilterBypass fb, int offset,
		String string, AttributeSet attr) throws BadLocationException
	{
		fb.insertString(offset, string.toUpperCase(), attr);
	}

	public void replace(DocumentFilter.FilterBypass fb, int offset,
		int length, String text, AttributeSet attrs)
		throws BadLocationException
	{
		fb.replace(offset, length, text.toUpperCase(), attrs);
	}

}

