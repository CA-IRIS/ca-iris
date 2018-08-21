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

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerIO;
import us.mn.state.dot.tms.CtrlCondition;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.proxy.ProxyTablePanel;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.ILabel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
				setCommState((CommState) v);
			else
				setCommState(null);
		}
	};

	/** Comm state combo box */
	private final JComboBox<CommState> comm_cbx =
		new JComboBox<CommState>(CommState.values_with_null());

	private final ILabel dev_name_label = new ILabel(
		"controller.dev.filter");

	private final JTextField dev_name_txt = new JTextField(16);

	private final SonarState state;

	/** Create a new controller panel */
	public ControllerPanel(Session s) {
		super(new ControllerTableModel(s));
		state = s.getSonarState();
	}

	/** Initialize the panel */
	@Override
	public void initialize() {
		super.initialize();
		cond_cbx.setAction(cond_act);
		comm_cbx.setRenderer(new CommListRenderer());
		comm_cbx.setAction(comm_act);
		dev_name_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String v = dev_name_txt.getText();
				if (v != null)
					setDevSearch(v);
				else
					setDevSearch(null);
			}
		});
	}

	/** Add create/delete widgets to the button panel */
	@Override
	protected void addCreateDeleteWidgets(
		GroupLayout.SequentialGroup hg,
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
			ControllerTableModel mdl =
				(ControllerTableModel) model;
			mdl.setCommLink(cl);
			updateSortFilter();
		}
	}

	/** Set condition filter */
	private void setCondition(CtrlCondition c) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl =
				(ControllerTableModel) model;
			mdl.setCondition(c);
			updateSortFilter();
		}
	}

	/** Set comm state filter */
	private void setCommState(CommState cs) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl =
				(ControllerTableModel) model;
			mdl.setCommState(cs);
			updateSortFilter();
		}
	}

	private void setDevSearch(String s) {
		if (model instanceof ControllerTableModel) {
			ControllerTableModel mdl =
				(ControllerTableModel) model;
			mdl.setDevSearch(getMatchingControllers(s));
			updateSortFilter();
		}
	}

	/**
	 * @return a set of controller names for any attached io device for
	 * which devSearch matches any part of, ignoring case. If the input is
	 * null or empty, the output is null.
	 */
	Set<String> getMatchingControllers(String devSearch) {
		Set<String> matched = null;
		if (devSearch != null && !devSearch.isEmpty()) {
			matched = new HashSet<String>();
			matched.addAll(collectMatchingNames(devSearch,
				state.getAlarms()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getCamCache().getCameras()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getDetCache().getDetectors()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getDmsCache().getDMSs()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getGateArms()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getLaneMarkings()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getLcsCache().getLCSIndications()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getRampMeters()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getBeacons()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getWeatherSensorsCache()
					.getWeatherSensors()));
			matched.addAll(collectMatchingNames(devSearch,
				state.getTagReaders()));
		}
		return matched;
	}

	private Set<String> collectMatchingNames(
		String devSearch, TypeCache devices)
	{
		Set<String> matched = new HashSet<String>();
		Iterator<ControllerIO> i = devices.iterator();
		while (i.hasNext()) {
			ControllerIO cio = i.next();
			if (cio.getName() != null
				&& cio.getController() != null
				&& cio.getController().getName() != null
				&& cio.getName().toLowerCase().contains(
					devSearch.toLowerCase()))
					matched.add(cio.getController()
						.getName());
		}
		return matched;
	}
}
