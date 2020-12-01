/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2016  Minnesota Department of Transportation
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
import java.util.LinkedList;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.*;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.*;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Flags;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;
import us.mn.state.dot.tms.server.comm.snmp.ASN1String;
import us.mn.state.dot.tms.server.comm.snmp.NoSuchName;

/**
 * This operation queries the status of an LCS.
 *
 */
public class OpQueryLCSStatus extends OpQueryDMSStatus {

    /** Pixel failure table row count */
    private final ASN1Integer pix_rows = pixelFailureTableNumRows.makeInt();

    /** Number of rows in pixel failure table found by pixel testing */
    private final ASN1Integer test_rows = dmsPixelFailureTestRows.makeInt();

    /** Number of rows in pixel failure table found by message display */
    private final ASN1Integer message_rows =
            dmsPixelFailureMessageRows.makeInt();

    /** Get the pixel error count */
    public int getPixelErrorCount() {
        int n_test = test_rows.getInteger();
        int n_msg = message_rows.getInteger();
        return Math.max(n_test, n_msg);
    }

    /** Create a new DMS query status object */
    public OpQueryLCSStatus(DMSImpl d) {
        super(d);
    }

    /** Create the second phase of the operation */
    @Override
    protected Phase phaseTwo() {
        return new QueryNumPermMsg();
    }

    /** Phase to query the LCS num permanent messages */
    protected class QueryNumPermMsg extends Phase {

        /** Query the LCS num permanent messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer perm_num = dmsNumPermanentMsg.makeInt();
            mess.add(perm_num);
            mess.queryProps();
            logQuery(perm_num);
            return new QueryNumChangeMsg();
        }
    }

    /** Phase to query the LCS num changeable messages */
    protected class QueryNumChangeMsg extends Phase {

        /** Query the LCS num changeable messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer chg_num = dmsNumChangeableMsg.makeInt();
            mess.add(chg_num);
            mess.queryProps();
            logQuery(chg_num);
            return new QueryMaxChangeMsg();
        }
    }

    /** Phase to query the LCS max changeable messages */
    protected class QueryMaxChangeMsg extends Phase {

        /** Query the LCS max changeable messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer chg_max = dmsMaxChangeableMsg.makeInt();
            mess.add(chg_max);
            mess.queryProps();
            logQuery(chg_max);
            return new QueryFreeChangeMsg();
        }
    }

    /** Phase to query the LCS free changeable messages */
    protected class QueryFreeChangeMsg extends Phase {

        /** Query the LCS free changeable messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer chg_mem = dmsFreeChangeableMemory.makeInt();
            mess.add(chg_mem);
            mess.queryProps();
            logQuery(chg_mem);
            return new QueryNumVolMsg();
        }
    }

    /** Phase to query the LCS num volatile messages */
    protected class QueryNumVolMsg extends Phase {

        /** Query the LCS num volatile messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer vol_num = dmsNumVolatileMsg.makeInt();
            mess.add(vol_num);
            mess.queryProps();
            logQuery(vol_num);
            return new QueryMaxVolMsg();
        }
    }

    /** Phase to query the LCS max volatile messages */
    protected class QueryMaxVolMsg extends Phase {

        /** Query the LCS max volatile messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer vol_max = dmsMaxVolatileMsg.makeInt();
            mess.add(vol_max);
            mess.queryProps();
            logQuery(vol_max);
            return new QueryFreeVolMsg();
        }
    }

    /** Phase to query the LCS free volatile messages */
    protected class QueryFreeVolMsg extends Phase {

        /** Query the LCS free volatile messages */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer vol_mem = dmsFreeVolatileMemory.makeInt();
            mess.add(vol_mem);
            mess.queryProps();
            logQuery(vol_mem);
            return null;
        }
    }


}
