/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010  AHMCT, University of California
 * Copyright (C) 2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ssi;

import java.io.IOException;
import us.mn.state.dot.tms.server.WeatherSensorImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to read the SSI file.
 *
 * @author Michael Darter
 */
public class OpRead extends OpDevice {

	/** Create a new device operation */
	protected OpRead(WeatherSensorImpl ws) {
		super(PriorityLevel.DATA_30_SEC, ws);
	}

	/** Create the second phase of the operation */
	protected Phase phaseTwo() {
		return new PhaseRead();
	}

	/** Phase to read the file */
	protected class PhaseRead extends Phase {

		/** Execute the phase
		 * @throws IOException received from queryProps call. */
		protected Phase poll(CommMessage cm) throws IOException {
			SsiMessage m = (SsiMessage)cm;
			m.add(new SsiProperty());
			m.queryProps();
			return null;
		}
	}
}