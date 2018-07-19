
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.credential.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.pacs.Attribute;


/**
 * 
 * 						A Credential is a physical/tangible object, a piece of knowledge, or a facet of a person's
 * 						physical being, that enables an individual access to a given physical facility or computer-based
 * 						information system. A credential holds one or more credential identifiers. To gain access one or
 * 						more identifiers may be required.
 * 					
 * 
 * <p>Java class for Credential complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Credential"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.onvif.org/ver10/credential/wsdl}CredentialInfo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CredentialIdentifier" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialIdentifier" maxOccurs="unbounded"/&gt;
 *         &lt;element name="CredentialAccessProfile" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialAccessProfile" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ExtendedGrantTime" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="Attribute" type="{http://www.onvif.org/ver10/pacs}Attribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Credential", propOrder = {
    "credentialIdentifier",
    "credentialAccessProfile",
    "extendedGrantTime",
    "attribute",
    "extension"
})
public class Credential
    extends CredentialInfo
{

    @XmlElement(name = "CredentialIdentifier", required = true)
    protected List<CredentialIdentifier> credentialIdentifier;
    @XmlElement(name = "CredentialAccessProfile")
    protected List<CredentialAccessProfile> credentialAccessProfile;
    @XmlElement(name = "ExtendedGrantTime", defaultValue = "false")
    protected boolean extendedGrantTime;
    @XmlElement(name = "Attribute")
    protected List<Attribute> attribute;
    @XmlElement(name = "Extension")
    protected CredentialExtension extension;

    /**
     * Gets the value of the credentialIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credentialIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredentialIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CredentialIdentifier }
     * 
     * 
     */
    public List<CredentialIdentifier> getCredentialIdentifier() {
        if (credentialIdentifier == null) {
            credentialIdentifier = new ArrayList<CredentialIdentifier>();
        }
        return this.credentialIdentifier;
    }

    /**
     * Gets the value of the credentialAccessProfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credentialAccessProfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredentialAccessProfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CredentialAccessProfile }
     * 
     * 
     */
    public List<CredentialAccessProfile> getCredentialAccessProfile() {
        if (credentialAccessProfile == null) {
            credentialAccessProfile = new ArrayList<CredentialAccessProfile>();
        }
        return this.credentialAccessProfile;
    }

    /**
     * Gets the value of the extendedGrantTime property.
     * 
     */
    public boolean isExtendedGrantTime() {
        return extendedGrantTime;
    }

    /**
     * Sets the value of the extendedGrantTime property.
     * 
     */
    public void setExtendedGrantTime(boolean value) {
        this.extendedGrantTime = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialExtension }
     *     
     */
    public CredentialExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialExtension }
     *     
     */
    public void setExtension(CredentialExtension value) {
        this.extension = value;
    }

}
