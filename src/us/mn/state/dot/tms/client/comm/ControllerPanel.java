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
import us.mn.state.dot.tms.CtrlCondition;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyTablePanel;
import us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.ILabel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static us.mn.state.dot.tms.client.comm.ControllerIOModel.IO_TYPE;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * A panel for displaying a table of controllers.
 *
 * @author Douglas Lau
 */
public class ControllerPanel extends ProxyTablePanel<Controller> {

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
		"controller.dev.type.filter");

	/** device type filter action */
	private Action dev_type_act = new IAction(
		"controller.dev.type") {
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
	private final ILabel dev_name_label = new ILabel(
		"controller.dev.name.search");

	/** a text field to hold the Controller IO search text */
	private final JTextField dev_name_txt = new JTextField(16);

	/** Create a new controller panel */
	public ControllerPanel(Session s) {
		super(new ControllerTableModel(s));
	}

	/** Initialize the panel */
	@Override
	public void initialize() {
		super.initialize();
		cond_cbx.setAction(cond_act);
		comm_cbx.setRenderer(new CommListRenderer());
		comm_cbx.setAction(comm_act);
		dev_type_cbx.setAction(dev_type_act);
		dev_name_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setDevSearch(dev_name_txt.getText());
			}
		});
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
		hg.addComponent(dev_name_label);
		vg.addComponent(dev_name_label);
		hg.addGap(UI.hgap);
		hg.addComponent(dev_name_txt);
		vg.addComponent(dev_name_txt);
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
		}
	}

	/** Set comm state filter */
	private void setCommState(CommState cs) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setCommState(cs);
			updateSortFilter();
		}
	}

	/** set the device type filter */
	private void setDeviceType(DeviceType d) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setDeviceType(d);
			updateSortFilter();
		}
	}

	/** set the device name search string */
	private void setDevSearch(String s) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl = (ControllerTableModel)model;
			mdl.setDevSearch(s);
			updateSortFilter();
		}
	}
}
