
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl;

import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.IPAddressFilter;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;


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
 *         &lt;element name="IPAddressFilter" type="{http://www.onvif.org/ver10/schema}IPAddressFilter"/&gt;
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
    "ipAddressFilter"
})
@XmlRootElement(name = "AddIPAddressFilter")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
public class AddIPAddressFilter {

    @XmlElement(name = "IPAddressFilter", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected IPAddressFilter ipAddressFilter;

    /**
     * Gets the value of the ipAddressFilter property.
     * 
     * @return
     *     possible object is
     *     {@link IPAddressFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public IPAddressFilter getIPAddressFilter() {
        return ipAddressFilter;
    }

    /**
     * Sets the value of the ipAddressFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link IPAddressFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public void setIPAddressFilter(IPAddressFilter value) {
        this.ipAddressFilter = value;
    }

}
