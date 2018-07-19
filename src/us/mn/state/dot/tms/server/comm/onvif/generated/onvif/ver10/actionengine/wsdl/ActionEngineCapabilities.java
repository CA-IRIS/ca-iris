
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * Action Engine Capabilities data structure contains the maximum number of supported actions and number of actions in use for generic as well as specific action types
 * 
 * <p>Java class for ActionEngineCapabilities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActionEngineCapabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ActionCapabilities" type="{http://www.onvif.org/ver10/actionengine/wsdl}ActionTypeLimits" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/actionengine/wsdl}ActionEngineCapabilitiesExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="MaximumTriggers" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="MaximumActions" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionEngineCapabilities", propOrder = {
    "actionCapabilities",
    "extension"
})
public class ActionEngineCapabilities {

    @XmlElement(name = "ActionCapabilities")
    protected List<ActionTypeLimits> actionCapabilities;
    @XmlElement(name = "Extension")
    protected ActionEngineCapabilitiesExtension extension;
    @XmlAttribute(name = "MaximumTriggers")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumTriggers;
    @XmlAttribute(name = "MaximumActions")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumActions;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the actionCapabilities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionCapabilities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionCapabilities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActionTypeLimits }
     * 
     * 
     */
    public List<ActionTypeLimits> getActionCapabilities() {
        if (actionCapabilities == null) {
            actionCapabilities = new ArrayList<ActionTypeLimits>();
        }
        return this.actionCapabilities;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ActionEngineCapabilitiesExtension }
     *     
     */
    public ActionEngineCapabilitiesExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionEngineCapabilitiesExtension }
     *     
     */
    public void setExtension(ActionEngineCapabilitiesExtension value) {
        this.extension = value;
    }

    /**
     * Gets the value of the maximumTriggers property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumTriggers() {
        return maximumTriggers;
    }

    /**
     * Sets the value of the maximumTriggers property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumTriggers(BigInteger value) {
        this.maximumTriggers = value;
    }

    /**
     * Gets the value of the maximumActions property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumActions() {
        return maximumActions;
    }

    /**
     * Sets the value of the maximumActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumActions(BigInteger value) {
        this.maximumActions = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
