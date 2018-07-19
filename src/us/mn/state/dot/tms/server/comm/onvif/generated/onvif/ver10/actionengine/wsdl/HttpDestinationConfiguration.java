
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for HttpDestinationConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HttpDestinationConfiguration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HostAddress" type="{http://www.onvif.org/ver10/actionengine/wsdl}HttpHostAddress"/&gt;
 *         &lt;element name="HttpAuthentication" type="{http://www.onvif.org/ver10/actionengine/wsdl}HttpAuthenticationConfiguration" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/actionengine/wsdl}HttpDestinationConfigurationExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="uri" type="{http://www.w3.org/2001/XMLSchema}string" default="/" /&gt;
 *       &lt;attribute name="protocol" type="{http://www.onvif.org/ver10/actionengine/wsdl}HttpProtocolType" default="http" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HttpDestinationConfiguration", propOrder = {
    "hostAddress",
    "httpAuthentication",
    "extension"
})
public class HttpDestinationConfiguration {

    @XmlElement(name = "HostAddress", required = true)
    protected HttpHostAddress hostAddress;
    @XmlElement(name = "HttpAuthentication")
    protected HttpAuthenticationConfiguration httpAuthentication;
    @XmlElement(name = "Extension")
    protected HttpDestinationConfigurationExtension extension;
    @XmlAttribute(name = "uri")
    protected String uri;
    @XmlAttribute(name = "protocol")
    protected HttpProtocolType protocol;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the hostAddress property.
     * 
     * @return
     *     possible object is
     *     {@link HttpHostAddress }
     *     
     */
    public HttpHostAddress getHostAddress() {
        return hostAddress;
    }

    /**
     * Sets the value of the hostAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link HttpHostAddress }
     *     
     */
    public void setHostAddress(HttpHostAddress value) {
        this.hostAddress = value;
    }

    /**
     * Gets the value of the httpAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link HttpAuthenticationConfiguration }
     *     
     */
    public HttpAuthenticationConfiguration getHttpAuthentication() {
        return httpAuthentication;
    }

    /**
     * Sets the value of the httpAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link HttpAuthenticationConfiguration }
     *     
     */
    public void setHttpAuthentication(HttpAuthenticationConfiguration value) {
        this.httpAuthentication = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link HttpDestinationConfigurationExtension }
     *     
     */
    public HttpDestinationConfigurationExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link HttpDestinationConfigurationExtension }
     *     
     */
    public void setExtension(HttpDestinationConfigurationExtension value) {
        this.extension = value;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        if (uri == null) {
            return "/";
        } else {
            return uri;
        }
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the protocol property.
     * 
     * @return
     *     possible object is
     *     {@link HttpProtocolType }
     *     
     */
    public HttpProtocolType getProtocol() {
        if (protocol == null) {
            return HttpProtocolType.HTTP;
        } else {
            return protocol;
        }
    }

    /**
     * Sets the value of the protocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link HttpProtocolType }
     *     
     */
    public void setProtocol(HttpProtocolType value) {
        this.protocol = value;
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
