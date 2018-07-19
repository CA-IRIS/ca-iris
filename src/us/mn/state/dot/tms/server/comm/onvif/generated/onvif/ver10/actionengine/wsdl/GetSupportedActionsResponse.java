
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SupportedActions" type="{http://www.onvif.org/ver10/actionengine/wsdl}SupportedActions"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "supportedActions"
})
@XmlRootElement(name = "GetSupportedActionsResponse")
public class GetSupportedActionsResponse {

    @XmlElement(name = "SupportedActions", required = true)
    protected SupportedActions supportedActions;

    /**
     * Gets the value of the supportedActions property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedActions }
     *     
     */
    public SupportedActions getSupportedActions() {
        return supportedActions;
    }

    /**
     * Sets the value of the supportedActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedActions }
     *     
     */
    public void setSupportedActions(SupportedActions value) {
        this.supportedActions = value;
    }

}
