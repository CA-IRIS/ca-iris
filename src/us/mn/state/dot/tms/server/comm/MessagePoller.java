/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2016  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
 * Copyright (C) 2015-2016  Southwest Research Institute
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
import java.net.SocketTimeoutException;
import java.util.Calendar;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.CommProtocol;
import us.mn.state.dot.tms.EventType;
import us.mn.state.dot.tms.server.ControllerImpl;

/**
 * MessagePoller is an abstract class which represents a communication channel 
 * with priority-queued polling.  Subclasses are MndotPoller, NtcipPoller, etc.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 * @author Dan Rossiter
 * @author Jacob Barde
 */
abstract public class MessagePoller<T extends ControllerProperty>
	implements DevicePoller
{
	/** Get a message describing an IO exception */
	static protected String exceptionMessage(IOException e) {
		String m = e.getMessage();
		if (m != null && m.length() > 0)
			return m;
		else
			return e.getClass().getSimpleName();
	}

	/** Auto-closer job service period (sec) */
	static private final int CLOSER_PERIOD = 3;

	/** Message polling log */
	static protected final DebugLog POLL_LOG = new DebugLog("polling");

	/** Priority change log */
	static protected final DebugLog PRIO_LOG = new DebugLog("prio");

	/** Thread group for all message poller threads */
	static private final ThreadGroup GROUP = new ThreadGroup("Poller");

	/** Thread state */
	private enum ThreadState {
		NOT_STARTED,
		STARTING,
		RUNNING,
		CLOSING,
		STOPPED
	}

	/** Write a message to the polling log */
	protected void plog(String msg) {
		if(POLL_LOG.isOpen())
			POLL_LOG.log(thread.getName() + " " + msg);
	}

	/** Thread to poll operations */
	private final Thread thread;

	/** Operation queue */
	protected final OperationQueue<T> queue = new OperationQueue<>();

	/** Messenger for poll/response streams */
	protected final Messenger messenger;

	/** Thread state */
	private volatile ThreadState state = ThreadState.NOT_STARTED;

	/** Whether we are performing a device acquisition */
	private boolean is_acquiring = true;

	/** Set the thread state */
	private void setThreadState(ThreadState st) {
		state = st;
		plog("state: " + st);
	}

	/** Poller status */
	private String status = null;

	/** Set the poller status */
	protected void setStatus(String s) {
		status = s;
	}

	/** Get the poller status */
	@Override
	public String getStatus() {
		String s = status;
		if (s != null)
			return s;
		ThreadState ts = state;
		return (ts == ThreadState.RUNNING) ? "" : ts.toString();
	}

	/** Check if ready for operation */
	@Override
	public boolean isReady() {
		ThreadState ts = state;
		switch(ts) {
		case NOT_STARTED:
		case STARTING:
		case RUNNING:
			return true;
		default:
			return false;
		}
	}

	/** Check if poller is connected */
	@Override
	public boolean isConnected() {
		ThreadState ts = state;
		switch(ts) {
		case STARTING:
		case RUNNING:
			return true;
		default:
			return false;
		}
	}

	/** Hung up flag */
	private boolean hung_up = false;

	/** Check if the messenger was hung up on */
	@Override
	public final boolean wasHungUp() {
		return hung_up;
	}

	/** Max idle time (sec) */
	private long max_idle = Integer.MAX_VALUE;

	/** Scheduler job to automatically close idle connections */
	private class CloserJob extends Job {
		public CloserJob() {
			super(Calendar.SECOND, CLOSER_PERIOD,
				Calendar.SECOND, 0);
		}
		public void perform() {
			closeIfIdle();
		}
	}

	/** The CloserJob instance */
	private final CloserJob closer_job;

	/** Timer thread to auto-close messenger */
	static private final Scheduler CLOSER = new Scheduler("mpcloser");

	/**
	 * Create a new message poller.
	 * @param n CommLink name
	 * @param m the Messenger
	 */
	protected MessagePoller(String n, Messenger m) {
		closer_job = new CloserJob();
 		thread = new Thread(GROUP, "Poller: " + n) {
			@Override
			public void run() {
				operationLoop();
			}
		};
		thread.setDaemon(true);
		setThreadState(ThreadState.NOT_STARTED);
		messenger = m;
	}

	/** Set the receive timeout */
	@Override
	public final void setTimeout(int t) throws IOException {
		messenger.setTimeout(t);
	}

	/** Set the allowed idle time in secs */
	public void setIdleSecs(int is) {
		max_idle = is;
	}

	/** Add an operation to the message poller */
	protected void addOperation(Operation<T> op) {
		if(queue.enqueue(op))
			ensureStarted();
		else
			plog("DROPPING: " + op);
	}

	/** Ensure the thread is started */
	private void ensureStarted() {
		if (shouldStart())
			startPolling();
	}

	/** Should the thread be started? */
	private boolean shouldStart() {
		ThreadState ts = state;
		if (ts == ThreadState.NOT_STARTED) {
			setThreadState(ThreadState.STARTING);
			return true;
		} else
			return false;
	}

	/** Start polling */
	protected void startPolling() {
		try {
			thread.start();
		} catch (IllegalThreadStateException e) {
			// thread was started on another thread between
			// shouldStart returning true and us getting here
			plog("Attempted to start polling when we were already polling.");
		}
	}

	/** Stop polling */
	protected void stopPolling() {
		addOperation(new KillThread<T>());
	}

	/** Destroy the poller */
	@Override
	public final void destroy() {
		if (isConnected())
			stopPolling();
	}

	/** Open messenger and perform operations */
	private void operationLoop() {
		try {
			ensureOpen();
			CLOSER.addJob(closer_job);
			setThreadState(ThreadState.RUNNING);
			performOperations();
			setThreadState(ThreadState.CLOSING);
		}
		catch (HangUpException e) {
			setStatus(exceptionMessage(e));
			hung_up = true;
		}
		catch (IOException e) {
			setStatus(exceptionMessage(e));
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}
		finally {
			ensureClosed();
			drainQueue();
			CLOSER.removeJob(closer_job);
			setThreadState(ThreadState.STOPPED);
		}
	}

	/** Messenger connection state */
	private boolean messenger_open = false;

	/** Timestamp of last activity */
	private long last_activity = 0;

	/** Accessing the last_activity should be performed within synchronization on this object */
	private final Object last_activity_lock = new Object();

	/** Open messenger connection if none present */
	private void ensureOpen() throws IOException {
		synchronized (messenger) {
			if (messenger_open)
				return;

			messenger.open();
			messenger_open = true;
		}
		plog("messenger opened.");
		}

	/** Close messenger connection if present */
	private void ensureClosed() {
		synchronized (messenger) {
			if (!messenger_open)
				return;

			messenger.close();
			messenger_open = false;
		}
		plog("messenger closed.");
	}

	/** If socket has been idle longer than max_idle then close connection */
	private void closeIfIdle() {
		if (is_acquiring)
			return;

		boolean closed = false;
		long idle = 0;

		// do not allow closing based on idle state mid-poll
		synchronized (messenger) {
			if (messenger_open) {
				synchronized (last_activity_lock) {
					idle = calculate_elapsed(last_activity);
				}

				if (idle >= (max_idle * 1000L)) {
					closed = true;
					ensureClosed();
				}
			}
		}
		// avoid performing file I/O inside sync block
		if (closed) {
			plog("idle time " + idle + " ms.  closing messenger.");
		}
	}

	/** Update last activity time to current time */
	private void bump() {
		synchronized (last_activity_lock) {
			last_activity = TimeSteward.currentTimeMillis();
		}
	}

	/** Drain the poll queue */
	private void drainQueue() {
		queue.close();
		while(queue.hasNext()) {
			Operation<T> o = queue.next();
			o.handleCommError(EventType.QUEUE_DRAINED, getStatus());
			if(hung_up)
				o.setSucceeded();
			o.cleanup();
		}
	}

	/** Perform operations on the poll queue */
	private void performOperations() throws IOException {

		Class clazz;

		while(true) {
			// for 0-sec idle timeout, do not start a second op
			closeIfIdle();

			Operation<T> o = queue.next();

			if(o instanceof KillThread)
				break;

			// identify what phase is being polled prior to polling
			clazz = o.phaseClass();

			synchronized (messenger) {
				ensureOpen();
				doPoll(o);
				bump();
			}

			// set after performing poll to ensure we never close
			// before attempting at least one op
			is_acquiring = (OpDevice.AcquireDevice.class.equals(clazz));
		}
	}

	/** Perform one poll for an operation */
	private void doPoll(final Operation<T> o) throws IOException {

		final String oname = o.toString();
		final long start = TimeSteward.currentTimeMillis();

		try {
			synchronized (messenger) {
				o.poll(createMessage(o));
		}

		} catch (DeviceContentionException e) {
			plog("ERROR: DeviceContentionException.");
			handleContention(o, e);
		} catch (DownloadRequestException e) {
			plog("ERROR: DownloadRequestException.");
			if (o instanceof OpController)
				download(((OpController<T>) o).getController(),
					((OpController<T>) o).getPriority());
		} catch (ChecksumException e) {
			plog("ERROR: ChecksumException, draining queue.");
			o.handleCommError(EventType.CHECKSUM_ERROR,
				exceptionMessage(e));
			messenger.drain();
		} catch (ParsingException e) {
			plog("ERROR: ParsingException, draining queue.");
			o.handleCommError(EventType.PARSING_ERROR,
				exceptionMessage(e));
			messenger.drain();
		} catch (ControllerException e) {
			plog("ERROR: ControllerException.");
			if (o instanceof OpController) {
				((OpController<T>) o).handleCommError(
					EventType.CONTROLLER_ERROR,
					exceptionMessage(e));
				((OpController<T>) o).setFailed();
				((OpController<T>) o).setMaintStatus(
				exceptionMessage(e));
		}
		} catch (SocketTimeoutException e) {
			plog("ERROR: SocketTimeoutException.");
			o.handleCommError(EventType.POLL_TIMEOUT_ERROR,
				exceptionMessage(e));
		} finally {
			if (o.isDone() || !requeueOperation(o))
				o.cleanup();

			plog(oname + " elapsed: " + calculate_elapsed(start));
		}
	}

	/** Handle device contention.  Another operation has the device lock.
	 * Ensure that we don't have a priority inversion problem. */
	@SuppressWarnings("unchecked")
	private void handleContention(Operation<T> op,
		DeviceContentionException e)
	{
		handleContention(op, e.operation);
	}

	/** Handle device contention */
	private void handleContention(Operation<T> op, Operation<T> oc) {
		if (oc.getPriority().ordinal() > op.getPriority().ordinal()) {
			if (PRIO_LOG.isOpen()) {
				PRIO_LOG.log("BUMPING " + oc + " from " +
					oc.getPriority() + " to " +
					op.getPriority());
			}
			oc.setPriority(op.getPriority());

			// If, for some crazy reason, the operation is
			// not on our queue, it will not be requeued.
			if (!requeueOperation(oc)) {
				oc.setFailed();
				oc.cleanup();
			}
		} else if (oc.getPriority().ordinal() == op.getPriority()
			.ordinal()) {
			// If, for some crazy reason, the operation is
			// not on our queue, it will not be requeued.
			if (!requeueOperation(oc)) {
				oc.setFailed();
				oc.cleanup();
			} else {
				if (PRIO_LOG.isOpen()) {
					PRIO_LOG.log(
						"BUMPING " + op + " from " +
							op.getPriority()
							+ " to " +
							PriorityLevel.URGENT);
				}
				op.setPriority(PriorityLevel.URGENT);
			}
		}
	}

	/** Requeue an in-progress operation */
	private boolean requeueOperation(Operation<T> op) {
		if(queue.requeue(op))
			return true;
		else {
			plog("DROPPING: " + op);
			return false;
		}
	}

	/** Calculate the elapsed time */
	private static long calculate_elapsed(long start) {
		return TimeSteward.currentTimeMillis() - start;
	}

	/** Check if a drop address is valid */
	abstract public boolean isAddressValid(int drop);

	/** Create a CommMessage, based on Operation type. */
	private CommMessage<T> createMessage(Operation<T> o)
		throws IOException {
		if (o instanceof OpController)
			return createCommMessage((OpController<T>) o);
		else if (o != null)
			return createCommMessageOp(o);
		else
			return null;
	}

	/**
	 * Create a message for the specified OpController.
	 *
	 * @param o The OpController.
	 * @return New comm message.
	 */
	protected CommMessage<T> createCommMessage(OpController<T> o)
		throws IOException
	{
		return new CommMessageImpl<>(messenger, o, protocolLog());
	}

	/**
	 * Create a message for the specified Operation.
	 *
	 * @param o The Operation.
	 * @return New comm message.
	 */
	protected CommMessage<T> createCommMessageOp(Operation<T> o)
		throws IOException {
		// to be overriden by subclass if needed
		return null;
	}

	/** Get the protocol debug log */
	protected DebugLog protocolLog() {
		return null;
	}

	/** Respond to a download request from a controller */
	protected void download(ControllerImpl c, PriorityLevel p) {
		// Subclasses should override this if necessary
	}
}
