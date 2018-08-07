
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for CapabilitiesExtension complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapabilitiesExtension"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DeviceIO" type="{http://www.onvif.org/ver10/schema}DeviceIOCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="Display" type="{http://www.onvif.org/ver10/schema}DisplayCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="Recording" type="{http://www.onvif.org/ver10/schema}RecordingCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="Search" type="{http://www.onvif.org/ver10/schema}SearchCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="Replay" type="{http://www.onvif.org/ver10/schema}ReplayCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="Receiver" type="{http://www.onvif.org/ver10/schema}ReceiverCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="AnalyticsDevice" type="{http://www.onvif.org/ver10/schema}AnalyticsDeviceCapabilities" minOccurs="0"/&gt;
 *         &lt;element name="Extensions" type="{http://www.onvif.org/ver10/schema}CapabilitiesExtension2" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesExtension", propOrder = {
    "any",
    "deviceIO",
    "display",
    "recording",
    "search",
    "replay",
    "receiver",
    "analyticsDevice",
    "extensions"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class CapabilitiesExtension {

    @XmlAnyElement(lax = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Object> any;
    @XmlElement(name = "DeviceIO")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected DeviceIOCapabilities deviceIO;
    @XmlElement(name = "Display")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected DisplayCapabilities display;
    @XmlElement(name = "Recording")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected RecordingCapabilities recording;
    @XmlElement(name = "Search")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected SearchCapabilities search;
    @XmlElement(name = "Replay")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ReplayCapabilities replay;
    @XmlElement(name = "Receiver")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ReceiverCapabilities receiver;
    @XmlElement(name = "AnalyticsDevice")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected AnalyticsDeviceCapabilities analyticsDevice;
    @XmlElement(name = "Extensions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected CapabilitiesExtension2 extensions;

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the deviceIO property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceIOCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public DeviceIOCapabilities getDeviceIO() {
        return deviceIO;
    }

    /**
     * Sets the value of the deviceIO property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceIOCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setDeviceIO(DeviceIOCapabilities value) {
        this.deviceIO = value;
    }

    /**
     * Gets the value of the display property.
     * 
     * @return
     *     possible object is
     *     {@link DisplayCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public DisplayCapabilities getDisplay() {
        return display;
    }

    /**
     * Sets the value of the display property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplayCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setDisplay(DisplayCapabilities value) {
        this.display = value;
    }

    /**
     * Gets the value of the recording property.
     * 
     * @return
     *     possible object is
     *     {@link RecordingCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public RecordingCapabilities getRecording() {
        return recording;
    }

    /**
     * Sets the value of the recording property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordingCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setRecording(RecordingCapabilities value) {
        this.recording = value;
    }

    /**
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link SearchCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public SearchCapabilities getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setSearch(SearchCapabilities value) {
        this.search = value;
    }

    /**
     * Gets the value of the replay property.
     * 
     * @return
     *     possible object is
     *     {@link ReplayCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ReplayCapabilities getReplay() {
        return replay;
    }

    /**
     * Sets the value of the replay property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplayCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setReplay(ReplayCapabilities value) {
        this.replay = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link ReceiverCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ReceiverCapabilities getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReceiverCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setReceiver(ReceiverCapabilities value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the analyticsDevice property.
     * 
     * @return
     *     possible object is
     *     {@link AnalyticsDeviceCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public AnalyticsDeviceCapabilities getAnalyticsDevice() {
        return analyticsDevice;
    }

    /**
     * Sets the value of the analyticsDevice property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnalyticsDeviceCapabilities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setAnalyticsDevice(AnalyticsDeviceCapabilities value) {
        this.analyticsDevice = value;
    }

    /**
     * Gets the value of the extensions property.
     * 
     * @return
     *     possible object is
     *     {@link CapabilitiesExtension2 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public CapabilitiesExtension2 getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapabilitiesExtension2 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtensions(CapabilitiesExtension2 value) {
        this.extensions = value;
    }

}
