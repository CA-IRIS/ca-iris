
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
 * <prop>Java class for PTZPresetTourPresetDetailOptions complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PTZPresetTourPresetDetailOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PresetToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Home" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="PanTiltPositionSpace" type="{http://www.onvif.org/ver10/schema}Space2DDescription" minOccurs="0"/&gt;
 *         &lt;element name="ZoomPositionSpace" type="{http://www.onvif.org/ver10/schema}Space1DDescription" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}PTZPresetTourPresetDetailOptionsExtension" minOccurs="0"/&gt;
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
@XmlType(name = "PTZPresetTourPresetDetailOptions", propOrder = {
    "presetToken",
    "home",
    "panTiltPositionSpace",
    "zoomPositionSpace",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class PTZPresetTourPresetDetailOptions {

    @XmlElement(name = "PresetToken")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<String> presetToken;
    @XmlElement(name = "Home")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Boolean home;
    @XmlElement(name = "PanTiltPositionSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Space2DDescription panTiltPositionSpace;
    @XmlElement(name = "ZoomPositionSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Space1DDescription zoomPositionSpace;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected PTZPresetTourPresetDetailOptionsExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the presetToken property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the presetToken property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPresetToken().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<String> getPresetToken() {
        if (presetToken == null) {
            presetToken = new ArrayList<String>();
        }
        return this.presetToken;
    }

    /**
     * Gets the value of the home property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Boolean isHome() {
        return home;
    }

    /**
     * Sets the value of the home property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setHome(Boolean value) {
        this.home = value;
    }

    /**
     * Gets the value of the panTiltPositionSpace property.
     * 
     * @return
     *     possible object is
     *     {@link Space2DDescription }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Space2DDescription getPanTiltPositionSpace() {
        return panTiltPositionSpace;
    }

    /**
     * Sets the value of the panTiltPositionSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link Space2DDescription }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setPanTiltPositionSpace(Space2DDescription value) {
        this.panTiltPositionSpace = value;
    }

    /**
     * Gets the value of the zoomPositionSpace property.
     * 
     * @return
     *     possible object is
     *     {@link Space1DDescription }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Space1DDescription getZoomPositionSpace() {
        return zoomPositionSpace;
    }

    /**
     * Sets the value of the zoomPositionSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link Space1DDescription }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setZoomPositionSpace(Space1DDescription value) {
        this.zoomPositionSpace = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link PTZPresetTourPresetDetailOptionsExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public PTZPresetTourPresetDetailOptionsExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link PTZPresetTourPresetDetailOptionsExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(PTZPresetTourPresetDetailOptionsExtension value) {
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
