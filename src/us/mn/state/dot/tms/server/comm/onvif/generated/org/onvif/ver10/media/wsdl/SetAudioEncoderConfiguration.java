
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.media.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.AudioEncoderConfiguration;


/**
 * <prop>Java class for anonymous complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Configuration" type="{http://www.onvif.org/ver10/schema}AudioEncoderConfiguration"/&gt;
 *         &lt;element name="ForcePersistence" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
    "configuration",
    "forcePersistence"
})
@XmlRootElement(name = "SetAudioEncoderConfiguration")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
public class SetAudioEncoderConfiguration {

    @XmlElement(name = "Configuration", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    protected AudioEncoderConfiguration configuration;
    @XmlElement(name = "ForcePersistence")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    protected boolean forcePersistence;

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link AudioEncoderConfiguration }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public AudioEncoderConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link AudioEncoderConfiguration }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public void setConfiguration(AudioEncoderConfiguration value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the forcePersistence property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public boolean isForcePersistence() {
        return forcePersistence;
    }

    /**
     * Sets the value of the forcePersistence property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:48:27-05:00", comments = "JAXB RI v2.2.11")
    public void setForcePersistence(boolean value) {
        this.forcePersistence = value;
    }

}
