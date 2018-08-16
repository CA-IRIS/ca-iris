
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for ExposureOptions20 complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExposureOptions20"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Mode" type="{http://www.onvif.org/ver10/schema}ExposureMode" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Priority" type="{http://www.onvif.org/ver10/schema}ExposurePriority" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="MinExposureTime" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="MaxExposureTime" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="MinGain" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="MaxGain" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="MinIris" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="MaxIris" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="ExposureTime" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="Gain" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="Iris" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExposureOptions20", propOrder = {
    "mode",
    "priority",
    "minExposureTime",
    "maxExposureTime",
    "minGain",
    "maxGain",
    "minIris",
    "maxIris",
    "exposureTime",
    "gain",
    "iris"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class ExposureOptions20 {

    @XmlElement(name = "Mode", required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<ExposureMode> mode;
    @XmlElement(name = "Priority")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<ExposurePriority> priority;
    @XmlElement(name = "MinExposureTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange minExposureTime;
    @XmlElement(name = "MaxExposureTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange maxExposureTime;
    @XmlElement(name = "MinGain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange minGain;
    @XmlElement(name = "MaxGain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange maxGain;
    @XmlElement(name = "MinIris")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange minIris;
    @XmlElement(name = "MaxIris")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange maxIris;
    @XmlElement(name = "ExposureTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange exposureTime;
    @XmlElement(name = "Gain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange gain;
    @XmlElement(name = "Iris")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange iris;

    /**
     * Gets the value of the mode property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mode property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMode().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link ExposureMode }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<ExposureMode> getMode() {
        if (mode == null) {
            mode = new ArrayList<ExposureMode>();
        }
        return this.mode;
    }

    /**
     * Gets the value of the priority property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the priority property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPriority().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link ExposurePriority }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<ExposurePriority> getPriority() {
        if (priority == null) {
            priority = new ArrayList<ExposurePriority>();
        }
        return this.priority;
    }

    /**
     * Gets the value of the minExposureTime property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getMinExposureTime() {
        return minExposureTime;
    }

    /**
     * Sets the value of the minExposureTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMinExposureTime(FloatRange value) {
        this.minExposureTime = value;
    }

    /**
     * Gets the value of the maxExposureTime property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getMaxExposureTime() {
        return maxExposureTime;
    }

    /**
     * Sets the value of the maxExposureTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMaxExposureTime(FloatRange value) {
        this.maxExposureTime = value;
    }

    /**
     * Gets the value of the minGain property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getMinGain() {
        return minGain;
    }

    /**
     * Sets the value of the minGain property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMinGain(FloatRange value) {
        this.minGain = value;
    }

    /**
     * Gets the value of the maxGain property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getMaxGain() {
        return maxGain;
    }

    /**
     * Sets the value of the maxGain property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMaxGain(FloatRange value) {
        this.maxGain = value;
    }

    /**
     * Gets the value of the minIris property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getMinIris() {
        return minIris;
    }

    /**
     * Sets the value of the minIris property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMinIris(FloatRange value) {
        this.minIris = value;
    }

    /**
     * Gets the value of the maxIris property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getMaxIris() {
        return maxIris;
    }

    /**
     * Sets the value of the maxIris property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setMaxIris(FloatRange value) {
        this.maxIris = value;
    }

    /**
     * Gets the value of the exposureTime property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getExposureTime() {
        return exposureTime;
    }

    /**
     * Sets the value of the exposureTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExposureTime(FloatRange value) {
        this.exposureTime = value;
    }

    /**
     * Gets the value of the gain property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getGain() {
        return gain;
    }

    /**
     * Sets the value of the gain property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setGain(FloatRange value) {
        this.gain = value;
    }

    /**
     * Gets the value of the iris property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getIris() {
        return iris;
    }

    /**
     * Sets the value of the iris property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setIris(FloatRange value) {
        this.iris = value;
    }

}
