
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.credential.wsdl;

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
 *         &lt;element name="CredentialToken" type="{http://www.onvif.org/ver10/pacs}ReferenceToken"/&gt;
 *         &lt;element name="CredentialIdentifier" type="{http://www.onvif.org/ver10/credential/wsdl}CredentialIdentifier"/&gt;
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
    "credentialToken",
    "credentialIdentifier"
})
@XmlRootElement(name = "SetCredentialIdentifier")
public class SetCredentialIdentifier {

    @XmlElement(name = "CredentialToken", required = true)
    protected String credentialToken;
    @XmlElement(name = "CredentialIdentifier", required = true)
    protected CredentialIdentifier credentialIdentifier;

    /**
     * Gets the value of the credentialToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCredentialToken() {
        return credentialToken;
    }

    /**
     * Sets the value of the credentialToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCredentialToken(String value) {
        this.credentialToken = value;
    }

    /**
     * Gets the value of the credentialIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialIdentifier }
     *     
     */
    public CredentialIdentifier getCredentialIdentifier() {
        return credentialIdentifier;
    }

    /**
     * Sets the value of the credentialIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialIdentifier }
     *     
     */
    public void setCredentialIdentifier(CredentialIdentifier value) {
        this.credentialIdentifier = value;
    }

}
