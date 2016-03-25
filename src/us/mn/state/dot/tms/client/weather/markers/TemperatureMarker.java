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
 * The style is that of a thermometer.
 *
 * TODO: Would be nice if we could change the height of the
 * fill rather than the color, but more involved and no
 * time at present to implement.
 *
 * @author Dan Rossiter
 */
public class TemperatureMarker extends AbstractMarker {

    /** Create a new temp markers */
    public TemperatureMarker() {
        super(22);
        moveTo(2.893782, 14.964747);
        curveTo(3.546732, 14.964747, 4.2017817, 14.964747, 4.854752, 14.964747);
        moveTo(2.8937821, 11.293077);
        curveTo(3.5467322, 11.293077, 4.201782, 11.293077, 4.854752, 11.293077);
        moveTo(2.8937821, 7.6216173);
        curveTo(3.5467322, 7.6216173, 4.201782, 7.6216173, 4.854752, 7.6216173);
        moveTo(2.8937821, 13.128907);
        curveTo(3.5467322, 13.128907, 4.201782, 13.128907, 4.854752, 13.128907);
        moveTo(2.8937821, 9.457257);
        curveTo(3.5467322, 9.457257, 4.201782, 9.457257, 4.854752, 9.457257);
        moveTo(2.8937821, 5.7855844);
        curveTo(3.5467322, 5.7855844, 4.201782, 5.7855844, 4.854752, 5.7855844);
        moveTo(4.355182, 0.18794441);
        curveTo(3.4244423, 0.1471444, 2.7569623, 1.0751644, 2.8937821, 1.9379145);
        curveTo(2.8937821, 6.8255773, 2.8937821, 11.713438, 2.8937821, 16.601097);
        curveTo(0.430542, 17.414886, -0.622288, 20.678637, 0.9224421, 22.737186);
        curveTo(2.361032, 24.983387, 6.016192, 25.110857, 7.612552, 22.969706);
        curveTo(9.370372, 20.954596, 8.406472, 17.479956, 5.8567924, 16.615585);
        curveTo(5.7531424, 16.319836, 5.8430924, 15.903766, 5.8152924, 15.561115);
        curveTo(5.8102922, 10.866795, 5.8255925, 6.1718826, 5.8082924, 1.4777822);
        curveTo(5.7460923, 0.7598422, 5.084842, 0.1724422, 4.355172, 0.18672216);
        closePath();
    }

}
