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
package us.mn.state.dot.tms.server.comm.urms;

import us.mn.state.dot.tms.utils.ByteBlob;

/**
 * A raw URMS record.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class UrmsBlob extends ByteBlob {

	/** Get the station ID.*/
	protected int getStationId() {
		return 1234;	//FIXME
	}

}
