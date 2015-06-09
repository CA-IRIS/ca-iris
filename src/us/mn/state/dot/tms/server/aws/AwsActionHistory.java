/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.aws;

import java.util.HashMap;
import java.util.LinkedList;
import us.mn.state.dot.tms.server.DMSImpl;

/**
 * AWS action history, per DMS. This container hashes AwsAction names
 * to an AwsAction that contains information required to make decisions
 * about action activation.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsActionHistory {

	/** Hash action names to action detail */
	private HashMap<String, AwsAction> action_hash = create();

	/** Constructor */
	public AwsActionHistory() {
	}

	/** Create new hash */
	private static HashMap<String, AwsAction> create() {
		return new HashMap<String, AwsAction>();
	}

	/** Get hash size */
	private int size() {
		return action_hash.size();
	}

	/**
	 * Update the state of actions using a list of actions generated
	 * from current sensor conditions.
	 * @param na List of new actions generated in this period.
	 */
	protected void update(long now, LinkedList<AwsAction> na) {
		AwsJob.logfinest("will update hash for new actions=" + na);
		if (na.size() == 0 && size() == 0)
			return;
		// add genenerated actions to container
		markAllNonupdated();
		AwsJob.logfinest("hash1=" + toString());
		for (AwsAction act : na)
			update(act, now);
		AwsJob.logfinest("hash2=" + toString());
		updateUnusedActions(now);
		AwsJob.logfinest("hash3=" + toString());
	}

	/**
	 * Update an action by putting the newly generated action into
	 * the container or updating an action already in the container.
	 * @param na Action to add to container, ignored if null.
	 */
	private void update(AwsAction na, long now) {
		if (na == null)
			return;
		AwsJob.logfinest("adding to container act=" + na);
		AwsJob.logfinest("container=" + toString());
		AwsAction ea = value(na.getName());
		AwsJob.logfinest("existing act=" + ea);
		if (ea == null) {
			na.setState(AwsAction.State.DEPLOYING);
			na.setDeployTimeMs(now);
			AwsJob.logfinest("act=" + na.getName() +
				", not in container, adding");
		}
		else {
			na.setState(ea.getState());
			if (na.getState() == AwsAction.State.UNDEPLOYED) {
				na.setState(AwsAction.State.DEPLOYING);
				na.setDeployTimeMs(now);
			}
			else if (na.getState() == AwsAction.State.DEPLOYING) {
				na.setDeployTimeMs(ea.getDeployTimeMs());
			}
			else if (na.getState() == AwsAction.State.DEPLOYED) {
				na.setDeployTimeMs(ea.getDeployTimeMs());
			}
			else if (na.getState() == AwsAction.State.UNDEPLOYING)
			{
				na.setState(AwsAction.State.DEPLOYED);
				na.setDeployTimeMs(now);
			}
			else
				AwsJob.logsevere("unknown state");
		}
		na.setUpdated(true);
		action_hash.put(na.getName(), na);
		AwsJob.logfinest("action in hash=" + value(na.getName()));
	}

	/** Mark all container actions as non-updated */
	private void markAllNonupdated() {
		AwsJob.logfinest("marking all hash values as non-updated");
		for (String ak : action_hash.keySet()) {
			AwsAction aa = value(ak);
			if (aa != null)
				aa.setUpdated(false);
		}
	}

	/** Return an action in the container with the specified name.
	 * @return The named AwsAction or null if doesn't exist. */
	private AwsAction value(String key) {
		return action_hash.get(key);
	}

	/** Update unused actions not already updated in this period */
	private void updateUnusedActions(long now) {
		AwsJob.logfinest("updating state of non-updated actions");
		for (String ak : action_hash.keySet()) {
			AwsAction aa = value(ak);
			if (aa == null || aa.getUpdated())
				continue;
			if (aa.getState() == AwsAction.State.UNDEPLOYED)
				;
			else if (aa.getState() == AwsAction.State.DEPLOYING) {
				aa.setState(AwsAction.State.UNDEPLOYED);
				AwsJob.logfinest("state changed: " +
					"DEPLOYING to UNDEPLOYED=" + aa);
			}
			else if (aa.getState() == AwsAction.State.DEPLOYED) {
				aa.setState(AwsAction.State.UNDEPLOYING);
				// time indicates when the action undeployed
				aa.setDeployTimeMs(now);
				AwsJob.logfinest("state changed: " +
					"DEPLOYED to UNDEPLOYING=" + aa);
			}
			else if (aa.getState() == AwsAction.State.UNDEPLOYING)
				;
			else
				AwsJob.logsevere("unknown state");
		}
	}

	/** To string */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(AwsActionHistory: ");
		sb.append(" size=").append(size());
		for (String ak : action_hash.keySet()) {
			sb.append(", key=").append(ak);
			sb.append(", value=").append(value(ak));
		}
		sb.append(")");
		return sb.toString();
	}

	/** Select the best (highest priority) action that is deployable.
	 * @return The highest priority action or blank if none. */
	protected AwsAction getBestAction(DMSImpl di, long now) {
		AwsJob.logfinest("d=" + di + ", get best action");
		LinkedList<AwsAction> aas = new LinkedList<AwsAction>();
		for (String ak : action_hash.keySet()) {
			AwsAction aa = value(ak);
			AwsJob.logfinest("d=" + di + ", potential best=" + aa);
			if (aa != null && aa.isDeployable(di, now))
				aas.add(aa);
		}
		AwsJob.logfinest("d=" + di + ", deployable acts=" + aas);
		AwsAction hp = AwsAction.highestPriority(aas);
		if (hp != null) {
			AwsJob.loginfo("d=" + di + ", best action=" + hp.getName());
			return hp;
		}
		else {
			AwsJob.loginfo("d=" + di + ", best action=blank");
			return AwsAction.createBlankAction();
		}
	}

	/** Set all UNDEPLOYING actions to UNDEPLOYED that have expired */
	public void updateUndeploying(DMSImpl di, long now) {
		AwsJob.logfinest("Setting UNDEPLOYING actions to UNDEPLOYED");
		for (String ak : action_hash.keySet()) {
			AwsAction aa = value(ak);
			if (aa == null || aa.getUpdated())
				continue;
			if (aa.getState() != AwsAction.State.UNDEPLOYING)
				continue;
			if (aa.isAgeDeployable(di, now, false)) {
				aa.setState(AwsAction.State.UNDEPLOYED);
				aa.setDeployTimeMs(now);
				AwsJob.logfinest("d=" + di +
					", state changed: " +
					"UNDEPLOYING to UNDEPLOYED=" +
					aa);
			}
		}
	}

}
