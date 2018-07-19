
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver20.analytics.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for Capabilities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Capabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RuleSupport" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="AnalyticsModuleSupport" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="CellBasedSceneDescriptionSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="RuleOptionsSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="AnalyticsModuleOptionsSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
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
    "any"
})
public class Capabilities {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "RuleSupport")
    protected Boolean ruleSupport;
    @XmlAttribute(name = "AnalyticsModuleSupport")
    protected Boolean analyticsModuleSupport;
    @XmlAttribute(name = "CellBasedSceneDescriptionSupported")
    protected Boolean cellBasedSceneDescriptionSupported;
    @XmlAttribute(name = "RuleOptionsSupported")
    protected Boolean ruleOptionsSupported;
    @XmlAttribute(name = "AnalyticsModuleOptionsSupported")
    protected Boolean analyticsModuleOptionsSupported;
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
     * Gets the value of the ruleSupport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRuleSupport() {
        return ruleSupport;
    }

    /**
     * Sets the value of the ruleSupport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRuleSupport(Boolean value) {
        this.ruleSupport = value;
    }

    /**
     * Gets the value of the analyticsModuleSupport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAnalyticsModuleSupport() {
        return analyticsModuleSupport;
    }

    /**
     * Sets the value of the analyticsModuleSupport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAnalyticsModuleSupport(Boolean value) {
        this.analyticsModuleSupport = value;
    }

    /**
     * Gets the value of the cellBasedSceneDescriptionSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCellBasedSceneDescriptionSupported() {
        return cellBasedSceneDescriptionSupported;
    }

    /**
     * Sets the value of the cellBasedSceneDescriptionSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCellBasedSceneDescriptionSupported(Boolean value) {
        this.cellBasedSceneDescriptionSupported = value;
    }

    /**
     * Gets the value of the ruleOptionsSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRuleOptionsSupported() {
        return ruleOptionsSupported;
    }

    /**
     * Sets the value of the ruleOptionsSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRuleOptionsSupported(Boolean value) {
        this.ruleOptionsSupported = value;
    }

    /**
     * Gets the value of the analyticsModuleOptionsSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAnalyticsModuleOptionsSupported() {
        return analyticsModuleOptionsSupported;
    }

    /**
     * Sets the value of the analyticsModuleOptionsSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAnalyticsModuleOptionsSupported(Boolean value) {
        this.analyticsModuleOptionsSupported = value;
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
