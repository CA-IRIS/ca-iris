/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.BitmapGraphic;
import us.mn.state.dot.tms.DMSImpl;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMessageImpl;
import us.mn.state.dot.tms.SignTextHelper;
import us.mn.state.dot.tms.comm.AddressedMessage;
import us.mn.state.dot.tms.utils.HexString;
import us.mn.state.dot.tms.utils.STime;

/**
 * Operation to query the current message on a DMS.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class OpQueryMsg extends OpDms {

	/** constructor */
	public OpQueryMsg(DMSImpl d, User u) {
		super(DEVICE_DATA, d, "Retrieving message", u);
	}

	/**
	 * Calculate message duration
	 * @param useont true to use on time
	 * @param useofft true to use off time else infinite message
	 * @param ontime message on time
	 * @param offtime message off time
	 * @return Duration in minutes; null indicates no expiration.
	 * @throws IllegalArgumentException if invalid args.
	 */
	static private Integer calcMsgDuration(boolean useont, boolean useofft,
					   Calendar ontime, Calendar offtime)
	{
		if(!useont) {
			throw new IllegalArgumentException("must have ontime in calcMsgDuration.");
		}
		if(!useofft)
			return null;
		if(ontime == null) {
			throw new IllegalArgumentException("invalid null ontime in calcMsgDuration.");
		}
		if(offtime == null) {
			throw new IllegalArgumentException("invalid null offtime in calcMsgDuration.");
		}

		// calc diff in mins
		long delta = offtime.getTimeInMillis() -
		             ontime.getTimeInMillis();
		long m = ((delta < 0) ? 0 : delta / 1000 / 60);
		return (int)m;
	}

	/**
	 * Create message text given a bitmap.
	 * It is important to create message text for the message because
	 * the cmsserver returns a message containing a bitmap but with
	 * no message text. IRIS requires both a bitmap and message text,
	 * so this method constructs message text so IRIS will think it's a
	 * message, rather than a blank sign.
	 * 
	 * @return If bitmap is not blank, a page indicating it is an other
	 *         system message. If bitmap is blank, then "" is returned.
	 */
	static protected String createMessageTextUsingBitmap(
		BitmapGraphic[] pages)
	{
		if(areBitmapsBlank(pages))
			return ""; 

		MultiString multi = new MultiString();

		// default text if no bitmap, see comments in method for why this is a hack
		final String TEXT1 = SignTextHelper.flagIgnoredSignLineHack("OTHER");
		final String TEXT2 = SignTextHelper.flagIgnoredSignLineHack("SYSTEM");
		final String TEXT3 = SignTextHelper.flagIgnoredSignLineHack("MESSAGE");
		for(int i = 0; i < pages.length; i++) {
			multi.addText(TEXT1);
			multi.addLine();
			multi.addText(TEXT2);
			multi.addLine();
			multi.addText(TEXT3);
			multi.addPage();
		}
		return multi.toString();
	}

	/** Check if an array of bitmaps is blank */
	static protected boolean areBitmapsBlank(BitmapGraphic[] pages) {
		for(int i = 0; i < pages.length; i++)
			if(pages[i].getLitCount() > 0)
				return false;
		return true;
	}

	/** Calculate the number of pages in a bitmap */
	static protected int calcNumPages(byte[] bm) {
		return bm.length / BM_PGLEN_BYTES;
	}

	/** Extract a single page bitmap from a byte array.
	 * @param argbitmap Bitmap of all pages
	 * @param pg Page number to extract
	 * @return BitmapGraphic of requested page */
	static protected BitmapGraphic extractBitmap(byte[] argbitmap, int pg) {
		byte[] nbm = extractPage(argbitmap, pg);
		BitmapGraphic bm = new BitmapGraphic(BM_WIDTH, BM_HEIGHT);
		bm.setBitmap(nbm);
		return bm;
	}

	/** Extract a single page from a byte array.
	 * @param argbitmap Bitmap of all pages
	 * @param pg Page number to extract
	 * @return Bitmap of requested page only */
	static protected byte[] extractPage(byte[] argbitmap, int pg) {
		byte[] nbm = new byte[BM_PGLEN_BYTES];
		System.arraycopy(argbitmap, pg * BM_PGLEN_BYTES, nbm, 0,
			BM_PGLEN_BYTES);
		return nbm;
	}

	/** Create the first real phase of the operation */
	protected Phase phaseOne() {

		if(dmsConfigured())
			return new PhaseQueryCurrentMessage();

		// dms not configured
		Phase phase2 = new PhaseQueryCurrentMessage();
		Phase phase1 = new PhaseGetConfig(phase2);
		return phase1;
	}

	/**
	 * Create a SignMessage using a bitmap and no message text.
	 * @param sbitmap Bitmap as hexstring associated with message text.
	 *                This bitmap is required to be a 96x25 bitmap which
	 *                dmslite will always return.
	 * @param duration Message duration (in minutes).
	 * @return A SignMessage that contains the text of the message and 
	 *         a rendered bitmap.
	 */
	private SignMessageImpl createSignMessageWithBitmap(String sbitmap,
		Integer duration)
	{
		if(sbitmap == null)
			return null;
		byte[] argbitmap = new HexString(sbitmap).toByteArray();
		if(argbitmap.length % BM_PGLEN_BYTES != 0) {
			System.err.println("WARNING: received bogus bitmap " +
				"size: len=" + argbitmap.length +
				", BM_PGLEN_BYTES=" + BM_PGLEN_BYTES);
			return null;
		}

		System.err.println("OpQueryMsg.createSignMessageWithBitmap() " +
			"called: argbitmap.len=" + argbitmap.length + ".");

		int numpgs = calcNumPages(argbitmap);
		System.err.println("OpQueryMsg.createSignMessageWithBitmap(): "+
			"numpages=" + numpgs);
		if(numpgs <= 0)
			return null;

		BitmapGraphic[] pages = new BitmapGraphic[numpgs];
		for(int pg = 0; pg < numpgs; pg++)
			pages[pg] = extractBitmap(argbitmap, pg);

		String multi = createMessageTextUsingBitmap(pages);
		System.err.println("OpQueryMsg.createSignMessageWithBitmap(): "+
			"multistring=" + multi);

		try {
			return (SignMessageImpl)m_dms.createMessage(multi,
				pages, DMSMessagePriority.OTHER_SYSTEM,
				duration);
		}
		catch(SonarException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Phase to get current message
	 * Note, the type of exception throw here determines
	 * if the messenger reopens the connection on failure.
	 *
	 * @see MessagePoller#doPoll()
	 * @see Messenger#handleException()
	 * @see Messenger#shouldReopen()
	 */
	protected class PhaseQueryCurrentMessage extends Phase
	{
		/** Query current message */
		protected Phase poll(AddressedMessage argmess)
			throws IOException
		{
			System.err.println(
			    "OpQueryMsg.PhaseQueryCurrentMessage.poll(msg) called.");
			assert argmess instanceof Message :
			       "wrong message type";

			Message mess = (Message) argmess;

			// set message attributes as a function of the operation
			setMsgAttributes(mess);

			// build req msg and expected response
			mess.setName(getOpName());
			mess.setReqMsgName("StatusReqMsg");
			mess.setRespMsgName("StatusRespMsg");
			String addr = Integer.toString(controller.getDrop());
			ReqRes rr0 = new ReqRes("Id", generateId(), new String[] {"Id"});
			ReqRes rr1 = new ReqRes("Address", addr, new String[] {
				"IsValid", "ErrMsg", "MsgTextAvailable", "MsgText",
				"FontName", "Owner", "UseOnTime", "OnTime", "UseOffTime",
				"OffTime", "UseBitmap", "Bitmap"});

			// send msg
			mess.add(rr0);
			mess.add(rr1);
            		mess.getRequest();	// throws IOException

			// parse resp msg
			long id = 0;
			boolean valid = false;
			String errmsg = "";
			boolean msgtextavailable = false;
			String msgtext = "";
			String fontname = "";
			String owner = "";
			boolean useont = false;
			Calendar ont = new GregorianCalendar();
			boolean useofft = false;
			Calendar offt = new GregorianCalendar();
			boolean usebitmap = false;
			String bitmap = "";

			// parse
			try {
				// id
				id = new Long(rr0.getResVal("Id"));

				// valid flag
				valid = new Boolean(rr1.getResVal("IsValid"));

				// error message text
				errmsg = rr1.getResVal("ErrMsg");
				if(!valid && errmsg.length() <= 0)
					errmsg = FAILURE_UNKNOWN;

				if(valid) {
					// msg text available
					msgtextavailable = new Boolean(
					    rr1.getResVal("MsgTextAvailable"));

					// msg text
					msgtext = rr1.getResVal("MsgText");

					// font name
					fontname = rr1.getResVal("FontName");

					// owner
					owner = rr1.getResVal("Owner");

					// ontime
					useont = new Boolean(rr1.getResVal("UseOnTime"));
					if(useont) {
						ont.setTime(STime.XMLtoDate(rr1.getResVal("OnTime")));
					}

					// offtime
					useofft = new Boolean(rr1.getResVal("UseOffTime"));
					if(useofft) {
						offt.setTime(STime.XMLtoDate(rr1.getResVal("OffTime")));
					}

					// bitmap
					usebitmap = new Boolean(rr1.getResVal("UseBitmap"));
					bitmap = rr1.getResVal("Bitmap");

					System.err.println(
					    "OpQueryMsg.PhaseQueryCurrentMessage.poll(msg) parsed msg values: IsValid:"
					    + valid + ", MsgTextAvailable:"
					    + msgtextavailable + ", MsgText:"
					    + msgtext + "FontName:" + fontname + ", OnTime:" 
					    + ont.getTime() + ", OffTime:" + offt.getTime() 
					    + ", bitmap:" + bitmap);
				}
			} catch (IllegalArgumentException ex) {
				System.err.println("OpQueryMsg.PhaseQueryCurrentMessage: Malformed XML received:"
				    + ex+", id="+id);
				valid=false;
				errmsg=ex.getMessage();
				handleException(new IOException(errmsg));
			}

			// update 
			complete(mess);

			// process response
			if(valid) {

				// error checking: have on time? if not, create new ontime
				if (!useont) {
					useont=true;
					ont=new GregorianCalendar();
					//System.err.println("NOTE: DmsLite.OpQueryMsg.PhaseQueryCurrentMessage():"+
					//	" no ontime specified, assuming now.");
				}

				// error checking: valid off time?
				if (useont && useofft && offt.compareTo(ont)<=0) {
					useofft=false;
					//System.err.println("NOTE: DmsLite.OpQueryMsg.PhaseQueryCurrentMessage():"+
					//	" offtime <= ontime, so off time ignored.");
				}

				// calc message duration
				Integer duramins = calcMsgDuration(useont,
					useofft, ont, offt);

				// have text
				if(msgtextavailable) {
					try {
						SignMessageImpl sm = (SignMessageImpl)
						m_dms.createMessage(msgtext,
						DMSMessagePriority.OPERATOR, duramins);
//FIXME: mtod from r8p9 merge: something should be done with fontname here to update current message, 8p9 code:
//+ m_dms.setMessageFromController(msgtext, duramins, owner, MsgActPriorityD10.PRI_D10_OPER_MSG, fontname);
						m_dms.setMessageCurrent(sm, 
							null);
					}
					catch(SonarException e) {
						e.printStackTrace();
					}

				// don't have text
				} else {
					SignMessageImpl sm = null;
					if(usebitmap) {
						sm = createSignMessageWithBitmap(bitmap, duramins);
						m_dms.setMessageCurrent(sm,
							null);
					}
					if(sm == null) {
						try {
							m_dms.setMessageCurrent(
								m_dms.createMessage("",
								DMSMessagePriority.BLANK, null
							), null);
						}
						catch(SonarException e) {
							e.printStackTrace();
						}
					}
				}

			// valid flag is false
			} else {
				System.err.println(
				    "OpQueryMsg: response from cmsserver received, ignored because Xml valid field is false, errmsg="+errmsg);
				errorStatus = errmsg;

				// try again
				if (flagFailureShouldRetry(errmsg)) {
					System.err.println("OpQueryMsg: will retry failed operation.");
					return this;
				}
			}

			// this operation is complete
			return null;
		}
	}
}
