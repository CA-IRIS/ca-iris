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
package us.mn.state.dot.tms.client.dms;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import us.mn.state.dot.tms.DmsPgTime;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.I18N;
import us.mn.state.dot.tms.utils.SString;

/**
 * A spinner for the DMS message page time. This control is used to specify
 * page on-time (and in the future, perhaps page off-time). Page on-time is
 * a function of the current number of pages. For a single page message, zero
 * should be the default, with non-zero values possible, which indicates
 * a single page flashing message. For multi-page messages, the page on-time
 * must be non-zero. System attributes are used to specify the minimum,
 * default,and maximum values for page on-time. This spinner enforces these
 * values. Because the valid value for page on-time is a function of the
 * current number of pages (in the composer), those controls notify the
 * spinner of the current number of pages, so the spinner can adjust the
 * current value--e.g. if it's 0, and the user has just entered a 2nd page,
 * the spinner sets a non-default page on-time.
 *
 * @see DmsPgTime
 * @author Michael Darter
 * @author Douglas Lau
 */
public class PgTimeSpinner extends JSpinner {

	/** Page on-time increment value */
	static public final float INC_ONTIME_SECS = .1f;

	/** Is this control IRIS enabled? */
	static public boolean getIEnabled() {
		return SystemAttrEnum.DMS_PAGE_ON_SELECTION_ENABLE.getBoolean();
	}

	/** Does the current message have single or multiple pages.
	 *  This determines if zero is an acceptable value. */
	protected boolean m_singlepg = true;

	/** Page time spinner model, which allows for an closed range of
	 *  values. Single page messages also allow a value of zero. */
	protected class PgTimeSpinnerModel extends AbstractSpinnerModel {

		/** Inclusive minimum value allowed */
		private final double m_min;

		/** Inclusive maximum value allowed */
		private final double m_max;

		/** Increment value */
		private final double m_inc;

		/** Current model value */
		private double m_value = 0;

		/** Constructor.
		 *  @param def Initial value.
		 *  @param max Maximum (inclusive) allowed value.
		 *  @param min Minimum (inclusive) allowed value.
		 *  @param inc Increment value. */
		public PgTimeSpinnerModel(double def, double min, double max,
			double inc)
		{
			m_min = Math.min(min, max);
			m_max = Math.max(min, max);
			m_value = validate(def);
			m_inc = inc;
		}

		/** Return a validated spinner value in seconds. A value of
		 * zero is valid for single page messages only. */
		private double validate(double value) {
			DmsPgTime t = new DmsPgTime(value).validateValue(
				m_singlepg, new DmsPgTime(m_min),
				new DmsPgTime(m_max));
			return t.toSecs();
		}

		/** Get the next value, or null if the next value would be
		 *  out of range. */
		public Object getNextValue() {
			if(m_singlepg && m_value == 0)
				return m_min;
			else
				return validate(m_value + m_inc);
		}

		/** Get previous value, or null if the previous value is
		 *  out of range. */
		public Object getPreviousValue() {
			if(m_singlepg && m_value == 0)
				return null;
			else
				return validate(m_value - m_inc);
		}

		/** Get current value */
		public Object getValue() {
			return m_value;
		}

		/** Set current value */
		public void setValue(Object value) {
			if(value == null)
				setValueDouble(0.0);
			else if(value instanceof DmsPgTime) {
				DmsPgTime pt = (DmsPgTime)value;
				setValueDouble(pt.toSecs());
			} else if(value instanceof Number) {
				Number n = (Number)value;
				setValueDouble(n.doubleValue());
			} else {
				setValueDouble(SString.stringToDouble(
					value.toString()));
			}
		}

		/** Set the current value */
		protected void setValueDouble(double value) {
			m_value = validate(value);
			fireStateChanged();
		}
	}

	/** Create a new page time spinner */
	public PgTimeSpinner() {
		setModel(new PgTimeSpinnerModel(
			DmsPgTime.getDefaultOn(true).toSecs(),
			DmsPgTime.getMinOnTime().toSecs(),
			DmsPgTime.getMaxOnTime().toSecs(), INC_ONTIME_SECS));
		setToolTipText(I18N.get("dms.page.on.time.tooltip"));

		// force the spinner to be editable
		JFormattedTextField tf = ((JSpinner.DefaultEditor)
			this.getEditor()).getTextField();
    		tf.setEditable(true);
	}

	/** Enable or disable */
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		// if disabled, reset value to default
		if(!b)
			setValue(DmsPgTime.getDefaultOn(m_singlepg).toSecs());
	}

	/** Set value using seconds. */
	public void setValue(float secs) {
		super.setValue(new DmsPgTime(secs).toSecs());
	}

	/** Set value. */
	public void setValue(DmsPgTime t) {
		super.setValue(t.toSecs());
	}

	/** Get the current value as a DmsPgTime. */
	public DmsPgTime getValuePgTime() {
		Object v = getValue();
		if(v instanceof Number)
			return new DmsPgTime(((Number)v).floatValue());
		else
			return DmsPgTime.getDefaultOn(m_singlepg);
	}

	/** Set value using the page on-time specified in the 1st page
	 * of the MULTI string. If no value is specified in the MULTI,
	 * the default value is used for multi-page messages else 0
	 * for single page messages.
	 * @param m A MULTI string, containing possible page times. */
	public void setValue(String m) {
		MultiString ms = new MultiString(m);
		setSinglePage(ms.singlePage());
		setValue(ms.getPageOnTime());
	}

	/** Set number of pages in current message. */
	private void setSinglePage(boolean sp) {
		m_singlepg = sp;
	}
}
