
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.CertificateInformation;


/**
 * <prop>Java class for anonymous complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CertificateInformation" type="{http://www.onvif.org/ver10/schema}CertificateInformation"/&gt;
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
    "certificateInformation"
})
@XmlRootElement(name = "GetCertificateInformationResponse")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
public class GetCertificateInformationResponse {

    @XmlElement(name = "CertificateInformation", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected CertificateInformation certificateInformation;

    /**
     * Gets the value of the certificateInformation property.
     * 
     * @return
     *     possible object is
     *     {@link CertificateInformation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public CertificateInformation getCertificateInformation() {
        return certificateInformation;
    }

    /**
     * Sets the value of the certificateInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateInformation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public void setCertificateInformation(CertificateInformation value) {
        this.certificateInformation = value;
    }

}
