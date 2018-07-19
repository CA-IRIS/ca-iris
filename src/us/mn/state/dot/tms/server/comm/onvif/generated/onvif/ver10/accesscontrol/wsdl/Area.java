
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.accesscontrol.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 						The Area structure shall include all properties of the AreaInfo structure and optionally
 * 						a parent area token, an OccupancyControl structure and/or an Antipassback structure.
 * 					
 * 
 * <p>Java class for Area complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Area"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.onvif.org/ver10/accesscontrol/wsdl}AreaInfo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/accesscontrol/wsdl}AreaExtension" minOccurs="0"/&gt;
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
@XmlType(name = "Area", propOrder = {
    "extension"
})
public class Area
    extends AreaInfo
{

    @XmlElement(name = "Extension")
    protected AreaExtension extension;

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link AreaExtension }
     *     
     */
    public AreaExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link AreaExtension }
     *     
     */
    public void setExtension(AreaExtension value) {
        this.extension = value;
    }

}
