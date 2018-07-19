
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.advancedsecurity.wsdl;

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
 * The capabilities of an Advanced Security Service implementation on a device.
 * 
 * <p>Java class for Capabilities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Capabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="KeystoreCapabilities" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}KeystoreCapabilities"/&gt;
 *         &lt;element name="TLSServerCapabilities" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}TLSServerCapabilities"/&gt;
 *         &lt;element name="Dot1XCapabilities" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}Dot1XCapabilities" minOccurs="0"/&gt;
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
@XmlType(name = "Capabilities", propOrder = {
    "keystoreCapabilities",
    "tlsServerCapabilities",
    "dot1XCapabilities",
    "any"
})
public class Capabilities {

    @XmlElement(name = "KeystoreCapabilities", required = true)
    protected KeystoreCapabilities keystoreCapabilities;
    @XmlElement(name = "TLSServerCapabilities", required = true)
    protected TLSServerCapabilities tlsServerCapabilities;
    @XmlElement(name = "Dot1XCapabilities")
    protected Dot1XCapabilities dot1XCapabilities;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the keystoreCapabilities property.
     * 
     * @return
     *     possible object is
     *     {@link KeystoreCapabilities }
     *     
     */
    public KeystoreCapabilities getKeystoreCapabilities() {
        return keystoreCapabilities;
    }

    /**
     * Sets the value of the keystoreCapabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeystoreCapabilities }
     *     
     */
    public void setKeystoreCapabilities(KeystoreCapabilities value) {
        this.keystoreCapabilities = value;
    }

    /**
     * Gets the value of the tlsServerCapabilities property.
     * 
     * @return
     *     possible object is
     *     {@link TLSServerCapabilities }
     *     
     */
    public TLSServerCapabilities getTLSServerCapabilities() {
        return tlsServerCapabilities;
    }

    /**
     * Sets the value of the tlsServerCapabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link TLSServerCapabilities }
     *     
     */
    public void setTLSServerCapabilities(TLSServerCapabilities value) {
        this.tlsServerCapabilities = value;
    }

    /**
     * Gets the value of the dot1XCapabilities property.
     * 
     * @return
     *     possible object is
     *     {@link Dot1XCapabilities }
     *     
     */
    public Dot1XCapabilities getDot1XCapabilities() {
        return dot1XCapabilities;
    }

    /**
     * Sets the value of the dot1XCapabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dot1XCapabilities }
     *     
     */
    public void setDot1XCapabilities(Dot1XCapabilities value) {
        this.dot1XCapabilities = value;
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
