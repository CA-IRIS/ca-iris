/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.meter;

import java.awt.BorderLayout;
import java.io.IOException;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.StyleSummary;

/**
 * Gui for opererating ramp meters.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class RampMeterTab extends MapTab {

	/** Meter device manager */
	private final MeterManager manager;

	/** Meter status panel */
	private final MeterStatusPanel statusPanel;

	/** Summary of meters of each status */
	private final StyleSummary<RampMeter> summary;

	/** Create a new ramp meter tab */
  	public RampMeterTab(Session session, MeterManager man)
		throws IOException
	{
		super("ramp.meter");
		manager = man;
		statusPanel = new MeterStatusPanel(session, manager);
		summary = manager.createStyleSummary();
		add(statusPanel, BorderLayout.NORTH);
		add(summary, BorderLayout.CENTER);
	}

	/** Dispose of the ramp meter tab */
	@Override public void dispose() {
		super.dispose();
		manager.getSelectionModel().clearSelection();
		summary.dispose();
		statusPanel.dispose();
	}
}
