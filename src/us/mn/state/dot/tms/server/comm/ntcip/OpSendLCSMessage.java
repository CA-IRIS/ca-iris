package us.mn.state.dot.tms.server.comm.ntcip;

import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageCRC;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.DmsMessageMemoryType;
import us.mn.state.dot.tms.server.comm.ntcip.mib1203.MessageActivationCode;
import us.mn.state.dot.tms.server.comm.snmp.GenError;
import us.mn.state.dot.tms.server.comm.snmp.NoSuchName;

import java.io.IOException;

import static us.mn.state.dot.tms.server.comm.ntcip.mib1203.MIB1203.dmsActivateMessage;

public class OpSendLCSMessage extends OpSendDMSMessage {

    /** Maximum message priority */
    static private final int MAX_MESSAGE_PRIORITY = 255;

    /** Sign message */
    private final SignMessage message;

    /** MULTI string */
    private final String multi;

    /** User who deployed the message */
    private final User owner;

    /** Message number (row in changeable message table).  This is normally
     * 1 for uncached messages.  If a number greater than 1 is used, an
     * attempt will be made to activate that message -- if that fails, the
     * changeable message table will be updated and then the message will
     * be activated.  This allows complex messages to remain cached and
     * activated quickly. */
    private final int msg_num;

    /** Message CRC */
    private final int message_crc;

    /** Create a new send DMS message operation */
    public OpSendLCSMessage(DMSImpl d, SignMessage sm, User o) {
        super(d, sm, o);
        message = sm;
        multi = parseMulti(sm.getMulti());
        owner = o;
        message_crc = DmsMessageCRC.calculate(multi,
                sm.getBeaconEnabled(), 0);
        msg_num = lookupMsgNum(multi);
    }

    /** Operation equality test */
    @Override
    public boolean equals(Object o) {
        if (o instanceof OpSendLCSMessage) {
            OpSendLCSMessage op = (OpSendLCSMessage) o;
            return dms == op.dms && SignMessageHelper.isEquivalent(
                    message, op.message);
        } else
            return false;
    }

    /** Create the second phase of the operation */
    @Override
    protected Phase phaseTwo() {
        dms.setMessageNext(message);
        if (SignMessageHelper.isBlank(message))
            return new ActivateBlankMsg();
        else
            return new ActivateMsg();
    }

    /** Phase to activate a blank message */
    protected class ActivateBlankMsg extends Phase {

        /** Activate a blank message */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            MessageActivationCode act = new MessageActivationCode(
                    dmsActivateMessage.node);
            act.setDuration(DURATION_INDEFINITE);
            act.setPriority(MAX_MESSAGE_PRIORITY);
            act.setMemoryType(DmsMessageMemoryType.blank);
            act.setNumber(1);
            act.setCrc(0);
            act.setAddress(0);
            mess.add(act);
            try {
                logStore(act);
                mess.storeProps();
            }
            catch (NoSuchName e) {
                // Some Ledstar signs will return NoSuchName
                // when trying to set dmsActivateMessage with
                // the "wrong" community name (Public).
                setErrorStatus("READ ONLY (NoSuchName)");
                return null;
            }
            catch (GenError e) {
                return new QueryActivateMsgErr();
            }
            dms.setMessageCurrent(message, owner);
            return new SetLossMsgs();
        }
    }

    /** Phase to activate the message */
    protected class ActivateMsg extends Phase {

        /** Activate the message */
        @SuppressWarnings("unchecked")
        protected Phase poll(CommMessage mess) throws IOException {
            MessageActivationCode act = new MessageActivationCode(
                    dmsActivateMessage.node);
            act.setDuration(getDuration());
            act.setPriority(MAX_MESSAGE_PRIORITY);
            act.setMemoryType(DmsMessageMemoryType.changeable);
            act.setNumber(msg_num);
            act.setCrc(message_crc);
            act.setAddress(0);
            mess.add(act);
            try {
                logStore(act);
                mess.storeProps();
            }
            catch (NoSuchName e) {
                // Some Ledstar signs will return NoSuchName
                // when trying to set dmsActivateMessage with
                // the "wrong" community name (Public).
                setErrorStatus("READ ONLY (NoSuchName)");
                return null;
            }
            catch (GenError e) {
                return new QueryActivateMsgErr();
            }
            dms.setMessageCurrent(message, owner);
            return new SetLossMsgs();
        }
    }

    /** Get the message duration */
    private int getDuration() {
        return getDuration(message.getDuration());
    }
}
