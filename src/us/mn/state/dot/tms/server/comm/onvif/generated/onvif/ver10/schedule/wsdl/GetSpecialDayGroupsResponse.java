
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schedule.wsdl;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="SpecialDayGroup" type="{http://www.onvif.org/ver10/schedule/wsdl}SpecialDayGroup" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlRootElement(name = "GetSpecialDayGroupsResponse")
public class GetSpecialDayGroupsResponse {

    @XmlElement(name = "SpecialDayGroup")
    protected List<SpecialDayGroup> specialDayGroup;

    /**
     * Gets the value of the specialDayGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialDayGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialDayGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialDayGroup }
     * 
     * 
     */
    public List<SpecialDayGroup> getSpecialDayGroup() {
        if (specialDayGroup == null) {
            specialDayGroup = new ArrayList<SpecialDayGroup>();
        }
        return this.specialDayGroup;
    }

}
