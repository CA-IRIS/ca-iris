package us.mn.state.dot.tms.server.comm.ntcip;

import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ControllerException;
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

    private ASN1Enum<DMSMessagePriority> lcsPrior;

    /** Phase to query the message priority */
    protected class QueryCurrentMsgPrior extends Phase {

        /** Query the message priority */
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DMSMessagePriority> prior = new ASN1Enum<
                    DMSMessagePriority>(DMSMessagePriority.class,
                    dmsMessageRunTimePriority.node,
                    DmsMessageMemoryType.currentBuffer.ordinal(), 1);
            mess.add(prior);
            mess.queryProps();
            logQuery(prior);
            lcsPrior = prior; // Set OpQueryLCSMessage priority
            return new QueryCurrentMsgTime();
        }
    }

    private ASN1Integer lcsTime;

    /** Phase to query the message time */
    protected class QueryCurrentMsgTime extends Phase {

        /** Query the message time */
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer time = dmsMessageTimeRemaining.makeInt();
            mess.add(time);
            mess.queryProps();
            logQuery(time);
            lcsTime = time; // Set OpQueryLCSMessage time
            return new QueryMsgMultiString(); // look at OpQueryDMSMessage as reference
        }
    }

    private ASN1String lcsMulti;

    /** Phase to query the message MULTI string*/
    protected class QueryMsgMultiString extends Phase {

        /** Query the message MULTI string */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1String ms = new ASN1String(dmsMessageMultiString
                    .node, DmsMessageMemoryType.currentBuffer
                    .ordinal(), 1);
            mess.add(ms);
            mess.queryProps();
            logQuery(ms);
            lcsMulti = ms;
            return new QueryMsgBeacon();
        }
    }

    private ASN1Integer lcsBeacon;

    /** Phase to query the message beacon*/
    protected class QueryMsgBeacon extends Phase {

        /** Query the message beacon */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer beacon = dmsMessageBeacon.makeInt(
            DmsMessageMemoryType.currentBuffer, 1);
            mess.add(beacon);
            mess.queryProps();
            logQuery(beacon);
            lcsBeacon = beacon;
            return new QueryMsgStatus();
        }
    }

    private ASN1Enum<DmsMessageStatus> lcsStatus;

    /** Phase to query the message status*/
    protected class QueryMsgStatus extends Phase {

        /** Query the message status */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Enum<DmsMessageStatus> status = new ASN1Enum<
            DmsMessageStatus>(DmsMessageStatus.class,
            dmsMessageStatus.node,
            DmsMessageMemoryType.currentBuffer.ordinal(),1);
            mess.add(status);
            mess.queryProps();
            logQuery(status);
            lcsStatus = status;
            setCurrentMessage();
            return null;
        }
    }

    @Override
    protected Phase processMessageValid() throws IOException {
        try {
            if (source.getMemoryType() == DmsMessageMemoryType.permanent
                    && !dms.isMsgBlank()) {
                SignMessage sm = dms.getMessageCurrent();
                int msg_num = findDmsLaneUseMulti(sm.getMulti()).getMsgNum();
                if (source.getNumber() == msg_num) {
                    setCurrentMessage(sm);
                    return null;
                }
            }
        } catch (InvalidMessageException exception) {
            logError("INVALID LANE USE MULTI");
        }
        return new QueryCurrentMsgPrior();
    }

    private void setCurrentMessage() throws IOException {
        if (lcsStatus.getEnum() == DmsMessageStatus.valid) {
            Integer d = parseDuration(lcsTime.getInteger());
            DMSMessagePriority rp = lcsPrior.getEnum();
            if (rp == null)
                rp = DMSMessagePriority.OTHER_SYSTEM;
            setCurrentMessage(lcsMulti.getValue(),
                    lcsBeacon.getInteger(), rp, d);
        } else {
            logError("INVALID STATUS");
            setErrorStatus(lcsStatus.toString());
        }
    }
}
