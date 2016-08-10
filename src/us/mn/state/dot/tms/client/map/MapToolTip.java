/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.map;

import javax.swing.JToolTip;

/**
 * A ToolTip that allows multiple lines
 *
 * @author Erik Engstrom
 */
public class MapToolTip extends JToolTip {

	/** Create a new map tooltip */
	public MapToolTip() {
		updateUI();
	}

	/** Update the UI */
	public void updateUI() {
		setUI(MapToolTipUI.createUI(this));
	}
}
