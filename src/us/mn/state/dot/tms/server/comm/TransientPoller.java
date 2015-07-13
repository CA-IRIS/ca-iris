/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2012-2014  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm;

import static us.mn.state.dot.tms.server.comm.MessagePoller.ConnMode;

/**
 * TransientPoller is a MessagePoller which causes equal operations to be
 * replaced instead of rejected.  It is useful for PTZ pollers (which consist
 * of transient PTZ commands only).
 *
 * @author Douglas Lau
 * @author Travis Swanston
 */
abstract public class TransientPoller<T extends ControllerProperty>
	extends MessagePoller<T>
{

	/**
	 * Create a new transient poller with persistent connection mode.
	 * @param n CommLink name
	 * @param m the Messenger
	 */
	protected TransientPoller(String name, Messenger m) {
		super(name, m);
	}

	/**
	 * Create a new transient poller with specified connection mode.
	 * @param n CommLink name
	 * @param m the Messenger
	 * @param cm the connection mode
	 * @param idle max idle time (sec) to use for conn mode AUTO
	 */
	protected TransientPoller(String name, Messenger m, ConnMode cm,
		int idle)
	{
		super(name, m, cm, idle);
	}

	/** Add an operation to the transient poller */
	@Override
	protected void addOperation(final Operation<T> op) {
		queue.forEach(new OperationHandler<T>() {
			public void handle(PriorityLevel prio, Operation<T> o) {
				if(o.equals(op))
					o.setSucceeded();
			}
		});
		super.addOperation(op);
	}
}
