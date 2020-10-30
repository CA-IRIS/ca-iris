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
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.ntcip.mib1201.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1201.MIB1201.*;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.*;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Flags;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;
import us.mn.state.dot.tms.server.comm.snmp.ASN1String;
import us.mn.state.dot.tms.server.comm.snmp.Counter;
import us.mn.state.dot.tms.server.comm.snmp.NoSuchName;
import us.mn.state.dot.tms.server.comm.snmp.SNMP;

/**
 * Operation to query the configuration of a DMS.
 *
 * @author Douglas Lau
 */
public class OpQueryDMSConfiguration extends OpDMS {

	/** Number of graphics defined in graphic table */
	private final ASN1Integer num_graphics = dmsGraphicNumEntries.makeInt();

	/** Maximum size of a graphic */
	private final ASN1Integer max_size = dmsGraphicMaxSize.makeInt();

	/** Available memory for storing graphics */
	private final Counter available_memory = new Counter(
		availableGraphicMemory.node);

	/** Create a new DMS query configuration object */
	public OpQueryDMSConfiguration(DMSImpl d) {
		super(PriorityLevel.DOWNLOAD, d);
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase phaseTwo() {
		return new QueryModuleCount();
	}

	/** Phase to query the number of modules */
	protected class QueryModuleCount extends Phase {

		/** Query the number of modules */
		@SuppressWarnings("unchecked")
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Integer modules = globalMaxModules.makeInt();
			mess.add(modules);
			mess.queryProps();
			logQuery(modules);
			return new QueryModules(modules.getInteger());
		}
	}

	/** Phase to query the module information */
	protected class QueryModules extends Phase {

		/** Count of rows in the module table */
		private final int count;

		/** Module number to query */
		private int mod = 1;

		/** Create a queryModules phase */
		protected QueryModules(int c) {
			count = c;
		}

		/** Query the module make, model and version */
		@SuppressWarnings("unchecked")
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1String make = moduleMake.makeStr(mod);
			ASN1String model = moduleModel.makeStr(mod);
			ASN1String version = moduleVersion.makeStr(mod);
			ASN1Enum<ModuleType> m_type = new ASN1Enum<ModuleType>(
				ModuleType.class, moduleType.node, mod);
			mess.add(make);
			mess.add(model);
			mess.add(version);
			mess.add(m_type);
			mess.queryProps();
			logQuery(make);
			logQuery(model);
			logQuery(version);
			logQuery(m_type);
			if (m_type.getEnum() == ModuleType.software) {
				dms.setMake(make.getValue());
				dms.setModel(model.getValue());
				dms.setVersion(version.getValue());
			}
			mod += 1;
			if (mod < count)
				return this;
			else
				return new QueryDmsInfo();
		}
	}

	/** Phase to query the DMS information */
	protected class QueryDmsInfo extends Phase {

		/** Query the DMS information */
		@SuppressWarnings("unchecked")
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Flags<DmsSignAccess> access = new ASN1Flags<
				DmsSignAccess>(DmsSignAccess.class,
				dmsSignAccess.node);
			DmsSignType type = new DmsSignType();
			ASN1Integer height = dmsSignHeight.makeInt();
			ASN1Integer width = dmsSignWidth.makeInt();
			ASN1Integer h_border = dmsHorizontalBorder.makeInt();
			ASN1Integer v_border = dmsVerticalBorder.makeInt();
			ASN1Enum<DmsLegend> legend = new ASN1Enum<DmsLegend>(
				DmsLegend.class, dmsLegend.node);
			ASN1Enum<DmsBeaconType> beacon = new ASN1Enum<
				DmsBeaconType>(DmsBeaconType.class,
				dmsBeaconType.node);
			ASN1Flags<DmsSignTechnology> tech = new ASN1Flags<
				DmsSignTechnology>(DmsSignTechnology.class,
				dmsSignTechnology.node);
			mess.add(access);
			mess.add(type);
			mess.add(height);
			mess.add(width);
			mess.add(h_border);
			mess.add(v_border);
			mess.add(legend);
			mess.add(beacon);
			mess.add(tech);
			mess.queryProps();
			logQuery(access);
			logQuery(type);
			logQuery(height);
			logQuery(width);
			logQuery(h_border);
			logQuery(v_border);
			logQuery(legend);
			logQuery(beacon);
			logQuery(tech);
			dms.setSignAccess(access.getValue());
			dms.setDmsType(type.getValueEnum());
			dms.setFaceHeight(height.getInteger());
			dms.setFaceWidth(width.getInteger());
			dms.setHorizontalBorder(h_border.getInteger());
			dms.setVerticalBorder(v_border.getInteger());
			dms.setLegend(legend.getValue());
			dms.setBeaconType(beacon.getValue());
			dms.setTechnology(tech.getValue());
			return new QueryVmsInfo();
		}
	}

	/** Phase to query the VMS information */
	protected class QueryVmsInfo extends Phase {

		/** Query the VMS information */
		@SuppressWarnings("unchecked")
		protected Phase poll(CommMessage mess) throws IOException {
			ASN1Integer s_height = vmsSignHeightPixels.makeInt();
			ASN1Integer s_width = vmsSignWidthPixels.makeInt();
			ASN1Integer h_pitch = vmsHorizontalPitch.makeInt();
			ASN1Integer v_pitch = vmsVerticalPitch.makeInt();
			ASN1Integer c_height =
				vmsCharacterHeightPixels.makeInt();
			ASN1Integer c_width = vmsCharacterWidthPixels.makeInt();
			mess.add(s_height);
			mess.add(s_width);
			mess.add(h_pitch);
			mess.add(v_pitch);
			mess.add(c_height);
			mess.add(c_width);
			mess.queryProps();
			logQuery(s_height);
			logQuery(s_width);
			logQuery(h_pitch);
			logQuery(v_pitch);
			logQuery(c_height);
			logQuery(c_width);
			dms.setHeightPixels(s_height.getInteger());
			dms.setWidthPixels(s_width.getInteger());
			dms.setHorizontalPitch(h_pitch.getInteger());
			dms.setVerticalPitch(v_pitch.getInteger());
			// NOTE: these must be set last
			dms.setCharHeightPixels(c_height.getInteger());
			dms.setCharWidthPixels(c_width.getInteger());
			return new QueryV2();
		}
	}

	/** Phase to query the 1203v2 objects */
	protected class QueryV2 extends Phase {

		/** Query the 1203v2 objects */
		@SuppressWarnings("unchecked")
		protected Phase poll(CommMessage mess) throws IOException {
			MonochromeColor m_color = new MonochromeColor();
			ASN1Enum<DmsColorScheme> color_scheme = new ASN1Enum<
				DmsColorScheme>(DmsColorScheme.class,
				dmsColorScheme.node);
			DmsSupportedMultiTags tags =new DmsSupportedMultiTags();
			ASN1Integer pages = dmsMaxNumberPages.makeInt();
			ASN1Integer m_len = dmsMaxMultiStringLength.makeInt();
			mess.add(m_color);
			mess.add(color_scheme);
			mess.add(tags);
			mess.add(pages);
			mess.add(m_len);
			try {
				mess.queryProps();
				logQuery(m_color);
				logQuery(color_scheme);
				logQuery(tags);
				logQuery(pages);
				logQuery(m_len);
			}
			catch (NoSuchName e) {
				// Sign supports 1203v1 only
				return null;
			}
			return new QueryGraphics();
		}
	}

	/** Phase to query graphics objects */
	protected class QueryGraphics extends Phase {

		/** Query graphics objects */
		@SuppressWarnings("unchecked")
		protected Phase poll(CommMessage mess) throws IOException {
			mess.add(num_graphics);
			mess.add(max_size);
			mess.add(available_memory);
			try {
				mess.queryProps();
			}
			catch (NoSuchName e) {
				logError("no graphics support");
				return null;
			}
			logQuery(num_graphics);
			logQuery(max_size);
			logQuery(available_memory);
			return null;
		}
	}

	/** Cleanup the operation */
	@Override
	public void cleanup() {
		dms.setConfigure(isSuccess());
		super.cleanup();
	}
}
