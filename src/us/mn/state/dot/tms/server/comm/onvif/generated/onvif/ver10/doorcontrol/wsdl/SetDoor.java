
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.doorcontrol.wsdl;

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
 *         &lt;element name="Door" type="{http://www.onvif.org/ver10/doorcontrol/wsdl}Door"/&gt;
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
    "door"
})
@XmlRootElement(name = "SetDoor")
public class SetDoor {

    @XmlElement(name = "Door", required = true)
    protected Door door;

    /**
     * Gets the value of the door property.
     * 
     * @return
     *     possible object is
     *     {@link Door }
     *     
     */
    public Door getDoor() {
        return door;
    }

    /**
     * Sets the value of the door property.
     * 
     * @param value
     *     allowed object is
     *     {@link Door }
     *     
     */
    public void setDoor(Door value) {
        this.door = value;
    }

}
