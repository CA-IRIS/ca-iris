
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
 * 						The AuthenticationProfile structure shall include all properties of the
 * 						AuthenticationProfileInfo structure
 * 						and also a default security level, an authentication mode, and a list of AuthenticationProfile
 * 						instances.
 * 					
 * 
 * <p>Java class for AuthenticationProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthenticationProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}AuthenticationProfileInfo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DefaultSecurityLevelToken" type="{http://www.onvif.org/ver10/pacs}ReferenceToken"/&gt;
 *         &lt;element name="AuthenticationPolicy" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}AuthenticationPolicy" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}AuthenticationProfileExtension" minOccurs="0"/&gt;
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
@XmlType(name = "AuthenticationProfile", propOrder = {
    "defaultSecurityLevelToken",
    "authenticationPolicy",
    "extension"
})
public class AuthenticationProfile
    extends AuthenticationProfileInfo
{

    @XmlElement(name = "DefaultSecurityLevelToken", required = true)
    protected String defaultSecurityLevelToken;
    @XmlElement(name = "AuthenticationPolicy")
    protected List<AuthenticationPolicy> authenticationPolicy;
    @XmlElement(name = "Extension")
    protected AuthenticationProfileExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the defaultSecurityLevelToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSecurityLevelToken() {
        return defaultSecurityLevelToken;
    }

    /**
     * Sets the value of the defaultSecurityLevelToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSecurityLevelToken(String value) {
        this.defaultSecurityLevelToken = value;
    }

    /**
     * Gets the value of the authenticationPolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authenticationPolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthenticationPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthenticationPolicy }
     * 
     * 
     */
    public List<AuthenticationPolicy> getAuthenticationPolicy() {
        if (authenticationPolicy == null) {
            authenticationPolicy = new ArrayList<AuthenticationPolicy>();
        }
        return this.authenticationPolicy;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationProfileExtension }
     *     
     */
    public AuthenticationProfileExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationProfileExtension }
     *     
     */
    public void setExtension(AuthenticationProfileExtension value) {
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
