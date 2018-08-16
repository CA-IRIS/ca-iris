
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <prop>Java class for SystemCapabilities complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SystemCapabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DiscoveryResolve" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="DiscoveryBye" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="RemoteDiscovery" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="SystemBackup" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="SystemLogging" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="FirmwareUpgrade" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="SupportedVersions" type="{http://www.onvif.org/ver10/schema}OnvifVersion" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}SystemCapabilitiesExtension" minOccurs="0"/&gt;
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
@XmlType(name = "SystemCapabilities", propOrder = {
    "discoveryResolve",
    "discoveryBye",
    "remoteDiscovery",
    "systemBackup",
    "systemLogging",
    "firmwareUpgrade",
    "supportedVersions",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class SystemCapabilities {

    @XmlElement(name = "DiscoveryResolve")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected boolean discoveryResolve;
    @XmlElement(name = "DiscoveryBye")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected boolean discoveryBye;
    @XmlElement(name = "RemoteDiscovery")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected boolean remoteDiscovery;
    @XmlElement(name = "SystemBackup")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected boolean systemBackup;
    @XmlElement(name = "SystemLogging")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected boolean systemLogging;
    @XmlElement(name = "FirmwareUpgrade")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected boolean firmwareUpgrade;
    @XmlElement(name = "SupportedVersions", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<OnvifVersion> supportedVersions;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected SystemCapabilitiesExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the discoveryResolve property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public boolean isDiscoveryResolve() {
        return discoveryResolve;
    }

    /**
     * Sets the value of the discoveryResolve property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setDiscoveryResolve(boolean value) {
        this.discoveryResolve = value;
    }

    /**
     * Gets the value of the discoveryBye property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public boolean isDiscoveryBye() {
        return discoveryBye;
    }

    /**
     * Sets the value of the discoveryBye property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setDiscoveryBye(boolean value) {
        this.discoveryBye = value;
    }

    /**
     * Gets the value of the remoteDiscovery property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public boolean isRemoteDiscovery() {
        return remoteDiscovery;
    }

    /**
     * Sets the value of the remoteDiscovery property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setRemoteDiscovery(boolean value) {
        this.remoteDiscovery = value;
    }

    /**
     * Gets the value of the systemBackup property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public boolean isSystemBackup() {
        return systemBackup;
    }

    /**
     * Sets the value of the systemBackup property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setSystemBackup(boolean value) {
        this.systemBackup = value;
    }

    /**
     * Gets the value of the systemLogging property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public boolean isSystemLogging() {
        return systemLogging;
    }

    /**
     * Sets the value of the systemLogging property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setSystemLogging(boolean value) {
        this.systemLogging = value;
    }

    /**
     * Gets the value of the firmwareUpgrade property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public boolean isFirmwareUpgrade() {
        return firmwareUpgrade;
    }

    /**
     * Sets the value of the firmwareUpgrade property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setFirmwareUpgrade(boolean value) {
        this.firmwareUpgrade = value;
    }

    /**
     * Gets the value of the supportedVersions property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedVersions property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedVersions().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link OnvifVersion }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<OnvifVersion> getSupportedVersions() {
        if (supportedVersions == null) {
            supportedVersions = new ArrayList<OnvifVersion>();
        }
        return this.supportedVersions;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link SystemCapabilitiesExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public SystemCapabilitiesExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemCapabilitiesExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(SystemCapabilitiesExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <prop>
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
