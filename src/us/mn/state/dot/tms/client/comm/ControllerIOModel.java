/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
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
import java.util.LinkedList;
import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import us.mn.state.dot.sched.SwingRunner;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.tms.Alarm;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerIO;
import us.mn.state.dot.tms.Detector;
import us.mn.state.dot.tms.DeviceStyle;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.LaneMarking;
import us.mn.state.dot.tms.LaneUseIndication;
import us.mn.state.dot.tms.LCS;
import us.mn.state.dot.tms.LCSIndication;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.WarningSign;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;

/**
 * Special table model for Controller I/O pins.
 *
 * @author Douglas Lau
 */
public class ControllerIOModel extends AbstractTableModel {

	/** Count of columns in table model */
	static protected final int COLUMN_COUNT = 3;

	/** Pin column number */
	static protected final int COL_PIN = 0;

	/** Device type column number */
	static protected final int COL_TYPE = 1;

	/** Device column number */
	static protected final int COL_DEVICE = 2;

	/** Device types which can be associated with controller IO */
	protected enum DeviceType {
		Alarm, Camera, Detector, DMS, Lane_Marking, LCSIndication,
		Ramp_Meter, Warning_Sign, Weather_Sensor
	}

	/** Types of IO devices */
	static protected final LinkedList<DeviceType> IO_TYPE =
		new LinkedList<DeviceType>();
	static {
		IO_TYPE.add(null);
		IO_TYPE.add(DeviceType.Alarm);
		IO_TYPE.add(DeviceType.Camera);
		IO_TYPE.add(DeviceType.Detector);
		IO_TYPE.add(DeviceType.DMS);
		IO_TYPE.add(DeviceType.Lane_Marking);
		IO_TYPE.add(DeviceType.LCSIndication);
		IO_TYPE.add(DeviceType.Ramp_Meter);
		IO_TYPE.add(DeviceType.Warning_Sign);
		IO_TYPE.add(DeviceType.Weather_Sensor);
	}

	/** Get the type of the specified ControllerIO device */
	static protected DeviceType getType(ControllerIO cio) {
		if(cio instanceof Alarm)
			return DeviceType.Alarm;
		else if(cio instanceof Camera)
			return DeviceType.Camera;
		else if(cio instanceof Detector)
			return DeviceType.Detector;
		else if(cio instanceof DMS)
			return DeviceType.DMS;
		else if(cio instanceof LaneMarking)
			return DeviceType.Lane_Marking;
		else if(cio instanceof LCSIndication)
			return DeviceType.LCSIndication;
		else if(cio instanceof RampMeter)
			return DeviceType.Ramp_Meter;
		else if(cio instanceof WarningSign)
			return DeviceType.Warning_Sign;
		else if(cio instanceof WeatherSensor)
			return DeviceType.Weather_Sensor;
		else
			return null;
	}

	/** Compare two device types for equality */
	static protected boolean compareTypes(DeviceType t0, DeviceType t1) {
		if(t0 == null)
			return t1 == null;
		else
			return t0 == t1;
	}

	/** Controller object */
	protected final Controller controller;

	/** Login session */
	protected final Session session;

	/** SONAR state */
	protected final SonarState state;

	/** Array of ControllerIO assignments */
	protected final ControllerIO[] io;

	/** Array of ControllerIO device types */
	protected final DeviceType[] types;

	/** Available alarm model */
	protected final WrapperComboBoxModel a_model;

	/** Controller IO watcher for alarms */
	protected final ControllerIOWatcher<Alarm> a_watcher;

	/** Available camera model */
	protected final WrapperComboBoxModel c_model;

	/** Controller IO watcher for cameras */
	protected final ControllerIOWatcher<Camera> c_watcher;

	/** Available detector model */
	protected final WrapperComboBoxModel dt_model;

	/** Controller IO watcher for detectors */
	protected final ControllerIOWatcher<Detector> det_watcher;

	/** Available DMS model */
	protected final WrapperComboBoxModel dms_model;

	/** Controller IO watcher for DMSs */
	protected final ControllerIOWatcher<DMS> dms_watcher;

	/** Available lane markings model */
	protected final WrapperComboBoxModel lmark_model;

	/** Controller IO watcher for lane markings */
	protected final ControllerIOWatcher<LaneMarking> lmark_watcher;

	/** Available LCS indication model */
	protected final WrapperComboBoxModel lcsi_model;

	/** Controller IO watcher for LCS indications */
	protected final ControllerIOWatcher<LCSIndication> lcsi_watcher;

	/** Available ramp meter model */
	protected final WrapperComboBoxModel m_model;

	/** Controller IO watcher for ramp meters */
	protected final ControllerIOWatcher<RampMeter> m_watcher;

	/** Available warning sign model */
	protected final WrapperComboBoxModel w_model;

	/** Controller IO watcher for warning signs */
	protected final ControllerIOWatcher<WarningSign> w_watcher;

	/** Available weather sensors model */
	protected final WrapperComboBoxModel wsensor_model;

	/** Controller IO watcher for weather sensors */
	protected final ControllerIOWatcher<WeatherSensor> wsensor_watcher;

	/** Model for null device type */
	protected final ComboBoxModel no_model = new DefaultComboBoxModel();

	/** Device combo box */
	protected final JComboBox d_combo = new JComboBox();

	/** Create a new controller IO model */
	public ControllerIOModel(Session s, Controller c) {
		session = s;
		state = s.getSonarState();;
		controller = c;
		io = new ControllerIO[Controller.ALL_PINS];
		types = new DeviceType[Controller.ALL_PINS];
		d_combo.setRenderer(new DeviceComboRenderer());
		a_model = new WrapperComboBoxModel(state.getAvailableAlarms(),
			 true);
		final String st = DeviceStyle.NO_CONTROLLER.toString();
		c_model = new WrapperComboBoxModel(
			s.getCameraManager().getStyleModel(st), true);
		dt_model = new WrapperComboBoxModel(
			s.getDetectorManager().getStyleModel(st), true);
		dms_model = new WrapperComboBoxModel(
			s.getDMSManager().getStyleModel(st), true);
		lmark_model = new WrapperComboBoxModel(
			s.getLaneMarkingManager().getStyleModel(st), true);
		lcsi_model = new WrapperComboBoxModel(
			s.getLCSIManager().getStyleModel(st), true);
		m_model = new WrapperComboBoxModel(
			s.getMeterManager().getStyleModel(st), true);
		w_model = new WrapperComboBoxModel(
			s.getWarnManager().getStyleModel(st), true);
		wsensor_model = new WrapperComboBoxModel(
			s.getWeatherSensorManager().getStyleModel(st), true);
		a_watcher = new ControllerIOWatcher<Alarm>();
		c_watcher = new ControllerIOWatcher<Camera>();
		det_watcher = new ControllerIOWatcher<Detector>();
		dms_watcher = new ControllerIOWatcher<DMS>();
		lmark_watcher = new ControllerIOWatcher<LaneMarking>();
		lcsi_watcher = new ControllerIOWatcher<LCSIndication>();
		m_watcher = new ControllerIOWatcher<RampMeter>();
		w_watcher = new ControllerIOWatcher<WarningSign>();
		wsensor_watcher = new ControllerIOWatcher<WeatherSensor>();
	}

	/** Initialize the model */
	public void initialize() {
		state.getAlarms().addProxyListener(a_watcher);
		state.getCamCache().getCameras().addProxyListener(c_watcher);
		state.getDetCache().getDetectors().addProxyListener(
			det_watcher);
		state.getDmsCache().getDMSs().addProxyListener(dms_watcher);
		state.getLaneMarkings().addProxyListener(lmark_watcher);
		state.getLcsCache().getLCSIndications().addProxyListener(
			lcsi_watcher);
		state.getRampMeters().addProxyListener(m_watcher);
		state.getWarningSigns().addProxyListener(w_watcher);
		state.getWeatherSensors().addProxyListener(wsensor_watcher);
	}

	/** Dispose of the model */
	public void dispose() {
		state.getAlarms().removeProxyListener(a_watcher);
		state.getCamCache().getCameras().removeProxyListener(c_watcher);
		state.getDetCache().getDetectors().removeProxyListener(
			det_watcher);
		state.getDmsCache().getDMSs().removeProxyListener(dms_watcher);
		state.getLaneMarkings().removeProxyListener(lmark_watcher);
		state.getLcsCache().getLCSIndications().removeProxyListener(
			lcsi_watcher);
		state.getRampMeters().removeProxyListener(m_watcher);
		state.getWarningSigns().removeProxyListener(w_watcher);
		state.getWeatherSensors().removeProxyListener(wsensor_watcher);
	}

	/** Get the count of columns in the table */
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	/** Get the count of rows in the table */
	public int getRowCount() {
		return io.length - 1;
	}

	/** Get the value at the specified cell */
	public Object getValueAt(int row, int column) {
		int pin = row + 1;
		switch(column) {
		case COL_PIN:
			return pin;
		case COL_TYPE:
			return types[pin];
		case COL_DEVICE:
			return io[pin];
		default:
			return null;
		}
	}

	/** Is the specified cell editable? */
	public boolean isCellEditable(int row, int col) {
		return col != COL_PIN && canUpdateIO();
	}

	/** Check if the user can update device IO */
	protected boolean canUpdateIO() {
		return canUpdateIO(Alarm.SONAR_TYPE) &&
		       canUpdateIO(Camera.SONAR_TYPE) &&
		       canUpdateIO(Detector.SONAR_TYPE) &&
		       canUpdateIO(DMS.SONAR_TYPE) &&
		       canUpdateIO(LaneMarking.SONAR_TYPE) &&
		       canUpdateIO(LCSIndication.SONAR_TYPE) &&
		       canUpdateIO(RampMeter.SONAR_TYPE) &&
		       canUpdateIO(WarningSign.SONAR_TYPE) &&
		       canUpdateIO(WeatherSensor.SONAR_TYPE);
	}

	/** Check if the user can update one device IO */
	protected boolean canUpdateIO(String tname) {
		return session.canUpdate(tname, "pin") &&
		       session.canUpdate(tname, "controller");
	}

	/** Set the value of one cell in the table */
	public void setValueAt(Object value, int row, int column) {
		int pin = row + 1;
		switch(column) {
		case COL_TYPE:
			setDeviceType(pin, (DeviceType)value);
			break;
		case COL_DEVICE:
			setDevice(pin, value);
			break;
		}
	}

	/** Set the device type */
	protected void setDeviceType(int pin, DeviceType io_type) {
		int row = pin - 1;
		if(io_type != types[pin]) {
			ControllerIO cio = io[pin];
			if(cio != null)
				cio.setController(null);
			types[pin] = io_type;
			io[pin] = null;
		}
	}

	/** Set the device */
	protected void setDevice(int pin, Object value) {
		clearDevice(pin);
		if(value instanceof ControllerIO) {
			ControllerIO cio = (ControllerIO)value;
			cio.setPin(pin);
			cio.setController(controller);
		}
	}

	/** Clear the device at the specified pin */
	protected void clearDevice(int pin) {
		ControllerIO cio = io[pin];
		if(cio != null)
			cio.setController(null);
	}

	/** Create the pin column */
	protected TableColumn createPinColumn() {
		TableColumn c = new TableColumn(COL_PIN, 44);
		c.setHeaderValue("Pin");
		return c;
	}

	/** Create the type column */
	protected TableColumn createTypeColumn() {
		TableColumn c = new TableColumn(COL_TYPE, 100);
		c.setHeaderValue("Type");
		JComboBox combo = new JComboBox(IO_TYPE.toArray());
		c.setCellEditor(new DefaultCellEditor(combo));
		return c;
	}

	/** Create the device column */
	protected TableColumn createDeviceColumn() {
		TableColumn c = new TableColumn(COL_DEVICE, 140);
		c.setHeaderValue("Device");
		c.setCellEditor(new DeviceCellEditor());
		c.setCellRenderer(new DeviceCellRenderer());
		return c;
	}

	/** Get the device model for the given device type */
	protected ComboBoxModel getDeviceModel(DeviceType d) {
		if(d == null)
			return no_model;
		switch(d) {
		case Alarm:
			return a_model;
		case Camera:
			return c_model;
		case Detector:
			return dt_model;
		case DMS:
			return dms_model;
		case Lane_Marking:
			return lmark_model;
		case LCSIndication:
			return lcsi_model;
		case Ramp_Meter:
			return m_model;
		case Warning_Sign:
			return w_model;
		case Weather_Sensor:
			return wsensor_model;
		default:
			return no_model;
		}
	}

	/** Inner class for editing cells in the device column */
	protected class DeviceCellEditor extends AbstractCellEditor
		implements TableCellEditor
	{
		public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column)
		{
			int pin = row + 1;
			ComboBoxModel model = getDeviceModel(types[pin]);
			model.setSelectedItem(value);
			d_combo.setModel(new CellEditorComboBoxModel(this,
				model));
			return d_combo;
		}
		public Object getCellEditorValue() {
			return d_combo.getSelectedItem();
		}
	}

	/** Inner class for rendering cells in the device column */
	protected class DeviceCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			return super.getTableCellRendererComponent(table,
				getDeviceLabel(value), isSelected, hasFocus,
				row, column);
		}
	}

	/** Inner class for rendering combo editor in the device column */
	protected class DeviceComboRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus)
		{
			return super.getListCellRendererComponent(list,
				getDeviceLabel(value), index, isSelected,
				cellHasFocus);
		}
	}

	/** Get a device label (normally name) */
	protected Object getDeviceLabel(Object value) {
		if(value instanceof LCSIndication) {
			LCSIndication lcsi = (LCSIndication)value;
			LCS lcs = lcsi.getLcs();
			LaneUseIndication lui = LaneUseIndication.fromOrdinal(
				lcsi.getIndication());
			return lcs.getName() + " " + lui.description;
		} else
			return value;
	}

	/** Create the table column model */
	public TableColumnModel createColumnModel() {
		TableColumnModel m = new DefaultTableColumnModel();
		m.addColumn(createPinColumn());
		m.addColumn(createTypeColumn());
		m.addColumn(createDeviceColumn());
		return m;
	}

	/** Controller IO watcher */
	protected class ControllerIOWatcher<T extends ControllerIO>
		implements ProxyListener<T>
	{
		public void proxyAdded(ControllerIO p) {
			addIO(p);
		}
		public void enumerationComplete() { }
		public void proxyRemoved(ControllerIO p) {
			removeIO(p);
		}
		public void proxyChanged(ControllerIO p, final String a) {
			if("controller".equals(a) || "pin".equals(a)) {
				removeIO(p);
				addIO(p);
			}
		}
	}

	/** Add an IO to a pin on the controller */
	protected void addIO(ControllerIO p) {
		if(p.getController() == controller) {
			int pin = p.getPin();
			if(pin > 0 && pin < io.length) {
				io[pin] = p;
				types[pin] = getType(p);
				final int row = pin - 1;
				SwingRunner.invoke(new Runnable() {
					public void run() {
						fireTableRowsUpdated(row, row);
					}
				});
			}
		}
	}

	/** Remove an IO from a pin on the controller */
	protected void removeIO(ControllerIO p) {
		for(int pin = 0; pin < io.length; pin++) {
			if(io[pin] == p) {
				io[pin] = null;
				types[pin] = null;
				final int row = pin - 1;
				SwingRunner.invoke(new Runnable() {
					public void run() {
						fireTableRowsUpdated(row, row);
					}
				});
				return;
			}
		}
	}
}
