/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.schedule;

import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.Beacon;
import us.mn.state.dot.tms.BeaconAction;
import us.mn.state.dot.tms.BeaconActionHelper;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DmsAction;
import us.mn.state.dot.tms.DmsActionHelper;
import us.mn.state.dot.tms.DmsSignGroup;
import us.mn.state.dot.tms.DmsSignGroupHelper;
import us.mn.state.dot.tms.LaneAction;
import us.mn.state.dot.tms.LaneActionHelper;
import us.mn.state.dot.tms.LaneMarking;
import us.mn.state.dot.tms.MeterAction;
import us.mn.state.dot.tms.MeterActionHelper;
import us.mn.state.dot.tms.PlanPhase;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxyView;
import us.mn.state.dot.tms.client.proxy.ProxyWatcher;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.utils.I18N;

import static us.mn.state.dot.tms.client.widget.SwingRunner.runSwing;

/**
 * A plan dispatcher is a GUI panel for dispatching action plans
 *
 * @author Douglas Lau
 */
public class PlanDispatcher extends IPanel implements ProxyView<ActionPlan> {

	/** Name component */
	private final JLabel name_lbl = createValueLabel();

	/** Description component */
	private final JLabel description_lbl = createValueLabel();

	/** DMS count component */
	private final JLabel dms_lbl = createValueLabel();

	/** Beacon count component */
	private final JLabel beacon_lbl = createValueLabel();

	/** Lane count component */
	private final JLabel lane_lbl = createValueLabel();

	/** Meter count component */
	private final JLabel meter_lbl = createValueLabel();

	/** Plan status component */
	private final JLabel plan_lbl = createValueLabel();

	/** Status time component */
	private final JLabel time_lbl = createValueLabel();

	/** Plan phase combo box */
	private final JComboBox<PlanPhase> phase_cbx =
		new JComboBox<PlanPhase>();

	/** Current session */
	private final Session session;

	/** Action plan manager */
	private final PlanManager manager;

	/** Selection model */
	private final ProxySelectionModel<ActionPlan> sel_model;

	/** Selection listener */
	private final ProxySelectionListener sel_listener =
		new ProxySelectionListener()
	{
		public void selectionChanged() {
			setSelected(sel_model.getSingleSelection());
		}
	};

	/** Proxy watcher */
	private final ProxyWatcher<ActionPlan> watcher;

	/** Cache of Action Plan proxy objects */
	private final TypeCache<ActionPlan> cache;

	/** Create a new plan dispatcher */
	public PlanDispatcher(Session s, PlanManager m) {
		session = s;
		manager = m;
		sel_model = manager.getSelectionModel();
		cache =s.getSonarState().getActionPlans();
		watcher = new ProxyWatcher<ActionPlan>(cache, this, true);
	}

	/** Initialize the widgets on the panel */
	@Override
	public void initialize() {
		super.initialize();
		setTitle(I18N.get("action.plan.selected"));
		add("action.plan.name");
		add(name_lbl, Stretch.LEFT);
		add("device.description");
		add(description_lbl, Stretch.LEFT);
		add("dms");
		add(dms_lbl, Stretch.LEFT);
		add("beacons");
		add(beacon_lbl, Stretch.LEFT);
		add("lane_marking.title");
		add(lane_lbl, Stretch.LEFT);
		add("ramp_meter.title");
		add(meter_lbl, Stretch.LEFT);
		add("action.plan.status");
		add(plan_lbl, Stretch.LEFT);
		add("action.plan.status.updated");
		add(time_lbl, Stretch.LEFT);
		add("action.plan.phase");
		add(phase_cbx, Stretch.LEFT);
		watcher.initialize();
		clear();
		sel_model.addProxySelectionListener(sel_listener);
	}

	/** Dispose of the panel */
	@Override
	public void dispose() {
		watcher.dispose();
		sel_model.removeProxySelectionListener(sel_listener);
		clear();
		super.dispose();
	}

	/** Set the selected action plan */
	public void setSelected(ActionPlan ap) {
		watcher.setProxy(ap);
	}

	/** Update one attribute on the form */
	@Override
	public void update(ActionPlan ap, String a) {
		if (a == null) {
			phase_cbx.setAction(null);
			phase_cbx.setModel(createPhaseModel(ap));
			phase_cbx.setEnabled(isUpdatePermitted(ap));
		}
		if (a == null || a.equals("name"))
			name_lbl.setText(ap.getName());
		if (a == null || a.equals("description"))
			description_lbl.setText(ap.getDescription());
		if (a == null || a.equals("active")) {
			dms_lbl.setText(Integer.toString(countDMS(ap)));
			beacon_lbl.setText(Integer.toString(countBeacons(ap)));
			lane_lbl.setText(Integer.toString(countLanes(ap)));
			meter_lbl.setText(Integer.toString(countMeters(ap)));
			description_lbl.setText(ap.getDescription());
		}
		if (a == null || a.equals("planStatus")) {
			plan_lbl.setText(ap.getPlanStatus());
		}
		if (a == null || a.equals("planStatusTimestamp")) {
			time_lbl.setText(ap.getPlanStatusTimestamp());
		}
		if (a == null || a.equals("phase")) {
			phase_cbx.setAction(null);
			ComboBoxModel mdl = phase_cbx.getModel();
			// We must call setSelectedItem on the model, because
			// it might not contain the phase.  In that case,
			// calling JComboBox.setSelectedItem will fail.
			if (mdl instanceof DefaultComboBoxModel) {
				DefaultComboBoxModel dcm =
					(DefaultComboBoxModel) mdl;
				dcm.setSelectedItem(ap.getPhase());
			}
			phase_cbx.setAction(new ChangePhaseAction(ap,
				phase_cbx));
		}
	}

	/** Create a combo box model for plan phases */
	private ComboBoxModel<PlanPhase> createPhaseModel(final ActionPlan ap) {
		TreeSet<PlanPhase> phases = createPhaseSet(ap);
		removeNextPhases(phases);
		DefaultComboBoxModel<PlanPhase> mdl = new DefaultComboBoxModel
			<PlanPhase>();
		mdl.addElement(ap.getDefaultPhase());
		phases.remove(ap.getDefaultPhase());
		for (PlanPhase p: phases)
			mdl.addElement(p);
		mdl.setSelectedItem(ap.getPhase());
		return mdl;
	}

	/** Create a set of phases for an action plan */
	private TreeSet<PlanPhase> createPhaseSet(final ActionPlan ap) {
		final TreeSet<PlanPhase> phases =
			new TreeSet<PlanPhase>(comparator);
		Iterator<DmsAction> dit = DmsActionHelper.iterator();
		while (dit.hasNext()) {
			DmsAction da = dit.next();
			if (da.getActionPlan() == ap)
				phases.add(da.getPhase());
		}
		Iterator<BeaconAction> bit = BeaconActionHelper.iterator();
		while (bit.hasNext()) {
			BeaconAction ba = bit.next();
			if (ba.getActionPlan() == ap)
				phases.add(ba.getPhase());
		}
		Iterator<LaneAction> lit = LaneActionHelper.iterator();
		while (lit.hasNext()) {
			LaneAction la = lit.next();
			if (la.getActionPlan() == ap)
				phases.add(la.getPhase());
		}
		Iterator<MeterAction> mit = MeterActionHelper.iterator();
		while (mit.hasNext()) {
			MeterAction ma = mit.next();
			if (ma.getActionPlan() == ap)
				phases.add(ma.getPhase());
		}
		return phases;
	}

	/** Comparator for plan phases */
	private final Comparator<PlanPhase> comparator =
		new Comparator<PlanPhase>()
	{
		public int compare(PlanPhase a, PlanPhase b) {
			String aa = a.getName();
			String bb = b.getName();
			return aa.compareTo(bb);
		}
	};

	/** Remove phases which are "next" phases */
	private void removeNextPhases(TreeSet<PlanPhase> phases) {
		TreeSet<PlanPhase> n_phases =
			new TreeSet<PlanPhase>(comparator);
		for (PlanPhase p: phases) {
			PlanPhase np = p.getNextPhase();
			if (np != null)
				n_phases.add(np);
		}
		phases.removeAll(n_phases);
	}

	/** Get a count of DMS controlled by an action plan */
	private int countDMS(ActionPlan p) {
		HashSet<SignGroup> plan_groups = new HashSet<SignGroup>();
		Iterator<DmsAction> dit = DmsActionHelper.iterator();
		while (dit.hasNext()) {
			DmsAction da = dit.next();
			if (da.getActionPlan() == p)
				plan_groups.add(da.getSignGroup());
		}
		HashSet<DMS> plan_signs = new HashSet<DMS>();
		Iterator<DmsSignGroup> git = DmsSignGroupHelper.iterator();
		while (git.hasNext()) {
			DmsSignGroup dsg = git.next();
			if (plan_groups.contains(dsg.getSignGroup()))
				plan_signs.add(dsg.getDms());
		}
		return plan_signs.size();
	}

	/** Get a count a beacons controlled by an action plan */
	private int countBeacons(ActionPlan p) {
		HashSet<Beacon> plan_beacons = new HashSet<Beacon>();
		Iterator<BeaconAction> bit = BeaconActionHelper.iterator();
		while (bit.hasNext()) {
			BeaconAction ba = bit.next();
			if (ba.getActionPlan() == p)
				plan_beacons.add(ba.getBeacon());
		}
		return plan_beacons.size();
	}

	/** Get a count a lane markings controlled by an action plan */
	private int countLanes(ActionPlan p) {
		HashSet<LaneMarking> plan_lanes = new HashSet<LaneMarking>();
		Iterator<LaneAction> lit = LaneActionHelper.iterator();
		while (lit.hasNext()) {
			LaneAction la = lit.next();
			if (la.getActionPlan() == p)
				plan_lanes.add(la.getLaneMarking());
		}
		return plan_lanes.size();
	}

	/** Get a count a ramp meters controlled by an action plan */
	private int countMeters(ActionPlan p) {
		HashSet<RampMeter> plan_meters = new HashSet<RampMeter>();
		Iterator<MeterAction> mit = MeterActionHelper.iterator();
		while (mit.hasNext()) {
			MeterAction ma = mit.next();
			if (ma.getActionPlan() == p)
				plan_meters.add(ma.getRampMeter());
		}
		return plan_meters.size();
	}

	/** Check if the user is permitted to update the given action plan */
	private boolean isUpdatePermitted(ActionPlan ap) {
		return session.isUpdatePermitted(ap, "phase") && ap.getActive();
	}

	/** Clear the proxy view */
	@Override
	public void clear() {
		name_lbl.setText("");
		description_lbl.setText("");
		dms_lbl.setText("");
		beacon_lbl.setText("");
		lane_lbl.setText("");
		meter_lbl.setText("");
		phase_cbx.setAction(null);
		plan_lbl.setText("");
		time_lbl.setText("");
		phase_cbx.setModel(new DefaultComboBoxModel<PlanPhase>());
		phase_cbx.setSelectedItem(null);
		phase_cbx.setEnabled(false);
	}
}
