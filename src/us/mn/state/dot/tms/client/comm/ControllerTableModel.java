/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.*;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;

import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Beacon_Verify;

/**
 * Table model for controllers.
 *
 * @author Douglas Lau
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class ControllerTableModel extends ProxyTableModel<Controller> {

	/** Get a controller comm state */
	static private CommState getCommState(Controller c) {
		if (ControllerHelper.isFailed(c))
			return CommState.FAILED;
		else if (ControllerHelper.isActive(c))
			return CommState.OK;
		else
			return CommState.INACTIVE;
	}

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<Controller>> createColumns() {
		ArrayList<ProxyColumn<Controller>> cols =
			new ArrayList<ProxyColumn<Controller>>(8);
		cols.add(new ProxyColumn<Controller>("comm.link", 100) {
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
		cols.add(new ProxyColumn<Controller>("controller.condition",100,
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
				JComboBox<CtrlCondition> cbx = new JComboBox
					<CtrlCondition>(CtrlCondition.values());
				return new DefaultCellEditor(cbx);
			}
		});
		cols.add(new ProxyColumn<Controller>("controller.comm", 44,
			CommState.class)
		{
			public Object getValueAt(Controller c) {
				return getCommState(c);
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
	private CommLink comm_link = null;

	/** Set the comm link filter */
	public void setCommLink(CommLink cl) {
		comm_link = cl;
	}

	/** Condition to filter controllers */
	private CtrlCondition condition = null;

	/** Set the condition filter */
	public void setCondition(CtrlCondition c) {
		condition = c;
	}

	/** Comm state filter */
	private CommState comm_state = null;

	/** Set the comm state filter */
	public void setCommState(CommState cs) {
		comm_state = cs;
	}

	/**
	 * A set of Controller names that match the current dev_type (or any
	 * if null) and have a ControllerIO that match the current dev_search
	 * String (or any if null or empty). If dev_type and dev_search are both
	 * null (and empty in the case of dev_search, then matched_controllers
	 * will also be null.
	 */
	private Set<String> matched_controllers = null;

	/**
	 * A list of Device names that match the same criteria as
	 * matched_controllers, except that in the case no filter is applied,
	 * it is not null as matched_controllers is.
	 */
	private List<String> devices = null;


	/** a device type to filter (null means match all types) */
	private DeviceType dev_type = null;

	/** set the device type filter */
	public void setDeviceType(DeviceType d) {
		dev_type = d;
		findDevices();
	}

	/** a String to match against any part of any Controller IO name to
	 * filter attached Controller (null or empty means match all names).
	 */
	private String dev_search = null;

	/** Set the ControllerIO name filter String */
	public void setDeviceSearch(String s) {
		dev_search = s.trim();
		findDevices();
	}

	public String getDevSearch() {
		return dev_search;
	}

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

	/**
	 * container of all TypeCaches of Devices
	 */
	private final SonarState state;

	/** Create a new controller table model */
	public ControllerTableModel(Session s) {
		super(s, s.getSonarState().getConCache().getControllers(),
		      true,	/* has_properties */
		      true,	/* has_create_delete */
		      false);	/* has_name */
		state = s.getSonarState();
		findDevices();
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
		if (isFiltered())
			sorter.setRowFilter(createFilter());
		return sorter;
	}

	/** Check if rows are filtered */
	private boolean isFiltered() {
		return (comm_link != null)
		    || (condition != null)
		    || (comm_state != null)
			|| (matched_controllers != null);
	}

	/** Create a row filter */
	private RowFilter<ProxyTableModel<Controller>, Integer> createFilter() {
		return new RowFilter<ProxyTableModel<Controller>, Integer>() {
			public boolean include(RowFilter.Entry<? extends
				ProxyTableModel<Controller>, ? extends Integer>
				entry)
			{
				int i = entry.getIdentifier();
				Controller c = getRowProxy(i);
				return (c != null)
				    && isMatchingLink(c)
				    && isMatchingCondition(c)
				    && isMatchingCommState(c)
					&& isMatchingDevice(c);
			}
		};
	}

	/** Check if comm link matches filter */
	private boolean isMatchingLink(Controller c) {
		return (comm_link == null)
		    || (comm_link == c.getCommLink());
	}

	/** Check if condition matches filter */
	private boolean isMatchingCondition(Controller c) {
		return (condition == null)
		    || (condition.ordinal() == c.getCondition());
	}

	/** Check if comm state matches filter */
	private boolean isMatchingCommState(Controller c) {
		return (comm_state == null)
		    || (comm_state == getCommState(c));
	}

	/**
	 *
	 * @return true if the Controller has a ControllerIO that matches
	 * that dev_type or dev_search filters or if we are filtering
	 * neither dev_type nor dev_search
	 */
	private boolean isMatchingDevice(Controller c) {
		boolean matched = false;
		// null matched_controllers means do not filter
		if (matched_controllers == null
			|| matched_controllers.contains(c.getName()))
			matched = true;
		return matched;
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

	public List<String> getMatchedDevices() {
		return devices;
	}

	/**
	 * If either dev_type or dev_search is set for filtering, then
	 * findDevices will build the set of names of matched_controllers
	 * and build the list of devices for which the filters are satisfied.
	 */
	private void findDevices() {
		matched_controllers = dev_type != null
			|| dev_search != null && !dev_search.isEmpty() ?
			new HashSet<String>() : null;
		devices = new ArrayList<String>();
		Iterator<ControllerIO> iter = dev_type == null ?
			new DeviceIterator(state)
			: new DeviceIterator(state, dev_type);
		// always include the empty reset sentinel value
		devices.add("");
		// Unfortunately, linear search is our only option, because
		// we are only given devices via the iterator. Tested with
		// 10K devices, and it was okay.
		while (iter.hasNext()) {
			ControllerIO cio = iter.next();
			if (cio != null
				&& cio.getController() != null
				&& cio.getController().getName() != null
				&& cio.getName() != null
				&& beaconVerifyMatch(cio)
				&& nameMatch(cio)) {
				devices.add(cio.getName());
				if (matched_controllers != null)
					matched_controllers.add(
						cio.getController().getName());
			}
		}
		Collections.sort(devices);
	}

	/**
	 *
	 * @return true if the device search is not set or the device name
	 * contains the search string
	 */
	private boolean nameMatch(ControllerIO cio) {
		return dev_search == null
			|| dev_search.isEmpty()
			|| cio.getName().toLowerCase().contains(
			dev_search.toLowerCase());
	}

	/**
	 * @return true if the device type is not a beacon verify or has a
	 * verify pin
	 */
	private boolean beaconVerifyMatch(ControllerIO cio) {
		return dev_type != Beacon_Verify
				|| ((Beacon) cio).getVerifyPin() != null;
	}
}
