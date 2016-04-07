/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016 California Department of Transportation
 * Copyright (C) 2016 Southwest Research Institute
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

import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.StyleSummary;

import java.awt.*;

/**
 * Provides a GUI for the weather tab on the operator interface for IRIS.
 *
 * @author Dan Rossiter
 */
public class WeatherTab extends MapTab<WeatherSensor> {

    /** Threshold configuration panel */
    private final WeatherThresholdsPanel thresholds;

    /** Weather sensor */
    private final WeatherPanel selected;

    /** Summary of weather sensors */
    private final StyleSummary<WeatherSensor> summary;

    /** Create a new weather sensor tab for the IRIS client */
    public WeatherTab(Session session, WeatherSensorManager man) {
        super(man, new GridBagLayout());
        thresholds = new WeatherThresholdsPanel(session);
        selected = new WeatherPanel(session, man.getSelectionModel());
        summary = man.getStyleSummary();
    }

    /** Get the tab ID */
    @Override
    public String getTabId() {
        return "weather";
    }

    /** Initialize the map tab */
    @Override
    public void initialize() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridx = 0;
        gc.gridy = 0;
        add(thresholds, gc);
        gc.gridy = 1;
        add(selected, gc);
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.gridy = 2;
        add(summary, gc);

        thresholds.initialize();
        selected.initialize();
        summary.initialize();
    }

    /** Perform any clean up necessary */
    @Override
    public void dispose() {
        thresholds.dispose();
        selected.dispose();
        summary.dispose();
    }
}
