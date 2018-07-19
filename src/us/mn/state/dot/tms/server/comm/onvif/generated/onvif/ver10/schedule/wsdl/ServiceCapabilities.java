
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schedule.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * 
 * 						The service capabilities reflect optional functionality of a service.
 * 						The information is static and does not change during device operation.
 * 						The following capabilities are available:
 * 					
 * 
 * <p>Java class for ServiceCapabilities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceCapabilities"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="MaxLimit" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="MaxSchedules" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="MaxTimePeriodsPerDay" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="MaxSpecialDayGroups" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="MaxDaysInSpecialDayGroup" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="MaxSpecialDaysSchedules" use="required" type="{http://www.onvif.org/ver10/pacs}PositiveInteger" /&gt;
 *       &lt;attribute name="ExtendedRecurrenceSupported" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="SpecialDaysSupported" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="StateReportingSupported" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="ClientSuppliedTokenSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceCapabilities", propOrder = {
    "any"
})
public class ServiceCapabilities {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "MaxLimit", required = true)
    protected long maxLimit;
    @XmlAttribute(name = "MaxSchedules", required = true)
    protected long maxSchedules;
    @XmlAttribute(name = "MaxTimePeriodsPerDay", required = true)
    protected long maxTimePeriodsPerDay;
    @XmlAttribute(name = "MaxSpecialDayGroups", required = true)
    protected long maxSpecialDayGroups;
    @XmlAttribute(name = "MaxDaysInSpecialDayGroup", required = true)
    protected long maxDaysInSpecialDayGroup;
    @XmlAttribute(name = "MaxSpecialDaysSchedules", required = true)
    protected long maxSpecialDaysSchedules;
    @XmlAttribute(name = "ExtendedRecurrenceSupported", required = true)
    protected boolean extendedRecurrenceSupported;
    @XmlAttribute(name = "SpecialDaysSupported", required = true)
    protected boolean specialDaysSupported;
    @XmlAttribute(name = "StateReportingSupported", required = true)
    protected boolean stateReportingSupported;
    @XmlAttribute(name = "ClientSuppliedTokenSupported")
    protected Boolean clientSuppliedTokenSupported;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the maxLimit property.
     * 
     */
    public long getMaxLimit() {
        return maxLimit;
    }

    /**
     * Sets the value of the maxLimit property.
     * 
     */
    public void setMaxLimit(long value) {
        this.maxLimit = value;
    }

    /**
     * Gets the value of the maxSchedules property.
     * 
     */
    public long getMaxSchedules() {
        return maxSchedules;
    }

    /**
     * Sets the value of the maxSchedules property.
     * 
     */
    public void setMaxSchedules(long value) {
        this.maxSchedules = value;
    }

    /**
     * Gets the value of the maxTimePeriodsPerDay property.
     * 
     */
    public long getMaxTimePeriodsPerDay() {
        return maxTimePeriodsPerDay;
    }

    /**
     * Sets the value of the maxTimePeriodsPerDay property.
     * 
     */
    public void setMaxTimePeriodsPerDay(long value) {
        this.maxTimePeriodsPerDay = value;
    }

    /**
     * Gets the value of the maxSpecialDayGroups property.
     * 
     */
    public long getMaxSpecialDayGroups() {
        return maxSpecialDayGroups;
    }

    /**
     * Sets the value of the maxSpecialDayGroups property.
     * 
     */
    public void setMaxSpecialDayGroups(long value) {
        this.maxSpecialDayGroups = value;
    }

    /**
     * Gets the value of the maxDaysInSpecialDayGroup property.
     * 
     */
    public long getMaxDaysInSpecialDayGroup() {
        return maxDaysInSpecialDayGroup;
    }

    /**
     * Sets the value of the maxDaysInSpecialDayGroup property.
     * 
     */
    public void setMaxDaysInSpecialDayGroup(long value) {
        this.maxDaysInSpecialDayGroup = value;
    }

    /**
     * Gets the value of the maxSpecialDaysSchedules property.
     * 
     */
    public long getMaxSpecialDaysSchedules() {
        return maxSpecialDaysSchedules;
    }

    /**
     * Sets the value of the maxSpecialDaysSchedules property.
     * 
     */
    public void setMaxSpecialDaysSchedules(long value) {
        this.maxSpecialDaysSchedules = value;
    }

    /**
     * Gets the value of the extendedRecurrenceSupported property.
     * 
     */
    public boolean isExtendedRecurrenceSupported() {
        return extendedRecurrenceSupported;
    }

    /**
     * Sets the value of the extendedRecurrenceSupported property.
     * 
     */
    public void setExtendedRecurrenceSupported(boolean value) {
        this.extendedRecurrenceSupported = value;
    }

    /**
     * Gets the value of the specialDaysSupported property.
     * 
     */
    public boolean isSpecialDaysSupported() {
        return specialDaysSupported;
    }

    /**
     * Sets the value of the specialDaysSupported property.
     * 
     */
    public void setSpecialDaysSupported(boolean value) {
        this.specialDaysSupported = value;
    }

    /**
     * Gets the value of the stateReportingSupported property.
     * 
     */
    public boolean isStateReportingSupported() {
        return stateReportingSupported;
    }

    /**
     * Sets the value of the stateReportingSupported property.
     * 
     */
    public void setStateReportingSupported(boolean value) {
        this.stateReportingSupported = value;
    }

    /**
     * Gets the value of the clientSuppliedTokenSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isClientSuppliedTokenSupported() {
        if (clientSuppliedTokenSupported == null) {
            return false;
        } else {
            return clientSuppliedTokenSupported;
        }
    }

    /**
     * Sets the value of the clientSuppliedTokenSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setClientSuppliedTokenSupported(Boolean value) {
        this.clientSuppliedTokenSupported = value;
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
