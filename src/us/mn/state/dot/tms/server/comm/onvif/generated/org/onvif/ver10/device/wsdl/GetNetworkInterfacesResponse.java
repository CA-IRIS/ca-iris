
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.NetworkInterface;


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
 *         &lt;element name="NetworkInterfaces" type="{http://www.onvif.org/ver10/schema}NetworkInterface" maxOccurs="unbounded"/&gt;
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
    "networkInterfaces"
})
@XmlRootElement(name = "GetNetworkInterfacesResponse")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
public class GetNetworkInterfacesResponse {

    @XmlElement(name = "NetworkInterfaces", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected List<NetworkInterface> networkInterfaces;

    /**
     * Gets the value of the networkInterfaces property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkInterfaces property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkInterfaces().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkInterface }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public List<NetworkInterface> getNetworkInterfaces() {
        if (networkInterfaces == null) {
            networkInterfaces = new ArrayList<NetworkInterface>();
        }
        return this.networkInterfaces;
    }

}
