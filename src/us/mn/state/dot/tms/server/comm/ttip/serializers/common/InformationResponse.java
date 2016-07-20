package us.mn.state.dot.tms.server.comm.ttip.serializers.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element ref="{}messageHeader"/>
 *         &lt;element ref="{}responseGroups"/>
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
        "messageHeader",
        "responseGroups"
})
@XmlRootElement(name = "informationResponse")
public class InformationResponse<TLocalResponseGroup> {

    @XmlElement(name = "messageHeader", required = true)
    protected MessageHeader messageHeader;
    @XmlElement(name = "responseGroups", required = true)
    protected ResponseGroups<TLocalResponseGroup> responseGroups;

    /**
     * Gets the value of the messageHeader property.
     *
     * @return
     *     possible object is
     *     {@link MessageHeader }
     *
     */
    public MessageHeader getMessageHeader() {
        return messageHeader;
    }

    /**
     * Sets the value of the messageHeader property.
     *
     * @param value
     *     allowed object is
     *     {@link MessageHeader }
     *
     */
    public void setMessageHeader(MessageHeader value) {
        this.messageHeader = value;
    }

    /**
     * Gets the value of the responseGroups property.
     *
     * @return
     *     possible object is
     *     {@link ResponseGroups }
     *
     */
    public ResponseGroups<TLocalResponseGroup> getResponseGroups() {
        return responseGroups;
    }

    /**
     * Sets the value of the responseGroups property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseGroups }
     *
     */
    public void setResponseGroups(ResponseGroups<TLocalResponseGroup> value) {
        this.responseGroups = value;
    }
}
