
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for FtpDestinationConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FtpDestinationConfiguration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HostAddress" type="{http://www.onvif.org/ver10/actionengine/wsdl}FtpHostAddress"/&gt;
 *         &lt;element name="UploadPath" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="FtpAuthentication" type="{http://www.onvif.org/ver10/actionengine/wsdl}FtpAuthenticationConfiguration"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/actionengine/wsdl}FtpDestinationConfigurationExtension" minOccurs="0"/&gt;
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
@XmlType(name = "FtpDestinationConfiguration", propOrder = {
    "hostAddress",
    "uploadPath",
    "ftpAuthentication",
    "extension"
})
public class FtpDestinationConfiguration {

    @XmlElement(name = "HostAddress", required = true)
    protected FtpHostAddress hostAddress;
    @XmlElement(name = "UploadPath", required = true)
    protected String uploadPath;
    @XmlElement(name = "FtpAuthentication", required = true)
    protected FtpAuthenticationConfiguration ftpAuthentication;
    @XmlElement(name = "Extension")
    protected FtpDestinationConfigurationExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the hostAddress property.
     * 
     * @return
     *     possible object is
     *     {@link FtpHostAddress }
     *     
     */
    public FtpHostAddress getHostAddress() {
        return hostAddress;
    }

    /**
     * Sets the value of the hostAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link FtpHostAddress }
     *     
     */
    public void setHostAddress(FtpHostAddress value) {
        this.hostAddress = value;
    }

    /**
     * Gets the value of the uploadPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * Sets the value of the uploadPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUploadPath(String value) {
        this.uploadPath = value;
    }

    /**
     * Gets the value of the ftpAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link FtpAuthenticationConfiguration }
     *     
     */
    public FtpAuthenticationConfiguration getFtpAuthentication() {
        return ftpAuthentication;
    }

    /**
     * Sets the value of the ftpAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link FtpAuthenticationConfiguration }
     *     
     */
    public void setFtpAuthentication(FtpAuthenticationConfiguration value) {
        this.ftpAuthentication = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link FtpDestinationConfigurationExtension }
     *     
     */
    public FtpDestinationConfigurationExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link FtpDestinationConfigurationExtension }
     *     
     */
    public void setExtension(FtpDestinationConfigurationExtension value) {
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
