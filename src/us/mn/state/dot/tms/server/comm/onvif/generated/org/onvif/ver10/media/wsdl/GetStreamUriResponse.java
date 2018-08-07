
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.MediaUri;


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
 *         &lt;element name="MediaUri" type="{http://www.onvif.org/ver10/schema}MediaUri"/&gt;
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
    "mediaUri"
})
@XmlRootElement(name = "GetStreamUriResponse")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
public class GetStreamUriResponse {

    @XmlElement(name = "MediaUri", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    protected MediaUri mediaUri;

    /**
     * Gets the value of the mediaUri property.
     * 
     * @return
     *     possible object is
     *     {@link MediaUri }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public MediaUri getMediaUri() {
        return mediaUri;
    }

    /**
     * Sets the value of the mediaUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link MediaUri }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public void setMediaUri(MediaUri value) {
        this.mediaUri = value;
    }

}
