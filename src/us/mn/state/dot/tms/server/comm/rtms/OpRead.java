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
package us.mn.state.dot.tms.server.comm.rtms;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.DevicePoller;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.utils.ByteBlob;

/**
 * Operation to continuously read traffic samples from a field device.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class OpRead extends Operation<RtmsProperty> {

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Hash of latest samples */
	private StationSampleHash sample_hash;

	/** Create a new "query binned samples" operation. */
	public OpRead(CommLinkImpl cl) {
		super(PriorityLevel.DATA_30_SEC);
		comm_link = cl;
		sample_hash = new StationSampleHash(comm_link);
		RtmsPoller.log("cl=" + comm_link);
	}

	/** Create the first phase of the operation. */
	@Override
	protected Phase<RtmsProperty> phaseOne() {
		RtmsPoller.log("cl=" + comm_link + ": suc=" + isSuccess());
		return new GetSamplesPhase();
	}

	/** Phase to get the most recent binned samples. */
	protected class GetSamplesPhase extends Phase<RtmsProperty> {
		/**
		 * Read byte stream from device, parsing into records,
		 * then into station data. Called by Operation.poll() if
		 * phase is set to this phase.
		 * @param mess Associated comm message.
		 * @return The same phase for continuous stream reading.
		 */
		@Override
		protected Phase<RtmsProperty> poll(CommMessage<RtmsProperty>
			mess) throws IOException
		{
			RtmsPoller.log("cl=" + comm_link +
				": creating list of active ctrls");
			final LinkedList<Controller> acs =
				comm_link.getActiveControllers();
			RtmsPoller.log("cl=" + comm_link + ": " +
				acs.size() + " active controllers.");
			if(acs.size() <= 0) {
				RtmsPoller.log("cl=" + comm_link +
					": sleeping");
				RtmsPoller.sleepy(10 * 1000);
				RtmsPoller.log("cl=" + comm_link +
					": sleep end");
			} else {
				RtmsPoller.log("cl=" + comm_link +
					": read bytes --> StationSample");
				mess.add(new RtmsProperty(comm_link, acs,
					sample_hash));
				mess.queryProps();
			}
			RtmsPoller.log("cl=" + comm_link +
				": done, success=" + isSuccess());
			return this;
		}
	}

	/** Cleanup the operation */
	public void cleanup() {
		RtmsPoller.log("cl=" + comm_link + ": suc=" + isSuccess());
		super.cleanup();
	}

}
