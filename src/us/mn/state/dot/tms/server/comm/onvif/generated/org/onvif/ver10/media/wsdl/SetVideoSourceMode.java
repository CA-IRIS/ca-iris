
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl;

import javax.annotation.Generated;
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
 *         &lt;element name="VideoSourceToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/&gt;
 *         &lt;element name="VideoSourceModeToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/&gt;
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
    "videoSourceToken",
    "videoSourceModeToken"
})
@XmlRootElement(name = "SetVideoSourceMode")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
public class SetVideoSourceMode {

    @XmlElement(name = "VideoSourceToken", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    protected String videoSourceToken;
    @XmlElement(name = "VideoSourceModeToken", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    protected String videoSourceModeToken;

    /**
     * Gets the value of the videoSourceToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public String getVideoSourceToken() {
        return videoSourceToken;
    }

    /**
     * Sets the value of the videoSourceToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public void setVideoSourceToken(String value) {
        this.videoSourceToken = value;
    }

    /**
     * Gets the value of the videoSourceModeToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public String getVideoSourceModeToken() {
        return videoSourceModeToken;
    }

    /**
     * Sets the value of the videoSourceModeToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public void setVideoSourceModeToken(String value) {
        this.videoSourceModeToken = value;
    }

}
