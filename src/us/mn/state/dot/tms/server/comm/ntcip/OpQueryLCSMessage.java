package us.mn.state.dot.tms.server.comm.ntcip;

import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageCRC;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageMemoryType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageStatus;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;

import java.io.IOException;

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
    protected Phase processMessageValid() throws IOException {
        if (source.getMemoryType() == DmsMessageMemoryType.permanent) {
            int msg_num = source.getNumber();
            String multi = findLaneUseMultiIndication(msg_num).getQuickMessage().getMulti();
            int crc = DmsMessageCRC.calculate(multi, false,0);
            setCurrentMessage(dms.getMessageCurrent()); // create new SM with ACTUAL priority and ACTUAL duration
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
                    DmsMessageMemoryType.permanent.ordinal(),1);
            lcsStatus = status; // Set OpQueryLCSMessage status
            mess.add(status);
            mess.queryProps();
            logQuery(status);
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
                    DmsMessageMemoryType.permanent.ordinal(),1);
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
        @Override
        protected Phase poll(CommMessage mess) throws IOException {
            ASN1Integer time = dmsMessageTimeRemaining.makeInt();
            lcsTime = time; // Set OpQueryLCSMessage time
            mess.add(time);
            mess.queryProps();
            logQuery(time);
            buildMessage();
            return null;
        }
    }

    private void buildMessage() throws IOException {
        if (lcsStatus.getEnum() == DmsMessageStatus.valid) {
            Integer d = parseDuration(lcsTime.getInteger());
            DMSMessagePriority rp = lcsPrior.getEnum();
            /* If it's null, IRIS didn't send it ... */
            if (rp == null)
                rp = DMSMessagePriority.OTHER_SYSTEM;
            String multi = findLaneUseMultiIndication(source.getNumber()).
                    getQuickMessage().getMulti();
            setCurrentMessage(multi, 0, rp, d);
        } else {
            logError("INVALID STATUS");
            setErrorStatus(lcsStatus.toString());
        }
    }
}
