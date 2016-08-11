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

import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.DMSPoller;
import us.mn.state.dot.tms.server.comm.ttip.serializers.dmsStatus.DmsDeviceStatus;

import java.util.HashMap;

/**
 * TTIP DMS poller, which periodically reads an XML feed via HTTP,
 * published by Oregon DOT's TripCheck website.
 *
 * @author Dan Rossiter
 */
public class TtipDmsPoller extends TtipPoller implements DMSPoller {

    /** Mapping of site_id to most recent DMS records */
    private final HashMap<Integer, DmsDeviceStatus> records =
            new HashMap<Integer, DmsDeviceStatus>();

    /**
     * Create a new message poller with persistent connection mode.
     *
     * @param n CommLink name
     */
    public TtipDmsPoller(String n) {
        super(n, "dmsstatus-SW");
    }

    /**
     * Check if a drop address is valid
     *
     * @param drop
     */
    @Override
    public boolean isAddressValid(int drop) {
        return true;
    }

    /**
     * Send a device request
     *
     * @param dms
     * @param r
     */
    @Override
    public void sendRequest(DMSImpl dms, DeviceRequest r) {
        switch(r) {
            case QUERY_CONFIGURATION:
                addOperation(new OpQueryConfig(dms));
                break;
            case QUERY_MESSAGE:
            case QUERY_STATUS:
                addOperation(new OpRead(dms, records));
                break;
        }
    }

    /**
     * Send a message to the sign. If the message is already deployed on
     * the sign, restart the time remaining.
     *
     * @param dms Sign to send message.
     * @param m   Message to send.
     * @param o   User who deployed message.
     */
    @Override
    public void sendMessage(DMSImpl dms, SignMessage m, User o)
        throws InvalidMessageException {
        // NOOP
    }
}
