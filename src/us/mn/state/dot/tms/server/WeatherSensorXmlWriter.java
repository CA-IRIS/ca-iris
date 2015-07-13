/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import us.mn.state.dot.tms.WeatherSensor;
import us.mn.state.dot.tms.WeatherSensorHelper;

/**
 * Write the current weather sensor state to an XML file.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class WeatherSensorXmlWriter extends XmlWriter {

	/** XML file */
	static protected final String FILE_NAME_XML = "weather_sensor.xml";

	/** Create a new XML writer */
	public WeatherSensorXmlWriter() {
		super(FILE_NAME_XML, false);
	}

	/** Write the XML to a writer */
	public void write(Writer w) {
		try {
			w.write(XML_DECLARATION + "\n");
			w.write("<list>\n");
			Iterator<WeatherSensor> it = WeatherSensorHelper
				.iterator();
			while(it.hasNext()) {
				WeatherSensor ws = it.next();
				if(ws instanceof WeatherSensorImpl)
					((WeatherSensorImpl)ws)
						.printXmlElement(w);
			}
			w.write("</list>\n");
		}
		catch (IOException e) {
			// fail
		}
	}

}
