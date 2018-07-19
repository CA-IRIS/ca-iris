
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
 * <p>Java class for ConfigurationOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConfigurationOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ColorPalette" type="{http://www.onvif.org/ver10/thermal/wsdl}ColorPalette" maxOccurs="unbounded"/&gt;
 *         &lt;element name="NUCTable" type="{http://www.onvif.org/ver10/thermal/wsdl}NUCTable" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CoolerOptions" type="{http://www.onvif.org/ver10/thermal/wsdl}CoolerOptions" minOccurs="0"/&gt;
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
@XmlType(name = "ConfigurationOptions", propOrder = {
    "colorPalette",
    "nucTable",
    "coolerOptions",
    "any"
})
public class ConfigurationOptions {

    @XmlElement(name = "ColorPalette", required = true)
    protected List<ColorPalette> colorPalette;
    @XmlElement(name = "NUCTable")
    protected List<NUCTable> nucTable;
    @XmlElement(name = "CoolerOptions")
    protected CoolerOptions coolerOptions;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the colorPalette property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorPalette property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorPalette().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorPalette }
     * 
     * 
     */
    public List<ColorPalette> getColorPalette() {
        if (colorPalette == null) {
            colorPalette = new ArrayList<ColorPalette>();
        }
        return this.colorPalette;
    }

    /**
     * Gets the value of the nucTable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nucTable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNUCTable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NUCTable }
     * 
     * 
     */
    public List<NUCTable> getNUCTable() {
        if (nucTable == null) {
            nucTable = new ArrayList<NUCTable>();
        }
        return this.nucTable;
    }

    /**
     * Gets the value of the coolerOptions property.
     * 
     * @return
     *     possible object is
     *     {@link CoolerOptions }
     *     
     */
    public CoolerOptions getCoolerOptions() {
        return coolerOptions;
    }

    /**
     * Sets the value of the coolerOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoolerOptions }
     *     
     */
    public void setCoolerOptions(CoolerOptions value) {
        this.coolerOptions = value;
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
