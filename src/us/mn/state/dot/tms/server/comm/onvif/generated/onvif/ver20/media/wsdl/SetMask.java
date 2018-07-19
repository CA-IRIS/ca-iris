
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver20.media.wsdl;

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
 *         &lt;element name="Mask" type="{http://www.onvif.org/ver20/media/wsdl}Mask"/&gt;
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
    "mask"
})
@XmlRootElement(name = "SetMask")
public class SetMask {

    @XmlElement(name = "Mask", required = true)
    protected Mask mask;

    /**
     * Gets the value of the mask property.
     * 
     * @return
     *     possible object is
     *     {@link Mask }
     *     
     */
    public Mask getMask() {
        return mask;
    }

    /**
     * Sets the value of the mask property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mask }
     *     
     */
    public void setMask(Mask value) {
        this.mask = value;
    }

}
