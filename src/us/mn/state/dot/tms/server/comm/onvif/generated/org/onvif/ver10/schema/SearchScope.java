
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * A structure for defining a limited scope when searching in recorded data.
 * 
 * <prop>Java class for SearchScope complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchScope"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IncludedSources" type="{http://www.onvif.org/ver10/schema}SourceReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="IncludedRecordings" type="{http://www.onvif.org/ver10/schema}RecordingReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RecordingInformationFilter" type="{http://www.onvif.org/ver10/schema}XPathExpression" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}SearchScopeExtension" minOccurs="0"/&gt;
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
@XmlType(name = "SearchScope", propOrder = {
    "includedSources",
    "includedRecordings",
    "recordingInformationFilter",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class SearchScope {

    @XmlElement(name = "IncludedSources")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<SourceReference> includedSources;
    @XmlElement(name = "IncludedRecordings")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<String> includedRecordings;
    @XmlElement(name = "RecordingInformationFilter")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected String recordingInformationFilter;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected SearchScopeExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the includedSources property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includedSources property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludedSources().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link SourceReference }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<SourceReference> getIncludedSources() {
        if (includedSources == null) {
            includedSources = new ArrayList<SourceReference>();
        }
        return this.includedSources;
    }

    /**
     * Gets the value of the includedRecordings property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includedRecordings property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludedRecordings().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<String> getIncludedRecordings() {
        if (includedRecordings == null) {
            includedRecordings = new ArrayList<String>();
        }
        return this.includedRecordings;
    }

    /**
     * Gets the value of the recordingInformationFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public String getRecordingInformationFilter() {
        return recordingInformationFilter;
    }

    /**
     * Sets the value of the recordingInformationFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setRecordingInformationFilter(String value) {
        this.recordingInformationFilter = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link SearchScopeExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public SearchScopeExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchScopeExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(SearchScopeExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <prop>
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
