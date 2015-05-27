/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ntcip;

import java.io.IOException;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.*;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;

/**
 * Operation to query the current message on a DMS.
 *
 * @author Douglas Lau
 */
public class OpQueryDMSMessage extends OpDMS {

	/** Create a new DMS query status object */
	public OpQueryDMSMessage(DMSImpl d) {
		super(PriorityLevel.DEVICE_DATA, d);
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase phaseTwo() {
		return new QueryMessageSource();
	}

	/** Source table (memory type) or the currently displayed message */
	private final DmsMsgTableSource source = new DmsMsgTableSource();

	/** Process the message table source from the sign controller */
	private Phase processMessageSource() {
		DmsMessageMemoryType mem_type = source.getMemoryType();
		if (mem_type != null) {
			/* We have to test isBlank before "valid", because some
			 * signs use 'undefined' source for blank messages. */
			if (mem_type.isBlank())
				return processMessageBlank();
			else if (mem_type.valid)
				return processMessageValid();
		}
		return processMessageInvalid();
	}

	/** Process a blank message source from the sign controller */
	private Phase processMessageBlank() {
		/* The sign is blank.  If IRIS thinks there is a message on it,
		 * that's wrong and needs to be updated. */
		if (!dms.isMsgBlank())
			setCurrentMessage(dms.createBlankMessage());
		return null;
	}

	/** Process a valid message source from the sign controller */
	private Phase processMessageValid() {
		/* The sign is not blank.  If IRIS thinks it is blank, then
		 * we need to query the current message on the sign. */
		if (dms.isMsgBlank())
			return new QueryCurrentMessage();
		/* Compare the CRC of the message on the sign to the
		 * CRC of the message IRIS knows about */
		SignMessage sm = dms.getMessageCurrent();
		int crc = DmsMessageCRC.calculate(sm.getMulti(),
			sm.getBeaconEnabled(), 0);
		if (crc != source.getCrc())
			return new QueryCurrentMessage();
		else
			return null;
	}

	/** Process an invalid message source from the sign controller */
	private Phase processMessageInvalid() {
		/* The source table is not valid.  This condition has been
		 * observed in old Skyline signs after being powered down for
		 * extended periods of time.  It can be cleared up by sending
		 * settings operation. */
		logError("INVALID SOURCE");
		setErrorStatus(source.toString());
		return null;
	}

	/** Phase to query the current message source */
	protected class QueryMessageSource extends Phase {

		/** Query the current message source */
		protected Phase poll(CommMessage mess) throws IOException {
			mess.add(source);
			mess.queryProps();
			logQuery(source);
			return processMessageSource();
		}
	}

	/** Phase to query the current message */
	protected class QueryCurrentMessage extends Phase {

		/** Query the current message */
		protected Phase poll(CommMessage mess) throws IOException {
			DmsMessageMultiString multi = new DmsMessageMultiString(
				DmsMessageMemoryType.currentBuffer, 1);
			ASN1Integer beacon = dmsMessageBeacon.makeInt(
				DmsMessageMemoryType.currentBuffer, 1);
			ASN1Enum<DMSMessagePriority> prior = new ASN1Enum<
				DMSMessagePriority>(dmsMessageRunTimePriority
				.node, DmsMessageMemoryType.currentBuffer
				.ordinal(), 1);
			ASN1Enum<DmsMessageStatus> status = new ASN1Enum<
				DmsMessageStatus>(dmsMessageStatus.node,
				DmsMessageMemoryType.currentBuffer.ordinal(),1);
			ASN1Integer time = dmsMessageTimeRemaining.makeInt();
			mess.add(multi);
			mess.add(beacon);
			mess.add(prior);
			mess.add(status);
			mess.add(time);
			mess.queryProps();
			logQuery(multi);
			logQuery(beacon);
			logQuery(prior);
			logQuery(status);
			logQuery(time);
			if (status.getEnum() == DmsMessageStatus.valid) {
				Integer d = parseDuration(time.getInteger());
				setCurrentMessage(multi.getValue(),
					beacon.getInteger(), prior.getEnum(),d);
			} else {
				logError("INVALID STATUS");
				setErrorStatus(status.toString());
			}
			return null;
		}
	}

	/** Set the current message on the sign */
	private void setCurrentMessage(String multi, int be,
		DMSMessagePriority p, Integer duration)
	{
		setCurrentMessage(dms.createMessage(multi, (be == 1), p, p,
			duration));
	}

	/** Set the current message on the sign */
	private void setCurrentMessage(SignMessage sm) {
		if (sm != null)
			dms.setMessageCurrent(sm, null);
		else
			setErrorStatus("MSG RENDER FAILED");
	}
}
