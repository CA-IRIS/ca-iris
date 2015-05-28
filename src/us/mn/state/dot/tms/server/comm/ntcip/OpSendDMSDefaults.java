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
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSType;
import us.mn.state.dot.tms.Multi.JustificationLine;
import us.mn.state.dot.tms.Multi.JustificationPage;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.*;
import us.mn.state.dot.tms.server.comm.ntcip.mibledstar.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mibledstar.MIB.*;
import us.mn.state.dot.tms.server.comm.ntcip.mibskyline.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mibskyline.MIB.*;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;
import us.mn.state.dot.tms.server.comm.snmp.SNMP;

/**
 * Operation to send default parameters to a DMS.
 *
 * @author Douglas Lau
 */
public class OpSendDMSDefaults extends OpDMS {

	/** Create a new operation to send DMS default parameters */
	public OpSendDMSDefaults(DMSImpl d) {
		super(PriorityLevel.DOWNLOAD, d);
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase phaseTwo() {
		return new SetCommPowerLoss();
	}

	/** Phase to set the comm and power loss times */
	protected class SetCommPowerLoss extends Phase {

		/** Set the comm loss action */
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Integer power_time =dmsShortPowerLossTime.makeInt();
			ASN1Integer comm_time = dmsTimeCommLoss.makeInt();
			MessageIDCode end_msg = new MessageIDCode(
				dmsEndDurationMessage.node);
			power_time.setInteger(0);
			comm_time.setInteger(SystemAttrEnum.
				DMS_COMM_LOSS_MINUTES.getInt());
			end_msg.setMemoryType(DmsMessageMemoryType.blank);
			end_msg.setNumber(1);
			end_msg.setCrc(0);
			mess.add(power_time);
			mess.add(comm_time);
			mess.add(end_msg);
			logStore(power_time);
			logStore(comm_time);
			logStore(end_msg);
			mess.storeProps();
			return new PixelService();
		}
	}

	/** Phase to set the pixel service schedule */
	protected class PixelService extends Phase {

		/** Set the pixel service schedule */
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Integer dur = vmsPixelServiceDuration.makeInt();
			ASN1Integer freq = vmsPixelServiceFrequency.makeInt();
			ASN1Integer time = vmsPixelServiceTime.makeInt();
			dur.setInteger(10);
			freq.setInteger(1440);
			time.setInteger(180);
			mess.add(dur);
			mess.add(freq);
			mess.add(time);
			logStore(dur);
			logStore(freq);
			logStore(time);
			mess.storeProps();
			return new MessageDefaults();
		}
	}

	/** Phase to set the message defaults */
	protected class MessageDefaults extends Phase {

		/** Set the message defaults */
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Enum<JustificationLine> line = new ASN1Enum<
				JustificationLine>(defaultJustificationLine
				.node);
			ASN1Enum<JustificationPage> page = new ASN1Enum<
				JustificationPage>(defaultJustificationPage
				.node);
			ASN1Integer on_time = defaultPageOnTime.makeInt();
			ASN1Integer off_time = defaultPageOffTime.makeInt();
			line.setEnum(JustificationLine.CENTER);
			page.setEnum(JustificationPage.TOP);
			on_time.setInteger(Math.round(10 * SystemAttrEnum.
				DMS_PAGE_ON_DEFAULT_SECS.getFloat()));
			off_time.setInteger(Math.round(10 * SystemAttrEnum.
				DMS_PAGE_OFF_DEFAULT_SECS.getFloat()));
			mess.add(line);
			mess.add(page);
			mess.add(on_time);
			mess.add(off_time);
			logStore(line);
			logStore(page);
			logStore(on_time);
			logStore(off_time);
			mess.storeProps();
			return new LedstarDefaults();
		}
	}

	/** Phase to set Ledstar-specific object defaults */
	protected class LedstarDefaults extends Phase {

		/** Set Ledstar-specific object defaults */
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Integer temp = ledHighTempCutoff.makeInt();
			ASN1Integer override = ledSignErrorOverride.makeInt();
			ASN1Integer limit = ledBadPixelLimit.makeInt();
			temp.setInteger(
				SystemAttrEnum.DMS_HIGH_TEMP_CUTOFF.getInt());
			limit.setInteger(500);
			mess.add(temp);
			mess.add(override);
			mess.add(limit);
			try {
				mess.storeProps();
				logStore(temp);
				logStore(override);
				logStore(limit);
			}
			catch (SNMP.Message.NoSuchName e) {
				// Must not be a Ledstar sign
				return new SkylineDefaults();
			}
			catch (SNMP.Message.BadValue e) {
				// Daktronics uses this instead of NoSuchName
				// Is there a better way to check for that?
			}
			return null;
		}
	}

	/** Phase to set Skyline-specific object defaults */
	protected class SkylineDefaults extends Phase {

		/** Set Skyline-specific object defaults */
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Integer temp = dmsTempCritical.makeInt();
			ASN1Integer day_night = dynBrightDayNight.makeInt();
			ASN1Integer day_rate = dynBrightDayRate.makeInt();
			ASN1Integer night_rate = dynBrightNightRate.makeInt();
			ASN1Integer max_lvl = dynBrightMaxNightManLvl.makeInt();
			temp.setInteger(
				SystemAttrEnum.DMS_HIGH_TEMP_CUTOFF.getInt());
			day_night.setInteger(32);
			day_rate.setInteger(1);
			night_rate.setInteger(15);
			max_lvl.setInteger(20);
			mess.add(temp);
			mess.add(day_night);
			mess.add(day_rate);
			mess.add(night_rate);
			mess.add(max_lvl);
			try {
				mess.storeProps();
				logStore(temp);
				logStore(day_night);
				logStore(day_rate);
				logStore(night_rate);
				logStore(max_lvl);
			}
			catch (SNMP.Message.NoSuchName e) {
				// Must not be a Skyline sign
				return new AddcoDefaults();
			}
			return null;
		}
	}

	/** Phase to set ADDCO-specific object defaults */
	protected class AddcoDefaults extends Phase {

		/** Set ADDCO-specific object defaults */
		protected Phase poll(CommMessage mess) throws IOException {
			// ADDCO brick signs have these dimensions
			String make = dms.getMake();
			// NOTE: setting these objects requires use of the
			//       "administrator" community name.  We need to
			//       check that the password is not null before
			//       attempting to set them.
			if (make != null &&
			    make.startsWith("ADDCO") &&
			    dms.getDmsType() == DMSType.VMS_CHAR.ordinal() &&
			    controller.getPassword() != null)
			{
				ASN1Integer h_border =
					dmsHorizontalBorder.makeInt();
				ASN1Integer v_border =
					dmsVerticalBorder.makeInt();
				ASN1Integer h_pitch =
					vmsHorizontalPitch.makeInt();
				ASN1Integer v_pitch =vmsVerticalPitch.makeInt();
				h_border.setInteger(50);
				v_border.setInteger(69);
				h_pitch.setInteger(69);
				v_pitch.setInteger(69);
				mess.add(h_border);
				mess.add(v_border);
				mess.add(h_pitch);
				mess.add(v_pitch);
				mess.storeProps();
				logStore(h_border);
				logStore(v_border);
				logStore(h_pitch);
				logStore(v_pitch);
			}
			return null;
		}
	}
}
