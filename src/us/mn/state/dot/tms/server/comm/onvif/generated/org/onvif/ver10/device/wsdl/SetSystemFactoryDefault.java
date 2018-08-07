
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FactoryDefaultType;


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
 *         &lt;element name="FactoryDefault" type="{http://www.onvif.org/ver10/schema}FactoryDefaultType"/&gt;
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
    "factoryDefault"
})
@XmlRootElement(name = "SetSystemFactoryDefault")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
public class SetSystemFactoryDefault {

    @XmlElement(name = "FactoryDefault", required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected FactoryDefaultType factoryDefault;

    /**
     * Gets the value of the factoryDefault property.
     * 
     * @return
     *     possible object is
     *     {@link FactoryDefaultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public FactoryDefaultType getFactoryDefault() {
        return factoryDefault;
    }

    /**
     * Sets the value of the factoryDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link FactoryDefaultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public void setFactoryDefault(FactoryDefaultType value) {
        this.factoryDefault = value;
    }

}
