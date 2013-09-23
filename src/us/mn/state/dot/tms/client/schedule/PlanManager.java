/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2013  Minnesota Department of Transportation
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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import javax.swing.JPopupMenu;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.ActionPlan;
import us.mn.state.dot.tms.DmsAction;
import us.mn.state.dot.tms.DmsActionHelper;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.LaneAction;
import us.mn.state.dot.tms.LaneActionHelper;
import us.mn.state.dot.tms.LaneMarking;
import us.mn.state.dot.tms.MeterAction;
import us.mn.state.dot.tms.MeterActionHelper;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.TimeAction;
import us.mn.state.dot.tms.TimeActionHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.dms.DmsMarker;
import us.mn.state.dot.tms.client.meter.MeterMarker;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.StyleSummary;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A plan manager is a container for SONAR action plan objects.
 *
 * @author Douglas Lau
 */
public class PlanManager extends ProxyManager<ActionPlan> {

	/** Get the action plan cache */
	static protected TypeCache<ActionPlan> getCache(Session s) {
		return s.getSonarState().getActionPlans();
	}

	/** User session */
	protected final Session session;

	/** Create a new action plan manager */
	public PlanManager(Session s, GeoLocManager lm) {
		super(getCache(s), lm);
		session = s;
		cache.addProxyListener(this);
	}

	/** Get the proxy type name */
	@Override public String getProxyType() {
		return I18N.get("action.plan");
	}

	/** Get longer proxy type name for display */
	@Override public String getLongProxyType() {
		return I18N.get("action.plan.long");
	}

	/** Create a plan map tab */
	public PlanTab createTab() {
		return new PlanTab(session, this);
	}

	/** Find the map geo location for a proxy */
	protected GeoLoc getGeoLoc(ActionPlan proxy) {
		return null;
	}

	/** Get a transformed marker shape */
	protected Shape getShape(AffineTransform at) {
		return null;
	}

	/** Create a theme for action plans */
	protected PlanTheme createTheme() {
		PlanTheme theme = new PlanTheme(this);
		theme.addStyle(ItemStyle.DMS, new DmsMarker());
		theme.addStyle(ItemStyle.METER, new MeterMarker());
		theme.addStyle(ItemStyle.LANE);
		theme.addStyle(ItemStyle.TIME, new TimeMarker());
		theme.addStyle(ItemStyle.ACTIVE);
		theme.addStyle(ItemStyle.ALL);
		return theme;
	}

	/** Show the properties form for the selected proxy */
	public void showPropertiesForm() {
		// There is no action plan properties form
	}

	/** Create a popup menu for the selected proxy object(s) */
	protected JPopupMenu createPopup() {
		return null;
	}

	/** Check the style of the specified proxy */
	public boolean checkStyle(ItemStyle is, ActionPlan proxy) {
		if(!canUpdate(proxy))
			return false;
		switch(is) {
		case DMS:
			return proxy.getActive() && hasDmsAction(proxy);
		case METER:
			return proxy.getActive() && hasMeterAction(proxy);
		case LANE:
			return proxy.getActive() && hasLaneAction(proxy);
		case TIME:
			return proxy.getActive() && hasTimeAction(proxy);
		case ACTIVE:
			return proxy.getActive();
		case ALL:
			return true;
		default:
			return false;
		}
	}

	/** Check if the user can update the given action plan */
	private boolean canUpdate(ActionPlan plan) {
		return session.canUpdate(plan, "phase");
	}

	/** Test if an action plan has time actions */
	private boolean hasTimeAction(ActionPlan p) {
		Iterator<TimeAction> it = TimeActionHelper.iterator();
		while(it.hasNext()) {
			TimeAction ta = it.next();
			if(ta.getActionPlan() == p)
				return true;
		}
		return false;
	}

	/** Test if an action plan has DMS actions */
	private boolean hasDmsAction(ActionPlan p) {
		Iterator<DmsAction> it = DmsActionHelper.iterator();
		while(it.hasNext()) {
			DmsAction da = it.next();
			if(da.getActionPlan() == p)
				return true;
		}
		return false;
	}

	/** Test if an action plan has lane actions */
	private boolean hasLaneAction(ActionPlan p) {
		Iterator<LaneAction> it = LaneActionHelper.iterator();
		while(it.hasNext()) {
			LaneAction la = it.next();
			if(la.getActionPlan() == p)
				return true;
		}
		return false;
	}

	/** Test if an action plan has meter actions */
	private boolean hasMeterAction(ActionPlan p) {
		Iterator<MeterAction> it = MeterActionHelper.iterator();
		while(it.hasNext()) {
			MeterAction ma = it.next();
			if(ma.getActionPlan() == p)
				return true;
		}
		return false;
	}

	/** Get the description of an action plan */
	public String getDescription(ActionPlan plan) {
		return plan.getName() + " -- " + plan.getDescription();
	}

	/** Get the layer zoom visibility threshold */
	protected int getZoomThreshold() {
		return 10;
	}
}
