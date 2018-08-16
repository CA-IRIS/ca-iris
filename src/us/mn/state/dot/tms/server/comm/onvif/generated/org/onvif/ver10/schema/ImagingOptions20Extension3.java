
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for ImagingOptions20Extension3 complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImagingOptions20Extension3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ToneCompensationOptions" type="{http://www.onvif.org/ver10/schema}ToneCompensationOptions" minOccurs="0"/&gt;
 *         &lt;element name="DefoggingOptions" type="{http://www.onvif.org/ver10/schema}DefoggingOptions" minOccurs="0"/&gt;
 *         &lt;element name="NoiseReductionOptions" type="{http://www.onvif.org/ver10/schema}NoiseReductionOptions" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}ImagingOptions20Extension4" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImagingOptions20Extension3", propOrder = {
    "toneCompensationOptions",
    "defoggingOptions",
    "noiseReductionOptions",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class ImagingOptions20Extension3 {

    @XmlElement(name = "ToneCompensationOptions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ToneCompensationOptions toneCompensationOptions;
    @XmlElement(name = "DefoggingOptions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected DefoggingOptions defoggingOptions;
    @XmlElement(name = "NoiseReductionOptions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected NoiseReductionOptions noiseReductionOptions;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ImagingOptions20Extension4 extension;

    /**
     * Gets the value of the toneCompensationOptions property.
     * 
     * @return
     *     possible object is
     *     {@link ToneCompensationOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ToneCompensationOptions getToneCompensationOptions() {
        return toneCompensationOptions;
    }

    /**
     * Sets the value of the toneCompensationOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ToneCompensationOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setToneCompensationOptions(ToneCompensationOptions value) {
        this.toneCompensationOptions = value;
    }

    /**
     * Gets the value of the defoggingOptions property.
     * 
     * @return
     *     possible object is
     *     {@link DefoggingOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public DefoggingOptions getDefoggingOptions() {
        return defoggingOptions;
    }

    /**
     * Sets the value of the defoggingOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link DefoggingOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setDefoggingOptions(DefoggingOptions value) {
        this.defoggingOptions = value;
    }

    /**
     * Gets the value of the noiseReductionOptions property.
     * 
     * @return
     *     possible object is
     *     {@link NoiseReductionOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public NoiseReductionOptions getNoiseReductionOptions() {
        return noiseReductionOptions;
    }

    /**
     * Sets the value of the noiseReductionOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoiseReductionOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setNoiseReductionOptions(NoiseReductionOptions value) {
        this.noiseReductionOptions = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ImagingOptions20Extension4 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ImagingOptions20Extension4 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImagingOptions20Extension4 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(ImagingOptions20Extension4 value) {
        this.extension = value;
    }

}
