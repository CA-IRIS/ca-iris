
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.FloatRange;
import org.w3c.dom.Element;


/**
 * 
 * 						Describes valid ranges for the different radiometry parameters required for accurate temperature calculation. 
 * 					
 * 
 * <p>Java class for RadiometryGlobalParameterOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RadiometryGlobalParameterOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReflectedAmbientTemperature" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *         &lt;element name="Emissivity" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *         &lt;element name="DistanceToObject" type="{http://www.onvif.org/ver10/schema}FloatRange"/&gt;
 *         &lt;element name="RelativeHumidity" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="AtmosphericTemperature" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="AtmosphericTransmittance" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="ExtOpticsTemperature" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;element name="ExtOpticsTransmittance" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "RadiometryGlobalParameterOptions", propOrder = {
    "reflectedAmbientTemperature",
    "emissivity",
    "distanceToObject",
    "relativeHumidity",
    "atmosphericTemperature",
    "atmosphericTransmittance",
    "extOpticsTemperature",
    "extOpticsTransmittance",
    "any"
})
public class RadiometryGlobalParameterOptions {

    @XmlElement(name = "ReflectedAmbientTemperature", required = true)
    protected FloatRange reflectedAmbientTemperature;
    @XmlElement(name = "Emissivity", required = true)
    protected FloatRange emissivity;
    @XmlElement(name = "DistanceToObject", required = true)
    protected FloatRange distanceToObject;
    @XmlElement(name = "RelativeHumidity")
    protected FloatRange relativeHumidity;
    @XmlElement(name = "AtmosphericTemperature")
    protected FloatRange atmosphericTemperature;
    @XmlElement(name = "AtmosphericTransmittance")
    protected FloatRange atmosphericTransmittance;
    @XmlElement(name = "ExtOpticsTemperature")
    protected FloatRange extOpticsTemperature;
    @XmlElement(name = "ExtOpticsTransmittance")
    protected FloatRange extOpticsTransmittance;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the reflectedAmbientTemperature property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getReflectedAmbientTemperature() {
        return reflectedAmbientTemperature;
    }

    /**
     * Sets the value of the reflectedAmbientTemperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setReflectedAmbientTemperature(FloatRange value) {
        this.reflectedAmbientTemperature = value;
    }

    /**
     * Gets the value of the emissivity property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getEmissivity() {
        return emissivity;
    }

    /**
     * Sets the value of the emissivity property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setEmissivity(FloatRange value) {
        this.emissivity = value;
    }

    /**
     * Gets the value of the distanceToObject property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getDistanceToObject() {
        return distanceToObject;
    }

    /**
     * Sets the value of the distanceToObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setDistanceToObject(FloatRange value) {
        this.distanceToObject = value;
    }

    /**
     * Gets the value of the relativeHumidity property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getRelativeHumidity() {
        return relativeHumidity;
    }

    /**
     * Sets the value of the relativeHumidity property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setRelativeHumidity(FloatRange value) {
        this.relativeHumidity = value;
    }

    /**
     * Gets the value of the atmosphericTemperature property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getAtmosphericTemperature() {
        return atmosphericTemperature;
    }

    /**
     * Sets the value of the atmosphericTemperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setAtmosphericTemperature(FloatRange value) {
        this.atmosphericTemperature = value;
    }

    /**
     * Gets the value of the atmosphericTransmittance property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getAtmosphericTransmittance() {
        return atmosphericTransmittance;
    }

    /**
     * Sets the value of the atmosphericTransmittance property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setAtmosphericTransmittance(FloatRange value) {
        this.atmosphericTransmittance = value;
    }

    /**
     * Gets the value of the extOpticsTemperature property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getExtOpticsTemperature() {
        return extOpticsTemperature;
    }

    /**
     * Sets the value of the extOpticsTemperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setExtOpticsTemperature(FloatRange value) {
        this.extOpticsTemperature = value;
    }

    /**
     * Gets the value of the extOpticsTransmittance property.
     * 
     * @return
     *     possible object is
     *     {@link FloatRange }
     *     
     */
    public FloatRange getExtOpticsTransmittance() {
        return extOpticsTransmittance;
    }

    /**
     * Sets the value of the extOpticsTransmittance property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatRange }
     *     
     */
    public void setExtOpticsTransmittance(FloatRange value) {
        this.extOpticsTransmittance = value;
    }

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
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
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
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
