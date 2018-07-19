
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * The provisioning capabilities of a video source on the device.
 * 
 * <p>Java class for SourceCapabilities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SourceCapabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="VideoSourceToken" use="required" type="{http://www.onvif.org/ver10/schema}ReferenceToken" /&gt;
 *       &lt;attribute name="MaximumPanMoves" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="MaximumTiltMoves" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="MaximumZoomMoves" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="MaximumRollMoves" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="AutoLevel" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="MaximumFocusMoves" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="AutoFocus" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceCapabilities", propOrder = {
    "any"
})
public class SourceCapabilities {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "VideoSourceToken", required = true)
    protected String videoSourceToken;
    @XmlAttribute(name = "MaximumPanMoves")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumPanMoves;
    @XmlAttribute(name = "MaximumTiltMoves")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumTiltMoves;
    @XmlAttribute(name = "MaximumZoomMoves")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumZoomMoves;
    @XmlAttribute(name = "MaximumRollMoves")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumRollMoves;
    @XmlAttribute(name = "AutoLevel")
    protected Boolean autoLevel;
    @XmlAttribute(name = "MaximumFocusMoves")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumFocusMoves;
    @XmlAttribute(name = "AutoFocus")
    protected Boolean autoFocus;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
     * Gets the value of the videoSourceToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVideoSourceToken() {
        return videoSourceToken;
    }

    /**
     * Sets the value of the videoSourceToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }

    /**
     * Gets the value of the maximumPanMoves property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumPanMoves() {
        return maximumPanMoves;
    }

    /**
     * Sets the value of the maximumPanMoves property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumPanMoves(BigInteger value) {
        this.maximumPanMoves = value;
    }

    /**
     * Gets the value of the maximumTiltMoves property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumTiltMoves() {
        return maximumTiltMoves;
    }

    /**
     * Sets the value of the maximumTiltMoves property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumTiltMoves(BigInteger value) {
        this.maximumTiltMoves = value;
    }

    /**
     * Gets the value of the maximumZoomMoves property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumZoomMoves() {
        return maximumZoomMoves;
    }

    /**
     * Sets the value of the maximumZoomMoves property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumZoomMoves(BigInteger value) {
        this.maximumZoomMoves = value;
    }

    /**
     * Gets the value of the maximumRollMoves property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumRollMoves() {
        return maximumRollMoves;
    }

    /**
     * Sets the value of the maximumRollMoves property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumRollMoves(BigInteger value) {
        this.maximumRollMoves = value;
    }

    /**
     * Gets the value of the autoLevel property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoLevel() {
        return autoLevel;
    }

    /**
     * Sets the value of the autoLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoLevel(Boolean value) {
        this.autoLevel = value;
    }

    /**
     * Gets the value of the maximumFocusMoves property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumFocusMoves() {
        return maximumFocusMoves;
    }

    /**
     * Sets the value of the maximumFocusMoves property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumFocusMoves(BigInteger value) {
        this.maximumFocusMoves = value;
    }

    /**
     * Gets the value of the autoFocus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoFocus() {
        return autoFocus;
    }

    /**
     * Sets the value of the autoFocus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoFocus(Boolean value) {
        this.autoFocus = value;
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
