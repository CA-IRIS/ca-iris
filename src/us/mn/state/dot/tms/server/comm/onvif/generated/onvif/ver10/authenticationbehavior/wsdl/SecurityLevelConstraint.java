
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.authenticationbehavior.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 						This structure defines what security level should be active depending on the state of the
 * 						schedule.
 * 					
 * 
 * <p>Java class for SecurityLevelConstraint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecurityLevelConstraint"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ActiveRegularSchedule" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="ActiveSpecialDaySchedule" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="AuthenticationMode" type="{http://www.onvif.org/ver10/pacs}Name" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SecurityLevelToken" type="{http://www.onvif.org/ver10/pacs}ReferenceToken"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}SecurityLevelConstraintExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecurityLevelConstraint", propOrder = {
    "activeRegularSchedule",
    "activeSpecialDaySchedule",
    "authenticationMode",
    "securityLevelToken",
    "extension"
})
public class SecurityLevelConstraint {

    @XmlElement(name = "ActiveRegularSchedule")
    protected boolean activeRegularSchedule;
    @XmlElement(name = "ActiveSpecialDaySchedule")
    protected boolean activeSpecialDaySchedule;
    @XmlElement(name = "AuthenticationMode")
    protected List<String> authenticationMode;
    @XmlElement(name = "SecurityLevelToken", required = true)
    protected String securityLevelToken;
    @XmlElement(name = "Extension")
    protected SecurityLevelConstraintExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the activeRegularSchedule property.
     * 
     */
    public boolean isActiveRegularSchedule() {
        return activeRegularSchedule;
    }

    /**
     * Sets the value of the activeRegularSchedule property.
     * 
     */
    public void setActiveRegularSchedule(boolean value) {
        this.activeRegularSchedule = value;
    }

    /**
     * Gets the value of the activeSpecialDaySchedule property.
     * 
     */
    public boolean isActiveSpecialDaySchedule() {
        return activeSpecialDaySchedule;
    }

    /**
     * Sets the value of the activeSpecialDaySchedule property.
     * 
     */
    public void setActiveSpecialDaySchedule(boolean value) {
        this.activeSpecialDaySchedule = value;
    }

    /**
     * Gets the value of the authenticationMode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authenticationMode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthenticationMode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAuthenticationMode() {
        if (authenticationMode == null) {
            authenticationMode = new ArrayList<String>();
        }
        return this.authenticationMode;
    }

    /**
     * Gets the value of the securityLevelToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityLevelToken() {
        return securityLevelToken;
    }

    /**
     * Sets the value of the securityLevelToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityLevelToken(String value) {
        this.securityLevelToken = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityLevelConstraintExtension }
     *     
     */
    public SecurityLevelConstraintExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityLevelConstraintExtension }
     *     
     */
    public void setExtension(SecurityLevelConstraintExtension value) {
        this.extension = value;
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
