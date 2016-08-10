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

import us.mn.state.dot.tms.client.map.AbstractMarker;

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
        moveTo(12.5, 15.648934);
        curveTo(17.952099, 15.648934, 22.677784, 12.474081, 25.0, 7.8364325);
        curveTo(22.677782, 3.198785, 17.952099, 0.02393174, 12.5, 0.02393174);
        curveTo(7.048047, 0.02393174, 2.3223145, 3.198785, -1.7616582E-8, 7.8364325);
        curveTo(2.3222175, 12.474081, 7.048047, 15.648934, 12.5, 15.648934);
        closePath();
        moveTo(6.33667, 11.505771);
        curveTo(4.867822, 10.568906, 3.6231928, 9.314021, 2.6882818, 7.8364325);
        curveTo(3.6231444, 6.3588443, 4.86792, 5.1039643, 6.33667, 4.167095);
        curveTo(8.182227, 2.9899004, 10.313525, 2.3676834, 12.5, 2.3676834);
        curveTo(14.686523, 2.3676834, 16.817772, 2.9899, 18.66333, 4.167095);
        curveTo(20.13208, 5.104012, 21.376709, 6.3588934, 22.311623, 7.8364325);
        curveTo(21.37676, 9.31407, 20.132082, 10.568955, 18.66333, 11.505772);
        curveTo(18.56768, 11.566812, 18.471046, 11.625938, 18.373829, 11.683994);
        curveTo(18.616943, 11.016805, 18.75, 10.296736, 18.75, 9.545416);
        curveTo(18.75, 6.093707, 15.951757, 3.2954178, 12.5, 3.2954178);
        curveTo(9.048291, 3.2954178, 6.25, 6.093707, 6.25, 9.545416);
        curveTo(6.25, 10.296736, 6.383057, 11.016805, 6.6261225, 11.684043);
        curveTo(6.5290523, 11.625993, 6.4323244, 11.566805, 6.33667, 11.505771);
        closePath();
        moveTo(12.5, 10.32667);
        curveTo(12.5, 9.032233, 13.549317, 7.982917, 14.843751, 7.982917);
        curveTo(16.138184, 7.982917, 17.1875, 9.032233, 17.1875, 10.32667);
        curveTo(17.1875, 11.621101, 16.138184, 12.670418, 14.843751, 12.670418);
        curveTo(13.549317, 12.670418, 12.5, 11.621102, 12.5, 10.32667);
        closePath();
    }
}
