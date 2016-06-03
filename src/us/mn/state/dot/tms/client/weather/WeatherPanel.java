/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016 California Department of Transportation
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
package us.mn.state.dot.tms.client.weather;

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.proxy.ProxyView;
import us.mn.state.dot.tms.client.proxy.ProxyWatcher;
import us.mn.state.dot.tms.utils.I18N;

import javax.swing.*;
import java.awt.*;

/**
 * A panel for viewing and editing weather sensor parameters.
 *
 * @author Dan Rossiter
 */
public class WeatherPanel extends JPanel {

    private final ProxySelectionModel<WeatherSensor> sel_model;

    /** Proxy watcher */
    private final ProxyWatcher<WeatherSensor> watcher;

    /** Tabbed pane */
    private final JTabbedPane tab = new JTabbedPane();

    /** Location panel */
    private final WeatherLocationPanel loc_pnl;

    /** Proxy view */
    private final ProxyView<WeatherSensor> view = new ProxyView<WeatherSensor>() {
        public void update(WeatherSensor n, String a) {
            loc_pnl.update(n, a);
        }
        public void clear() {
            loc_pnl.clear();
        }
    };

    /** Selection listener */
    private final ProxySelectionListener sel_listener = new ProxySelectionListener() {
        @Override
        public void selectionChanged() {
            setWeatherSensor(sel_model.getSingleSelection());
        }
    };

    /** Set a new weather sensor */
    public void setWeatherSensor(WeatherSensor n) {
        if(n != null)
            loc_pnl.setGeoLoc(n.getGeoLoc());
        else
            loc_pnl.setGeoLoc(null);
        watcher.setProxy(n);
    }

    /** Create a new weather sensor panel */
    public WeatherPanel(Session s, ProxySelectionModel<WeatherSensor> sm) {
        super(new BorderLayout());
        sel_model = sm;
        loc_pnl = new WeatherLocationPanel(s);
        TypeCache<WeatherSensor> cache =
            s.getSonarState().getWeatherSensorsCache().getWeatherSensors();
        watcher = new ProxyWatcher<WeatherSensor>(cache, view, false);
        setBorder(BorderFactory.createTitledBorder(I18N.get(
            "weather.selected")));
    }

    /** Initialize the widgets on the panel */
    public void initialize() {
        watcher.initialize();
        loc_pnl.initialize();
        tab.add(I18N.get("location"), loc_pnl);
        add(tab, BorderLayout.CENTER);
        sel_model.addProxySelectionListener(sel_listener);
    }

    /** Dispose of the panel */
    public void dispose() {
        loc_pnl.dispose();
        watcher.dispose();
        sel_model.removeProxySelectionListener(sel_listener);
    }
}
