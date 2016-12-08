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
 * The style is that of a rain drop.
 *
 * @author Dan Rossiter
 */
public class PrecipitationMarker extends Marker {

	public PrecipitationMarker() {
		super(19);
		moveTo(0.02910812, 9.620753);
		lineTo(0.02910812, 8.513372);
		curveTo(0.10558602, 8.308674, 0.03954232, 8.02344, 0.13339804, 7.8086753);
		curveTo(0.56444794, 4.184512, 3.7764826, 0.89256716, 7.503004, 0.2583375);
		curveTo(7.8819137, 0.1140455, 8.330345, 0.1173975, 8.719685, 0.056997493);
		lineTo(10.1449375, 0.056997493);
		curveTo(10.534277, 0.117397495, 10.98271, 0.1140475, 11.361616, 0.2583375);
		curveTo(15.380138, 0.9261225, 18.65287, 4.6643777, 18.835514, 8.54693);
		lineTo(18.835514, 9.687869);
		curveTo(18.708178, 10.44626, 18.54622, 11.234851, 18.235863, 11.959682);
		curveTo(17.921612, 12.543573, 17.597975, 13.12411, 17.227757, 13.671091);
		lineTo(16.04584, 15.214716);
		curveTo(13.402167, 18.248272, 11.086996, 21.50666, 9.345406, 25.056997);
		lineTo(8.865688, 24.075455);
		curveTo(7.426526, 20.691225, 5.156549, 17.69458, 2.6779957, 14.879143);
		curveTo(2.4103262, 14.49995, 2.1217995, 14.130823, 1.8089386, 13.771764);
		curveTo(1.343124, 13.080486, 0.88773674, 12.382498, 0.52273405, 11.62411);
		curveTo(0.27244443, 10.983171, 0.11601535, 10.291897, 0.02910812, 9.620753);
		closePath();
	}

}
