/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2014  Minnesota Department of Transportation
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.*;
import us.mn.state.dot.tms.client.proxy.TeslaAction;
import us.mn.state.dot.tms.server.comm.DMSPoller;
import us.mn.state.dot.tms.server.comm.DevicePoller;
import us.mn.state.dot.tms.server.comm.ntcip.*;

import static us.mn.state.dot.tms.R_Node.MAX_LANES;

/**
 * Job to update action plans
 *
 * @author Douglas Lau
 */
public class ActionPlanJob extends Job {

	/** Seconds to offset each poll from start of interval */
	static protected final int OFFSET_SECS = 29;

	/** Mapping of ramp meter operating states */
	private final HashMap<RampMeterImpl, Boolean> meters =
		new HashMap<RampMeterImpl, Boolean>();

	/** Create a new action plan job */
	public ActionPlanJob() {
		super(Calendar.SECOND, 30, Calendar.SECOND, OFFSET_SECS);
	}

	/** Perform the action plan job */
	public void perform() throws TMSException {
		updateActionPlanPhases();
		performTimeActions();
		performDmsActions();
		updateDmsMessages();
		performBeaconActions();
		performLaneActions();
		performMeterActions();
		updatePlanStatus();
	}

	/** Update the action plan phases */
	private void updateActionPlanPhases() throws TMSException {
		Iterator<ActionPlan> it = ActionPlanHelper.iterator();
		while(it.hasNext()) {
			ActionPlan ap = it.next();
			if(ap instanceof ActionPlanImpl) {
				ActionPlanImpl api = (ActionPlanImpl)ap;
				api.updatePhase();
			}
		}
	}

	/** Update the action plan status */
	private void updatePlanStatus() throws TMSException {
		Iterator<ActionPlan> it = ActionPlanHelper.iterator();
		while(it.hasNext()) {
			ActionPlan ap = it.next();
			if(ap instanceof ActionPlanImpl) {
				ActionPlanImpl api = (ActionPlanImpl)ap;
				String s = api.checkPlanStatus(api.getPhase());
				api.setPlanStatus(s);
			}
		}
	}

	/** Perform time actions */
	private void performTimeActions() throws TMSException {
		Calendar cal = TimeSteward.getCalendarInstance();
		int min = TimeSteward.currentMinuteOfDayInt();
		Iterator<TimeAction> it = TimeActionHelper.iterator();
		while(it.hasNext()) {
			TimeAction ta = it.next();
			if(ta instanceof TimeActionImpl) {
				TimeActionImpl tai = (TimeActionImpl)ta;
				tai.perform(cal, min);
			}
		}
	}

	/** Perform DMS actions */
	private void performDmsActions() {
		Iterator<DmsAction> it = DmsActionHelper.iterator();
		while(it.hasNext()) {
			DmsAction da = it.next();
			ActionPlan ap = da.getActionPlan();
			if(ap.getActive()) {
				if(ap.getPhase() == da.getPhase())
					performDmsAction(da);
			}
		}
	}

	/** Perform a DMS action */
	private void performDmsAction(DmsAction da) {
		SignGroup sg = da.getSignGroup();
		Iterator<DmsSignGroup> it = DmsSignGroupHelper.iterator();
		while(it.hasNext()) {
			DmsSignGroup dsg = it.next();
			if(dsg.getSignGroup() == sg) {
				DMS dms1 = dsg.getDms();
				if(checkDmsInLcsArray(dms1, da))
					break;
				if(dms1 instanceof DMSImpl) {
					DMSImpl dmsi = (DMSImpl) dms1;
					dmsi.performAction(da);
				}
			}
		}
	}

	/** Check if DMS exists in LCSArray */
	private boolean checkDmsInLcsArray(DMS dms1, DmsAction da) {
		Iterator<LCSArray> it = LCSArrayHelper.iterator();
		DMS dms2;
		while(it.hasNext()) {
			LCSArray lcs = it.next();
			for(int i = 1; i <= MAX_LANES; i++) {
				dms2 = LCSArrayHelper.lookupDMS(lcs, i);
				if(dms1 == dms2) {
					DMSImpl dmsi = (DMSImpl) dms2;
					dmsi.setIsLcs(true);
					performLcsAction(dmsi, da);
					return true;
				}
			}
		}
		return false;
	}

	/** Deploy DMSAction if valid Lane-Use MULTI exists */
	private void performLcsAction(DMSImpl dmsi, DmsAction da) {
		QuickMessage q = da.getQuickMessage();
		Iterator<LaneUseMulti> it = LaneUseMultiHelper.iterator();
		while(it.hasNext()) {
			// Get the Lane-Use MULTI as QuickMessage
			QuickMessage laneQm = it.next().getQuickMessage();
			if (q == laneQm)
				dmsi.performAction(da);
		}
	}

	/** Update the DMS messages */
	private void updateDmsMessages() {
		Iterator<DMS> it = DMSHelper.iterator();
		while(it.hasNext()) {
			DMS dms = it.next();
			if(dms instanceof DMSImpl) {
				DMSImpl dmsi = (DMSImpl)dms;
				dmsi.updateScheduledMessage();
			}
		}
	}

	/** Perform all beacon actions */
	private void performBeaconActions() {
		Iterator<BeaconAction> it = BeaconActionHelper.iterator();
		while(it.hasNext()) {
			BeaconAction ba = it.next();
			ActionPlan ap = ba.getActionPlan();
			if(ap.getActive())
				performBeaconAction(ba, ap.getPhase());
		}
	}

	/** Perform a beacon action */
	private void performBeaconAction(BeaconAction ba, PlanPhase phase) {
		Beacon b = ba.getBeacon();
		if(b != null)
			b.setFlashing(phase == ba.getPhase());
	}

	/** Perform all lane actions */
	private void performLaneActions() {
		Iterator<LaneAction> it = LaneActionHelper.iterator();
		while(it.hasNext()) {
			LaneAction la = it.next();
			ActionPlan ap = la.getActionPlan();
			if(ap.getActive())
				performLaneAction(la, ap.getPhase());
		}
	}

	/** Perform a lane action */
	private void performLaneAction(LaneAction la, PlanPhase phase) {
		LaneMarking lm = la.getLaneMarking();
		if(lm != null)
			lm.setDeployed(phase == la.getPhase());
	}

	/** Perform all meter actions */
	private void performMeterActions() {
		meters.clear();
		Iterator<MeterAction> it = MeterActionHelper.iterator();
		while(it.hasNext()) {
			MeterAction ma = it.next();
			ActionPlan ap = ma.getActionPlan();
			if(ap.getActive())
				updateMeterMap(ma, ap.getPhase());
		}
		for(Map.Entry<RampMeterImpl, Boolean> e: meters.entrySet())
			e.getKey().setOperating(e.getValue());
	}

	/** Update the meter action map */
	private void updateMeterMap(MeterAction ma, PlanPhase phase) {
		RampMeter rm = ma.getRampMeter();
		if(rm instanceof RampMeterImpl) {
			RampMeterImpl meter = (RampMeterImpl)rm;
			boolean o = (phase == ma.getPhase());
			if(meters.containsKey(meter))
				o |= meters.get(meter);
			meters.put(meter, o);
		}
	}
}
