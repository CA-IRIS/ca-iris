/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.dms;

import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import static us.mn.state.dot.tms.client.widget.SwingRunner.runSwing;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.MultiParser;
import us.mn.state.dot.tms.SignText;
import us.mn.state.dot.tms.SignGroup;

/**
 * Model for a sign text line combo box.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class SignTextComboBoxModel extends AbstractListModel
	implements ComboBoxModel
{
	/** Rank for on-the-fly created sign messages */
	static private final short ON_THE_FLY_RANK = 99;

	/** Blank client-side sign text object */
	static private final SignText BLANK_SIGN_TEXT =
		new ClientSignText("");

	/** Set of sorted SignText items */
	private final TreeSet<SignText> items =
		new TreeSet<SignText>(new SignTextComparator());

	/** Sign text line number */
	private final short line;

	/** Owning sign text model */
	private final SignTextModel model;

	/** Create a new sign text combo box model.
	 * @param ln Sign text line number.
	 * @param stm Sign text model. */
	protected SignTextComboBoxModel(short ln, SignTextModel stm) {
		line = ln;
		model = stm;
		items.add(BLANK_SIGN_TEXT);
	}

	/** Get the element at the specified index */
	@Override
	public Object getElementAt(int index) {
		int i = 0;
		for(SignText t: items) {
			if(i == index)
				return t;
			i++;
		}
		return null;
	}

	/** Get the number of elements in the model */
	@Override
	public int getSize() {
		return items.size();
	}

	/** Selected SignText item */
	private SignText selected;

	/** Get the selected item */
	@Override
	public Object getSelectedItem() {
		SignText st = selected;
		// filter lines that should be ignored
		if(st != null && st instanceof ClientSignText) {
			if(DMSHelper.ignoreLineFilter(st.getMulti()))
				return BLANK_SIGN_TEXT;
		}
		return st;
	}

	/**
	 * Set the selected item. This method is called by the combobox when:
	 * 	-the focus leaves the combobox with a String arg when editable.
	 *      -a combobox item is clicked on via the mouse.
	 *      -a combobox item is moved to via the cursor keys.
	 */
	@Override
	public void setSelectedItem(Object s) {
		if(s instanceof SignText)
			selected = (SignText)s;
		else if(s instanceof String)
			selected = getSignText((String)s);
		else
			selected = null;
		// this results in a call to the editor's setSelectedItem method
		fireContentsChanged(this, -1, -1);
	}

	/** Get or create a sign text for the given string */
	private SignText getSignText(String s) {
		if(s.length() == 0)
			return BLANK_SIGN_TEXT;
		SignText st = lookupMessage(s);
		if(st != null)
			return st;
		else {
			return new ClientSignText(MultiParser.normalize(
				s.trim()));
		}
	}

	/** Lookup a sign text.
	 * @return Existing SignText, or null if not found. */
	private SignText lookupMessage(String t) {
		for(SignText st: items) {
			if(t.equals(st.getMulti()))
				return st;
		}
		return null;
	}

	/** Find the index of an item */
	private int find(SignText t) {
		int i = 0;
		for(SignText st: items) {
			if(st.equals(t))
				return i;
			i++;
		}
		return -1;
	}

	/** Add a SignText to the model */
	public void add(SignText t) {
		if(items.add(t)) {
			final int i = find(t);
			if(i >= 0) {
				runSwing(new Runnable() {
					public void run() {
						fireIntervalAdded(this, i, i);
					}
				});
			}
		}
	}

	/** Remove a sign text from the model */
	public void remove(SignText t) {
		final int i = find(t);
		if(i >= 0) {
			items.remove(t);
			if(t.equals(selected))
				selected = null;
			runSwing(new Runnable() {
				public void run() {
					fireIntervalRemoved(this, i, i);
				}
			});
		}
	}

	/** Update the message library with the currently selected messages */
	public void updateMessageLibrary() {
		SignText st = selected;
		if(st instanceof ClientSignText && st != BLANK_SIGN_TEXT)
			addMsgToLib(st.getMulti());
	}

	/** Add a message to the local sign group library */
	private void addMsgToLib(String multi) {
		model.createSignText(line, multi, ON_THE_FLY_RANK);
	}
}
