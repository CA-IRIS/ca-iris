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
 * The style is that of a rain drop.
 *
 * @author Dan Rossiter
 */
public class PrecipitationMarker extends AbstractMarker {
    
    public PrecipitationMarker() {
        super(19);
        moveTo(31.122257, 24.781855);
        lineTo(31.122257, 26.569567);
        curveTo(30.998795, 26.900024, 31.105417, 27.360493, 30.953896, 27.7072);
        curveTo(30.258026, 33.5579, 25.072649, 38.87228, 19.056707, 39.896156);
        curveTo(18.445013, 40.129097, 17.721083, 40.123684, 17.092548, 40.22119);
        lineTo(14.791678, 40.22119);
        curveTo(14.163145, 40.12368, 13.439214, 40.129093, 12.827521, 39.896156);
        curveTo(6.3401847, 38.81811, 1.0568209, 32.783226, 0.7619715, 26.515396);
        lineTo(0.7619715, 24.67351);
        curveTo(0.96753883, 23.449192, 1.2289958, 22.176125, 1.7300256, 21.005985);
        curveTo(2.237337, 20.063375, 2.7598057, 19.12618, 3.3574705, 18.243156);
        lineTo(5.2655096, 15.751193);
        curveTo(9.533344, 10.853944, 13.27086, 5.593737, 16.082413, -0.13778);
        lineTo(16.85685, 1.4467827);
        curveTo(19.180174, 6.91014, 22.84473, 11.747801, 26.846004, 16.292927);
        curveTo(27.27812, 16.905079, 27.743906, 17.500984, 28.248974, 18.080635);
        curveTo(29.000965, 19.196606, 29.736124, 20.323408, 30.32537, 21.547718);
        curveTo(30.729427, 22.582422, 30.98196, 23.698387, 31.12226, 24.781853);
        closePath();
    }

}
