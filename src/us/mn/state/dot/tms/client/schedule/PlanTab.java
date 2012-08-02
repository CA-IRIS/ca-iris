/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.schedule;

import java.awt.BorderLayout;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.StyleSummary;

/**
 * Tab for managing action plans.
 *
 * @author Douglas Lau
 */
public class PlanTab extends MapTab {

	/** Plan manager */
	private final PlanManager manager;

	/** Plan dispatcher */
	private final PlanDispatcher dispatcher;

	/** Summary of plans of each status */
	private final StyleSummary<ActionPlan> summary;

	/** Create a new action plan tab */
  	public PlanTab(Session session, PlanManager m) {
		super("action.plan");
		manager = m;
		dispatcher = new PlanDispatcher(session, m);
		summary = manager.createStyleSummary();
		add(dispatcher, BorderLayout.NORTH);
		add(summary, BorderLayout.CENTER);
	}

	/** Get the tab number */
	public int getNumber() {
		return 6;
	}

	/** Dispose of the plan tab */
	public void dispose() {
		super.dispose();
		manager.getSelectionModel().clearSelection();
		dispatcher.dispose();
		summary.dispose();
	}
}
