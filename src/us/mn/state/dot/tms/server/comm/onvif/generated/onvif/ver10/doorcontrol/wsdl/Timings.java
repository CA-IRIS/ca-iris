
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.doorcontrol.wsdl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;


/**
 * 
 * 						A structure defining times such as how long the door is unlocked when accessed,
 * 						extended grant time, etc.
 * 					
 * 
 * <p>Java class for Timings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Timings"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReleaseTime" type="{http://www.w3.org/2001/XMLSchema}duration"/&gt;
 *         &lt;element name="OpenTime" type="{http://www.w3.org/2001/XMLSchema}duration"/&gt;
 *         &lt;element name="ExtendedReleaseTime" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/&gt;
 *         &lt;element name="DelayTimeBeforeRelock" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/&gt;
 *         &lt;element name="ExtendedOpenTime" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/&gt;
 *         &lt;element name="PreAlarmTime" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/doorcontrol/wsdl}TimingsExtension" minOccurs="0"/&gt;
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
@XmlType(name = "Timings", propOrder = {
    "releaseTime",
    "openTime",
    "extendedReleaseTime",
    "delayTimeBeforeRelock",
    "extendedOpenTime",
    "preAlarmTime",
    "extension"
})
public class Timings {

    @XmlElement(name = "ReleaseTime", required = true)
    protected Duration releaseTime;
    @XmlElement(name = "OpenTime", required = true)
    protected Duration openTime;
    @XmlElement(name = "ExtendedReleaseTime")
    protected Duration extendedReleaseTime;
    @XmlElement(name = "DelayTimeBeforeRelock")
    protected Duration delayTimeBeforeRelock;
    @XmlElement(name = "ExtendedOpenTime")
    protected Duration extendedOpenTime;
    @XmlElement(name = "PreAlarmTime")
    protected Duration preAlarmTime;
    @XmlElement(name = "Extension")
    protected TimingsExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the releaseTime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getReleaseTime() {
        return releaseTime;
    }

    /**
     * Sets the value of the releaseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setReleaseTime(Duration value) {
        this.releaseTime = value;
    }

    /**
     * Gets the value of the openTime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getOpenTime() {
        return openTime;
    }

    /**
     * Sets the value of the openTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setOpenTime(Duration value) {
        this.openTime = value;
    }

    /**
     * Gets the value of the extendedReleaseTime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getExtendedReleaseTime() {
        return extendedReleaseTime;
    }

    /**
     * Sets the value of the extendedReleaseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setExtendedReleaseTime(Duration value) {
        this.extendedReleaseTime = value;
    }

    /**
     * Gets the value of the delayTimeBeforeRelock property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getDelayTimeBeforeRelock() {
        return delayTimeBeforeRelock;
    }

    /**
     * Sets the value of the delayTimeBeforeRelock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setDelayTimeBeforeRelock(Duration value) {
        this.delayTimeBeforeRelock = value;
    }

    /**
     * Gets the value of the extendedOpenTime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getExtendedOpenTime() {
        return extendedOpenTime;
    }

    /**
     * Sets the value of the extendedOpenTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setExtendedOpenTime(Duration value) {
        this.extendedOpenTime = value;
    }

    /**
     * Gets the value of the preAlarmTime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getPreAlarmTime() {
        return preAlarmTime;
    }

    /**
     * Sets the value of the preAlarmTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setPreAlarmTime(Duration value) {
        this.preAlarmTime = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TimingsExtension }
     *     
     */
    public TimingsExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimingsExtension }
     *     
     */
    public void setExtension(TimingsExtension value) {
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
