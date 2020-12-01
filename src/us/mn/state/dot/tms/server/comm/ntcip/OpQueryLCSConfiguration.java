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

import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ntcip.mib1201.ModuleType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsBeaconType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsColorScheme;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsLegend;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsSignAccess;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsSignTechnology;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsSignType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsSupportedMultiTags;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.MonochromeColor;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Flags;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;
import us.mn.state.dot.tms.server.comm.snmp.ASN1String;
import us.mn.state.dot.tms.server.comm.snmp.Counter;
import us.mn.state.dot.tms.server.comm.snmp.NoSuchName;

import java.io.IOException;

import static us.mn.state.dot.tms.server.comm.ntcip.mib1201.MIB1201.globalMaxModules;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1201.MIB1201.moduleMake;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1201.MIB1201.moduleModel;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1201.MIB1201.moduleType;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1201.MIB1201.moduleVersion;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.availableGraphicMemory;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsBeaconType;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsColorScheme;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsGraphicMaxSize;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsGraphicNumEntries;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsHorizontalBorder;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsLegend;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMaxMultiStringLength;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMaxNumberPages;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsSignAccess;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsSignHeight;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsSignTechnology;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsSignType;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsSignWidth;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsVerticalBorder;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.vmsCharacterHeightPixels;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.vmsCharacterWidthPixels;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.vmsHorizontalPitch;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.vmsSignHeightPixels;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.vmsSignWidthPixels;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.vmsVerticalPitch;

/**
 * Operation to query the configuration of an LCS.
 *
 */
public class OpQueryLCSConfiguration extends OpQueryDMSConfiguration {




    /** Create a new DMS query configuration object */
    public OpQueryLCSConfiguration(DMSImpl d) {
        super(d);
    }

    /** Create the second phase of the operation */
    @Override
    protected Phase phaseTwo() {
        return new OpQueryLCSConfiguration.QueryModuleCount();
    }

    /** Phase to query the number of modules */
    protected class QueryModuleCount extends Phase {

        /** Query the number of modules */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer modules = globalMaxModules.makeInt();
            mess.add(modules);
            mess.queryProps();
            logQuery(modules);
            return new OpQueryLCSConfiguration.QueryModuleType(modules.getInteger());
        }
    }

    /** Module Type for Phase access */
    private ASN1Enum<ModuleType> modType;

    /** Phase to query the module type information */
    protected class QueryModuleType extends Phase {

        /** Count of rows in the module table */
        private final int count;

        /** Module number to query */
        private int mod = 1;

        /** Create a queryModules phase */
        protected QueryModuleType(int c) {
            count = c;
        }

        /** Query the module type */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<ModuleType> m_type = new ASN1Enum<ModuleType>(
                    ModuleType.class, moduleType.node, mod);
            mess.add(m_type);
            mess.queryProps();
            logQuery(m_type);
            modType = m_type; // Store type for other phases
            return new QueryModuleMake(count);
        }
    }

    /** Phase to query the module make information */
    protected class QueryModuleMake extends Phase {

        /** Count of rows in the module table */
        private final int count;

        /** Module number to query
         */
        private int mod = 1;

        /** Create a queryModules phase */
        protected QueryModuleMake(int c) {
            count = c;
        }

        /** Query the module make */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1String make = moduleMake.makeStr(mod);
            mess.add(make);
            mess.queryProps();
            logQuery(make);
            if (modType.getEnum() == ModuleType.software)
                dms.setMake(make.getValue());
            return new QueryModuleModel(count);
        }
    }

    /** Phase to query the module model information */
    protected class QueryModuleModel extends Phase {

        /** Count of rows in the module table */
        private final int count;

        /** Module number to query */
        private int mod = 1;

        /** Create a queryModules phase */
        protected QueryModuleModel(int c) {
            count = c;
        }

        /** Query the module model */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1String model = moduleModel.makeStr(mod);
            mess.add(model);
            mess.queryProps();
            logQuery(model);
            if (modType.getEnum() == ModuleType.software)
                dms.setModel(model.getValue());
            return new QueryModuleVersion(count);
        }
    }

    /** Phase to query the module version information */
    protected class QueryModuleVersion extends Phase {

        /** Count of rows in the module table */
        private final int count;

        /**  Module number to query */
        private int mod = 1;

        /** Create a queryModules phase */
        protected QueryModuleVersion(int c) {
            count = c;
        }

        /** Query the module version */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1String version = moduleVersion.makeStr(mod);
            mess.add(version);
            mess.queryProps();
            logQuery(version);
            if (modType.getEnum() == ModuleType.software)
                dms.setVersion(version.getValue());
            return new QueryDmsSignAccess();
        }
    }

    /** Phase to query the DMS  sign access information */
    protected class QueryDmsSignAccess extends Phase {

        /** Query the DMS sign access information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Flags<DmsSignAccess> access = new ASN1Flags<
                    DmsSignAccess>(DmsSignAccess.class,
                    dmsSignAccess.node);
            mess.add(access);
            mess.queryProps();
            logQuery(access);
            dms.setSignAccess(access.getValue());
            return new QueryDmsHeight();
        }
    }

    /** Phase to query the DMS  sign height information */
    protected class QueryDmsHeight extends Phase {

        /** Query the DMS sign height information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer height = dmsSignHeight.makeInt();
            mess.add(height);
            mess.queryProps();
            logQuery(height);
            dms.setFaceHeight(height.getInteger());
            return new QueryDmsWidth();
        }
    }

    /** Phase to query the DMS  sign width information */
    protected class QueryDmsWidth extends Phase {

        /** Query the DMS sign width information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer width = dmsSignWidth.makeInt();
            mess.add(width);
            mess.queryProps();
            logQuery(width);
            dms.setFaceWidth(width.getInteger());
            return new QueryDmsHBorder();
        }
    }

    /** Phase to query the DMS  sign horizontal border information */
    protected class QueryDmsHBorder extends Phase {

        /** Query the DMS sign horizontal border information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer h_border = dmsHorizontalBorder.makeInt();
            mess.add(h_border);
            mess.queryProps();
            logQuery(h_border);
            dms.setHorizontalBorder(h_border.getInteger());
            return new QueryDmsVBorder();
        }
    }

    /** Phase to query the DMS  sign vertical border information */
    protected class QueryDmsVBorder extends Phase {

        /** Query the DMS sign vertical border information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer v_border = dmsVerticalBorder.makeInt();
            mess.add(v_border);
            mess.queryProps();
            logQuery(v_border);
            dms.setVerticalBorder(v_border.getInteger());
            return new QueryDmsLegend();
        }
    }

    /** Phase to query the DMS  legend information */
    protected class QueryDmsLegend extends Phase {

        /** Query the DMS legend information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DmsLegend> legend = new ASN1Enum<DmsLegend>(
                    DmsLegend.class, dmsLegend.node);
            mess.add(legend);
            mess.queryProps();
            logQuery(legend);
            dms.setLegend(legend.getValue());
            return new QueryDmsBeacon();
        }
    }

    /** Phase to query the DMS beacon type information */
    protected class QueryDmsBeacon extends Phase {

        /** Query the DMS beacon type information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DmsBeaconType> beacon = new ASN1Enum<
                    DmsBeaconType>(DmsBeaconType.class,
                    dmsBeaconType.node);
            mess.add(beacon);
            mess.queryProps();
            logQuery(beacon);
            dms.setBeaconType(beacon.getValue());
            return new QueryDmsType();
        }
    }

    /** Phase to query the DMS sign type information */
    protected class QueryDmsType extends Phase {

        /** Query the DMS sign type information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            DmsSignType type = new DmsSignType();
            mess.add(type);
            mess.queryProps();
            logQuery(type);
            dms.setDmsType(type.getValueEnum());
            return new QueryDmsTech();
        }
    }

    /** Phase to query the DMS technology information */
    protected class QueryDmsTech extends Phase {

        /** Query the DMS technology information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Flags<DmsSignTechnology> tech = new ASN1Flags<
                    DmsSignTechnology>(DmsSignTechnology.class,
                    dmsSignTechnology.node);
            mess.add(tech);
            mess.queryProps();
            logQuery(tech);
            dms.setTechnology(tech.getName());
            return new QueryVmsSignHeight();
        }
    }

    /** Phase to query the VMS sign height information */
    protected class QueryVmsSignHeight extends Phase {

        /** Query the VMS sign height information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer s_height = vmsSignHeightPixels.makeInt();
            mess.add(s_height);
            mess.queryProps();
            logQuery(s_height);
            dms.setHeightPixels(s_height.getInteger());
            return new QueryVmsSignWidth();
        }
    }

    /** Phase to query the VMS sign width information */
    protected class QueryVmsSignWidth extends Phase {

        /** Query the VMS sign width information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer s_width = vmsSignWidthPixels.makeInt();
            mess.add(s_width);
            mess.queryProps();
            logQuery(s_width);
            dms.setWidthPixels(s_width.getInteger());
            return new QueryVmsHorzPitch();
        }
    }

    /** Phase to query the VMS horizontal pitch information */
    protected class QueryVmsHorzPitch extends Phase {

        /** Query the VMS horizontal pitch information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer h_pitch = vmsHorizontalPitch.makeInt();
            mess.add(h_pitch);
            mess.queryProps();
            logQuery(h_pitch);
            dms.setHorizontalPitch(h_pitch.getInteger());
            return new QueryVmsVertPitch();
        }
    }

    /** Phase to query the VMS vertical pitch information */
    protected class QueryVmsVertPitch extends Phase {

        /** Query the VMS vertical pitch information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer v_pitch = vmsVerticalPitch.makeInt();
            mess.add(v_pitch);
            mess.queryProps();
            logQuery(v_pitch);
            dms.setVerticalPitch(v_pitch.getInteger());
            return new QueryVmsCharHeight();
        }
    }

    /** Phase to query the VMS character height information */
    protected class QueryVmsCharHeight extends Phase {

        /** Query the VMS character height information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer c_height =
                    vmsCharacterHeightPixels.makeInt();
            mess.add(c_height);
            mess.queryProps();
            logQuery(c_height);
            dms.setCharHeightPixels(c_height.getInteger());
            return new QueryVmsCharWidth();
        }
    }

    /** Phase to query the VMS character height information */
    protected class QueryVmsCharWidth extends Phase {

        /** Query the VMS character height information */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer c_width = vmsCharacterWidthPixels.makeInt();
            mess.add(c_width);
            mess.queryProps();
            logQuery(c_width);
            dms.setCharWidthPixels(c_width.getInteger());
            return new QueryV2ColorScheme();
        }
    }

    /** Phase to query the 1203v2 Color scheme */
    protected class QueryV2ColorScheme extends Phase {

        /** Query the 1203v2 Color scheme */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DmsColorScheme> color_scheme = new ASN1Enum<
                    DmsColorScheme>(DmsColorScheme.class,
                    dmsColorScheme.node);
            mess.add(color_scheme);
            try {
                mess.queryProps();
                logQuery(color_scheme);
            }
            catch (NoSuchName e) {
                // Sign supports 1203v1 only
                return null;
            }
            return new QueryV2MaxPages();
        }
    }

    /** Phase to query the 1203v2 Max Number Pages */
    protected class QueryV2MaxPages extends Phase {

        /** Query the 1203v2 Max Number Pages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer pages = dmsMaxNumberPages.makeInt();
            mess.add(pages);
            try {
                mess.queryProps();
                logQuery(pages);
            }
            catch (NoSuchName e) {
                // Sign supports 1203v1 only
                return null;
            }
            return new QueryV2MaxMultiLength();
        }
    }

    /** Phase to query the 1203v2 Multi String Length */
    protected class QueryV2MaxMultiLength extends Phase {

        /** Query the 1203v2 Multi String Length */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer m_len = dmsMaxMultiStringLength.makeInt();
            mess.add(m_len);
            try {
                mess.queryProps();
                logQuery(m_len);
            }
            catch (NoSuchName e) {
                // Sign supports 1203v1 only
                return null;
            }
            return new QueryNumGraphics();
        }
    }

    /** Phase to query number of graphics entries */
    protected class QueryNumGraphics extends Phase {

        /** Query number of graphics entries */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer num_graphics = dmsGraphicNumEntries.makeInt();
            mess.add(num_graphics);
            try {
                mess.queryProps();
                logQuery(num_graphics);
            }
            catch (NoSuchName e) {
                logError("no graphics support");
                return null;
            }
            return new QueryGraphicMax();
        }
    }

    /** Phase to query graphic max size */
    protected class QueryGraphicMax extends Phase {

        /** Query graphic max size */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer max_size = dmsGraphicMaxSize.makeInt();
            mess.add(max_size);
            try {
                mess.queryProps();
                logQuery(max_size);
            }
            catch (NoSuchName e) {
                logError("no graphics support");
                return null;
            }
            return new QueryMemAvail();
        }
    }

    /** Phase to available graphic memory */
    protected class QueryMemAvail extends Phase {

        /** Query available graphic memory */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            Counter available_memory = new Counter(
                    availableGraphicMemory.node);
            mess.add(available_memory);
            try {
                mess.queryProps();
                logQuery(available_memory);
            }
            catch (NoSuchName e) {
                logError("no graphics support");
                return null;
            }
            return null;
        }
    }
}
