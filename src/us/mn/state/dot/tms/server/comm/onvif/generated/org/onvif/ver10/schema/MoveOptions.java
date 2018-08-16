
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for MoveOptions complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MoveOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Absolute" type="{http://www.onvif.org/ver10/schema}AbsoluteFocusOptions" minOccurs="0"/&gt;
 *         &lt;element name="Relative" type="{http://www.onvif.org/ver10/schema}RelativeFocusOptions" minOccurs="0"/&gt;
 *         &lt;element name="Continuous" type="{http://www.onvif.org/ver10/schema}ContinuousFocusOptions" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MoveOptions", propOrder = {
    "absolute",
    "relative",
    "continuous"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class MoveOptions {

    @XmlElement(name = "Absolute")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected AbsoluteFocusOptions absolute;
    @XmlElement(name = "Relative")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected RelativeFocusOptions relative;
    @XmlElement(name = "Continuous")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected ContinuousFocusOptions continuous;

    /**
     * Gets the value of the absolute property.
     * 
     * @return
     *     possible object is
     *     {@link AbsoluteFocusOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public AbsoluteFocusOptions getAbsolute() {
        return absolute;
    }

    /**
     * Sets the value of the absolute property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbsoluteFocusOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setAbsolute(AbsoluteFocusOptions value) {
        this.absolute = value;
    }

    /**
     * Gets the value of the relative property.
     * 
     * @return
     *     possible object is
     *     {@link RelativeFocusOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public RelativeFocusOptions getRelative() {
        return relative;
    }

    /**
     * Sets the value of the relative property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelativeFocusOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setRelative(RelativeFocusOptions value) {
        this.relative = value;
    }

    /**
     * Gets the value of the continuous property.
     * 
     * @return
     *     possible object is
     *     {@link ContinuousFocusOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public ContinuousFocusOptions getContinuous() {
        return continuous;
    }

    /**
     * Sets the value of the continuous property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContinuousFocusOptions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setContinuous(ContinuousFocusOptions value) {
        this.continuous = value;
    }

}
