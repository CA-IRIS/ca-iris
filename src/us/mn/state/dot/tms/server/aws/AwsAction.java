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

import java.util.LinkedList;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.IrisUserHelper;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMsgSource;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.QuickMessage;
import us.mn.state.dot.tms.QuickMessageHelper;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.utils.MultiString;

/**
 * An AWS action, which contains all the data necessary to place
 * an AWS message on a sign.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class AwsAction {

	/** Action state type */
	protected enum State {UNDEPLOYED, DEPLOYING, DEPLOYED, UNDEPLOYING};

	/** Interval for time boundary calculations */
	static protected int SLOP_SECS = 5;

	/** State of this action */
	private State action_state = State.UNDEPLOYED;

	/** Name of this action */
	private final String action_name;

	/** Time the action was deployed or undeployed */
	//FIXME: separate field and methods for deploy and undeploy
	private long deploy_time_ms = -1;

	/** Name of quick message associated with this action. Is
	 * empty for the blanking action. */
	private final String quick_message_name;

	/** Activation priority
	 * @see us.mn.state.dot.tms.DMSMessagePriority */
	private final DMSMessagePriority act_priority;

	/** Runtime priority
	 * @see us.mn.state.dot.tms.DMSMessagePriority */
	private final DMSMessagePriority run_priority;

	/** AWS priority, a higher value is higher priority */
	private final int aws_priority;

	/** Updated indicator */
	private boolean action_updated = false;

	/** Rule that produced this action, may be null */
	private final AwsRule aws_rule;

	/** Constructor */
	public AwsAction(AwsRule ar, int awsp, String n, String qmn,
		DMSMessagePriority ap, DMSMessagePriority rp)
	{
		aws_rule = ar;
		aws_priority = awsp;
		action_name = n;
		setDeployTimeMs(TimeSteward.currentTimeMillis());
		quick_message_name = qmn;
		act_priority = ap;
		run_priority = rp;
	}

	/** Create a blanking action */
	static protected AwsAction createBlankAction() {
		return new AwsAction(null, 0, "Blank", "",
			DMSMessagePriority.AWS, DMSMessagePriority.BLANK);
	}

	/** Get name */
	public String getName() {
		return action_name;
	}

	/** Get updated */
	protected boolean getUpdated() {
		return action_updated;
	}

	/** Set updated */
	protected void setUpdated(boolean u) {
		action_updated = u;
	}

	/** Get the deploy/undeploy time in MS (may be -1) */
	public long getDeployTimeMs() {
		return deploy_time_ms;
	}

	/** Set the deploy/undeploy time in MS (may be -1) */
	public void setDeployTimeMs(long t) {
		deploy_time_ms = t;
	}

	/** Get action state */
	protected State getState() {
		return action_state;
	}

	/** Set action state */
	protected void setState(State s) {
		action_state = s;
	}

	/** Calc delta seconds between now and enabled time */
	public long getAgeSecs() {
		return getAgeSecs(TimeSteward.currentTimeMillis());
	}

	/** Calc delta seconds between now and enabled time */
	public long getAgeSecs(long now) {
		return (now - getDeployTimeMs()) / 1000L;
	}

	/** Get the AWS rule priority */
	public int getAwsPriority() {
		return aws_priority;
	}

	/** Is a blanking action? */
	private boolean isBlank() {
		return quick_message_name.isEmpty();
	}

	/** To string */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(AwsAction: name=").append(action_name);
		sb.append(", qmsg=").append(quick_message_name);
		sb.append(", state=").append(action_state);
		sb.append(", updated=").append(action_updated);
		sb.append(", age_secs=").append(getAgeSecs());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Get the quick message for this action.
	 * @return Quick message or null if not found
	 */
	private QuickMessage getQuickMessage() {
		return QuickMessageHelper.lookup(quick_message_name);
	}

	/** Get the activation priority */
	public DMSMessagePriority getActivationPriority() {
		return act_priority;
	}

	/** Get the run-time priority */
	public DMSMessagePriority getRunTimePriority() {
		return run_priority;
	}

	/**
	 * Build the action's sign message.
	 * @param di The DMS
	 * @return A SignMessage that contains the text of the message and
	 *         rendered bitmap(s) or null on error.
	 */
	private SignMessage toSignMessage(DMSImpl di) {
		MultiString multi = getActionMulti();
		if (multi == null)
			return null;
		AwsJob.logfinest("d=" + di + ", creating sign message " +
			"with actp=" + act_priority + ", runp=" + run_priority +
			", multi=" + multi);
		//FIXME CA-MN-MERGE check on this, aws => external?
//		SignMessage sm = di.createMessage(multi.toString(),
//			false, act_priority, run_priority,
//			getSignMessageDuration());
		SignMessage sm = di.createMsg(multi.toString(),
			false, act_priority, run_priority,
			SignMsgSource.external, getSignMessageDuration());
		return sm;
	}

	/**
	 * Get action message as a multi string.
	 * @return A multi string or null on error.
	 */
	private MultiString getActionMulti() {
		if (isBlank())
			return new MultiString("");
		QuickMessage qm = getQuickMessage();
		if (qm == null) {
			AwsJob.logsevere("Quick message not found " +
				"for action=" + this);
			return null;
		}
		String ms = qm.getMulti();
		if (ms == null || ms.length() <= 0) {
			AwsJob.logsevere("MULTI not defined for qmsg=" + qm);
			return null;
		}
		return new MultiString(ms);
	}

	/**
	 * Return the SignMessage duration.
	 * @return Duration in seconds or null for infinite.
	 */
	private Integer getSignMessageDuration() {
		return isBlank() ? 0 : null;
	}

	/**
	 * Get the AWS user specified by the system attribute.
	 * @return AWS user or null on error.
	 */
	protected static User getAwsUser() {
		User u = IrisUserHelper.lookup(getAwsUserName());
		if (u == null)
			AwsJob.logsevere("The user specified by system " +
				"attribute " +
				SystemAttrEnum.DMS_AWS_USER_NAME +
				" does not exist.");
		return u;
	}

	/**
	 * Get the AWS user name.
	 * @return The AWS user name, never null.
	 */
	static public String getAwsUserName() {
		return SystemAttrEnum.DMS_AWS_USER_NAME.getString();
	}

	/**
	 * Perform this action on the specified DMS.
	 * @param di DMS to perform action on, may be null.
	 */
	public void perform(DMSImpl di) {
		if (di == null)
			return;
		MsgFile.writeToMsgFile(di, getActionMulti());
		if (shouldDeploy(di))
			activate(di);
		else
			AwsJob.loginfo("d=" + di + ", ACTION: didn't send " +
				"action to dms: action=" + this);
	}

	/**
	 * Decide if the AWS message should be deployed to a DMS.
	 * A message can only be deployed if the AWS is activated and the
	 * specified DMS is CAWS-controlled and CAWS-allowed.
	 * @param di Sign to deploy new message to, may be null.
	 * @return True to send the message.
	 */
	private boolean shouldDeploy(DMSImpl di) {
		if (di == null)
			return false;
		if (!SystemAttrEnum.DMS_AWS_ENABLE.getBoolean()) {
			AwsJob.loginfo("d=" + di + ", system attribute" +
				" dms_aws_enable is false, AWS disabled.");
			return false;
		}
		if (!DMSHelper.isAwsControlled(di)) {
			AwsJob.loginfo("d=" + di +
				" is not aws controlled and allowed.");
			return false;
		}

		// deploy an AWS-generated blank if existing msg is a
		// non-blank AWS message, regardless of DMS error state.
		MultiString newmulti = getActionMulti();
		if (newmulti == null)
			return false;
		AwsJob.logfinest("d=" + di + ", newmulti=" + newmulti);
		boolean sdeploy;
		if (newmulti.isBlank()) {
			AwsJob.logfinest("d=" + di + ", new msg is a blank");
			sdeploy = DMSHelper.isAwsDeployed(di);
			AwsJob.logfinest("d=" + di + ", deployed msg is " +
				"aws=" + sdeploy);
		}
		else {
			AwsJob.logfinest("d=" + di + ", new msg is nonblank");
			sdeploy = true;
		}
		AwsJob.logfinest("d=" + di + ", should deploy=" + sdeploy);
		return sdeploy;
	}

	/** Activate action on the specified DMS */
	private void activate(DMSImpl di) {
		AwsJob.logfinest("d=" + di + ", might send msg to sign");
		updateStateDeployed();
		if (equalsDeployedMsg(di)) {
			AwsJob.logfinest("d=" + di + ", new equals " +
				"deployed, not sending");
			return;
		}
		else {
			AwsJob.logfinest("d=" + di + ", DOES NOT equal " +
				"deployed");
		}
		SignMessage sm = toSignMessage(di);
		if (sm == null) {
			AwsJob.logfinest("d=" + di + ", sm=null");
			return;
		}
		try {
			di.doSetMessageNext(sm, getAwsUser());
			AwsJob.loginfo("d=" + di +
				", ACTION: sm="+sm.getMulti()+", sent action=" + this);
		}
		catch(Exception e) {
			e.printStackTrace();
			AwsJob.logsevere("d=" + di + ", ex=" + e);
			return;
		}
	}

	/** Update state to deployed */
	private void updateStateDeployed() {
		if (getState() != State.UNDEPLOYING)
			setState(State.DEPLOYED);
		AwsJob.logfinest("for action=" + getName() +
			", state now=" + getState());
	}

	/**
	 * Does the new message equal the deployed message?
	 * @param di The associated DMSImpl.
	 * @return True if the new message is equivalent to the deployed.
	 */
	private boolean equalsDeployedMsg(DMSImpl di) {
		MultiString newmulti = getActionMulti();
		if (newmulti == null)
			return false;
		String dmulti = getDeployedMulti(di);
		AwsJob.logfinest("d=" + di + ", deployed msg=" + dmulti);
		boolean eq = newmulti.equals(dmulti);
		AwsJob.logfinest("d=" + di + ", new equals deployed=" + eq);
		return eq;
	}

	/**
	 * Get the deployed MULTI.
	 * @return The deployed MULTI or an empty string.
	 */
	static private String getDeployedMulti(DMSImpl di) {
		SignMessage dsm = di.getMessageCurrent();
		return (dsm == null ? "" : dsm.getMulti());
	}

	/**
	 * Get the highest priority action.
	 * @return the highest priority action or null if none specified.
	 */
	static protected AwsAction highestPriority(LinkedList<AwsAction> acts)
	{
		if (acts.size() <= 0)
			return null;
		AwsAction highest = null;
		int hpri = -1;
		for (AwsAction aa : acts) {
			int newpri = aa.getAwsPriority();
			if (newpri > hpri) {
				hpri = newpri;
				highest = aa;
			}
		}
		return highest;
	}

	/**
	 * Can this action be deployed?
	 * @return True if deployable else false
	 */
	protected boolean isDeployable(DMSImpl di, long now) {
		boolean dep;
		if (getState() == State.UNDEPLOYED)
			dep = false;
		else if (getState() == State.DEPLOYING)
			dep = bothSpeedRules(di) ||
				isAgeDeployable(di, now, true);
		else if (getState() == State.DEPLOYED)
			dep = true;
		else if (getState() == State.UNDEPLOYING)
			dep = !isAgeDeployable(di, now, false);
		else {
			AwsJob.logsevere("d=" + di + ", unknown state");
			dep = false;
		}
		AwsJob.loginfo("d=" + di + ", state=" + getState() +
			", is deployable=" + dep + ", action=" + this);
		return dep;
	}

	/**
	 * Is an action deployable or undeployable based only on age?
	 * @param dep True for deployable else false for undeployable
	 * @return True if the action is deployable or undeployable as
	 *         determined by the argument dep.
	 */
	protected boolean isAgeDeployable(DMSImpl di, long now, boolean dep) {
		long delta = getAgeSecs(now);
		if (aws_rule == null)
			return false;
		long limit = aws_rule.getMinDeployingTimeSecs(dep);
		AwsJob.logfinest("d=" + di + ", limit=" + limit +
			" secs, SLOP_SECS=" + SLOP_SECS);
		boolean act = (delta >= limit - SLOP_SECS);
		AwsJob.loginfo("d=" + di + ", action=" + this +
			", delta_secs=" + delta + ", limit_secs=" + limit +
			", " + (dep ? "deployable" : "undeployable") +
			"=" + act);
		return act;
	}

	/**
	 * Return true if this action was produced by a a speed rule
	 * and the deployed message is from a speed rule.
	 */
	private boolean bothSpeedRules(DMSImpl di) {
		if (aws_rule == null)
			return false;
		return aws_rule.bothSpeedRules(di);
	}

}
