/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.roads;

import java.util.List;
import javax.swing.DefaultListModel;
import us.mn.state.dot.tms.R_Node;

/**
 * A list model for r_node models.
 *
 * @author Douglas Lau
 */
public class R_NodeListModel extends DefaultListModel<R_NodeModel> {

	/** Create a new r_node list model */
	public R_NodeListModel() {
		// nothing to do
	}

	/** Create a new r_node list model */
	public R_NodeListModel(List<R_NodeModel> n_list) {
		for (R_NodeModel r: n_list)
			addElement(r);
	}

	/** Update the list item for the specified r_node */
	public void updateItem(R_Node n) {
		int row = getRow(n);
		if (row >= 0)
			fireContentsChanged(this, row, row);
	}

	/** Get the r_node at the specified row */
	public R_Node getProxy(int row) {
		R_NodeModel m = get(row);
		return (m != null) ? m.r_node : null;
	}

	/** Get the row for the specified r_node */
	public int getRow(R_Node n) {
		for (int i = 0; i < getSize(); i++) {
			R_NodeModel m = get(i);
			if (m.r_node == n)
				return i;
		}
		return -1;
	}
}
