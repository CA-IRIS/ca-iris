/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2011  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerIO;
import us.mn.state.dot.tms.Device;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.MessagePoller;

/**
 * DeviceImpl is the base class for all field devices, including detectors,
 * cameras, ramp meters, dynamic message signs, etc.
 *
 * @author Douglas Lau
 */
abstract public class DeviceImpl extends BaseObjectImpl implements Device,
	ControllerIO
{
	/** Create a new device */
	protected DeviceImpl(String n) throws TMSException, SonarException {
		super(n);
		notes = "";
	}

	/** Create a device */
	protected DeviceImpl(String n, ControllerImpl c, int p, String nt) {
		super(n);
		controller = c;
		pin = p;
		notes = nt;
	}

	/** Initialize the controller for this device */
	public void initTransients() {
		updateControllerPin(null, 0, controller, pin);
	}

	/** Get the active status */
	public boolean isActive() {
		ControllerImpl c = controller;	// Avoid race
		if(c == null)
			return false;
		else
			return c.getActive();
	}

	/** Get the failure status */
	public boolean isFailed() {
		ControllerImpl c = controller;	// Avoid race
		if(c == null)
			return true;
		else
			return c.isFailed();
	}

	/** Check if the controller has an error */
	public boolean hasError() {
		ControllerImpl c = controller;	// Avoid race
		return c == null || isFailed() || !c.getStatus().isEmpty();
	}

	/** Get the message poller */
	public MessagePoller getPoller() {
		ControllerImpl c = controller;	// Avoid race
		if(c != null)
			return c.getPoller();
		else
			return null;
	}

	/** Controller associated with this traffic device */
	protected ControllerImpl controller;

	/** Update the controller and/or pin.
	 * @param oc Old controller.
	 * @param op Old pin.
	 * @param nc New controller.
	 * @param np New pin. */
	protected void updateControllerPin(ControllerImpl oc, int op,
		ControllerImpl nc, int np)
	{
		if(oc != null)
			oc.setIO(op, null);
		if(nc != null)
			nc.setIO(np, this);
	}

	/** Set the controller of the device */
	public void setController(Controller c) {
		controller = (ControllerImpl)c;
	}

	/** Set the controller of the device */
	public void doSetController(Controller c) throws TMSException {
		if(c == controller)
			return;
		if(c != null && controller != null)
			throw new ChangeVetoException("Device has controller");
		if(c != null && !(c instanceof ControllerImpl))
			throw new ChangeVetoException("Invalid controller");
		if(pin < 1 || pin > Controller.ALL_PINS)
			throw new ChangeVetoException("Invalid pin: " + pin);
		store.update(this, "controller", c);
		updateControllerPin(controller, pin, (ControllerImpl)c, pin);
		setController(c);
	}

	/** Get the controller to which this device is assigned */
	public Controller getController() {
		return controller;
	}

	/** Controller I/O pin number */
	protected int pin = 0;

	/** Set the controller I/O pin number */
	public void setPin(int p) {
		pin = p;
	}

	/** Set the controller I/O pin number */
	public void doSetPin(int p) throws TMSException {
		if(p == pin)
			return;
		if(p < 1 || p > Controller.ALL_PINS)
			throw new ChangeVetoException("Invalid pin: " + p);
		store.update(this, "pin", p);
		updateControllerPin(controller, pin, controller, p);
		setPin(p);
	}

	/** Get the controller I/O pin number */
	public int getPin() {
		return pin;
	}

	/** Administrator notes for this device */
	protected String notes;

	/** Set the administrator notes */
	public void setNotes(String n) {
		notes = n;
	}

	/** Set the administrator notes */
	public void doSetNotes(String n) throws TMSException {
		if(n.equals(notes))
			return;
		store.update(this, "notes", n);
		setNotes(n);
	}

	/** Get the administrator notes */
	public String getNotes() {
		return notes;
	}

	/** Operation which owns the device */
	protected transient OpDevice owner;

	/** Acquire ownership of the device */
	public OpDevice acquire(OpDevice o) {
		try {
			// Name used for unique device acquire/release lock
			synchronized(name) {
				if(owner == null)
					owner = o;
				return owner;
			}
		}
		finally {
			notifyAttribute("operation");
		}
	}

	/** Release ownership of the device */
	public OpDevice release(OpDevice o) {
		try {
			// Name used for unique device acquire/release lock
			synchronized(name) {
				OpDevice _owner = owner;
				if(owner == o)
					owner = null;
				return _owner;
			}
		}
		finally {
			notifyAttribute("operation");
		}
	}

	/** Get a description of the current device operation */
	public String getOperation() {
		OpDevice o = owner;
		if(o == null)
			return "None";
		else
			return o.getOperationDescription();
	}

	/** Device operation status. This is updated during the course of an
	 * operation to indicate the real-time status. */
	protected transient String opStatus = "";

	/** Get the device operoption status */
	public String getOpStatus() {
		return opStatus;
	}

	/** Set the device operation status */
	public void setOpStatus(String s) {
		if(s == null || s.equals(opStatus))
			return;
		opStatus = s;
		notifyAttribute("opStatus");
	}

	/** Destroy an object */
	public void doDestroy() throws TMSException {
		// Don't allow a device to be destroyed if it is assigned to
		// a controller.  This is needed because the Controller io_pins
		// HashMap will still have a reference to the device.
		if(controller != null) {
			throw new ChangeVetoException("Device must be removed" +
				" from controller before being destroyed: " +
				name);
		}
		super.doDestroy();
	}
}
