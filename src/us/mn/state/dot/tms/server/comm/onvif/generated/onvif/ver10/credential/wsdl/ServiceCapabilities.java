
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.credential.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 						The service capabilities reflect optional functionality of a service. The information is static
 * 						and does not change during device operation. The following capabilities are available:
 * 					
 * 
 * <p>Java class for ServiceCapabilities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceCapabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SupportedIdentifierType" type="{http://www.onvif.org/ver10/pacs}Name" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/credential/wsdl}ServiceCapabilitiesExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="MaxLimit" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="CredentialValiditySupported" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="CredentialAccessProfileValiditySupported" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="ValiditySupportsTimeValue" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="MaxCredentials" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="MaxAccessProfilesPerCredential" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="ResetAntipassbackSupported" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="ClientSuppliedTokenSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DefaultCredentialSuspensionDuration" type="{http://www.w3.org/2001/XMLSchema}string" default="PT5M" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceCapabilities", propOrder = {
    "supportedIdentifierType",
    "extension"
})
public class ServiceCapabilities {

    @XmlElement(name = "SupportedIdentifierType", required = true)
    protected List<String> supportedIdentifierType;
    @XmlElement(name = "Extension")
    protected ServiceCapabilitiesExtension extension;
    @XmlAttribute(name = "MaxLimit", required = true)
    protected long maxLimit;
    @XmlAttribute(name = "CredentialValiditySupported", required = true)
    protected boolean credentialValiditySupported;
    @XmlAttribute(name = "CredentialAccessProfileValiditySupported", required = true)
    protected boolean credentialAccessProfileValiditySupported;
    @XmlAttribute(name = "ValiditySupportsTimeValue", required = true)
    protected boolean validitySupportsTimeValue;
    @XmlAttribute(name = "MaxCredentials", required = true)
    protected long maxCredentials;
    @XmlAttribute(name = "MaxAccessProfilesPerCredential", required = true)
    protected long maxAccessProfilesPerCredential;
    @XmlAttribute(name = "ResetAntipassbackSupported", required = true)
    protected boolean resetAntipassbackSupported;
    @XmlAttribute(name = "ClientSuppliedTokenSupported")
    protected Boolean clientSuppliedTokenSupported;
    @XmlAttribute(name = "DefaultCredentialSuspensionDuration")
    protected String defaultCredentialSuspensionDuration;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the supportedIdentifierType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedIdentifierType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedIdentifierType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSupportedIdentifierType() {
        if (supportedIdentifierType == null) {
            supportedIdentifierType = new ArrayList<String>();
        }
        return this.supportedIdentifierType;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCapabilitiesExtension }
     *     
     */
    public ServiceCapabilitiesExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCapabilitiesExtension }
     *     
     */
    public void setExtension(ServiceCapabilitiesExtension value) {
        this.extension = value;
    }

    /**
     * Gets the value of the maxLimit property.
     * 
     */
    public long getMaxLimit() {
        return maxLimit;
    }

    /**
     * Sets the value of the maxLimit property.
     * 
     */
    public void setMaxLimit(long value) {
        this.maxLimit = value;
    }

    /**
     * Gets the value of the credentialValiditySupported property.
     * 
     */
    public boolean isCredentialValiditySupported() {
        return credentialValiditySupported;
    }

    /**
     * Sets the value of the credentialValiditySupported property.
     * 
     */
    public void setCredentialValiditySupported(boolean value) {
        this.credentialValiditySupported = value;
    }

    /**
     * Gets the value of the credentialAccessProfileValiditySupported property.
     * 
     */
    public boolean isCredentialAccessProfileValiditySupported() {
        return credentialAccessProfileValiditySupported;
    }

    /**
     * Sets the value of the credentialAccessProfileValiditySupported property.
     * 
     */
    public void setCredentialAccessProfileValiditySupported(boolean value) {
        this.credentialAccessProfileValiditySupported = value;
    }

    /**
     * Gets the value of the validitySupportsTimeValue property.
     * 
     */
    public boolean isValiditySupportsTimeValue() {
        return validitySupportsTimeValue;
    }

    /**
     * Sets the value of the validitySupportsTimeValue property.
     * 
     */
    public void setValiditySupportsTimeValue(boolean value) {
        this.validitySupportsTimeValue = value;
    }

    /**
     * Gets the value of the maxCredentials property.
     * 
     */
    public long getMaxCredentials() {
        return maxCredentials;
    }

    /**
     * Sets the value of the maxCredentials property.
     * 
     */
    public void setMaxCredentials(long value) {
        this.maxCredentials = value;
    }

    /**
     * Gets the value of the maxAccessProfilesPerCredential property.
     * 
     */
    public long getMaxAccessProfilesPerCredential() {
        return maxAccessProfilesPerCredential;
    }

    /**
     * Sets the value of the maxAccessProfilesPerCredential property.
     * 
     */
    public void setMaxAccessProfilesPerCredential(long value) {
        this.maxAccessProfilesPerCredential = value;
    }

    /**
     * Gets the value of the resetAntipassbackSupported property.
     * 
     */
    public boolean isResetAntipassbackSupported() {
        return resetAntipassbackSupported;
    }

    /**
     * Sets the value of the resetAntipassbackSupported property.
     * 
     */
    public void setResetAntipassbackSupported(boolean value) {
        this.resetAntipassbackSupported = value;
    }

    /**
     * Gets the value of the clientSuppliedTokenSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isClientSuppliedTokenSupported() {
        if (clientSuppliedTokenSupported == null) {
            return false;
        } else {
            return clientSuppliedTokenSupported;
        }
    }

    /**
     * Sets the value of the clientSuppliedTokenSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setClientSuppliedTokenSupported(Boolean value) {
        this.clientSuppliedTokenSupported = value;
    }

    /**
     * Gets the value of the defaultCredentialSuspensionDuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultCredentialSuspensionDuration() {
        if (defaultCredentialSuspensionDuration == null) {
            return "PT5M";
        } else {
            return defaultCredentialSuspensionDuration;
        }
    }

    /**
     * Sets the value of the defaultCredentialSuspensionDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultCredentialSuspensionDuration(String value) {
        this.defaultCredentialSuspensionDuration = value;
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
