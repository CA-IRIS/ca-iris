
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl;

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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <prop>Java class for Capabilities complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Capabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ImageStabilization" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="Presets" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Capabilities", propOrder = {
    "any"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
public class Capabilities {

    @XmlAnyElement(lax = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    protected List<Object> any;
    @XmlAttribute(name = "ImageStabilization")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    protected Boolean imageStabilization;
    @XmlAttribute(name = "Presets")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    protected Boolean presets;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the imageStabilization property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public Boolean isImageStabilization() {
        return imageStabilization;
    }

    /**
     * Sets the value of the imageStabilization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public void setImageStabilization(Boolean value) {
        this.imageStabilization = value;
    }

    /**
     * Gets the value of the presets property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public Boolean isPresets() {
        return presets;
    }

    /**
     * Sets the value of the presets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public void setPresets(Boolean value) {
        this.presets = value;
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
