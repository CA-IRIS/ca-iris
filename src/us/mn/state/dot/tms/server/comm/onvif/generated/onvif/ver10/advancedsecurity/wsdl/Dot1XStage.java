
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.advancedsecurity.wsdl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * The configuration parameters required for a particular authentication method.
 * 
 * <p>Java class for Dot1XStage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Dot1XStage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CertificationPathID" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}CertificationPathID" minOccurs="0"/&gt;
 *         &lt;element name="PassphraseID" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}PassphraseID" minOccurs="0"/&gt;
 *         &lt;element name="Inner" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}Dot1XStage" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/advancedsecurity/wsdl}Dot1XStageExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Method" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Dot1XStage", propOrder = {
    "identity",
    "certificationPathID",
    "passphraseID",
    "inner",
    "extension"
})
public class Dot1XStage {

    @XmlElement(name = "Identity")
    protected String identity;
    @XmlElement(name = "CertificationPathID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String certificationPathID;
    @XmlElement(name = "PassphraseID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String passphraseID;
    @XmlElement(name = "Inner")
    protected Dot1XStage inner;
    @XmlElement(name = "Extension")
    protected Dot1XStageExtension extension;
    @XmlAttribute(name = "Method", required = true)
    protected String method;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the identity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets the value of the identity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentity(String value) {
        this.identity = value;
    }

    /**
     * Gets the value of the certificationPathID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificationPathID() {
        return certificationPathID;
    }

    /**
     * Sets the value of the certificationPathID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificationPathID(String value) {
        this.certificationPathID = value;
    }

    /**
     * Gets the value of the passphraseID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassphraseID() {
        return passphraseID;
    }

    /**
     * Sets the value of the passphraseID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassphraseID(String value) {
        this.passphraseID = value;
    }

    /**
     * Gets the value of the inner property.
     * 
     * @return
     *     possible object is
     *     {@link Dot1XStage }
     *     
     */
    public Dot1XStage getInner() {
        return inner;
    }

    /**
     * Sets the value of the inner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dot1XStage }
     *     
     */
    public void setInner(Dot1XStage value) {
        this.inner = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link Dot1XStageExtension }
     *     
     */
    public Dot1XStageExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dot1XStageExtension }
     *     
     */
    public void setExtension(Dot1XStageExtension value) {
        this.extension = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethod(String value) {
        this.method = value;
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
