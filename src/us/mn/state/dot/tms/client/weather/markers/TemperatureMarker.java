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

import us.mn.state.dot.tms.client.map.Marker;

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
public class TemperatureMarker extends Marker {

    /** Create a new temp markers */
    public TemperatureMarker() {
        super(22);
        moveTo(5.884533, 9.840892);
        curveTo(5.223366, 9.840892, 4.560072, 9.840892, 3.8988848, 9.840892);
        moveTo(5.884533, 13.558767);
        curveTo(5.223366, 13.558767, 4.560072, 13.558767, 3.8988848, 13.558767);
        moveTo(5.884533, 17.27643);
        curveTo(5.223366, 17.27643, 4.560072, 17.27643, 3.8988848, 17.27643);
        moveTo(5.884533, 11.699835);
        curveTo(5.223366, 11.699835, 4.560072, 11.699835, 3.8988848, 11.699835);
        moveTo(5.884533, 15.41769);
        curveTo(5.223366, 15.41769, 4.560072, 15.41769, 3.8988848, 15.41769);
        moveTo(5.884533, 19.135569);
        curveTo(5.223366, 19.135569, 4.560072, 19.135569, 3.8988848, 19.135569);
        moveTo(4.404742, 24.803652);
        curveTo(5.3471947, 24.844961, 6.0230737, 23.905268, 5.884533, 23.03166);
        curveTo(5.884533, 18.082489, 5.884533, 13.133118, 5.884533, 8.18395);
        curveTo(8.378771, 7.3599195, 9.44485, 4.0550976, 7.8806806, 1.9706426);
        curveTo(6.423987, -0.30382442, 2.7228289, -0.43289828, 1.10638, 1.7351966);
        curveTo(-0.673561, 3.7756646, 0.30246896, 7.2940307, 2.884235, 8.169279);
        curveTo(2.989189, 8.468751, 2.898105, 8.890057, 2.926255, 9.237019);
        curveTo(2.931355, 13.990414, 2.915825, 18.744408, 2.933355, 23.497581);
        curveTo(2.996335, 24.224556, 3.66591, 24.81935, 4.4047623, 24.804888);
        closePath();
    }

}
