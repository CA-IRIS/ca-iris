/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.client.camera;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import static us.mn.state.dot.tms.client.widget.SwingRunner.runSwing;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraPreset;
import us.mn.state.dot.tms.CameraPresetHelper;
import us.mn.state.dot.tms.PresetAlias;
import us.mn.state.dot.tms.PresetAliasName;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;

/**
 * Table model for camera preset aliases.
 *
 * @author Travis Swanston
 */
public class PresetAliasMappingModel extends AbstractTableModel {

	/** User session */
	private final Session session;

	/** Proxy type cache */
	private final TypeCache<PresetAlias> cache;

	/** Proxy columns */
	private final ArrayList<ProxyColumn<PresetAlias>> columns;

	/** Camera to assign presets */
	private final Camera camera;

	/** Array of all presets */
	private final PresetAlias[] proxies =
		new PresetAlias[PresetAliasName.size];

	/** Listener for SONAR proxy events */
	private final ProxyListener<PresetAlias> proxy_listener =
		new ProxyListener<PresetAlias>()
	{
		public void proxyAdded(final PresetAlias proxy) {
			// Don't hog the SONAR TaskProcessor thread
			runSwing(new Runnable() {
				public void run() {
					proxyAddedSwing(proxy);
				}
			});
		}
		public void proxyRemoved(final PresetAlias proxy) {
			// Don't hog the SONAR TaskProcessor thread
			runSwing(new Runnable() {
				public void run() {
					proxyRemovedSwing(proxy);
				}
			});
		}
		public void proxyChanged(final PresetAlias proxy,
			final String attrib)
		{
			// Don't hog the SONAR TaskProcessor thread
			runSwing(new Runnable() {
				public void run() {
					proxyChangedSwing(proxy, attrib);
				}
			});
		}
		public void enumerationComplete() {
			// Nothing to do
		}
	};

	/** Create a new camera alias mapping table model */
	public PresetAliasMappingModel(Session s, Camera c) {
		session = s;
		cache = s.getSonarState().getCamCache().getPresetAliases();
		camera = c;
		columns = createColumns();
	}

	/** Initialize the proxy table model */
	public void initialize() {
		cache.addProxyListener(proxy_listener);
	}

	/** Dispose of the proxy table model */
	public void dispose() {
		cache.removeProxyListener(proxy_listener);
	}

	/** Column for preset alias */
	private final ProxyColumn<PresetAlias> col_alias =
		new ProxyColumn<PresetAlias>("camera.preset.alias", 60)
	{
		public Object getValueAt(PresetAlias pa) {
			return pa.getPresetAliasName();
		}
	};

	/** Column for enabled */
	private final ProxyColumn<PresetAlias> col_enabled =
		new ProxyColumn<PresetAlias>("camera.preset.enabled", 60,
		                             Boolean.class)
	{
		public Object getValueAt(PresetAlias pa) {
			return pa != null;
		}
		public boolean isEditable(PresetAlias pa) {
			return ((pa == null) && canAdd())
			    || ((pa != null) && canRemove(pa));
		}
	};

	/** Column for mapped preset number */
	private final ProxyColumn<PresetAlias> col_number =
		new ProxyColumn<PresetAlias>("camera.preset.number", 60,
		                             Integer.class)
	{
		public Object getValueAt(PresetAlias pa) {
			return pa.getPresetNum();
		}
		public boolean isEditable(PresetAlias pa) {
			return canUpdate(pa, "preset_num");
		}
		public void setValueAt(PresetAlias pa, Object value) {
			if (value instanceof Integer) {
				int pn = ((Integer)value).intValue();
				pa.setPresetNum(pn);
			}
		}
		protected TableCellEditor createCellEditor() {
			JComboBox combo = new JComboBox(
				CameraPresetHelper.getAllowedPresets());
			return new DefaultCellEditor(combo);
		}

	};

	/** Create the columns in the model */
	private ArrayList<ProxyColumn<PresetAlias>> createColumns() {
		ArrayList<ProxyColumn<PresetAlias>> cols =
			new ArrayList<ProxyColumn<PresetAlias>>(3);
		cols.add(col_alias);
		cols.add(col_enabled);
		cols.add(col_number);
		return cols;
	}

	/** Get the count of columns in the table */
	@Override
	public int getColumnCount() {
		return columns.size();
	}

	/** Get the proxy column at the given column index */
	public ProxyColumn<PresetAlias> getProxyColumn(int col) {
		return (col >= 0 && col < columns.size())
		      ? columns.get(col)
		      : null;
	}

	/** Get the class of the specified column */
	@Override
	public Class getColumnClass(int col) {
		ProxyColumn pc = getProxyColumn(col);
		return (pc != null) ? pc.getColumnClass() : null;
	}

	/** Get the value at the specified cell */
	@Override
	public Object getValueAt(int row, int col) {
		ProxyColumn pc = getProxyColumn(col);
		if (pc == col_alias)
			return PresetAliasName.fromOrdinal(row).alias;
		else if (pc != null) {
			PresetAlias proxy = getRowProxy(row);
			if (proxy != null)
				return pc.getValueAt(proxy);
		}
		return null;
	}

	/** Check if the specified cell is editable */
	@Override
	public boolean isCellEditable(int row, int col) {
		ProxyColumn pc = getProxyColumn(col);
		return pc != null && pc.isEditable(getRowProxy(row));
	}

	/** Set the value at the specified cell */
	@Override
	public void setValueAt(Object value, int row, int col) {
		ProxyColumn pc = getProxyColumn(col);
		if (pc == col_enabled) {
			if (value instanceof Boolean)
				setAliasEnabled(row, (Boolean)value);
		} else if (pc != null)
			pc.setValueAt(getRowProxy(row), value);
	}

	/** Set alias enabled value */
	private void setAliasEnabled(int row, boolean e) {
		if (e)
			createPresetAlias(row);
		else
			destroyPresetAlias(row);
	}

	/** Create a new camera preset.
	 * @param pn Preset number */
	private void createPresetAlias(int pa) {
		String name = createUniqueName();
		if (canAdd(name)) {
			HashMap<String, Object> attrs =
				new HashMap<String, Object>();
			attrs.put("camera", camera);
			attrs.put("alias", new Integer(pa));
			attrs.put("preset_num",
				new Integer(CameraPreset.MIN_PRESET));
			cache.createObject(name, attrs);
		}
	}

	/** Create a unique preset name */
	private String createUniqueName() {
		for (int uid = 1; uid <= 99999; uid++) {
			String n = "ALI_" + uid;
			if (cache.lookupObject(n) == null)
				return n;
		}
		assert false;
		return null;
	}

	/** Delete the specified row */
	private void destroyPresetAlias(int row) {
		PresetAlias proxy = getRowProxy(row);
		if (canRemove(proxy))
			proxy.destroy();
	}

	/** Get the count of rows in the table */
	@Override
	public int getRowCount() {
		return PresetAliasName.size;
	}

	/** Create the table column model */
	public TableColumnModel createColumnModel() {
		TableColumnModel m = new DefaultTableColumnModel();
		for (int i = 0; i < columns.size(); ++i)
			columns.get(i).addColumn(m, i);
		return m;
	}

	/** Fire an update event */
	private void fireUpdate(final int row) {
		if (row >= 0)
			fireTableRowsUpdated(row, row);
	}

	/** Add a new proxy to the table model */
	private void proxyAddedSwing(PresetAlias proxy) {
		fireUpdate(doProxyAdded(proxy));
	}

	/** Add a new proxy to the table model */
	private int doProxyAdded(PresetAlias proxy) {
		if (proxy.getCamera() == camera) {
			int row = proxy.getPresetAliasName();
			if (row >= 0 && row < getRowCount()) {
				proxies[row] = proxy;
				return row;
			}
		}
		return -1;
	}

	/** Remove a proxy from the table model */
	private void proxyRemovedSwing(PresetAlias proxy) {
		fireUpdate(doProxyRemoved(proxy));
	}

	/** Remove a proxy from the table model */
	private int doProxyRemoved(PresetAlias proxy) {
		int row = getRow(proxy);
		if (row >= 0)
			proxies[row] = null;
		return row;
	}

	/** Change a proxy in the table model */
	private void proxyChangedSwing(PresetAlias proxy, String attrib) {
		fireUpdate(getRow(proxy));
	}

	/** Get the proxy at the specified row */
	public PresetAlias getRowProxy(int row) {
		return (row >= 0 && row < getRowCount()) ? proxies[row] : null;
	}

	/**
	 * Get the row for the specified proxy.
	 * @param proxy Proxy object.
	 * @return Row number in table, or -1.
	 */
	private int getRow(PresetAlias proxy) {
		for (int row = 0; row < proxies.length; row++) {
			if (proxies[row] == proxy)
				return row;
		}
		return -1;
	}

	/** Check if the user can add a proxy */
	public boolean canAdd(String n) {
		String tname = getSonarType();
		return (tname != null) ? session.canAdd(tname, n) : false;
	}

	/** Check if the user can add a proxy */
	public boolean canAdd() {
		return canAdd("oname");
	}

	/** Get the SONAR type name */
	private String getSonarType() {
		return PresetAlias.SONAR_TYPE;
	}

	/** Check if the user can update a proxy */
	public boolean canUpdate(PresetAlias proxy) {
		return session.canUpdate(proxy);
	}

	/** Check if the user can update a proxy */
	public boolean canUpdate(PresetAlias proxy, String aname) {
		return session.canUpdate(proxy, aname);
	}

	/** Check if the user can remove a proxy */
	public boolean canRemove(PresetAlias proxy) {
		return session.canRemove(proxy);
	}

}
