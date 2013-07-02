/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2013  Minnesota Department of Transportation
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

import java.io.IOException;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.EventType;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.utils.SString;

/**
 * An operation which is performed on a field controller.
 *
 * @author Douglas Lau
 */
abstract public class OpController<T extends ControllerProperty>
	extends Operation<T>
{
	/** Comm error log */
	static protected final DebugLog COMM_LOG = new DebugLog("comm");

	/** Filter a message */
	static protected String filterMessage(String m) {
		final int MAXLEN = 64;
		return SString.truncate(m, MAXLEN);
	}

	/** Controller to be polled */
	protected final ControllerImpl controller;

	/** Get the controller being polled */
	public ControllerImpl getController() {
		return controller;
	}

	/** Drop address of the controller to be polled */
	protected final int drop;

	/** Device ID */
	protected final String id;

	/** Maint status message */
	private String maintStatus = null;

	/** Set the maint status message.  If non-null, the controller "maint"
	 * attribute is set to this message when the operation completes. */
	public void setMaintStatus(String s) {
		maintStatus = s;
	}

	/** Error status message */
	private String errorStatus = null;

	/** Set the error status message.  If non-null, the controller "error"
	 * attribute is set to this message when the operation completes. */
	public void setErrorStatus(String s) {
		assert s != null;
		if(errorStatus != null) {
			if(s.length() > 0) {
				if(errorStatus.length() > 0)
					errorStatus = errorStatus + ", " + s;
				else
					errorStatus = s;
			}
		} else
			errorStatus = s;
	}

	/** Operation error counter */
	protected int errorCounter = 0;

	/** Create a new controller operation */
	protected OpController(PriorityLevel p, ControllerImpl c, String i) {
		super(p);
		assert c != null;
		controller = c;
		drop = controller.getDrop();
		id = i;
	}

	/** Create a new controller operation */
	protected OpController(PriorityLevel p, ControllerImpl c) {
		this(p, c, c.toString());
	}

	/** Operation equality test */
	public boolean equals(Object o) {
		return (o instanceof OpController) &&
		       (getClass() == o.getClass()) &&
		       ((OpController)o).controller == controller;
	}

	/** Get a string description of the operation */
	public String toString() {
		return super.toString() + " (" + id + ")";
	}

	/** Get the operation key name */
	protected String getKey() {
		return getOpName() + ":" + controller.getName();
	}

	/** Handle a communication error */
	@Override public void handleCommError(EventType et, String msg) {
		COMM_LOG.log(id + " " + et + ", " + msg);
		controller.logCommEvent(et, id, filterMessage(msg));
		if(!retry())
 			super.handleCommError(et, msg);
	}

	/** Determine if this operation should be retried */
	public boolean retry() {
		if(controller.isFailed())
			return false;
		errorCounter++;
		return errorCounter < getRetryThreshold();
	}

	/** Update controller maintenance status */
	protected final void updateMaintStatus() {
		String s = maintStatus;
		if(s != null) {
			controller.setMaint(filterMessage(s));
			maintStatus = null;
		}
	}

	/** Update controller error status */
	protected final void updateErrorStatus() {
		String s = errorStatus;
		if(s != null) {
			controller.setErrorStatus(filterMessage(s));
			errorStatus = null;
		}
	}

	/** Cleanup the operation */
	public void cleanup() {
		updateMaintStatus();
		updateErrorStatus();
		controller.completeOperation(id, isSuccess());
		super.cleanup();
	}

	/** Get the error retry threshold */
	public int getRetryThreshold() {
		return SystemAttrEnum.OPERATION_RETRY_THRESHOLD.getInt();
	}
}
