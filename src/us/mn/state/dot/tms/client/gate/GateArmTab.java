/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.gate;

import java.awt.BorderLayout;
import us.mn.state.dot.tms.GateArmArray;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.StyleSummary;

/**
 * Tab for managing gate arms.
 *
 * @author Douglas Lau
 */
public class GateArmTab extends MapTab<GateArmArray> {

	/** Gate Arm dispatch panel */
	private final GateArmDispatcher dispatcher;

	/** Summary of gate arm arrays of each status */
	private final StyleSummary<GateArmArray> summary;

	/** Create a new gate arm tab */
  	public GateArmTab(Session session, GateArmArrayManager man) {
		super(man);
		dispatcher = new GateArmDispatcher(session, man);
		dispatcher.initialize();
		summary = man.createStyleSummary();
		add(dispatcher, BorderLayout.NORTH);
		add(summary, BorderLayout.CENTER);
	}

	/** Dispose of the tab */
	@Override
	public void dispose() {
		super.dispose();
		manager.getSelectionModel().clearSelection();
		summary.dispose();
		dispatcher.dispose();
	}

	/** Get the tab ID */
	@Override
	public String getTabId() {
		return "gate.arm";
	}
}
