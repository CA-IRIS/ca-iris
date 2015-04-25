/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.pems;

import java.io.IOException;
import java.util.Iterator;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.StationHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.StationImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to write data to the PeMS system.
 * @author Michael Darter
 * @author Travis Swanston
 */
public class OpWrite extends Operation<PemsProperty> {

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Constructor */
	public OpWrite(CommLinkImpl cl) {
		super(PriorityLevel.DATA_30_SEC);
		comm_link = cl;
	}

	/** Create the first phase of the operation. */
	@Override
	protected Phase<PemsProperty> phaseOne() {
		return new WritePhase();
	}

	/** Phase to get the most recent binned samples */
	protected class WritePhase extends Phase<PemsProperty> {
		protected Phase<PemsProperty> poll(
			final CommMessage<PemsProperty> mess) throws IOException
		{
			PemsPoller.log("Writing all stations to PeMS");
			Iterator<Station> it = StationHelper.iterator();
			while (it.hasNext()) {
				Station s = it.next();
				if(!(s instanceof StationImpl))
					continue;
				final StationImpl si = (StationImpl)s;
				mess.add(new PemsProperty(si));
			}
			mess.storeProps();
			PemsPoller.log("Returning, success=" + isSuccess());
			return null;
		}
	}

	/** Cleanup the operation */
	public void cleanup() {
		PemsPoller.log("success=" + isSuccess());
		super.cleanup();
	}

}
