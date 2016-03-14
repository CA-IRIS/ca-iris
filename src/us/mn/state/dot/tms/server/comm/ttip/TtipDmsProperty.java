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

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.ttip.serializers.dmsStatus.DmsDeviceStatus;
import us.mn.state.dot.tms.server.comm.ttip.serializers.dmsStatus.DmsInformationResponse;

import javax.xml.bind.JAXB;
import java.io.EOFException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * TTIP DMS property.
 *
 * @author Dan Rossiter
 */
public class TtipDmsProperty extends ControllerProperty {

    /** Mapping of controller pin to most recent DMS records */
    private final HashMap<Integer, DmsDeviceStatus> records;

    /** Constructor. */
    public TtipDmsProperty(HashMap<Integer, DmsDeviceStatus> recs) {
        records = recs;
    }

    /** Decode a QUERY response.  */
    @Override
    public void decodeQuery(ControllerImpl c, InputStream is)
        throws EOFException {
        if (is == null)
            throw new EOFException();

        try {
            DmsInformationResponse resp =
                JAXB.unmarshal(is, DmsInformationResponse.class);
            List<DmsDeviceStatus> status = resp
                .getResponseGroups()
                .getLocalResponseGroup()
                .getDmsListDeviceStatus();

            for (DmsDeviceStatus s : status)
                records.put((int)s.getHead().getId(), s);
        } catch (Exception e) {
            TtipPoller.log("Failed to parse DMS status: " + e);
        }

        TtipPoller.log("parsing complete.  recs: "
                + ((records == null) ? "null" : records.size()));
    }

}
