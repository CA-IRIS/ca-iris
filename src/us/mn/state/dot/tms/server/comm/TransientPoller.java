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

/**
 * TransientPoller is a MessagePoller which causes equal operations to be
 * replaced instead of rejected.  It is useful for PTZ pollers (which consist
 * of transient PTZ commands only).
 *
 * @author Douglas Lau
 * @author Travis Swanston
 * @author Dan Rossiter
 */
abstract public class TransientPoller<T extends ControllerProperty>
	extends MessagePoller<T> {

	/**
	 * Create a new transient poller with specified connection mode.
	 * @param name CommLink name
	 * @param m the Messenger
	 */
	protected TransientPoller(String name, Messenger m) {
		super(name, m);
	}

	/** Add an operation to the transient poller */
	@Override
	protected void addOperation(final Operation<T> op) {
		/*
		 * FIXME the logic commented out below fails for slower networks
		 * California has documented that in networks that are slower,
		 * where queued operations can build up, that valid operations
		 * are removed from the queue that results in unwanted behavior
		 * within PTZ cameras, such as camera spinning.  This has been
		 * seen in Cohu and Axis. Assumed to be the same for Manchester,
		 * PelcoD and Vicon. Contention handling may also contribute in
		 * concert with the logic below to cause this behavior.
		 */
//		queue.forEach(new OperationHandler<T>() {
//			public void handle(PriorityLevel prio, Operation<T> o) {
//				if(o.equals(op))
//					o.setSucceeded();
//			}
//		});

		super.addOperation(op);
	}
}
