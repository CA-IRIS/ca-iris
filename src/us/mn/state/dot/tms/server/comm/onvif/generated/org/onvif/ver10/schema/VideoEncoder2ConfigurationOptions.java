
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <prop>Java class for VideoEncoder2ConfigurationOptions complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VideoEncoder2ConfigurationOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Encoding" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="QualityRange" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *         &lt;element name="ResolutionsAvailable" type="{http://www.onvif.org/ver10/schema}VideoResolution2" maxOccurs="unbounded"/&gt;
 *         &lt;element name="BitrateRange" type="{http://www.onvif.org/ver10/schema}IntRange"/&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="GovLengthRange" type="{http://www.onvif.org/ver10/schema}IntAttrList" /&gt;
 *       &lt;attribute name="FrameRatesSupported" type="{http://www.onvif.org/ver10/schema}FloatAttrList" /&gt;
 *       &lt;attribute name="ProfilesSupported" type="{http://www.onvif.org/ver10/schema}StringAttrList" /&gt;
 *       &lt;attribute name="ConstantBitRateSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VideoEncoder2ConfigurationOptions", propOrder = {
    "encoding",
    "qualityRange",
    "resolutionsAvailable",
    "bitrateRange",
    "any"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class VideoEncoder2ConfigurationOptions {

    @XmlElement(name = "Encoding", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected String encoding;
    @XmlElement(name = "QualityRange", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange qualityRange;
    @XmlElement(name = "ResolutionsAvailable", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<VideoResolution2> resolutionsAvailable;
    @XmlElement(name = "BitrateRange", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected IntRange bitrateRange;
    @XmlAnyElement(lax = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Object> any;
    @XmlAttribute(name = "GovLengthRange")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Integer> govLengthRange;
    @XmlAttribute(name = "FrameRatesSupported")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Float> frameRatesSupported;
    @XmlAttribute(name = "ProfilesSupported")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<String> profilesSupported;
    @XmlAttribute(name = "ConstantBitRateSupported")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Boolean constantBitRateSupported;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the qualityRange property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getQualityRange() {
        return qualityRange;
    }

    /**
     * Sets the value of the qualityRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setQualityRange(FloatRange value) {
        this.qualityRange = value;
    }

    /**
     * Gets the value of the resolutionsAvailable property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resolutionsAvailable property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResolutionsAvailable().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link VideoResolution2 }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<VideoResolution2> getResolutionsAvailable() {
        if (resolutionsAvailable == null) {
            resolutionsAvailable = new ArrayList<VideoResolution2>();
        }
        return this.resolutionsAvailable;
    }

    /**
     * Gets the value of the bitrateRange property.
     * 
     * @return
     *     possible object is
     *     {@link IntRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public IntRange getBitrateRange() {
        return bitrateRange;
    }

    /**
     * Sets the value of the bitrateRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setBitrateRange(IntRange value) {
        this.bitrateRange = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
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
     * Gets the value of the govLengthRange property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the govLengthRange property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGovLengthRange().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Integer> getGovLengthRange() {
        if (govLengthRange == null) {
            govLengthRange = new ArrayList<Integer>();
        }
        return this.govLengthRange;
    }

    /**
     * Gets the value of the frameRatesSupported property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the frameRatesSupported property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFrameRatesSupported().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Float }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Float> getFrameRatesSupported() {
        if (frameRatesSupported == null) {
            frameRatesSupported = new ArrayList<Float>();
        }
        return this.frameRatesSupported;
    }

    /**
     * Gets the value of the profilesSupported property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profilesSupported property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfilesSupported().add(newItem);
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
    public List<String> getProfilesSupported() {
        if (profilesSupported == null) {
            profilesSupported = new ArrayList<String>();
        }
        return this.profilesSupported;
    }

    /**
     * Gets the value of the constantBitRateSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Boolean isConstantBitRateSupported() {
        return constantBitRateSupported;
    }

    /**
     * Sets the value of the constantBitRateSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setConstantBitRateSupported(Boolean value) {
        this.constantBitRateSupported = value;
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
