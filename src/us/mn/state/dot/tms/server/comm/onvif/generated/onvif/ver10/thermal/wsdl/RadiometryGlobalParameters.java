
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
import org.w3c.dom.Element;


/**
 * 
 * 						Holds default values that will be used in measurement modules when local parameters are not specified for the module (these are still required for valid temperature calculations). 
 * 						Having ReflectedAmbientTemperature, Emissivity and DistanceToObject as mandatory ensures minimum parameters are available to obtain valid temperature values.
 * 					
 * 
 * <p>Java class for RadiometryGlobalParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RadiometryGlobalParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReflectedAmbientTemperature" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;element name="Emissivity" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;element name="DistanceToObject" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;element name="RelativeHumidity" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="AtmosphericTemperature" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="AtmosphericTransmittance" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="ExtOpticsTemperature" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="ExtOpticsTransmittance" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
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
@XmlType(name = "RadiometryGlobalParameters", propOrder = {
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
public class RadiometryGlobalParameters {

    @XmlElement(name = "ReflectedAmbientTemperature")
    protected float reflectedAmbientTemperature;
    @XmlElement(name = "Emissivity")
    protected float emissivity;
    @XmlElement(name = "DistanceToObject")
    protected float distanceToObject;
    @XmlElement(name = "RelativeHumidity")
    protected Float relativeHumidity;
    @XmlElement(name = "AtmosphericTemperature")
    protected Float atmosphericTemperature;
    @XmlElement(name = "AtmosphericTransmittance")
    protected Float atmosphericTransmittance;
    @XmlElement(name = "ExtOpticsTemperature")
    protected Float extOpticsTemperature;
    @XmlElement(name = "ExtOpticsTransmittance")
    protected Float extOpticsTransmittance;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the reflectedAmbientTemperature property.
     * 
     */
    public float getReflectedAmbientTemperature() {
        return reflectedAmbientTemperature;
    }

    /**
     * Sets the value of the reflectedAmbientTemperature property.
     * 
     */
    public void setReflectedAmbientTemperature(float value) {
        this.reflectedAmbientTemperature = value;
    }

    /**
     * Gets the value of the emissivity property.
     * 
     */
    public float getEmissivity() {
        return emissivity;
    }

    /**
     * Sets the value of the emissivity property.
     * 
     */
    public void setEmissivity(float value) {
        this.emissivity = value;
    }

    /**
     * Gets the value of the distanceToObject property.
     * 
     */
    public float getDistanceToObject() {
        return distanceToObject;
    }

    /**
     * Sets the value of the distanceToObject property.
     * 
     */
    public void setDistanceToObject(float value) {
        this.distanceToObject = value;
    }

    /**
     * Gets the value of the relativeHumidity property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getRelativeHumidity() {
        return relativeHumidity;
    }

    /**
     * Sets the value of the relativeHumidity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setRelativeHumidity(Float value) {
        this.relativeHumidity = value;
    }

    /**
     * Gets the value of the atmosphericTemperature property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getAtmosphericTemperature() {
        return atmosphericTemperature;
    }

    /**
     * Sets the value of the atmosphericTemperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setAtmosphericTemperature(Float value) {
        this.atmosphericTemperature = value;
    }

    /**
     * Gets the value of the atmosphericTransmittance property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getAtmosphericTransmittance() {
        return atmosphericTransmittance;
    }

    /**
     * Sets the value of the atmosphericTransmittance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setAtmosphericTransmittance(Float value) {
        this.atmosphericTransmittance = value;
    }

    /**
     * Gets the value of the extOpticsTemperature property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getExtOpticsTemperature() {
        return extOpticsTemperature;
    }

    /**
     * Sets the value of the extOpticsTemperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setExtOpticsTemperature(Float value) {
        this.extOpticsTemperature = value;
    }

    /**
     * Gets the value of the extOpticsTransmittance property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getExtOpticsTransmittance() {
        return extOpticsTransmittance;
    }

    /**
     * Sets the value of the extOpticsTransmittance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setExtOpticsTransmittance(Float value) {
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
