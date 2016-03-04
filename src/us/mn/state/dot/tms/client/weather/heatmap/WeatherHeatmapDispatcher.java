/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       Southwest Research Institute
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
package us.mn.state.dot.tms.client.weather.heatmap;

import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyListModel;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.utils.I18N;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

import static us.mn.state.dot.tms.client.widget.SwingRunner.runSwing;

/**
 * Provides a GUI for interacting with weather sensor data
 *
 * @author Jacob Barde
 */
public class WeatherHeatmapDispatcher extends JPanel {

	/** User session */
	private final Session session;

	private final WeatherSensorManager manager;

	/** Client properties */
	private final Properties client_props;

	/** Selection model */
	private final ProxySelectionModel<WeatherSensor> sel_model;

	/** Selection listener */
	private final ProxySelectionListener sel_listener =
		new ProxySelectionListener()
		{
			public void selectionChanged() {
				selectWeatherSensor(sel_model.getSingleSelection());
			}
		};

	/** Cache of Camera proxy objects */
	private final TypeCache<WeatherSensor> cache;

	/** Proxy listener */
	private final ProxyListener<WeatherSensor> proxy_listener =
		new ProxyListener<WeatherSensor>()
		{
			public void proxyAdded(WeatherSensor proxy) {}
			public void enumerationComplete() {}
			public void proxyRemoved(WeatherSensor proxy) {}
			public void proxyChanged(WeatherSensor proxy, String a) {
				if (proxy != selected)
					return;
				if ((a == null) || ("opStatus".equals(a))) {
					final String stat = proxy.getOpStatus();
					runSwing(new Runnable() {
						public void run() {
							updateOpStatus(stat);
						}
					});
				}
			}
		};

	/** WeatherSensor list model */
//	private final ProxyListModel<WeatherSensor> model;

	/** WeatherSensor name label */
	private final JLabel name_lbl = IPanel.createValueLabel();

	/** WeatherSensor location label */
	private final JLabel location_lbl = IPanel.createValueLabel();

	/** Displays the current device op status */
	private final JLabel op_status_lbl = IPanel.createValueLabel();

	/** WeatherSensor information panel */
	private final JPanel info_pnl;

	/** Currently selected weather sensor */
	private WeatherSensor selected = null;


	public WeatherHeatmapDispatcher(Session s, WeatherSensorManager man) {

		session = s;
		manager = man;
		client_props = session.getProperties();
		setLayout(new BorderLayout());
		sel_model = manager.getSelectionModel();
		//model = session.getSonarState().getWeatherSensorCache().getWeatherSensorModel();
		cache = session.getSonarState().getWeatherSensors();
		info_pnl = createInfoPanel();
	}

	/** Initialize the widgets on the panel */
	public void initialize() {

		setTitle(I18N.get("weather_sensor.selected"));

		// FIXME or remove?
		add(info_pnl, BorderLayout.NORTH);
		add(new JPanel(new GridBagLayout()), BorderLayout.CENTER);
		add(new JPanel(new GridBagLayout()), BorderLayout.SOUTH);

		clear();
		sel_model.addProxySelectionListener(sel_listener);
	}

	/** dispose of weather sensor panel widgets */
	public void dispose() {
		sel_model.removeProxySelectionListener(sel_listener);
		cache.removeProxyListener(proxy_listener);
		selected = null;
		removeAll();
	}

	/** Set the selected weather sensor */
	private void selectWeatherSensor(final WeatherSensor ws) {

		if (ws == selected)
			return;
		if (selected != null)
			cache.ignoreObject(selected);
		selected = ws;
		if (ws != null) {
			name_lbl.setText(ws.getName());
			location_lbl.setText(GeoLocHelper.getDescription(
				ws.getGeoLoc()));
			cache.watchObject(ws);
		} else
			clear();
	}

	/** Clear all of the fields */
	private void clear() {

		name_lbl.setText("");
		location_lbl.setText("");
		op_status_lbl.setText("");
	}

	/** Set the title */
	public void setTitle(String t) {
		setBorder(BorderFactory.createTitledBorder(t));
	}

	/** Create weather sensor information panel */
	private JPanel createInfoPanel() {
		// FIXME or remove?
		JPanel p = new JPanel(new GridBagLayout());

		return p;
	}

	/**
	 * Update the Op Status field.  The resulting field will contain the
	 * status string and a current timestamp.
	 * @param stat the status string
	 */
	private void updateOpStatus(String stat) {
		String s = "";
		if ((stat != null) && (!(stat.equals(""))))
			s += stat + ", "
				+ TimeSteward.currentDateTimeString(true);
		op_status_lbl.setText(s);
	}

}
