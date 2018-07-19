
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.authenticationbehavior.wsdl;

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
 *         &lt;element name="AuthenticationProfile" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}AuthenticationProfile"/&gt;
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
    "authenticationProfile"
})
@XmlRootElement(name = "SetAuthenticationProfile")
public class SetAuthenticationProfile {

    @XmlElement(name = "AuthenticationProfile", required = true)
    protected AuthenticationProfile authenticationProfile;

    /**
     * Gets the value of the authenticationProfile property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationProfile }
     *     
     */
    public AuthenticationProfile getAuthenticationProfile() {
        return authenticationProfile;
    }

    /**
     * Sets the value of the authenticationProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationProfile }
     *     
     */
    public void setAuthenticationProfile(AuthenticationProfile value) {
        this.authenticationProfile = value;
    }

}
