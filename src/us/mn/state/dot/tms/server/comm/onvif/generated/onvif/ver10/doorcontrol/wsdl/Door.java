
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.doorcontrol.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 						The door structure shall include all properties of the DoorInfo structure and also a timings
 * 						structure.
 * 					
 * 
 * <p>Java class for Door complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Door"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.onvif.org/ver10/doorcontrol/wsdl}DoorInfo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DoorType" type="{http://www.onvif.org/ver10/pacs}Name"/&gt;
 *         &lt;element name="Timings" type="{http://www.onvif.org/ver10/doorcontrol/wsdl}Timings"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/doorcontrol/wsdl}DoorExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Door", propOrder = {
    "doorType",
    "timings",
    "extension"
})
public class Door
    extends DoorInfo
{

    @XmlElement(name = "DoorType", required = true)
    protected String doorType;
    @XmlElement(name = "Timings", required = true)
    protected Timings timings;
    @XmlElement(name = "Extension")
    protected DoorExtension extension;

    /**
     * Gets the value of the doorType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDoorType() {
        return doorType;
    }

    /**
     * Sets the value of the doorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDoorType(String value) {
        this.doorType = value;
    }

    /**
     * Gets the value of the timings property.
     * 
     * @return
     *     possible object is
     *     {@link Timings }
     *     
     */
    public Timings getTimings() {
        return timings;
    }

    /**
     * Sets the value of the timings property.
     * 
     * @param value
     *     allowed object is
     *     {@link Timings }
     *     
     */
    public void setTimings(Timings value) {
        this.timings = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link DoorExtension }
     *     
     */
    public DoorExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoorExtension }
     *     
     */
    public void setExtension(DoorExtension value) {
        this.extension = value;
    }

}
