
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.MoveOptions20;


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
 *         &lt;element name="MoveOptions" type="{http://www.onvif.org/ver10/schema}MoveOptions20"/&gt;
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
    "moveOptions"
})
@XmlRootElement(name = "GetMoveOptionsResponse")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
public class GetMoveOptionsResponse {

    @XmlElement(name = "MoveOptions", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    protected MoveOptions20 moveOptions;

    /**
     * Gets the value of the moveOptions property.
     * 
     * @return
     *     possible object is
     *     {@link MoveOptions20 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public MoveOptions20 getMoveOptions() {
        return moveOptions;
    }

    /**
     * Sets the value of the moveOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link MoveOptions20 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
    public void setMoveOptions(MoveOptions20 value) {
        this.moveOptions = value;
    }

}
