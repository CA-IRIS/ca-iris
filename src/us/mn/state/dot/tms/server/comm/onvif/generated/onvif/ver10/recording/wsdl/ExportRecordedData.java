
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.recording.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.SearchScope;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.StorageReferencePath;


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
 *         &lt;element name="StartPoint" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="EndPoint" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="SearchScope" type="{http://www.onvif.org/ver10/schema}SearchScope"/&gt;
 *         &lt;element name="FileFormat" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="StorageDestination" type="{http://www.onvif.org/ver10/schema}StorageReferencePath"/&gt;
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
    "startPoint",
    "endPoint",
    "searchScope",
    "fileFormat",
    "storageDestination"
})
@XmlRootElement(name = "ExportRecordedData")
public class ExportRecordedData {

    @XmlElement(name = "StartPoint")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startPoint;
    @XmlElement(name = "EndPoint")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endPoint;
    @XmlElement(name = "SearchScope", required = true)
    protected SearchScope searchScope;
    @XmlElement(name = "FileFormat", required = true)
    protected String fileFormat;
    @XmlElement(name = "StorageDestination", required = true)
    protected StorageReferencePath storageDestination;

    /**
     * Gets the value of the startPoint property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartPoint() {
        return startPoint;
    }

    /**
     * Sets the value of the startPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartPoint(XMLGregorianCalendar value) {
        this.startPoint = value;
    }

    /**
     * Gets the value of the endPoint property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndPoint() {
        return endPoint;
    }

    /**
     * Sets the value of the endPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndPoint(XMLGregorianCalendar value) {
        this.endPoint = value;
    }

    /**
     * Gets the value of the searchScope property.
     * 
     * @return
     *     possible object is
     *     {@link SearchScope }
     *     
     */
    public SearchScope getSearchScope() {
        return searchScope;
    }

    /**
     * Sets the value of the searchScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchScope }
     *     
     */
    public void setSearchScope(SearchScope value) {
        this.searchScope = value;
    }

    /**
     * Gets the value of the fileFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the value of the fileFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileFormat(String value) {
        this.fileFormat = value;
    }

    /**
     * Gets the value of the storageDestination property.
     * 
     * @return
     *     possible object is
     *     {@link StorageReferencePath }
     *     
     */
    public StorageReferencePath getStorageDestination() {
        return storageDestination;
    }

    /**
     * Sets the value of the storageDestination property.
     * 
     * @param value
     *     allowed object is
     *     {@link StorageReferencePath }
     *     
     */
    public void setStorageDestination(StorageReferencePath value) {
        this.storageDestination = value;
    }

}
