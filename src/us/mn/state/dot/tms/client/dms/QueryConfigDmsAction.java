/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016       California Department of Transportation
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

import java.awt.event.ActionEvent;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.widget.IAction;

/**
 * Action to query all selected DMS.
 *
 * @author Jacob Barde
 */
public class QueryConfigDmsAction extends IAction {

	/** DMS dispatcher */
	private final DMSDispatcher dispatcher;

	/** Create a new action to blank the selected DMS */
	public QueryConfigDmsAction(DMSDispatcher d) {
		super("dms.query.config");
		dispatcher = d;
	}

	/** Actually perform the action */
	protected void doActionPerformed(ActionEvent e) {
		dispatcher.queryConfigMessage();
	}
}
