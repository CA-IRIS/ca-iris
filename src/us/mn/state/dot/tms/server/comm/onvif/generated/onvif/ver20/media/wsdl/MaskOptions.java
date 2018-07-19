
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver20.media.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.ColorOptions;
import org.w3c.dom.Element;


/**
 * <p>Java class for MaskOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MaskOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MaxMasks" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MaxPoints" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Types" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Color" type="{http://www.onvif.org/ver10/schema}ColorOptions"/&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RectangleOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="SingleColorOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaskOptions", propOrder = {
    "maxMasks",
    "maxPoints",
    "types",
    "color",
    "any"
})
public class MaskOptions {

    @XmlElement(name = "MaxMasks")
    protected int maxMasks;
    @XmlElement(name = "MaxPoints")
    protected int maxPoints;
    @XmlElement(name = "Types", required = true)
    protected List<String> types;
    @XmlElement(name = "Color", required = true)
    protected ColorOptions color;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "RectangleOnly")
    protected Boolean rectangleOnly;
    @XmlAttribute(name = "SingleColorOnly")
    protected Boolean singleColorOnly;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the maxMasks property.
     * 
     */
    public int getMaxMasks() {
        return maxMasks;
    }

    /**
     * Sets the value of the maxMasks property.
     * 
     */
    public void setMaxMasks(int value) {
        this.maxMasks = value;
    }

    /**
     * Gets the value of the maxPoints property.
     * 
     */
    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * Sets the value of the maxPoints property.
     * 
     */
    public void setMaxPoints(int value) {
        this.maxPoints = value;
    }

    /**
     * Gets the value of the types property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the types property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTypes() {
        if (types == null) {
            types = new ArrayList<String>();
        }
        return this.types;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link ColorOptions }
     *     
     */
    public ColorOptions getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorOptions }
     *     
     */
    public void setColor(ColorOptions value) {
        this.color = value;
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
     * {@link Element }
     * {@link Object }
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
     * Gets the value of the rectangleOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRectangleOnly() {
        return rectangleOnly;
    }

    /**
     * Sets the value of the rectangleOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRectangleOnly(Boolean value) {
        this.rectangleOnly = value;
    }

    /**
     * Gets the value of the singleColorOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSingleColorOnly() {
        return singleColorOnly;
    }

    /**
     * Sets the value of the singleColorOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSingleColorOnly(Boolean value) {
        this.singleColorOnly = value;
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
