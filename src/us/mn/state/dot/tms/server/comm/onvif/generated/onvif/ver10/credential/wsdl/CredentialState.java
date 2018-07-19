
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.credential.wsdl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 						The CredentialState structure contains information about the state of the credential and
 * 						optionally the reason of why the credential was disabled.
 * 					
 * 
 * <p>Java class for CredentialState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CredentialState"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Enabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="Reason" type="{http://www.onvif.org/ver10/pacs}Name" minOccurs="0"/&gt;
 *         &lt;element name="AntipassbackState" type="{http://www.onvif.org/ver10/credential/wsdl}AntipassbackState" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialStateExtension" minOccurs="0"/&gt;
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
@XmlType(name = "CredentialState", propOrder = {
    "enabled",
    "reason",
    "antipassbackState",
    "extension"
})
public class CredentialState {

    @XmlElement(name = "Enabled")
    protected boolean enabled;
    @XmlElement(name = "Reason")
    protected String reason;
    @XmlElement(name = "AntipassbackState")
    protected AntipassbackState antipassbackState;
    @XmlElement(name = "Extension")
    protected CredentialStateExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the enabled property.
     * 
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     * 
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the antipassbackState property.
     * 
     * @return
     *     possible object is
     *     {@link AntipassbackState }
     *     
     */
    public AntipassbackState getAntipassbackState() {
        return antipassbackState;
    }

    /**
     * Sets the value of the antipassbackState property.
     * 
     * @param value
     *     allowed object is
     *     {@link AntipassbackState }
     *     
     */
    public void setAntipassbackState(AntipassbackState value) {
        this.antipassbackState = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialStateExtension }
     *     
     */
    public CredentialStateExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialStateExtension }
     *     
     */
    public void setExtension(CredentialStateExtension value) {
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
