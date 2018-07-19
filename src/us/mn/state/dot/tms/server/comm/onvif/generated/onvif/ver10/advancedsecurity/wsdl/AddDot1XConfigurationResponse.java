
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.advancedsecurity.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Dot1XID" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}Dot1XID"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dot1XID"
})
@XmlRootElement(name = "AddDot1XConfigurationResponse")
public class AddDot1XConfigurationResponse {

    @XmlElement(name = "Dot1XID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String dot1XID;

    /**
     * Gets the value of the dot1XID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDot1XID() {
        return dot1XID;
    }

    /**
     * Sets the value of the dot1XID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDot1XID(String value) {
        this.dot1XID = value;
    }

}
