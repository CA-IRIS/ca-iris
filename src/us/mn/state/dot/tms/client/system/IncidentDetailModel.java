/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.system;

import java.util.ArrayList;
import us.mn.state.dot.tms.IncidentDetail;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel2;

/**
 * Table model for incident details.
 *
 * @author Douglas Lau
 */
public class IncidentDetailModel extends ProxyTableModel2<IncidentDetail> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<IncidentDetail>> createColumns() {
		ArrayList<ProxyColumn<IncidentDetail>> cols =
			new ArrayList<ProxyColumn<IncidentDetail>>(2);
		cols.add(new ProxyColumn<IncidentDetail>("device.name", 90) {
			public Object getValueAt(IncidentDetail dtl) {
				return dtl.getName();
			}
		});
		cols.add(new ProxyColumn<IncidentDetail>("device.description",
			200)
		{
			public Object getValueAt(IncidentDetail dtl) {
				return dtl.getDescription();
			}
			public boolean isEditable(IncidentDetail dtl) {
				return canUpdate(dtl);
			}
			public void setValueAt(IncidentDetail dtl,
				Object value)
			{
				dtl.setDescription(value.toString());
			}
		});
		return cols;
	}

	/** Create a new incident detail table model */
	public IncidentDetailModel(Session s) {
		super(s, s.getSonarState().getIncidentDetails(),
		      false,	/* has_properties */
		      true,	/* has_create_delete */
		      true);	/* has_name */
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return IncidentDetail.SONAR_TYPE;
	}

	/** Get the visible row count */
	@Override
	public int getVisibleRowCount() {
		return 12;
	}
}
