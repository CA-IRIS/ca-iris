
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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for Configuration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Configuration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ColorPalette" type="{http://www.onvif.org/ver10/thermal/wsdl}ColorPalette"/&gt;
 *         &lt;element name="Polarity" type="{http://www.onvif.org/ver10/thermal/wsdl}Polarity"/&gt;
 *         &lt;element name="NUCTable" type="{http://www.onvif.org/ver10/thermal/wsdl}NUCTable" minOccurs="0"/&gt;
 *         &lt;element name="Cooler" type="{http://www.onvif.org/ver10/thermal/wsdl}Cooler" minOccurs="0"/&gt;
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
@XmlType(name = "Configuration", propOrder = {
    "colorPalette",
    "polarity",
    "nucTable",
    "cooler",
    "any"
})
public class Configuration {

    @XmlElement(name = "ColorPalette", required = true)
    protected ColorPalette colorPalette;
    @XmlElement(name = "Polarity", required = true)
    @XmlSchemaType(name = "string")
    protected Polarity polarity;
    @XmlElement(name = "NUCTable")
    protected NUCTable nucTable;
    @XmlElement(name = "Cooler")
    protected Cooler cooler;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the colorPalette property.
     * 
     * @return
     *     possible object is
     *     {@link ColorPalette }
     *     
     */
    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    /**
     * Sets the value of the colorPalette property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorPalette }
     *     
     */
    public void setColorPalette(ColorPalette value) {
        this.colorPalette = value;
    }

    /**
     * Gets the value of the polarity property.
     * 
     * @return
     *     possible object is
     *     {@link Polarity }
     *     
     */
    public Polarity getPolarity() {
        return polarity;
    }

    /**
     * Sets the value of the polarity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Polarity }
     *     
     */
    public void setPolarity(Polarity value) {
        this.polarity = value;
    }

    /**
     * Gets the value of the nucTable property.
     * 
     * @return
     *     possible object is
     *     {@link NUCTable }
     *     
     */
    public NUCTable getNUCTable() {
        return nucTable;
    }

    /**
     * Sets the value of the nucTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link NUCTable }
     *     
     */
    public void setNUCTable(NUCTable value) {
        this.nucTable = value;
    }

    /**
     * Gets the value of the cooler property.
     * 
     * @return
     *     possible object is
     *     {@link Cooler }
     *     
     */
    public Cooler getCooler() {
        return cooler;
    }

    /**
     * Sets the value of the cooler property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cooler }
     *     
     */
    public void setCooler(Cooler value) {
        this.cooler = value;
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
