
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schedule.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 						A override schedule that defines alternate time periods for a group of special days.
 * 					
 * 
 * <p>Java class for SpecialDaysSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecialDaysSchedule"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GroupToken" type="{http://www.onvif.org/ver10/pacs}ReferenceToken"/&gt;
 *         &lt;element name="TimeRange" type="{http://www.onvif.org/ver10/schedule/wsdl}TimePeriod" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schedule/wsdl}SpecialDaysScheduleExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecialDaysSchedule", propOrder = {
    "groupToken",
    "timeRange",
    "extension"
})
public class SpecialDaysSchedule {

    @XmlElement(name = "GroupToken", required = true)
    protected String groupToken;
    @XmlElement(name = "TimeRange")
    protected List<TimePeriod> timeRange;
    @XmlElement(name = "Extension")
    protected SpecialDaysScheduleExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the groupToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupToken() {
        return groupToken;
    }

    /**
     * Sets the value of the groupToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupToken(String value) {
        this.groupToken = value;
    }

    /**
     * Gets the value of the timeRange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeRange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimeRange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TimePeriod }
     * 
     * 
     */
    public List<TimePeriod> getTimeRange() {
        if (timeRange == null) {
            timeRange = new ArrayList<TimePeriod>();
        }
        return this.timeRange;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link SpecialDaysScheduleExtension }
     *     
     */
    public SpecialDaysScheduleExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecialDaysScheduleExtension }
     *     
     */
    public void setExtension(SpecialDaysScheduleExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
