
package us.mn.state.dot.tms.server.comm.onvif.generated.org.w3._2003._05.soap_envelope;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <prop>Java class for NotUnderstoodType complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotUnderstoodType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="qname" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotUnderstoodType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class NotUnderstoodType {

    @XmlAttribute(name = "qname", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected QName qname;

    /**
     * Gets the value of the qname property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public QName getQname() {
        return qname;
    }

    /**
     * Sets the value of the qname property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setQname(QName value) {
        this.qname = value;
    }

}
