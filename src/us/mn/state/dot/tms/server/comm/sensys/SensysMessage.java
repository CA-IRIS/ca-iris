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
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.ProtocolException;

/**
 * Sensys message
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class SensysMessage implements CommMessage {

	/** Associated messenger */
	protected final Messenger ss_messenger;

	/** Device property */
	protected SensysProperty dev_prop;

	/** Constructor for new message. */
	public SensysMessage(Messenger m) {
		ss_messenger = m;
	}

	/** Add a controller property */
	@Override
	public void add(ControllerProperty cp) {
		SensysPoller.log("adding property");
		if(cp instanceof SensysProperty)
			dev_prop = (SensysProperty)cp;
	}

	/**
	 * Read data from a field controller.
	 * @throws IOException On IO errors
	 */
	@Override
	public void queryProps() throws IOException {
		SensysPoller.log("called, dev_prop=" + dev_prop);

//		if(dev_prop == null)
//			throw new ProtocolException("No property");
//		ss_messenger.reopen();
//		dev_prop.doGetRequest(ss_messenger.getOutputStream(),
//			ss_messenger.getInputStream());
//		ss_messenger.close();
		if(dev_prop != null)
			dev_prop.doGetRequest(ss_messenger.getOutputStream(),
				ss_messenger.getInputStream(null));
		else
			throw new ProtocolException("No property");

	}

	/** Store the controller properties */
	@Override
	public void storeProps() throws IOException {
	}

	@Override
	public void logQuery(ControllerProperty prop) {
	}

	@Override
	public void logStore(ControllerProperty prop) {
	}

}
