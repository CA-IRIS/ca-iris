package us.mn.state.dot.tms.server.comm.ntcip;

import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
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
    protected Phase processMessageSource() throws IOException {
        DmsMessageMemoryType mem_type = source.getMemoryType();
        if (mem_type != null) {
            /* We have to test isBlank before "valid", because some
             * signs use 'undefined' source for blank messages. */
            if (mem_type.isBlank())
                return processMessageBlank();
            else if (mem_type.valid) {
                return processMessageValid();
            } else
                return new QueryCurrentMsgPrior();
        }
        return processMessageInvalid();
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
            lcsPrior = prior; // Set OpQueryLCSMessage priority
            mess.add(prior);
            mess.queryProps();
            logQuery(prior);
            return new QueryCurrentMsgTime();
        }
    }

    private ASN1Integer lcsTime;

    /** Phase to query the message time */
    protected class QueryCurrentMsgTime extends Phase {

        /** Query the message time */
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer time = dmsMessageTimeRemaining.makeInt();
            lcsTime = time; // Set OpQueryLCSMessage time
            mess.add(time);
            mess.queryProps();
            logQuery(time);
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
            lcsMulti = ms;
            logQuery(ms);
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
            lcsBeacon = beacon;
            logQuery(beacon);
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
            lcsStatus = status;
            logQuery(status);
            setCurrentMessage();
            return null;
        }
    }

    @Override
    protected Phase processMessageValid() throws IOException {
        if (source.getMemoryType() == DmsMessageMemoryType.permanent) {
            try {
                SignMessage sm = dms.getMessageCurrent();
                int msg_num = findDmsLaneUseMulti(sm.getMulti()).getMsgNum();
                if (source.getNumber() == msg_num)
                    setCurrentMessage(sm);
                else
                    return new QueryCurrentMsgPrior();
            } catch (InvalidMessageException e) {
                e.printStackTrace();
            }
        } else {
            return new QueryCurrentMsgPrior();
        }
        return null;
    }

    private void setCurrentMessage() throws IOException {
        Integer d = parseDuration(lcsTime.getInteger());
        DMSMessagePriority rp = lcsPrior.getEnum();
        /* If it's null, IRIS didn't send it ... */
        if (rp == null)
            rp = DMSMessagePriority.OTHER_SYSTEM;
        setCurrentMessage(lcsMulti.getValue(),
                lcsBeacon.getInteger(), rp, d);
    }
}
