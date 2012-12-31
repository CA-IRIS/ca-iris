/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DmsAction;
import us.mn.state.dot.tms.DmsActionHelper;
import us.mn.state.dot.tms.LaneAction;
import us.mn.state.dot.tms.LaneActionHelper;
import us.mn.state.dot.tms.LaneMarking;
import us.mn.state.dot.tms.MeterAction;
import us.mn.state.dot.tms.MeterActionHelper;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.SignGroupHelper;
import us.mn.state.dot.tms.TMSException;

/**
 * An action plan is a set of actions which can be deployed together.
 *
 * @author Douglas Lau
 */
public class ActionPlanImpl extends BaseObjectImpl implements ActionPlan {

	/** Load all the action plans */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, ActionPlanImpl.class);
		store.query("SELECT name, description, sync_actions, " +
			"sticky, active, default_phase, phase FROM iris." +
			SONAR_TYPE  + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new ActionPlanImpl(
					namespace,
					row.getString(1),  // name
					row.getString(2),  // description
					row.getBoolean(3), // sync_actions
					row.getBoolean(4), // sticky
					row.getBoolean(5), // active
					row.getString(6),  // default_phase
					row.getString(7)   // phase
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("description", description);
		map.put("sync_actions", sync_actions);
		map.put("sticky", sticky);
		map.put("active", active);
		map.put("default_phase", default_phase);
		map.put("phase", phase);
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new action plan */
	public ActionPlanImpl(String n) {
		super(n);
		description = "";
	}

	/** Create a new action plan */
	private ActionPlanImpl(Namespace ns, String n, String dsc, boolean sa,
		boolean st, boolean a, String dp, String p)
	{
		this(n, dsc, sa, st, a,
		     (PlanPhase)ns.lookupObject(PlanPhase.SONAR_TYPE, dp),
		     (PlanPhase)ns.lookupObject(PlanPhase.SONAR_TYPE, p));
	}

	/** Create a new action plan */
	private ActionPlanImpl(String n, String dsc, boolean sa, boolean st,
		boolean a, PlanPhase dp, PlanPhase p)
	{
		this(n);
		description = dsc;
		sync_actions = sa;
		sticky = st;
		active = a;
		default_phase = dp;
		phase = p;
	}

	/** Plan description */
	protected String description;

	/** Set the description */
	public void setDescription(String d) {
		description = d;
	}

	/** Set the description */
	public void doSetDescription(String d) throws TMSException {
		if(d.equals(description))
			return;
		store.update(this, "description", d);
		setDescription(d);
	}

	/** Get the description */
	public String getDescription() {
		return description;
	}

	/** Sync actions flag */
	protected boolean sync_actions;

	/** Set the sync actions flag */
	public void setSyncActions(boolean s) {
		sync_actions = s;
	}

	/** Set the sync actions flag */
	public void doSetSyncActions(boolean s) throws TMSException {
		if(s == sync_actions)
			return;
		store.update(this, "sync_actions", s);
		setSyncActions(s);
	}

	/** Get the sync actions flag */
	public boolean getSyncActions() {
		return sync_actions;
	}

	/** Sticky flag */
	protected boolean sticky;

	/** Set the sticky flag */
	public void setSticky(boolean s) {
		sticky = s;
	}

	/** Set the sticky flag */
	public void doSetSticky(boolean s) throws TMSException {
		if(s == sticky)
			return;
		store.update(this, "sticky", s);
		setSticky(s);
	}

	/** Get the sticky flag */
	public boolean getSticky() {
		return sticky;
	}

	/** Active status */
	protected boolean active;

	/** Set the active status */
	public void setActive(boolean a) {
		active = a;
	}

	/** Set the active status */
	public void doSetActive(boolean a) throws TMSException {
		if(a == active)
			return;
		store.update(this, "active", a);
		setActive(a);
	}

	/** Get the active status */
	public boolean getActive() {
		return active;
	}

	/** Default plan phase */
	private PlanPhase default_phase;

	/** Set the default phase */
	public void setDefaultPhase(PlanPhase dp) {
		default_phase = dp;
	}

	/** Set the default phase */
	public void doSetDefaultPhase(PlanPhase dp) throws TMSException {
		if(dp == default_phase)
			return;
		store.update(this, "default_phase", dp);
		setDefaultPhase(dp);
	}

	/** Get the default phase */
	public PlanPhase getDefaultPhase() {
		return default_phase;
	}

	/** Current plan phase */
	private PlanPhase phase;

	/** Time stamp for last phase change */
	private long phase_time = TimeSteward.currentTimeMillis();

	/** Set the phase */
	public void setPhase(PlanPhase p) {
		phase = p;
		phase_time = TimeSteward.currentTimeMillis();
	}

	/** Set the phase */
	public void doSetPhase(PlanPhase p) throws TMSException {
		if(p == phase)
			return;
		if(getSyncActions()) {
			validateDmsActions();
			validateLaneActions();
			validateMeterActions();
		}
		store.update(this, "phase", p);
		setPhase(p);
	}

	/** Get the phase */
	public PlanPhase getPhase() {
		return phase;
	}

	/** Set the deployed phase (and notify clients) */
	public void setPhaseNotify(PlanPhase p) throws TMSException {
		if(p != phase)
			doSetPhaseNotify(p);
	}

	/** Set the deployed phase with notification */
	private void doSetPhaseNotify(PlanPhase p) throws TMSException {
		doSetPhase(p);
		notifyAttribute("phase");
	}

	/** Validate that all DMS actions are deployable */
	private void validateDmsActions() throws ChangeVetoException {
		Iterator<DmsAction> it = DmsActionHelper.iterator();
		while(it.hasNext()) {
			DmsAction da = it.next();
			if(da.getActionPlan() == this && !isDeployable(da)) {
				throw new ChangeVetoException("DMS action " +
					da.getName() + " not deployable");
			}
		}
	}

	/** Check if a DMS action is deployable */
	private boolean isDeployable(final DmsAction da) {
		for(DMS dms: SignGroupHelper.find(da.getSignGroup())) {
			if(dms instanceof DMSImpl) {
				if(!((DMSImpl)dms).isDeployable(da))
					return false;
			}
		}
		return true;
	}

	/** Validate that all lane actions are deployable */
	private void validateLaneActions() throws ChangeVetoException {
		Iterator<LaneAction> it = LaneActionHelper.iterator();
		while(it.hasNext()) {
			LaneAction la = it.next();
			if(la.getActionPlan() == this && !isDeployable(la)) {
				throw new ChangeVetoException("Lane action " +
					la.getName() + " not deployable");
			}
		}
	}

	/** Check if a lane action is deployable */
	private boolean isDeployable(LaneAction la) {
		LaneMarking lm = la.getLaneMarking();
		if(lm instanceof LaneMarkingImpl)
			return !((LaneMarkingImpl)lm).isFailed();
		else
			return false;
	}

	/** Validate that all meter actions are deployable */
	private void validateMeterActions() throws ChangeVetoException {
		Iterator<MeterAction> it = MeterActionHelper.iterator();
		while(it.hasNext()) {
			MeterAction ma = it.next();
			if(ma.getActionPlan() == this && !isDeployable(ma)) {
				throw new ChangeVetoException("Meter action " +
					ma.getName() + " not deployable");
			}
		}
	}

	/** Check if a meter action is deployable */
	private boolean isDeployable(MeterAction ma) {
		RampMeter rm = ma.getRampMeter();
		if(rm instanceof RampMeterImpl)
			return !((RampMeterImpl)rm).isFailed();
		else
			return false;
	}

	/** Update the plan phase */
	public void updatePhase() throws TMSException {
		PlanPhase p = phase;
		if(p != null) {
			PlanPhase np = p.getNextPhase();
			if(np != null && phaseSecs() >= p.getHoldTime())
				setPhaseNotify(np);
		}
	}

	/** Get the number of seconds in the current phase */
	private int phaseSecs() {
		long elapsed = TimeSteward.currentTimeMillis() - phase_time;
		return (int)(elapsed / 1000);
	}
}
