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

import java.io.IOException;
import java.util.HashMap;

import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.ttip.serializers.dmsStatus.DmsDeviceStatus;

/**
 * Operation to read the feed.
 *
 * @author Dan Rossiter
 */
public class OpRead extends OpDevice {

    /** DMS to read */
    private final DMSImpl dms;

    /** DMS controller pin */
    private final Integer dms_pin;

    /** Mapping of dms_pin to most recent DMS records */
    private final HashMap<Integer, DmsDeviceStatus> records;

    /** Constructor. */
    protected OpRead(DMSImpl d, HashMap<Integer, DmsDeviceStatus> recs) {
        super(PriorityLevel.DATA_30_SEC, d);
        records = recs;
        dms = d;
        dms_pin = dms.getPin();
    }

    /** Create the second phase of the operation. */
    @Override
    protected Phase phaseTwo() {
        DmsDeviceStatus rec = records.get(dms_pin);
        if (rec == null || rec.isExpired()) {
            // Add a null mapping for dms_pin
            records.put(dms_pin, null);
            return new PhaseRead();
        } else
            return new PhaseUpdate();
    }

    /** Phase to read the feed */
    private class PhaseRead extends Phase {
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            mess.add(new TtipDmsProperty(records));
            mess.queryProps();
            return new PhaseUpdate();
        }
    }

    /** Phase to update the DMS */
    private class PhaseUpdate extends Phase {
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            DmsDeviceStatus rec = records.get(dms_pin);
            if (rec == null || rec.isExpired())
                TtipPoller.log("Could not find status for " + dms + ", pin #" + dms_pin);
            else {
                TtipPoller.log("Storing status for " + dms);
                rec.store(dms);
            }

            return null;
        }
    }

}
