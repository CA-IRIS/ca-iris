
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
 * <prop>Java class for PTZSpaces complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PTZSpaces"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AbsolutePanTiltPositionSpace" type="{http://www.onvif.org/ver10/schema}Space2DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="AbsoluteZoomPositionSpace" type="{http://www.onvif.org/ver10/schema}Space1DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RelativePanTiltTranslationSpace" type="{http://www.onvif.org/ver10/schema}Space2DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RelativeZoomTranslationSpace" type="{http://www.onvif.org/ver10/schema}Space1DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ContinuousPanTiltVelocitySpace" type="{http://www.onvif.org/ver10/schema}Space2DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ContinuousZoomVelocitySpace" type="{http://www.onvif.org/ver10/schema}Space1DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PanTiltSpeedSpace" type="{http://www.onvif.org/ver10/schema}Space1DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ZoomSpeedSpace" type="{http://www.onvif.org/ver10/schema}Space1DDescription" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}PTZSpacesExtension" minOccurs="0"/&gt;
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
@XmlType(name = "PTZSpaces", propOrder = {
    "absolutePanTiltPositionSpace",
    "absoluteZoomPositionSpace",
    "relativePanTiltTranslationSpace",
    "relativeZoomTranslationSpace",
    "continuousPanTiltVelocitySpace",
    "continuousZoomVelocitySpace",
    "panTiltSpeedSpace",
    "zoomSpeedSpace",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class PTZSpaces {

    @XmlElement(name = "AbsolutePanTiltPositionSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space2DDescription> absolutePanTiltPositionSpace;
    @XmlElement(name = "AbsoluteZoomPositionSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space1DDescription> absoluteZoomPositionSpace;
    @XmlElement(name = "RelativePanTiltTranslationSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space2DDescription> relativePanTiltTranslationSpace;
    @XmlElement(name = "RelativeZoomTranslationSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space1DDescription> relativeZoomTranslationSpace;
    @XmlElement(name = "ContinuousPanTiltVelocitySpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space2DDescription> continuousPanTiltVelocitySpace;
    @XmlElement(name = "ContinuousZoomVelocitySpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space1DDescription> continuousZoomVelocitySpace;
    @XmlElement(name = "PanTiltSpeedSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space1DDescription> panTiltSpeedSpace;
    @XmlElement(name = "ZoomSpeedSpace")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Space1DDescription> zoomSpeedSpace;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected PTZSpacesExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the absolutePanTiltPositionSpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the absolutePanTiltPositionSpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbsolutePanTiltPositionSpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space2DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space2DDescription> getAbsolutePanTiltPositionSpace() {
        if (absolutePanTiltPositionSpace == null) {
            absolutePanTiltPositionSpace = new ArrayList<Space2DDescription>();
        }
        return this.absolutePanTiltPositionSpace;
    }

    /**
     * Gets the value of the absoluteZoomPositionSpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the absoluteZoomPositionSpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbsoluteZoomPositionSpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space1DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space1DDescription> getAbsoluteZoomPositionSpace() {
        if (absoluteZoomPositionSpace == null) {
            absoluteZoomPositionSpace = new ArrayList<Space1DDescription>();
        }
        return this.absoluteZoomPositionSpace;
    }

    /**
     * Gets the value of the relativePanTiltTranslationSpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relativePanTiltTranslationSpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelativePanTiltTranslationSpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space2DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space2DDescription> getRelativePanTiltTranslationSpace() {
        if (relativePanTiltTranslationSpace == null) {
            relativePanTiltTranslationSpace = new ArrayList<Space2DDescription>();
        }
        return this.relativePanTiltTranslationSpace;
    }

    /**
     * Gets the value of the relativeZoomTranslationSpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relativeZoomTranslationSpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelativeZoomTranslationSpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space1DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space1DDescription> getRelativeZoomTranslationSpace() {
        if (relativeZoomTranslationSpace == null) {
            relativeZoomTranslationSpace = new ArrayList<Space1DDescription>();
        }
        return this.relativeZoomTranslationSpace;
    }

    /**
     * Gets the value of the continuousPanTiltVelocitySpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the continuousPanTiltVelocitySpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContinuousPanTiltVelocitySpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space2DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space2DDescription> getContinuousPanTiltVelocitySpace() {
        if (continuousPanTiltVelocitySpace == null) {
            continuousPanTiltVelocitySpace = new ArrayList<Space2DDescription>();
        }
        return this.continuousPanTiltVelocitySpace;
    }

    /**
     * Gets the value of the continuousZoomVelocitySpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the continuousZoomVelocitySpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContinuousZoomVelocitySpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space1DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space1DDescription> getContinuousZoomVelocitySpace() {
        if (continuousZoomVelocitySpace == null) {
            continuousZoomVelocitySpace = new ArrayList<Space1DDescription>();
        }
        return this.continuousZoomVelocitySpace;
    }

    /**
     * Gets the value of the panTiltSpeedSpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the panTiltSpeedSpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPanTiltSpeedSpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space1DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space1DDescription> getPanTiltSpeedSpace() {
        if (panTiltSpeedSpace == null) {
            panTiltSpeedSpace = new ArrayList<Space1DDescription>();
        }
        return this.panTiltSpeedSpace;
    }

    /**
     * Gets the value of the zoomSpeedSpace property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the zoomSpeedSpace property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getZoomSpeedSpace().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Space1DDescription }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Space1DDescription> getZoomSpeedSpace() {
        if (zoomSpeedSpace == null) {
            zoomSpeedSpace = new ArrayList<Space1DDescription>();
        }
        return this.zoomSpeedSpace;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link PTZSpacesExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public PTZSpacesExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link PTZSpacesExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(PTZSpacesExtension value) {
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
