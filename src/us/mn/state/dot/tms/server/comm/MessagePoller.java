/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
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
 */
abstract public class MessagePoller<T extends ControllerProperty>
	implements DevicePoller
{
	/** Get a message describing an IO exception */
	static private String exceptionMessage(IOException e) {
		String m = e.getMessage();
		if(m != null && m.length() > 0)
			return m;
		else
			return e.getClass().getSimpleName();
	}

	/** Connection mode enum */
	public enum ConnMode {PERSIST, AUTO, PER_OP};

	/** The connection mode */
	private final ConnMode conn_mode;

	/** Auto-closer job service period (sec) */
	static private final int CLOSER_PERIOD = 3;

	/** Message polling log */
	static private final DebugLog POLL_LOG = new DebugLog("polling");

	/** Priority change log */
	static private final DebugLog PRIO_LOG = new DebugLog("prio");

	/** Thread group for all message poller threads */
	static private final ThreadGroup GROUP = new ThreadGroup("Poller");

	/** Thread state */
	static private enum ThreadState {
		NOT_STARTED,
		STARTING,
		RUNNING,
		CLOSING,
		STOPPED;
	}

	/** Write a message to the polling log */
	private void plog(String msg) {
		if(POLL_LOG.isOpen())
			POLL_LOG.log(thread.getName() + " " + msg);
	}

	/** Thread to poll operations */
	private final Thread thread;

	/** Operation queue */
	protected final OperationQueue<T> queue = new OperationQueue<T>();

	/** Messenger for poll/response streams */
	protected final Messenger messenger;

	/** Thread state */
	private ThreadState state = ThreadState.NOT_STARTED;

	/** Set the thread state */
	private synchronized void setThreadState(ThreadState st) {
		state = st;
		plog("state: " + st);
	}

	/** Poller status */
	private String status = null;

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
	public synchronized boolean isReady() {
		switch(state) {
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
	public synchronized boolean isConnected() {
		switch(state) {
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

	/** Max idle time (sec) for connection mode AUTO */
	private long max_idle = 0;

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
	 * @param cm the connection mode
	 * @param idle max idle time (sec) to use for conn mode AUTO
	 */
	protected MessagePoller(String n, Messenger m, ConnMode cm, int idle) {
		conn_mode = cm;
		max_idle = idle;
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

	/**
	 * Create a new message poller with persistent connection mode.
	 * @param n CommLink name
	 * @param m the Messenger
	 */
	protected MessagePoller(String n, Messenger m) {
		this(n, m, ConnMode.PERSIST, 0);
	}

	/** Set the receive timeout */
	@Override
	public final void setTimeout(int t) throws IOException {
		messenger.setTimeout(t);
	}

	/** Add an operation to the message poller */
	protected void addOperation(Operation<T> op) {
		if(queue.enqueue(op))
			ensureStarted();
		else
			plog("DROPPING " + op);
	}

	/** Ensure the thread is started */
	private void ensureStarted() {
		if(shouldStart())
			thread.start();
	}

	/** Should the thread be started? */
	private synchronized boolean shouldStart() {
		if(state == ThreadState.NOT_STARTED) {
			setThreadState(ThreadState.STARTING);
			return true;
		} else
			return false;
	}

	/** Destroy the poller */
	@Override
	public final void destroy() {
		if (isConnected())
			addOperation(new KillThread<T>());
	}

	/** Open messenger and perform operations */
	private void operationLoop() {
		try {
			if (conn_mode == ConnMode.PERSIST)
				ensureOpen();
			else if (conn_mode == ConnMode.AUTO)
				CLOSER.addJob(closer_job);
			setThreadState(ThreadState.RUNNING);
			performOperations();
			setThreadState(ThreadState.CLOSING);
		}
		catch(HangUpException e) {
			status = exceptionMessage(e);
			hung_up = true;
		}
		catch(IOException e) {
			status = exceptionMessage(e);
		}
		catch(RuntimeException e) {
			e.printStackTrace();
		}
		finally {
			ensureClosed();
			drainQueue();
			if (conn_mode == ConnMode.AUTO)
				CLOSER.removeJob(closer_job);
			setThreadState(ThreadState.STOPPED);
		}
	}

	/** Messenger connection state */
	private boolean messenger_open = false;

	/** Timestamp of last activity */
	private long last_activity = 0;

	private synchronized void ensureOpen() throws IOException {
		if (messenger_open)
			return;
		messenger.open();
		plog("messenger opened.");
		messenger_open = true;
	}

	private synchronized void ensureClosed() {
		if (!messenger_open)
			return;
		messenger.close();
		plog("messenger closed.");
		messenger_open = false;
	}
	private synchronized void closeIfIdle() {
		long now = TimeSteward.currentTimeMillis();
		long idle = now - last_activity;
		if ((messenger_open) && (idle >= (max_idle * 1000))) {
			plog("idle time " + idle + " ms.  closing messenger.");
			ensureClosed();
		}
	}

	/** Update last activity time to current time */
	private synchronized void bump() {
		last_activity = TimeSteward.currentTimeMillis();
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
		while(true) {
			Operation<T> o = queue.next();
			boolean acquire = (o.getPhase() instanceof
				OpDevice.AcquireDevice);
			// bump before poll to prevent closure during poll
			bump();
			ensureOpen();
			if(o instanceof KillThread)
				break;
			if(o instanceof OpController)
				doPoll((OpController<T>)o);
			// final bump when poll complete
			bump();
			// don't disconnect after AcquireDevice phases
			if ((conn_mode == ConnMode.PER_OP) && (!acquire))
				ensureClosed();
		}
	}

	/** Perform one poll for an operation */
	private void doPoll(final OpController<T> o) throws IOException {
		final String oname = o.toString();
		long start = TimeSteward.currentTimeMillis();
		try {
			o.poll(createCommMessage(o));
		}
		catch(DeviceContentionException e) {
			handleContention(o, e);
		}
		catch(DownloadRequestException e) {
			download(o.getController(), o.getPriority());
		}
		catch(ChecksumException e) {
			o.handleCommError(EventType.CHECKSUM_ERROR,
				exceptionMessage(e));
			messenger.drain();
		}
		catch(ParsingException e) {
			o.handleCommError(EventType.PARSING_ERROR,
				exceptionMessage(e));
			messenger.drain();
		}
		catch(ControllerException e) {
			o.handleCommError(EventType.CONTROLLER_ERROR,
				exceptionMessage(e));
			o.setFailed();
			o.setMaintStatus(exceptionMessage(e));
		}
		catch(SocketTimeoutException e) {
			o.handleCommError(EventType.POLL_TIMEOUT_ERROR,
				exceptionMessage(e));
		}
		finally {
			if(o.isDone() || !requeueOperation(o))
				o.cleanup();
			if(POLL_LOG.isOpen()) {
				plog(oname + " elapsed: " +
					calculate_elapsed(start));
			}
		}
	}

	/** Handle device contention.  Another operation has the device lock.
	 * Ensure that we don't have a priority inversion problem. */
	private void handleContention(Operation<T> op,
		DeviceContentionException e)
	{
		Operation<T> oc = e.operation;
		if(oc.getPriority().ordinal() > op.getPriority().ordinal()) {
			if(PRIO_LOG.isOpen()) {
				PRIO_LOG.log("BUMPING " + oc + " from " +
					oc.getPriority() + " to " +
					op.getPriority());
			}
			oc.setPriority(op.getPriority());
			// If, for some crazy reason, the operation is
			// not on our queue, it will not be requeued.
			if(!requeueOperation(oc)) {
				oc.setFailed();
				oc.cleanup();
			}
		}
	}

	/** Requeue an in-progress operation */
	private boolean requeueOperation(Operation<T> op) {
		if(queue.requeue(op))
			return true;
		else {
			plog("DROPPING " + op);
			return false;
		}
	}

	/** Calculate the elapsed time */
	private long calculate_elapsed(long start) {
		return TimeSteward.currentTimeMillis() - start;
	}

	/** Check if a drop address is valid */
	abstract public boolean isAddressValid(int drop);

	/** Create a message for the specified operation.
	 * @param o The operation.
	 * @return New comm message. */
	protected CommMessage<T> createCommMessage(OpController<T> o)
		throws IOException
	{
		return new CommMessageImpl<T>(messenger, o, protocolLog());
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
