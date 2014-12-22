/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.comm;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.CtrlCondition;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for controllers.
 *
 * @author Douglas Lau
 */
public class ControllerTableModel extends ProxyTableModel<Controller> {

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<Controller>> createColumns() {
		ArrayList<ProxyColumn<Controller>> cols =
			new ArrayList<ProxyColumn<Controller>>(8);
		cols.add(new ProxyColumn<Controller>("comm.link", 120) {
			public Object getValueAt(Controller c) {
				return c.getCommLink().getName();
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.drop", 54,
			Short.class)
		{
			public Object getValueAt(Controller c) {
				return c.getDrop();
			}
			public boolean isEditable(Controller c) {
				return canUpdate(c);
			}
			public void setValueAt(Controller c, Object value) {
				if (value instanceof Number)
					c.setDrop(((Number)value).shortValue());
			}
			protected TableCellEditor createCellEditor() {
				return new DropCellEditor();
			}
		});
		cols.add(new ProxyColumn<Controller>("location", 200) {
			public Object getValueAt(Controller c) {
				return GeoLocHelper.getDescription(
					c.getCabinet().getGeoLoc());
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.condition", 88,
			CtrlCondition.class)
		{
			public Object getValueAt(Controller c) {
				return CtrlCondition.fromOrdinal(
					c.getCondition());
			}
			public boolean isEditable(Controller c) {
				return canUpdate(c, "condition");
			}
			public void setValueAt(Controller c, Object value) {
				if (value instanceof CtrlCondition) {
					CtrlCondition cc = (CtrlCondition)value;
					c.setCondition(cc.ordinal());
				}
			}
			protected TableCellEditor createCellEditor() {
				return new DefaultCellEditor(new JComboBox(
					CtrlCondition.values()));
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.comm", 44,
			CommState.class)
		{
			public Object getValueAt(Controller c) {
				if (ControllerHelper.isFailed(c))
					return CommState.FAILED;
				else if (ControllerHelper.isActive(c))
					return CommState.OK;
				else
					return CommState.INACTIVE;
			}
			protected TableCellRenderer createCellRenderer() {
				return new CommCellRenderer();
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.status", 240) {
			public Object getValueAt(Controller c) {
				return c.getStatus();
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.fail", 240,
			Long.class)
		{
			public Object getValueAt(Controller c) {
				return c.getFailTime();
			}
			protected TableCellRenderer createCellRenderer() {
				return new TimeCellRenderer();
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.version", 120){
			public Object getValueAt(Controller c) {
				return c.getVersion();
			}
		});
		return cols;
	}

	/** Comm link to filter controllers */
	private CommLink comm_link;

	/** Get a proxy comparator */
	@Override
	protected Comparator<Controller> comparator() {
		return new Comparator<Controller>() {
			public int compare(Controller a, Controller b) {
				Short aa = Short.valueOf(a.getDrop());
				Short bb = Short.valueOf(b.getDrop());
				int c = aa.compareTo(bb);
				if (c == 0) {
					String an = a.getName();
					String bn = b.getName();
					return an.compareTo(bn);
				} else
					return c;
			}
		};
	}

	/** Create a new controller table model */
	public ControllerTableModel(Session s) {
		super(s, s.getSonarState().getConCache().getControllers(),
		      true,	/* has_properties */
		      true,	/* has_create_delete */
		      false);	/* has_name */
		comm_link = null;
	}

	/** Get the SONAR type name */
	@Override
	protected String getSonarType() {
		return Controller.SONAR_TYPE;
	}

	/** Create a properties form for one proxy */
	@Override
	protected ControllerForm createPropertiesForm(Controller proxy) {
		return new ControllerForm(session, proxy);
	}

	/** Get the visible row count */
	@Override
	public int getVisibleRowCount() {
		return 10;
	}

	/** Get the row height */
	@Override
	public int getRowHeight() {
		return 24;
	}

	/** Get a table row sorter */
	@Override
	public RowSorter<ProxyTableModel<Controller>> createSorter() {
		TableRowSorter<ProxyTableModel<Controller>> sorter =
			new TableRowSorter<ProxyTableModel<Controller>>(this);
		sorter.setSortsOnUpdates(true);
		LinkedList<RowSorter.SortKey> keys =
			new LinkedList<RowSorter.SortKey>();
		keys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(keys);
		return sorter;
	}

	/** Check if the user can add a controller */
	@Override
	public boolean canAdd() {
		return (comm_link != null) && super.canAdd();
	}

	/** Create a new controller */
	@Override
	public void createObject(String n) {
		String name = createUniqueName();
		if (name != null && comm_link != null)
			cache.createObject(name, createAttrs());
	}

	/** Create a unique controller name */
	private String createUniqueName() {
		for (int uid = 1; uid <= 99999; uid++) {
			String n = "ctl_" + uid;
			if (cache.lookupObject(n) == null)
				return n;
		}
		assert false;
		return null;
	}

	/** Create a mapping of attributes */
	private HashMap<String, Object> createAttrs() {
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		DropNumberModel m = new DropNumberModel(comm_link, cache, 1);
		attrs.put("comm_link", comm_link);
		attrs.put("drop_id", m.getNextAvailable());
		attrs.put("notes", "");
		return attrs;
	}

	/** Editor for drop addresses in a table cell */
	protected class DropCellEditor extends AbstractCellEditor
		implements TableCellEditor
	{
		protected final DropNumberModel model =
			new DropNumberModel(comm_link, cache, 1);
		protected final JSpinner spinner = new JSpinner(model);

		public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int col)
		{
			spinner.setValue(value);
			return spinner;
		}
		public Object getCellEditorValue() {
			return spinner.getValue();
		}
	}
}
