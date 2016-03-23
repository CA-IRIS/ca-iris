/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016 California Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ttip;

import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

import static us.mn.state.dot.tms.DMSType.UNKNOWN;

/**
 * Operation to update configuration.
 *
 * @author Dan Rossiter
 */
public class OpQueryConfig extends OpTtipDms {

    /** Create a new DMS query configuration object */
    public OpQueryConfig(DMSImpl d) {
        super(PriorityLevel.DATA_30_SEC, d);
    }

    /** NOOP. Configuration for TTIP devices is hardcoded. */
    @Override
    protected Phase phaseTwo() {
        return null;
    }

    /** Set hardcoded values for addco brick signs */
    private void setHardcodedValues() {
        dms.setSignAccess("FRONT");
        dms.setDmsType(UNKNOWN);
        dms.setFaceHeight(500);
        dms.setFaceWidth(1620);
        dms.setHorizontalBorder(75);
        dms.setVerticalBorder(75);
        dms.setLegend("noLegend");
        dms.setBeaconType("none");
        dms.setTechnology("LED");
        dms.setHeightPixels(8);
        dms.setWidthPixels(36);
        dms.setHorizontalPitch(41);
        dms.setVerticalPitch(44);
        // NOTE: these must be set last
        dms.setCharHeightPixels(0);
        dms.setCharWidthPixels(0);
    }

    /** Cleanup the operation */
    @Override
    public void cleanup() {
        setHardcodedValues();
        dms.setConfigure(isSuccess());
        super.cleanup();
    }
}
