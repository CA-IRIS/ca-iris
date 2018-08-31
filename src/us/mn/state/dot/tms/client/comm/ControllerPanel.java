/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2016  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerIO;
import us.mn.state.dot.tms.CtrlCondition;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType;
import us.mn.state.dot.tms.client.proxy.ProxyTablePanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.ILabel;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.IO_TYPE;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * A panel for displaying a table of controllers.
 *
 * @author Douglas Lau
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class ControllerPanel extends ProxyTablePanel<Controller> {

	private static final Color COLOR_FILTERED = Color.BLUE;
	private static final Color COLOR_UNFILTERED = Color.BLACK;

	/**
	 * Set the color of the filter label when filtered.
	 */
	private void indicateFiltered(boolean filtered, ILabel lbl) {
		lbl.setForeground(filtered ? COLOR_FILTERED : COLOR_UNFILTERED);
	}

	/**
	 * clear filters when the user clicks create
	 */
	@Override
	protected void createObject() {
		super.createObject();
		clearFilters();
	}

	private void clearFilters() {
		cond_cbx.setSelectedItem(null);
		comm_cbx.setSelectedItem(null);
		dev_cbx.setSelectedItem(null);
		dev_type_cbx.setSelectedItem(null);
	}

	/** Condition filter label */
	private final ILabel cond_lbl = new ILabel(
		"controller.condition.filter");

	/** Condition filter action */
	private final IAction cond_act = new IAction(
		"controller.condition")
	{
		protected void doActionPerformed(ActionEvent e) {
			Object v = cond_cbx.getSelectedItem();
			if (v instanceof CtrlCondition)
				setCondition((CtrlCondition) v);
			else
				setCondition(null);
		}
	};

	/** Condition combobox */
	private final JComboBox<CtrlCondition> cond_cbx =
		new JComboBox<CtrlCondition>(CtrlCondition.values_with_null());

	/** Comm filter label */
	private final ILabel comm_lbl = new ILabel("controller.comm.filter");

	/** Comm filter action */
	private final IAction comm_act = new IAction("controller.comm") {
		protected void doActionPerformed(ActionEvent e) {
			Object v = comm_cbx.getSelectedItem();
			if (v instanceof CommState)
				setCommState((CommState)v);
			else
				setCommState(null);
		}
	};

	/** Comm state combo box */
	private final JComboBox<CommState> comm_cbx =
		new JComboBox<CommState>(CommState.values_with_null());

	/** label for the dev type filter */
	private final ILabel dev_type_label = new ILabel(
		"device.type.filter");

	/** device type filter action */
	private Action dev_type_act = new IAction(
		"device.type") {
		protected void doActionPerformed(ActionEvent e) {
			Object v = dev_type_cbx.getSelectedItem();
			if (v instanceof DeviceType)
				setDeviceType((DeviceType) v);
			else
				setDeviceType(null);
		}
	};

	/** device type filter combobox (all DeviceTypes and null is default) */
	private final JComboBox<DeviceType> dev_type_cbx =
		new JComboBox<DeviceType>(IO_TYPE.toArray(new DeviceType[0]));

	/** a string to search for and use to filter Controller IO names */
	private final ILabel dev_label = new ILabel(
		"device.filter");

	/**
	 * Handles selected items from the dev_cbx drop down.
	 */
	private ActionListener dev_act = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("comboBoxChanged")
				&& !updatingDeviceList) {
				String item = (String)
					dev_cbx.getSelectedItem();
				setDeviceSearch(item);
				dev_cbx.getEditor().setItem(item);
				if (item == null || item.isEmpty())
					updateDeviceList();
			}
		}
	};

	/**
	 * Handles search text in the dev_cbx
	 */
	private KeyListener dev_key_act = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case VK_UP:
			case VK_DOWN:
			case VK_LEFT:
			case VK_RIGHT:
			case VK_ENTER:
				break;
			default:
				dev_cbx.showPopup();
				String s = (String)
					dev_cbx.getEditor().getItem();
				setDeviceSearch(s);
				updateDeviceList();
			}
		}
	};

	/** a text field to hold the Controller IO name search */
	private final JComboBox<ControllerIO> dev_cbx;

	/** Create a new controller panel */
	public ControllerPanel(Session s) {
		super(new ControllerTableModel(s));
		dev_cbx = new JComboBox(((ControllerTableModel) model)
			.getMatchedDevices().toArray());
	}

	/** Initialize the panel */
	@Override
	public void initialize() {
		super.initialize();
		cond_cbx.setAction(cond_act);
		comm_cbx.setRenderer(new CommListRenderer());
		comm_cbx.setAction(comm_act);
		dev_type_cbx.setAction(dev_type_act);
		dev_cbx.setEditable(true);
		dev_cbx.addActionListener(dev_act);
		dev_cbx.getEditor().getEditorComponent().addKeyListener(
			dev_key_act);
	}

	private boolean updatingDeviceList = false;

	/**
	 * updates the the device list in dev_cbx
	 */
	private void updateDeviceList() {
		updatingDeviceList = true;
		boolean visibility = dev_cbx.isPopupVisible();
		dev_cbx.setPopupVisible(false);
		String s = (String) dev_cbx.getEditor().getItem();
		dev_cbx.removeAllItems();
		dev_cbx.setModel(new DefaultComboBoxModel((
			(ControllerTableModel) model)
			.getMatchedDevices().toArray()));
		dev_cbx.getEditor().setItem(s);
		dev_cbx.setPopupVisible(visibility);
		updatingDeviceList = false;
	}

	/** Add create/delete widgets to the button panel */
	@Override
	protected void addCreateDeleteWidgets(GroupLayout.SequentialGroup hg,
		GroupLayout.ParallelGroup vg)
	{
		hg.addComponent(cond_lbl);
		vg.addComponent(cond_lbl);
		hg.addGap(UI.hgap);
		hg.addComponent(cond_cbx);
		vg.addComponent(cond_cbx);
		hg.addGap(UI.hgap);
		hg.addComponent(comm_lbl);
		vg.addComponent(comm_lbl);
		hg.addGap(UI.hgap);
		hg.addComponent(comm_cbx);
		vg.addComponent(comm_cbx);
		hg.addGap(UI.hgap);
		hg.addComponent(dev_type_label);
		vg.addComponent(dev_type_label);
		hg.addGap(UI.hgap);
		hg.addComponent(dev_type_cbx);
		vg.addComponent(dev_type_cbx);
		hg.addGap(UI.hgap);
		hg.addComponent(dev_label);
		vg.addComponent(dev_label);
		hg.addGap(UI.hgap);
		hg.addComponent(dev_cbx);
		vg.addComponent(dev_cbx);
		hg.addGap(UI.hgap);
		super.addCreateDeleteWidgets(hg, vg);
	}

	/** Set comm link filter */
	public void setCommLink(CommLink cl) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setCommLink(cl);
			updateSortFilter();
		}
	}

	/** Set condition filter */
	private void setCondition(CtrlCondition c) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setCondition(c);
			updateSortFilter();
			indicateFiltered(c != null, cond_lbl);
		}
	}

	/** Set comm state filter */
	private void setCommState(CommState cs) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setCommState(cs);
			updateSortFilter();
			indicateFiltered(cs != null, comm_lbl);
		}
	}

	/** set the device type filter */
	private void setDeviceType(DeviceType d) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setDeviceType(d);
			updateSortFilter();
			updateDeviceList();
			indicateFiltered(d != null, dev_type_label);
		}
	}

	/** set the device name search string */
	private void setDeviceSearch(String s) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setDeviceSearch(s);
			updateSortFilter();
			indicateFiltered(s != null && !s.isEmpty(),
				dev_label);
		}
	}
}
