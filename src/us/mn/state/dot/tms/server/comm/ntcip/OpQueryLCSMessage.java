package us.mn.state.dot.tms.server.comm.ntcip;

import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.QuickMessage;
import us.mn.state.dot.tms.SignMsgSource;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageCRC;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageMemoryType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageStatus;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;
import us.mn.state.dot.tms.server.comm.snmp.ASN1String;

import java.io.IOException;

import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMessageBeacon;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMessageMultiString;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMessageRunTimePriority;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMessageStatus;
import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsMessageTimeRemaining;


public class OpQueryLCSMessage extends OpQueryDMSMessage {

    /** Create a new LCS query status object */
    public OpQueryLCSMessage(DMSImpl d) {
        super(d);
    }

    /** Create the second phase of the operation */
    @Override
    protected Phase phaseTwo() {
        return new QueryMessageSource();
    }

    @Override
    protected Phase processMessageValid() {
        if (source.getMemoryType() == DmsMessageMemoryType.permanent) {
            int msg_num = source.getNumber();
            String multi = findMultiMsgNum(msg_num).getQuickMessage().getMulti();
            int crc = DmsMessageCRC.calculate(multi, false,0);
            if (crc != source.getCrc())
                return new QueryCurrentMsgStatus();
            else
                setCurrentMessage(dms.getMessageCurrent());
        } else {
            logError("INVALID SOURCE");
            setErrorStatus(source.toString());
        }
        return null;
    }

    private ASN1Enum<DmsMessageStatus> lcsStatus;

    /** Phase to query the message status */
    protected class QueryCurrentMsgStatus extends Phase {

        /** Query the message status */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DmsMessageStatus> status = new ASN1Enum<
                    DmsMessageStatus>(DmsMessageStatus.class,
                    dmsMessageStatus.node,
                    DmsMessageMemoryType.currentBuffer.ordinal(),1);
            lcsStatus = status; // Set OpQueryLCSMessage status
            mess.add(status);
            mess.queryProps();
            logQuery(status);
            return new QueryCurrentMsgBeacon();
        }
    }

    private ASN1Integer lcsBeacon;

    /** Phase to query the message beacon */
    protected class QueryCurrentMsgBeacon extends Phase {

        /** Query the message beacon */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer beacon = dmsMessageBeacon.makeInt(
                    DmsMessageMemoryType.currentBuffer, 1);
            lcsBeacon = beacon; // Set OpQueryLCSMessage bacon
            mess.add(beacon);
            mess.queryProps();
            logQuery(beacon);
            return new QueryCurrentMsgPrior();
        }
    }

    private ASN1Enum<DMSMessagePriority> lcsPrior;

    /** Phase to query the message priority */
    protected class QueryCurrentMsgPrior extends Phase {

        /** Query the message priority */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DMSMessagePriority> prior = new ASN1Enum<
                    DMSMessagePriority>(DMSMessagePriority.class,
                    dmsMessageRunTimePriority.node,
                    DmsMessageMemoryType.currentBuffer.ordinal(),1);
            lcsPrior = prior; // Set OpQueryLCSMessage priority
            mess.add(prior);
            mess.queryProps();
            logQuery(prior);
            return new QueryCurrentMsgMulti();
        }
    }

    private ASN1String lcsMulti;

    /** Phase to query the message MULTI String */
    protected class QueryCurrentMsgMulti extends Phase {

        /** Query the message MULTI String */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1String ms = new ASN1String(dmsMessageMultiString
                    .node, DmsMessageMemoryType.permanent
                    .ordinal(), 1);
            lcsMulti = ms; // Set OpQueryLCSMessage MULTI
            mess.add(ms);
            mess.queryProps();
            logQuery(ms);
            return new QueryCurrentMsgTime();
        }
    }

    private ASN1Integer lcsTime;

    /** Phase to query the message time */
    protected class QueryCurrentMsgTime extends Phase {

        /** Query the message time */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer time = dmsMessageTimeRemaining.makeInt();
            lcsTime = time; // Set OpQueryLCSMessage time
            mess.add(time);
            mess.queryProps();
            logQuery(time);
            return new BuildSignMessage();
        }
    }

    /** Phase to build the sign message */
    protected class BuildSignMessage extends Phase {

        /** Query the message time */
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            if (lcsStatus.getEnum() == DmsMessageStatus.valid) {
                Integer d = parseDuration(lcsTime.getInteger());
                DMSMessagePriority rp = lcsPrior.getEnum();
                /* If it's null, IRIS didn't send it ... */
                if (rp == null)
                    rp = DMSMessagePriority.OTHER_SYSTEM;
                setCurrentMessage(lcsMulti.getValue(),
                        lcsBeacon.getInteger(), rp, d);
            } else {
                logError("INVALID STATUS");
                setErrorStatus(lcsStatus.toString());
            }
            return null;
        }
    }


}
