
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
 * 						The authentication policy is an association of a security level and a schedule. It defines when
 * 						a certain security level is required to grant access to a credential holder. Each security 
 * 						level is given a unique priority. If authentication policies have overlapping schedules, 
 * 						the security level with the highest priority is used.
 * 					
 * 
 * <p>Java class for AuthenticationPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthenticationPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ScheduleToken" type="{http://www.onvif.org/ver10/pacs}ReferenceToken"/&gt;
 *         &lt;element name="SecurityLevelConstraint" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}SecurityLevelConstraint" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}AuthenticationPolicyExtension" minOccurs="0"/&gt;
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
@XmlType(name = "AuthenticationPolicy", propOrder = {
    "scheduleToken",
    "securityLevelConstraint",
    "extension"
})
public class AuthenticationPolicy {

    @XmlElement(name = "ScheduleToken", required = true)
    protected String scheduleToken;
    @XmlElement(name = "SecurityLevelConstraint", required = true)
    protected List<SecurityLevelConstraint> securityLevelConstraint;
    @XmlElement(name = "Extension")
    protected AuthenticationPolicyExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the scheduleToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScheduleToken() {
        return scheduleToken;
    }

    /**
     * Sets the value of the scheduleToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduleToken(String value) {
        this.scheduleToken = value;
    }

    /**
     * Gets the value of the securityLevelConstraint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the securityLevelConstraint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecurityLevelConstraint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SecurityLevelConstraint }
     * 
     * 
     */
    public List<SecurityLevelConstraint> getSecurityLevelConstraint() {
        if (securityLevelConstraint == null) {
            securityLevelConstraint = new ArrayList<SecurityLevelConstraint>();
        }
        return this.securityLevelConstraint;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationPolicyExtension }
     *     
     */
    public AuthenticationPolicyExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationPolicyExtension }
     *     
     */
    public void setExtension(AuthenticationPolicyExtension value) {
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
