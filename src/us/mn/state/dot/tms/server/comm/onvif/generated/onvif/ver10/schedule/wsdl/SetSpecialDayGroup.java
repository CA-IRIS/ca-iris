
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schedule.wsdl;

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
 *         &lt;element name="SpecialDayGroup" type="{http://www.onvif.org/ver10/schedule/wsdl}SpecialDayGroup"/&gt;
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
    "specialDayGroup"
})
@XmlRootElement(name = "SetSpecialDayGroup")
public class SetSpecialDayGroup {

    @XmlElement(name = "SpecialDayGroup", required = true)
    protected SpecialDayGroup specialDayGroup;

    /**
     * Gets the value of the specialDayGroup property.
     * 
     * @return
     *     possible object is
     *     {@link SpecialDayGroup }
     *     
     */
    public SpecialDayGroup getSpecialDayGroup() {
        return specialDayGroup;
    }

    /**
     * Sets the value of the specialDayGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecialDayGroup }
     *     
     */
    public void setSpecialDayGroup(SpecialDayGroup value) {
        this.specialDayGroup = value;
    }

}
