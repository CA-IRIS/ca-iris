
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for PTZConfigurationExtension complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PTZConfigurationExtension"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PTControlDirection" type="{http://www.onvif.org/ver10/schema}PTControlDirection" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}PTZConfigurationExtension2" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PTZConfigurationExtension", propOrder = {
    "any",
    "ptControlDirection",
    "extension"
})
public class PTZConfigurationExtension {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlElement(name = "PTControlDirection")
    protected PTControlDirection ptControlDirection;
    @XmlElement(name = "Extension")
    protected PTZConfigurationExtension2 extension;

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
     * Gets the value of the ptControlDirection property.
     * 
     * @return
     *     possible object is
     *     {@link PTControlDirection }
     *     
     */
    public PTControlDirection getPTControlDirection() {
        return ptControlDirection;
    }

    /**
     * Sets the value of the ptControlDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link PTControlDirection }
     *     
     */
    public void setPTControlDirection(PTControlDirection value) {
        this.ptControlDirection = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link PTZConfigurationExtension2 }
     *     
     */
    public PTZConfigurationExtension2 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link PTZConfigurationExtension2 }
     *     
     */
    public void setExtension(PTZConfigurationExtension2 value) {
        this.extension = value;
    }

}
