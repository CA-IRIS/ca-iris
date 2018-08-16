
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for Scope complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Scope"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ScopeDef" type="{http://www.onvif.org/ver10/schema}ScopeDefinition"/&gt;
 *         &lt;element name="ScopeItem" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scope", propOrder = {
    "scopeDef",
    "scopeItem"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class Scope {

    @XmlElement(name = "ScopeDef", required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ScopeDefinition scopeDef;
    @XmlElement(name = "ScopeItem", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected String scopeItem;

    /**
     * Gets the value of the scopeDef property.
     * 
     * @return
     *     possible object is
     *     {@link ScopeDefinition }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ScopeDefinition getScopeDef() {
        return scopeDef;
    }

    /**
     * Sets the value of the scopeDef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScopeDefinition }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setScopeDef(ScopeDefinition value) {
        this.scopeDef = value;
    }

    /**
     * Gets the value of the scopeItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public String getScopeItem() {
        return scopeItem;
    }

    /**
     * Sets the value of the scopeItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setScopeItem(String value) {
        this.scopeItem = value;
    }

}
