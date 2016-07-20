/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012  Iteris Inc.
 * Copyright (C) 2012-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.sensys;

import java.io.IOException;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpController;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to read traffic samples from a field device.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class OpQuerySamples extends OpController<SensysProperty> {

	/** Traffic sample */
	private SensysRec ss_rec = new SensysRec();

	/** Time stamp of sample data */
	protected long stamp;

	/**
	 * Constructor for operation to read latest traffic sample.
	 * @param c Associated controller
	 * @param comp Completer
	 */
	public OpQuerySamples(ControllerImpl c) {
		super(PriorityLevel.DATA_30_SEC, c);
		SensysPoller.log("called: success=" + isSuccess());
	}

	/** Create the first phase of the operation. */
	@Override
	protected Phase<SensysProperty> phaseOne() {
		SensysPoller.log("ctrl=" + controller + ": suc=" + isSuccess());
		return new GetCurrentSamples();
	}

	/** Phase to get the most recent sample. */
	protected class GetCurrentSamples extends Phase<SensysProperty> {
		protected Phase<SensysProperty> poll(
			CommMessage<SensysProperty> mess) throws IOException
		{
			mess.add(new SensysProperty(controller, ss_rec));
			mess.queryProps();
			return null;
		}
	}

	/** Cleanup the operation. */
	@Override
	public void cleanup() {
		SensysPoller.log("called: success=" + isSuccess());
		ss_rec.store(controller);
		super.cleanup();
	}

}
