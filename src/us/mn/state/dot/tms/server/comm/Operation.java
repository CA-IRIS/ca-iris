/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2002-2014  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.EventType;
import us.mn.state.dot.tms.SystemAttrEnum;

import java.io.IOException;

/**
 * An operation is a sequence of phases to be performed on a field controller.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 * @author Dan Rossiter
 */
abstract public class Operation<T extends ControllerProperty> {

	/** Priority of the operation */
	private PriorityLevel priority;

	/**
	 * Get the priority of the operation.
	 *
	 * @return Priority of the operation (@see PriorityLevel)
	 */
	public final PriorityLevel getPriority() {
		return priority;
	}

	/** Set the priority of the operation */
	public final void setPriority(PriorityLevel p) {
		if (p.ordinal() < priority.ordinal())
			priority = p;
	}

	/** Base class for operation phases */
	abstract protected class Phase<T extends ControllerProperty> {

		/**
		 * Perform a poll.
		 *
		 * @return The next phase of the operation, or null
		 */
		abstract protected Phase<T> poll(CommMessage<T> mess)
			throws IOException, DeviceContentionException;
	}

	/** Current phase of the operation, or null if done */
	private Phase<T> phase;

	/** Create a new operation */
	public Operation(PriorityLevel prio) {
		priority = prio;
	}

	/**
	 * Begin the operation.  The operation begins when it is queued for
	 * processing.
	 */
	public final void begin() {
		phase = phaseOne();
	}

	/**
	 * Create the first phase of the operation.  This method cannot be
	 * called in the Operation constructor, because the object may not
	 * have been fully constructed yet (subclass initialization).
	 */
	abstract protected Phase<T> phaseOne();

	/**
	 * Cleanup the operation.  The operation gets cleaned up after
	 * processing is complete and it is removed from the queue.  This method
	 * may get called more than once after the operation is done.
	 */
	public void cleanup() {
	}

	/** Success or failure of operation */
	private boolean success = true;

	/** Check if the operation succeeded */
	public boolean isSuccess() {
		return success;
	}

	/** Set the success flag.  This will clear the error counter if true. */
	protected final void setSuccess(boolean s) {
		success = s;
		if (s)
			error_cnt = 0;
	}

	/** Set the operation to failed */
	public synchronized final void setFailed() {
		setSuccess(false);
		phase = null;
	}

	/** Set the operation to succeeded */
	public synchronized final void setSucceeded() {
		setSuccess(true);
		phase = null;
	}

	/**
	 * Perform a poll with the current phase.
	 *
	 * @param mess Message to use for polling.
	 */
	public final void poll(CommMessage<T> mess) throws IOException,
		DeviceContentionException {
		Phase<T> p = phase;
		if (p != null)
			updatePhase(p.poll(mess));
	}

	/** Update the phase of the operation */
	private synchronized void updatePhase(Phase<T> p) {
		// Need to synchronize against setFailed / setSucceeded
		if (!isDone())
			phase = p;
	}

	/** Check if the operation is done */
	public final boolean isDone() {
		return phase == null;
	}

	/** Handle a communication error */
	public void handleCommError(EventType et, String msg) {
		if (!retry())
			setFailed();
	}

	/** Operation error counter */
	private int error_cnt = 0;

	/** Check if the operation should be retried */
	private boolean retry() {
		++error_cnt;
		return error_cnt < getRetryThreshold();
	}

	/** Get the error retry threshold */
	public int getRetryThreshold() {
		return SystemAttrEnum.OPERATION_RETRY_THRESHOLD.getInt();
	}

	/** Get a description of the operation */
	public String getOperationDescription() {
		return getOpName();
	}

	/** Get the operation name */
	protected final String getOpName() {
		return getClass().getSimpleName();
	}

	/** Operation equality test */
	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	/** Get a string description of the operation */
	@Override
	public String toString() {
		return phaseClass().getSimpleName();
	}

	/** Get the class of the current phase */
	public Class phaseClass() {
		Phase<T> p = phase;
		return (p != null) ? p.getClass() : getClass();
	}

	/** Get the current phase */
	public Phase<T> getPhase() {
		return phase;
	}

}
