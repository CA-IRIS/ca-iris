
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl;

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
 *         &lt;element name="ConfigurationOptions" type="{http://www.onvif.org/ver10/thermal/wsdl}ConfigurationOptions"/&gt;
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
    "configurationOptions"
})
@XmlRootElement(name = "GetConfigurationOptionsResponse")
public class GetConfigurationOptionsResponse {

    @XmlElement(name = "ConfigurationOptions", required = true)
    protected ConfigurationOptions configurationOptions;

    /**
     * Gets the value of the configurationOptions property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigurationOptions }
     *     
     */
    public ConfigurationOptions getConfigurationOptions() {
        return configurationOptions;
    }

    /**
     * Sets the value of the configurationOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigurationOptions }
     *     
     */
    public void setConfigurationOptions(ConfigurationOptions value) {
        this.configurationOptions = value;
    }

}
