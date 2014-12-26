/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008  Minnesota Department of Transportation
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

package us.mn.state.dot.tms.kml;

/**
 * A Kml icon style.
 *
 * @author Michael Darter
 * @see KmlObject
 */
public interface KmlIconStyle extends KmlColorStyle 
{
	/** get scale */
	public double getKmlScale();

	/** set scale */
	public void setKmlScale(double scale);

	/** get icon */
	public KmlIcon getKmlIcon();

	/** set icon */
	public void setKmlIcon(KmlIcon icon);
}