
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.authenticationbehavior.wsdl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 						Recognition is the action of identifying authorized users requesting access by the comparison of
 * 						presented
 * 						credential data with recorded credential data. A recognition method is either memorized,
 * 						biometric or held
 * 						within a physical credential. A recognition type is either a recognition method or a physical
 * 						input such as
 * 						a request-to-exit button.
 * 					
 * 
 * <p>Java class for RecognitionMethod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecognitionMethod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RecognitionType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Order" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}RecognitionMethodExtension" minOccurs="0"/&gt;
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
@XmlType(name = "RecognitionMethod", propOrder = {
    "recognitionType",
    "order",
    "extension"
})
public class RecognitionMethod {

    @XmlElement(name = "RecognitionType", required = true)
    protected String recognitionType;
    @XmlElement(name = "Order")
    protected int order;
    @XmlElement(name = "Extension")
    protected RecognitionMethodExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the recognitionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecognitionType() {
        return recognitionType;
    }

    /**
     * Sets the value of the recognitionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecognitionType(String value) {
        this.recognitionType = value;
    }

    /**
     * Gets the value of the order property.
     * 
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     */
    public void setOrder(int value) {
        this.order = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link RecognitionMethodExtension }
     *     
     */
    public RecognitionMethodExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecognitionMethodExtension }
     *     
     */
    public void setExtension(RecognitionMethodExtension value) {
        this.extension = value;
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
