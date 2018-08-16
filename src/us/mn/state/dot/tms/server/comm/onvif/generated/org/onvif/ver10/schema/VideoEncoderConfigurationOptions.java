
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <prop>Java class for VideoEncoderConfigurationOptions complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VideoEncoderConfigurationOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="QualityRange" type="{http://www.onvif.org/ver10/schema}IntRange"/&gt;
 *         &lt;element name="JPEG" type="{http://www.onvif.org/ver10/schema}JpegOptions" minOccurs="0"/&gt;
 *         &lt;element name="MPEG4" type="{http://www.onvif.org/ver10/schema}Mpeg4Options" minOccurs="0"/&gt;
 *         &lt;element name="H264" type="{http://www.onvif.org/ver10/schema}H264Options" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}VideoEncoderOptionsExtension" minOccurs="0"/&gt;
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
@XmlType(name = "VideoEncoderConfigurationOptions", propOrder = {
    "qualityRange",
    "jpeg",
    "mpeg4",
    "h264",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class VideoEncoderConfigurationOptions {

    @XmlElement(name = "QualityRange", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected IntRange qualityRange;
    @XmlElement(name = "JPEG")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected JpegOptions jpeg;
    @XmlElement(name = "MPEG4")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Mpeg4Options mpeg4;
    @XmlElement(name = "H264")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected H264Options h264;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected VideoEncoderOptionsExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the qualityRange property.
     * 
     * @return
     *     possible object is
     *     {@link IntRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public IntRange getQualityRange() {
        return qualityRange;
    }

    /**
     * Sets the value of the qualityRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setQualityRange(IntRange value) {
        this.qualityRange = value;
    }

    /**
     * Gets the value of the jpeg property.
     * 
     * @return
     *     possible object is
     *     {@link JpegOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public JpegOptions getJPEG() {
        return jpeg;
    }

    /**
     * Sets the value of the jpeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link JpegOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setJPEG(JpegOptions value) {
        this.jpeg = value;
    }

    /**
     * Gets the value of the mpeg4 property.
     * 
     * @return
     *     possible object is
     *     {@link Mpeg4Options }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Mpeg4Options getMPEG4() {
        return mpeg4;
    }

    /**
     * Sets the value of the mpeg4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mpeg4Options }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMPEG4(Mpeg4Options value) {
        this.mpeg4 = value;
    }

    /**
     * Gets the value of the h264 property.
     * 
     * @return
     *     possible object is
     *     {@link H264Options }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public H264Options getH264() {
        return h264;
    }

    /**
     * Sets the value of the h264 property.
     * 
     * @param value
     *     allowed object is
     *     {@link H264Options }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setH264(H264Options value) {
        this.h264 = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link VideoEncoderOptionsExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public VideoEncoderOptionsExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link VideoEncoderOptionsExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(VideoEncoderOptionsExtension value) {
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
