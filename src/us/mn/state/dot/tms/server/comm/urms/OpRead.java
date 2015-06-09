/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.urms;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.utils.ByteBlob;

/**
 * Operation to continuously read traffic samples from a field device.
 * @author Michael Darter
 * @author Travis Swanston
 */
public class OpRead extends Operation<UrmsProperty> {

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/**
	 * Create a new operation to read a traffic record.
	 * This method is called on a scheduler thread.
	 */
	public OpRead(CommLinkImpl cl) {
		super(PriorityLevel.DATA_30_SEC);
		comm_link = cl;
		UrmsPoller.log("cl=" + cl);
	}

	/** Create the first phase of the operation. */
	@Override
	protected Phase<UrmsProperty> phaseOne() {
		UrmsPoller.log("cl=" + comm_link + ": suc=" + isSuccess());
		return new GetSamplesPhase();
	}

	/** Phase to get the most recent binned samples */
	protected class GetSamplesPhase extends Phase<UrmsProperty> {
		/**
		 * Read byte stream from device, parsing into records,
		 * then into station data. Called by Operation.poll() if
		 * phase is set to this phase.
		 * @param mess Associated comm message.
		 * @return The same phase for continuous stream reading.
		 */
		@Override
		protected Phase<UrmsProperty> poll(CommMessage<UrmsProperty>
			mess) throws IOException
		{
			UrmsPoller.log("Creating list of " +
				"active ctrls");
			final LinkedList<Controller> acs =
				comm_link.getActiveControllers();
			UrmsPoller.log(acs.size() +
				" active controllers.");
			if(acs.size() <= 0) {
				UrmsPoller.sleepy(10 * 1000);
			} else {
				UrmsPoller.log("Read bytes --> UrmsRec");
				mess.add(new UrmsProperty(acs));
				mess.queryProps();
			}
			UrmsPoller.log("Done, success=" + isSuccess());
			return this;
		}
	}

	/** Cleanup the operation.  Called when poller is closed. */
	public void cleanup() {
		UrmsPoller.log("Called: success=" + isSuccess());
		super.cleanup();
	}

}
