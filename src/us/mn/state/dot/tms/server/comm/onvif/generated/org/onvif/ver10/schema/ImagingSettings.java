
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <prop>Java class for ImagingSettings complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImagingSettings"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BacklightCompensation" type="{http://www.onvif.org/ver10/schema}BacklightCompensation" minOccurs="0"/&gt;
 *         &lt;element name="Brightness" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="ColorSaturation" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="Contrast" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="Exposure" type="{http://www.onvif.org/ver10/schema}Exposure" minOccurs="0"/&gt;
 *         &lt;element name="Focus" type="{http://www.onvif.org/ver10/schema}FocusConfiguration" minOccurs="0"/&gt;
 *         &lt;element name="IrCutFilter" type="{http://www.onvif.org/ver10/schema}IrCutFilterMode" minOccurs="0"/&gt;
 *         &lt;element name="Sharpness" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="WideDynamicRange" type="{http://www.onvif.org/ver10/schema}WideDynamicRange" minOccurs="0"/&gt;
 *         &lt;element name="WhiteBalance" type="{http://www.onvif.org/ver10/schema}WhiteBalance" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}ImagingSettingsExtension" minOccurs="0"/&gt;
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
@XmlType(name = "ImagingSettings", propOrder = {
    "backlightCompensation",
    "brightness",
    "colorSaturation",
    "contrast",
    "exposure",
    "focus",
    "irCutFilter",
    "sharpness",
    "wideDynamicRange",
    "whiteBalance",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class ImagingSettings {

    @XmlElement(name = "BacklightCompensation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected BacklightCompensation backlightCompensation;
    @XmlElement(name = "Brightness")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Float brightness;
    @XmlElement(name = "ColorSaturation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Float colorSaturation;
    @XmlElement(name = "Contrast")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Float contrast;
    @XmlElement(name = "Exposure")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Exposure exposure;
    @XmlElement(name = "Focus")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FocusConfiguration focus;
    @XmlElement(name = "IrCutFilter")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected IrCutFilterMode irCutFilter;
    @XmlElement(name = "Sharpness")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Float sharpness;
    @XmlElement(name = "WideDynamicRange")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected WideDynamicRange wideDynamicRange;
    @XmlElement(name = "WhiteBalance")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected WhiteBalance whiteBalance;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ImagingSettingsExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the backlightCompensation property.
     * 
     * @return
     *     possible object is
     *     {@link BacklightCompensation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public BacklightCompensation getBacklightCompensation() {
        return backlightCompensation;
    }

    /**
     * Sets the value of the backlightCompensation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BacklightCompensation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setBacklightCompensation(BacklightCompensation value) {
        this.backlightCompensation = value;
    }

    /**
     * Gets the value of the brightness property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Float getBrightness() {
        return brightness;
    }

    /**
     * Sets the value of the brightness property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setBrightness(Float value) {
        this.brightness = value;
    }

    /**
     * Gets the value of the colorSaturation property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Float getColorSaturation() {
        return colorSaturation;
    }

    /**
     * Sets the value of the colorSaturation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setColorSaturation(Float value) {
        this.colorSaturation = value;
    }

    /**
     * Gets the value of the contrast property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Float getContrast() {
        return contrast;
    }

    /**
     * Sets the value of the contrast property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setContrast(Float value) {
        this.contrast = value;
    }

    /**
     * Gets the value of the exposure property.
     * 
     * @return
     *     possible object is
     *     {@link Exposure }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Exposure getExposure() {
        return exposure;
    }

    /**
     * Sets the value of the exposure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Exposure }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExposure(Exposure value) {
        this.exposure = value;
    }

    /**
     * Gets the value of the focus property.
     * 
     * @return
     *     possible object is
     *     {@link FocusConfiguration }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FocusConfiguration getFocus() {
        return focus;
    }

    /**
     * Sets the value of the focus property.
     * 
     * @param value
     *     allowed object is
     *     {@link FocusConfiguration }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setFocus(FocusConfiguration value) {
        this.focus = value;
    }

    /**
     * Gets the value of the irCutFilter property.
     * 
     * @return
     *     possible object is
     *     {@link IrCutFilterMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public IrCutFilterMode getIrCutFilter() {
        return irCutFilter;
    }

    /**
     * Sets the value of the irCutFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link IrCutFilterMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setIrCutFilter(IrCutFilterMode value) {
        this.irCutFilter = value;
    }

    /**
     * Gets the value of the sharpness property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Float getSharpness() {
        return sharpness;
    }

    /**
     * Sets the value of the sharpness property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setSharpness(Float value) {
        this.sharpness = value;
    }

    /**
     * Gets the value of the wideDynamicRange property.
     * 
     * @return
     *     possible object is
     *     {@link WideDynamicRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public WideDynamicRange getWideDynamicRange() {
        return wideDynamicRange;
    }

    /**
     * Sets the value of the wideDynamicRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link WideDynamicRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setWideDynamicRange(WideDynamicRange value) {
        this.wideDynamicRange = value;
    }

    /**
     * Gets the value of the whiteBalance property.
     * 
     * @return
     *     possible object is
     *     {@link WhiteBalance }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public WhiteBalance getWhiteBalance() {
        return whiteBalance;
    }

    /**
     * Sets the value of the whiteBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link WhiteBalance }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setWhiteBalance(WhiteBalance value) {
        this.whiteBalance = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ImagingSettingsExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ImagingSettingsExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImagingSettingsExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(ImagingSettingsExtension value) {
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
