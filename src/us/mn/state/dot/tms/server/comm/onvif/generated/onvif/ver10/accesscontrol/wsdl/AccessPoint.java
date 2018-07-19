
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.accesscontrol.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 						The AccessPoint structure shall include all properties of the AccessPointInfo structure,
 * 						a reference to an authentication profile instance, and optionally a number of input and output devices.
 * 					
 * 
 * <p>Java class for AccessPoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccessPoint"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.onvif.org/ver10/accesscontrol/wsdl}AccessPointInfo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthenticationProfileToken" type="{http://www.onvif.org/ver10/pacs}ReferenceToken" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/accesscontrol/wsdl}AccessPointExtension" minOccurs="0"/&gt;
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
@XmlType(name = "AccessPoint", propOrder = {
    "authenticationProfileToken",
    "extension"
})
public class AccessPoint
    extends AccessPointInfo
{

    @XmlElement(name = "AuthenticationProfileToken")
    protected String authenticationProfileToken;
    @XmlElement(name = "Extension")
    protected AccessPointExtension extension;

    /**
     * Gets the value of the authenticationProfileToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationProfileToken() {
        return authenticationProfileToken;
    }

    /**
     * Sets the value of the authenticationProfileToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationProfileToken(String value) {
        this.authenticationProfileToken = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link AccessPointExtension }
     *     
     */
    public AccessPointExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessPointExtension }
     *     
     */
    public void setExtension(AccessPointExtension value) {
        this.extension = value;
    }

}
