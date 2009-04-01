/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2002-2009  Minnesota Department of Transportation
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

package us.mn.state.dot.tms.comm.dmslite;

import java.io.IOException;
import java.util.Random;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.DMSImpl;
import us.mn.state.dot.tms.DMSType;
import us.mn.state.dot.tms.DebugLog;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SystemAttributeHelperD10;
import us.mn.state.dot.tms.comm.AddressedMessage;
import us.mn.state.dot.tms.comm.ChecksumException;
import us.mn.state.dot.tms.comm.Device2Operation;
import us.mn.state.dot.tms.utils.I18NMessages;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * Operation to be performed on a dynamic message sign
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
abstract public class OpDms extends Device2Operation {

	/** failure message for unknown reasons */
	final static String FAILURE_UNKNOWN = "Failure, unknown reason";

	/** DMS debug log */
	static protected final DebugLog DMS_LOG = new DebugLog("dms");

	/** Bitmap width for dmslite protocol */
	static protected final int BM_WIDTH = 96;

	/** Bitmap height for dmslite protocol */
	static protected final int BM_HEIGHT = 25;

	/** Bitmap page length for dmslite protocol */
	static protected final int BM_PGLEN_BYTES = BM_WIDTH * BM_HEIGHT / 8;

	/** User who deployed the message */
	protected final User m_user;

	/** DMS to operate */
	protected final DMSImpl m_dms;

	/** operation description */
	private String m_opDesc = "";

	/** Create a new DMS operation */
	public OpDms(int p, DMSImpl d, String opDesc, User user) 
	{
		super(p, d);
		m_dms = d;
		m_opDesc = opDesc;
		m_user = user;
	}

	/** get operation name */
	public String getOpName() {
		return getClass().getName();
	}

	/** 
	* Log exceptions in the DMS debug log. This method should be called by
	* operations that fail.
	*/
	public void handleException(IOException e) {
		if(e instanceof ChecksumException) {
			ChecksumException ce = (ChecksumException)e;
			DMS_LOG.log(m_dms.getName() + " (" + toString() +
				"), " + ce.getScannedData());
		}
		super.handleException(e);
	}

	/** Cleanup the operation. This method is called by MessagePoller.doPoll() if an operation is successful */
	public void cleanup() {
		if(success)
			m_dms.requestConfigure();
		else
			m_dms.setConfigure(false);
		super.cleanup();
	}

	/** sign access type */
	public enum SignAccessType {DIALUPMODEM, WIZARD, UNKNOWN};

	/** return DMS sign access type */
	public static SignAccessType getSignAccessType(DMSImpl dms) {
		assert dms != null;
		if(dms == null)
			return SignAccessType.UNKNOWN;
		String a = dms.getSignAccess();
		if(a == null)
			return SignAccessType.UNKNOWN;
		else if(a.toLowerCase().contains("modem"))
			return SignAccessType.DIALUPMODEM;
		else if(a.toLowerCase().contains("wizard"))
			return SignAccessType.WIZARD;
		// unknown sign type, this happens when the first 
		// OpQueryConfig message is being sent.
		return SignAccessType.UNKNOWN;
	}

	/** Return true if the message is owned by the AWS */
	public static boolean ownerIsAws(final String msg_owner) {
		if(msg_owner == null)
			return false;
		final String awsName = I18NMessages.get("Aws.Name");
		return msg_owner.toLowerCase().equals(awsName.toLowerCase());
	}

	/** return the timeout for this operation */
	public int calcTimeoutMS() {
		int secs = 60; //FIXME: use existing sys attribute
		assert m_dms != null : "m_dms is null in OpDms.getTimeoutMS()";
		SignAccessType at = getSignAccessType(m_dms);
		if(at == SignAccessType.DIALUPMODEM) {
			secs = SystemAttributeHelperD10.dmsliteModemOpTimeoutSecs();
			System.err.println("connection type is modem" +
				", dms="+m_dms.toString()+", timeout secs="+secs);
		} else if(at == SignAccessType.WIZARD) {
			secs = SystemAttributeHelperD10.dmsliteOpTimeoutSecs();
			System.err.println("connection type is wizard" +
				", dms="+m_dms.toString()+", timeout secs="+secs);
		}
		// if unknown access type, this happens when the first 
		// OpQueryConfig message is being sent, so a default 
		// timeout should be used.
		return secs * 1000;
	}

	/** set message attributes which are a function of the operation, sign, etc. */
	public void setMsgAttributes(Message m) {
		m.setTimeoutMS(this.calcTimeoutMS());
	}

	/**
	  * handle a failed operation.
	  * @return true if the operation should be retried else false.
	  */
	protected boolean flagFailureShouldRetry(String errmsg) {
	 	String msg = m_dms.getName() + " error: " + errmsg;

		// trigger error handling, changes status if necessary
		handleException(new IOException(msg));

		// retry?
		boolean retry = (controller != null && controller.retry(msg));
		return retry;
	}

	/* reset error counter for DMS */
	protected void resetErrorCounter() {
	 	String id = m_dms.getName();
		if(controller != null) {
			controller.resetErrorCounter(id);
		}
	}

	/** random number generator */
	static private Random m_rand = new Random(System.currentTimeMillis());

	/** generate a unique operation id, which is a long, returned as a string */
	public static String generateId() {
		return new Long(System.currentTimeMillis()+m_rand.nextInt()).toString();
	}

	/** update iris status, called after operation complete */
	public void complete(Message m) {
		m_dms.setUserNote(buildUserNote(m));
	}

	/** Build user note */
	public String buildUserNote(Message m) {
		StringBuilder note = new StringBuilder();
		note.append("Last operation at " +
			STime.getCurTimeShortString());
		String delta = SString.doubleToString((
			((double)m.getCompletionTimeMS()) / 1000), 2);
		note.append(" (").append(delta).append(" secs)");
		note.append(".");
		return note.toString();
	}

	/** return description of operation */
	public String getOperationDescription() {
		m_opDesc = (m_opDesc == null ? "Unnamed operation" : m_opDesc);
		if(m_user == null)
			return m_opDesc;
		return m_opDesc + " (" + m_user.getFullName() + ")";
	}

	/** return true if dms has been configured */
	public boolean dmsConfigured() {
		// FIXME: there must be a better way to check for this condition
		return m_dms.getWidthPixels() != null;
	}

	/** Phase to query the dms config, which is used by subclasses */
	protected class PhaseGetConfig extends Phase
	{
		/** next phase to execute or null */
		private Phase m_next = null;

		/** constructor */
		protected PhaseGetConfig() {}

		/** 
		 *  constructor
		 *  @param next Phase to execute after this phase else null.
		 */
		protected PhaseGetConfig(Phase next) {
			m_next = next;
		}

		/**
		 * Get the DMS configuration. This phase is used by subclassed
		 * operations if the DMS configuration has not been requested.
		 * Note, the type of exception throw here determines
		 * if the messenger reopens the connection on failure.
		 * @see MessagePoller#doPoll()
		 * @see Messenger#handleException()
		 * @see Messenger#shouldReopen()
		 */
		protected Phase poll(AddressedMessage argmess)
			throws IOException {

			// System.err.println("dmslite.OpQueryConfig.PhaseGetConfig.poll(msg) called.");
			assert argmess instanceof Message : "wrong message type";

			Message mess = (Message) argmess;

			// set message attributes as a function of the operation
			setMsgAttributes(mess);

			// build req msg
			mess.setName(getOpName());
			mess.setReqMsgName("GetDmsConfigReqMsg");
			mess.setRespMsgName("GetDmsConfigRespMsg");

			String drop = Integer.toString(controller.getDrop());
			ReqRes rr0 = new ReqRes("Id", generateId(), new String[] {"Id"});
			ReqRes rr1 = new ReqRes("Address", drop, new String[] {
				"IsValid", "ErrMsg", "signAccess", "model", "make",
				"version", "type", "horizBorder", "vertBorder",
				"horizPitch", "vertPitch", "signHeight",
				"signWidth", "characterHeightPixels",
				"characterWidthPixels", "signHeightPixels",
				"signWidthPixels"
			});
			mess.add(rr0);
			mess.add(rr1);

			// send msg
            		mess.getRequest();	// throws IOException

			// parse resp msg
			long id = 0;
			boolean valid = false;
			String errmsg = "";
			String model = "";
			String signAccess = "";
			String make = "";
			String version = "";
			DMSType type = DMSType.VMS_FULL;
			int horizBorder = 0;
			int vertBorder = 0;
			int horizPitch = 0;
			int vertPitch = 0;
			int signHeight = 0;
			int signWidth = 0;
			int characterHeightPixels = 0;
			int characterWidthPixels = 0;
			int signHeightPixels = 0;
			int signWidthPixels = 0;

			try {
				// id
				id = new Long(rr0.getResVal("Id"));

				// valid flag
				valid = new Boolean(rr1.getResVal("IsValid"));

				// error message text
				errmsg = rr1.getResVal("ErrMsg");
				if(!valid && errmsg.length() <= 0)
					errmsg = FAILURE_UNKNOWN;

				// update 
				complete(mess);

				// valid message received?
				if(valid) {
					signAccess = rr1.getResVal("signAccess");
					model = rr1.getResVal("model");
					make = rr1.getResVal("make");
					version = rr1.getResVal("version");

					// determine matrix type
					String stype = rr1.getResVal("type");
					if (stype.toLowerCase().contains("full"))
						type = DMSType.VMS_FULL;
					else
						System.err.println("SEVERE: Unknown matrix type read ("+stype+")");

					horizBorder = SString.stringToInt(
						rr1.getResVal("horizBorder"));
					vertBorder = SString.stringToInt(
						rr1.getResVal("vertBorder"));
					horizPitch = SString.stringToInt(
						rr1.getResVal("horizPitch"));
					vertPitch = SString.stringToInt(
						rr1.getResVal("vertPitch"));
					signHeight = SString.stringToInt(
						rr1.getResVal("signHeight"));
					signWidth = SString.stringToInt(
						rr1.getResVal("signWidth"));
					characterHeightPixels = SString.stringToInt(
						rr1.getResVal(
							"characterHeightPixels"));
					characterWidthPixels = SString.stringToInt(
						rr1.getResVal(
							"characterWidthPixels"));
					signHeightPixels = SString.stringToInt(
						rr1.getResVal(
							"signHeightPixels"));
					signWidthPixels = SString.stringToInt(
						rr1.getResVal(
							"signWidthPixels"));

					// System.err.println("PhaseGetConfig.poll(msg) parsed msg values: valid:"+
					// valid+", model:"+model+", make:"+make+"...etc.");
				}
			} catch (IllegalArgumentException ex) {
				System.err.println(
				    "PhaseGetConfig: Malformed XML received:"+ex+", id="+id);
				valid = false;
				errmsg = ex.getMessage();
				handleException(new IOException(errmsg));
			}

			// set config values
			// these values are displayed in the DMS dialog, Configuration tab
			if(valid) {
				m_dms.setModel(model);
				m_dms.setSignAccess(signAccess);    // wizard, modem
				m_dms.setMake(make);
				m_dms.setVersion(version);
				m_dms.setDmsType(type);
				m_dms.setHorizontalBorder(horizBorder);    // in mm
				m_dms.setVerticalBorder(vertBorder);    // in mm
				m_dms.setHorizontalPitch(horizPitch);
				m_dms.setVerticalPitch(vertPitch);

				// values not set for these
				m_dms.setLegend("sign legend");
				m_dms.setBeaconType("beacon type");
				m_dms.setTechnology("sign technology");

				// note, these must be defined for comboboxes
				// in the "Compose message" control to appear
				m_dms.setFaceHeight(signHeight);    // mm
				m_dms.setFaceWidth(signWidth);      // mm
				m_dms.setHeightPixels(signHeightPixels);
				m_dms.setWidthPixels(signWidthPixels);
				// NOTE: these must be set last
				m_dms.setCharHeightPixels(characterHeightPixels);
				m_dms.setCharWidthPixels(characterWidthPixels);

			// failure
			} else {
				System.err.println(
				    "PhaseGetConfig: response from cmsserver received, ignored because Xml valid field is false, errmsg="
				    + errmsg);
				errorStatus = errmsg;

				// try again
				if(flagFailureShouldRetry(errmsg)) {
					System.err.println("PhaseGetConfig: will retry failed operation");
					return this;
				}
			}

			// if non-null, execute subsequent phase
			return m_next;
		}
	}
}
