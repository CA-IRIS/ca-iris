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
package us.mn.state.dot.tms.client.weather.markers;

import us.mn.state.dot.map.AbstractMarker;

/**
 * Marker used to paint weather sensors.
 * The style is that of an eye.
 *
 * @author Dan Rossiter
 */
public class VisibilityMarker extends AbstractMarker {
    /**
     * Create a new abstract markers
     */
    public VisibilityMarker() {
        super(26);
        moveTo(28.20339, 2.0);
        curveTo(15.901975, 2.0, 5.2395506, 9.16333, 0.0, 19.62712);
        curveTo(5.2395506, 30.090908, 15.901975, 37.25424, 28.20339, 37.25424);
        curveTo(40.504475, 37.25424, 51.167007, 30.090908, 56.40678, 19.62712);
        curveTo(51.16723, 9.16333, 40.504475, 2.0, 28.20339, 2.0);
        closePath();
        moveTo(42.109535, 11.3481);
        curveTo(45.423653, 13.46192, 48.231873, 16.29328, 50.34129, 19.627121);
        curveTo(48.231983, 22.96096, 45.42343, 25.79231, 42.109535, 27.906141);
        curveTo(37.945457, 30.562212, 33.13667, 31.9661, 28.203392, 31.9661);
        curveTo(23.270002, 31.9661, 18.461323, 30.56221, 14.297248, 27.906141);
        curveTo(10.983349, 25.792202, 8.175129, 22.960852, 6.065714, 19.627121);
        curveTo(8.175018, 16.29317, 10.983349, 13.461811, 14.297248, 11.348101);
        curveTo(14.51307, 11.210391, 14.731095, 11.076971, 14.950443, 10.945981);
        curveTo(14.401909, 12.451341, 14.101697, 14.076011, 14.101697, 15.771191);
        curveTo(14.101697, 23.559181, 20.41529, 29.87288, 28.203392, 29.87288);
        curveTo(35.991383, 29.87288, 42.305088, 23.559181, 42.305088, 15.771191);
        curveTo(42.305088, 14.076011, 42.004875, 12.451341, 41.45645, 10.945871);
        curveTo(41.67547, 11.076861, 41.893715, 11.210391, 42.109535, 11.348102);
        closePath();
        moveTo(28.20339, 14.00847);
        curveTo(28.20339, 16.92907, 25.835848, 19.29661, 22.915255, 19.29661);
        curveTo(19.994661, 19.29661, 17.62712, 16.92907, 17.62712, 14.008471);
        curveTo(17.62712, 11.087881, 19.994661, 8.720341, 22.915255, 8.720341);
        curveTo(25.835848, 8.720341, 28.20339, 11.087881, 28.20339, 14.008471);
        closePath();
    }
}
