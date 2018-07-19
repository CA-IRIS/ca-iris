
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
 * Contains information about a format type.
 * 
 * <p>Java class for CredentialData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CredentialData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Credential" type="{http://www.onvif.org/ver10/credential/wsdl}Credential"/&gt;
 *         &lt;element name="CredentialState" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialState"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialDataExtension" minOccurs="0"/&gt;
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
@XmlType(name = "CredentialData", propOrder = {
    "credential",
    "credentialState",
    "extension"
})
public class CredentialData {

    @XmlElement(name = "Credential", required = true)
    protected Credential credential;
    @XmlElement(name = "CredentialState", required = true)
    protected CredentialState credentialState;
    @XmlElement(name = "Extension")
    protected CredentialDataExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the credential property.
     * 
     * @return
     *     possible object is
     *     {@link Credential }
     *     
     */
    public Credential getCredential() {
        return credential;
    }

    /**
     * Sets the value of the credential property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credential }
     *     
     */
    public void setCredential(Credential value) {
        this.credential = value;
    }

    /**
     * Gets the value of the credentialState property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialState }
     *     
     */
    public CredentialState getCredentialState() {
        return credentialState;
    }

    /**
     * Sets the value of the credentialState property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialState }
     *     
     */
    public void setCredentialState(CredentialState value) {
        this.credentialState = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialDataExtension }
     *     
     */
    public CredentialDataExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialDataExtension }
     *     
     */
    public void setExtension(CredentialDataExtension value) {
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
