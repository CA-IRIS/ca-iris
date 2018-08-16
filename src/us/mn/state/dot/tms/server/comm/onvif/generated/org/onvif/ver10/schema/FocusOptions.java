
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
 * <prop>Java class for FocusOptions complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FocusOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AutoFocusModes" type="{http://www.onvif.org/ver10/schema}AutoFocusMode" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DefaultSpeed" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *         &lt;element name="NearLimit" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *         &lt;element name="FarLimit" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FocusOptions", propOrder = {
    "autoFocusModes",
    "defaultSpeed",
    "nearLimit",
    "farLimit"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class FocusOptions {

    @XmlElement(name = "AutoFocusModes")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<AutoFocusMode> autoFocusModes;
    @XmlElement(name = "DefaultSpeed", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange defaultSpeed;
    @XmlElement(name = "NearLimit", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange nearLimit;
    @XmlElement(name = "FarLimit", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected FloatRange farLimit;

    /**
     * Gets the value of the autoFocusModes property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the autoFocusModes property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAutoFocusModes().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link AutoFocusMode }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<AutoFocusMode> getAutoFocusModes() {
        if (autoFocusModes == null) {
            autoFocusModes = new ArrayList<AutoFocusMode>();
        }
        return this.autoFocusModes;
    }

    /**
     * Gets the value of the defaultSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getDefaultSpeed() {
        return defaultSpeed;
    }

    /**
     * Sets the value of the defaultSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setDefaultSpeed(FloatRange value) {
        this.defaultSpeed = value;
    }

    /**
     * Gets the value of the nearLimit property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getNearLimit() {
        return nearLimit;
    }

    /**
     * Sets the value of the nearLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setNearLimit(FloatRange value) {
        this.nearLimit = value;
    }

    /**
     * Gets the value of the farLimit property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public FloatRange getFarLimit() {
        return farLimit;
    }

    /**
     * Sets the value of the farLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setFarLimit(FloatRange value) {
        this.farLimit = value;
    }

}
