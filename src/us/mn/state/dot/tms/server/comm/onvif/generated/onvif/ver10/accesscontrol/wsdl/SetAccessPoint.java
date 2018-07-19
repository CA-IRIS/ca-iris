
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.accesscontrol.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="AccessPoint" type="{http://www.onvif.org/ver10/accesscontrol/wsdl}AccessPoint"/&gt;
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
    "accessPoint"
})
@XmlRootElement(name = "SetAccessPoint")
public class SetAccessPoint {

    @XmlElement(name = "AccessPoint", required = true)
    protected AccessPoint accessPoint;

    /**
     * Gets the value of the accessPoint property.
     * 
     * @return
     *     possible object is
     *     {@link AccessPoint }
     *     
     */
    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    /**
     * Sets the value of the accessPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessPoint }
     *     
     */
    public void setAccessPoint(AccessPoint value) {
        this.accessPoint = value;
    }

}
