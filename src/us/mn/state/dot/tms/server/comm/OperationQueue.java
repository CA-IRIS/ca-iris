/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
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


import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.comm.axisptz.OpAxisPTZ;

/**
 * A prioritized queue which sorts Operation objects by their priority
 * class. Operations with the same priority are sorted FIFO.
 *
 * @author Douglas Lau
 */
public final class OperationQueue<T extends ControllerProperty> {

	/** Queuing log */
	static protected final DebugLog QUEUE_LOG = new DebugLog("queuing");

	static protected void qlog(String msg) {
		if(QUEUE_LOG.isOpen())
			QUEUE_LOG.log(msg);
	}

	static String opInfo(Operation op) {
		if(op instanceof OpAxisPTZ)
			return ((OpAxisPTZ)op).getProp().toString();

		return op.toString();
	}
	/** Front node in the queue */
	private Node<T> front = null;

	/** Current working operation.  This is needed so that an "equal"
	 * operation cannot be added while work is in progress. */
	private Operation<T> work = null;

	/** Flag to tell when the poller is closing */
	private boolean closing = false;

	public OperationQueue() {
		qlog("creation.");
	}

	/** Close the queue for new operations */
	public synchronized void close() {
		qlog("closing.");
		closing = true;
	}

	/** Enqueue a new operation */
	public synchronized boolean enqueue(Operation<T> op) {
		if (shouldAdd(op)) {
			qlog("enqueue: accept operation, op=" + opInfo(op));
			op.begin();
			add(op);
			return true;
		} else {
			qlog("enqueue: reject operation, op=" + opInfo(op));
			return false;
		}
	}

	/** Check if an operation should be added to the queue */
	private synchronized boolean shouldAdd(Operation<T> op) {
		boolean c = !closing;
		boolean h = !contains(op);
		qlog("shouldAdd: closing=" + !c + ", contains=" + !h + ", op=" + opInfo(op));

		return c && h;
	}

	/** Check if the queue contains a given operation */
	private synchronized boolean contains(Operation<T> op) {
		if (op.equals(work) && !work.isDone()) {
			qlog("contains: work, it is not done");
			return true;
		}
		Node<T> node = front;
		while (node != null) {
			Operation<T> nop = node.operation;
			if (op.equals(nop) && !nop.isDone()) {
				qlog("contains: queue, it is not done");
				return true;
			}
			node = node.next;
		}
		return false;
	}

	/** Add an operation to the queue */
	private void add(Operation<T> op) {
		qlog("add: adding operation to queue, pre-count=" + count() + ", op=" + opInfo(op));
		PriorityLevel priority = op.getPriority();
		Node<T> prev = null;
		Node<T> node = front;
		while(node != null) {
			if(priority.ordinal() < node.priority.ordinal())
				break;
			prev = node;
			node = node.next;
		}
		node = new Node<T>(op, node);
		if(prev == null)
			front = node;
		else
			prev.next = node;
		qlog("add: adding operation to queue, post-count=" + count() + ", op=" + opInfo(op));
		notify();
	}

	/** return the count of the queue */
	private synchronized long count() {
		long i = 0;
		Node<T> node = front;
		while(node != null) {
			node = node.next;
			i++;
		}

		return i;
	}

	/** Requeue an in-progress operation */
	public synchronized boolean requeue(Operation<T> op) {
		if((remove(op) == op) && !closing) {
			qlog("requeue: accept operation, op=" + opInfo(op));
			add(op);
			return true;
		} else {
			qlog("requeue: reject operation, op=" + opInfo(op));
			return false;
		}
	}

	/** Remove an operation from the queue */
	private synchronized Operation<T> remove(Operation<T> op) {
		qlog("remove: removing operation from queue, pre-count=" + count() + ", op=" + opInfo(op));
		if(op == work) {
			work = null;
			qlog("remove: removing operation from work, post-count=" + count() + ", op=" + opInfo(op));
			return op;
		}
		Node<T> prev = null;
		Node<T> node = front;
		while(node != null) {
			if(node.operation == op) {
				if(prev == null)
					front = node;
				else
					prev.next = node;
				return op;
			}
			prev = node;
			node = node.next;
		}
		qlog("remove: removing operation from queue, post-count=" + count() + ", op=" + opInfo(op));
		return null;
	}

	/** Does the queue have any elements? */
	public synchronized boolean hasNext() {
		return front != null;
	}

	/** Get the next operation from the queue (and remove it) */
	public synchronized Operation<T> next() {
		work = null;
		waitOp();
		work = front.operation;
		front = front.next;
		qlog("next: count=" + count() + ", work=" + work);
		return work;
	}

	/** Wait for an operation to be added to the queue */
	private synchronized void waitOp() {
		while(!hasNext()) {
			try {
				wait();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** Inner class for nodes in the queue */
	static private final class Node<T extends ControllerProperty> {
		final Operation<T> operation;
		final PriorityLevel priority;
		Node<T> next;
		Node(Operation<T> op, Node<T> n) {
			operation = op;
			priority = op.getPriority();
			next = n;
		}
	}

	/** Do something to each operation in the queue */
	public synchronized void forEach(OperationHandler<T> handler) {
		Operation<T> w = work;
		if(w != null)
			handler.handle(w.getPriority(), w);
		Node<T> node = front;
		while(node != null) {
			handler.handle(node.priority, node.operation);
			node = node.next;
		}
	}
}
