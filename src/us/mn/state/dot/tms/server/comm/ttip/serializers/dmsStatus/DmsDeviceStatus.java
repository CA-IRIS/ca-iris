//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.04 at 05:25:19 PM CST 
//


package us.mn.state.dot.tms.server.comm.ttip.serializers.dmsStatus;

import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SignMsgSource;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.ttip.TtipPoller;
import us.mn.state.dot.tms.server.comm.ttip.serializers.common.Head;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}head"/>
 *         &lt;element ref="{}dms-device-status"/>
 *         &lt;element ref="{}dmsCurrentMessage"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Dan Rossiter
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "head",
    "dmsDeviceStatus",
    "dmsCurrentMessage"
})
public class DmsDeviceStatus {

    @XmlElement(name = "head", required = true)
    protected Head head;
    @XmlElement(name = "dms-device-status", required = true)
    protected String dmsDeviceStatus;
    @XmlElement(name = "dmsCurrentMessage", required = true)
    protected DmsCurrentMessage dmsCurrentMessage;

    /** Creation time */
    @XmlTransient
    private final long create_time;

    /** Constructor. */
    public DmsDeviceStatus() {
        create_time = TimeSteward.currentTimeMillis();
    }

    /**
     * Gets the value of the head property.
     * 
     * @return
     *     possible object is
     *     {@link Head }
     *     
     */
    public Head getHead() {
        return head;
    }

    /**
     * Sets the value of the head property.
     * 
     * @param value
     *     allowed object is
     *     {@link Head }
     *     
     */
    public void setHead(Head value) {
        this.head = value;
    }

    /**
     * Gets the value of the dmsDeviceStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDmsDeviceStatus() {
        return dmsDeviceStatus;
    }

    /**
     * Sets the value of the dmsDeviceStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDmsDeviceStatus(String value) {
        this.dmsDeviceStatus = value;
    }

    /**
     * Gets the value of the dmsCurrentMessage property.
     * 
     * @return
     *     possible object is
     *     {@link DmsCurrentMessage }
     *     
     */
    public DmsCurrentMessage getDmsCurrentMessage() {
        return dmsCurrentMessage;
    }

    /**
     * Sets the value of the dmsCurrentMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link DmsCurrentMessage }
     *     
     */
    public void setDmsCurrentMessage(DmsCurrentMessage value) {
        this.dmsCurrentMessage = value;
    }

    /** Check if the record is expired */
    public boolean isExpired() {
        long DURATION_MS = 2 * 1000;
        return create_time + DURATION_MS < TimeSteward.currentTimeMillis();
    }

    /** Store the record. Called via OpRead. */
    public void store(DMSImpl d) {
        d.setOpStatus(getDmsDeviceStatus());
        d.setMessageCurrent(createMessage(d), null);
        TtipPoller.log("Stored DMS rec=" + this);
    }

    /** Converts parsed message into IRIS representation */
    private SignMessage createMessage(DMSImpl dms) {
        String NP = "[np]";
        String NL = "[nl]";
        String multi;
        DmsCurrentMessage cur = getDmsCurrentMessage();
        String p1 = cur.getPhase1Line1() + NL + cur.getPhase1Line2() + NL + cur.getPhase1Line3();
        String p2 = cur.getPhase2Line1() + NL + cur.getPhase2Line2() + NL + cur.getPhase2Line3();

        if (p1.equals(NL + NL))
            p1 = "";
        if (p2.equals(NL + NL))
            p2 = "";

        if (!p2.equals(""))
            multi = p1 + NP + p2;
        else if (!p1.equals("") && p2.equals(""))
            multi = p1;
        else
            multi = "";
        return dms.createMsg(multi, false, DMSMessagePriority.OTHER_SYSTEM,
                             DMSMessagePriority.OTHER_SYSTEM, SignMsgSource.external, null);
    }
}
