
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import java.math.BigInteger;
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
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for TriggeredRecordingConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TriggeredRecordingConfiguration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PreRecordDuration" type="{http://www.w3.org/2001/XMLSchema}duration"/&gt;
 *         &lt;element name="PostRecordDuration" type="{http://www.w3.org/2001/XMLSchema}duration"/&gt;
 *         &lt;element name="RecordDuration" type="{http://www.w3.org/2001/XMLSchema}duration"/&gt;
 *         &lt;element name="RecordFrameRate" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="DoRecordAudio" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
@XmlType(name = "TriggeredRecordingConfiguration", propOrder = {
    "preRecordDuration",
    "postRecordDuration",
    "recordDuration",
    "recordFrameRate",
    "doRecordAudio",
    "any"
})
public class TriggeredRecordingConfiguration {

    @XmlElement(name = "PreRecordDuration", required = true)
    protected Duration preRecordDuration;
    @XmlElement(name = "PostRecordDuration", required = true)
    protected Duration postRecordDuration;
    @XmlElement(name = "RecordDuration", required = true)
    protected Duration recordDuration;
    @XmlElement(name = "RecordFrameRate")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger recordFrameRate;
    @XmlElement(name = "DoRecordAudio")
    protected boolean doRecordAudio;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the preRecordDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getPreRecordDuration() {
        return preRecordDuration;
    }

    /**
     * Sets the value of the preRecordDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setPreRecordDuration(Duration value) {
        this.preRecordDuration = value;
    }

    /**
     * Gets the value of the postRecordDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getPostRecordDuration() {
        return postRecordDuration;
    }

    /**
     * Sets the value of the postRecordDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setPostRecordDuration(Duration value) {
        this.postRecordDuration = value;
    }

    /**
     * Gets the value of the recordDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getRecordDuration() {
        return recordDuration;
    }

    /**
     * Sets the value of the recordDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setRecordDuration(Duration value) {
        this.recordDuration = value;
    }

    /**
     * Gets the value of the recordFrameRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRecordFrameRate() {
        return recordFrameRate;
    }

    /**
     * Sets the value of the recordFrameRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRecordFrameRate(BigInteger value) {
        this.recordFrameRate = value;
    }

    /**
     * Gets the value of the doRecordAudio property.
     * 
     */
    public boolean isDoRecordAudio() {
        return doRecordAudio;
    }

    /**
     * Sets the value of the doRecordAudio property.
     * 
     */
    public void setDoRecordAudio(boolean value) {
        this.doRecordAudio = value;
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
