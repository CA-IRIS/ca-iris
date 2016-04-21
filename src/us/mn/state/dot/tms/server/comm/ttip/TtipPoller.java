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

import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.HttpFileMessenger;
import us.mn.state.dot.tms.server.comm.MessagePoller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Base poller, which periodically reads XML feed via HTTP,
 * published by Oregon DOT's TripCheck website.
 *
 * @author Dan Rossiter
 */
public abstract class TtipPoller extends MessagePoller {

    /** Debug log */
    static public final DebugLog LOG = new DebugLog("ttip");

    /**
     * Create a new message poller with persistent connection mode.
     *
     * @param n CommLink name
     * @param f the filename to retrieve from TTIP API
     */
    protected TtipPoller(String n, String f) {
        super(n, createHttpFileMessenger(n, f));
    }

    /** Log a message. */
    static public void log(String msg) {
        LOG.log(msg);
    }

    /** Create an http file messenger */
    private static HttpFileMessenger createHttpFileMessenger(String name, String filename) {
        CommLink cl = CommLinkHelper.lookup(name);
        String uri = cl.getUri();
        Controller c = null;
        Iterator<Controller> it = ControllerHelper.iterator();
        while(it.hasNext()) {
            c = it.next();
            if (cl == c.getCommLink())
                break;
            else
                c = null;
        }

        if (c != null)
            uri += "?uid=" + ((ControllerImpl)c).getPassword() + "&fn=" + filename;
        else
            log("Could not find controller for comm link " + cl.getName());

        HttpFileMessenger m = null;
        try {
            m = new HttpFileMessenger(new URL(uri));
            log("Opening TTIP URL: " + uri);
        } catch (MalformedURLException e) {
            log("Malformed URI could not be parsed: " + uri);
        }

        return m;
    }
}
