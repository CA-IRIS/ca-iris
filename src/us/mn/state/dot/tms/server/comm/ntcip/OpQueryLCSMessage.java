package us.mn.state.dot.tms.server.comm.ntcip;

import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageCRC;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageMemoryType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageStatus;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Enum;
import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;
import us.mn.state.dot.tms.server.comm.snmp.ASN1String;

import java.io.IOException;

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
            else if (mem_type.valid)
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
            return processMessageValid();
        }
    }

    @Override
    protected Phase processMessageValid() throws IOException {
        if (source.getMemoryType() == DmsMessageMemoryType.permanent) {
            String multi = findLaneUseMultiIndication(source.getNumber()).
                            getQuickMessage().getMulti();
            int msg_num = findLaneUseMultiIndication(source.getNumber()).getMsgNum();
            if (source.getNumber() == msg_num)
                setCurrentMessage();
            else
                source.setMemoryType(DmsMessageMemoryType.undefined);
        } else {
            logError("INVALID SOURCE");
            setErrorStatus(source.toString());
        }
        return null;
    }

    private void setCurrentMessage() throws IOException {
        Integer d = parseDuration(lcsTime.getInteger());
        DMSMessagePriority prior = DMSMessagePriority.SCHEDULED;
        String multi = findLaneUseMultiIndication(source.getNumber()).
                getQuickMessage().getMulti();
        setCurrentMessage(multi, 0, prior, d);
    }
}
