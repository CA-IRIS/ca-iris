
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.authenticationbehavior.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 						The SecurityLevel structure shall include all properties of the SecurityLevelInfo structure and
 * 						also a set
 * 						of recognition groups.
 * 
 * 						The recognition groups are used to define a logical OR between the groups. Each recognition
 * 						group consists
 * 						of one or more recognition methods.
 * 					
 * 
 * <p>Java class for SecurityLevel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecurityLevel"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}SecurityLevelInfo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RecognitionGroup" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}RecognitionGroup" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/authenticationbehavior/wsdl}SecurityLevelExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecurityLevel", propOrder = {
    "recognitionGroup",
    "extension"
})
public class SecurityLevel
    extends SecurityLevelInfo
{

    @XmlElement(name = "RecognitionGroup")
    protected List<RecognitionGroup> recognitionGroup;
    @XmlElement(name = "Extension")
    protected SecurityLevelExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the recognitionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recognitionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecognitionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecognitionGroup }
     * 
     * 
     */
    public List<RecognitionGroup> getRecognitionGroup() {
        if (recognitionGroup == null) {
            recognitionGroup = new ArrayList<RecognitionGroup>();
        }
        return this.recognitionGroup;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityLevelExtension }
     *     
     */
    public SecurityLevelExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityLevelExtension }
     *     
     */
    public void setExtension(SecurityLevelExtension value) {
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
