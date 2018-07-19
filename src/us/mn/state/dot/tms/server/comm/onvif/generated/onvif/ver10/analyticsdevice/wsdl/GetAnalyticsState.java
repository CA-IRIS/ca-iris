
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.analyticsdevice.wsdl;

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
 *         &lt;element name="AnalyticsEngineControlToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/&gt;
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
    "analyticsEngineControlToken"
})
@XmlRootElement(name = "GetAnalyticsState")
public class GetAnalyticsState {

    @XmlElement(name = "AnalyticsEngineControlToken", required = true)
    protected String analyticsEngineControlToken;

    /**
     * Gets the value of the analyticsEngineControlToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalyticsEngineControlToken() {
        return analyticsEngineControlToken;
    }

    /**
     * Sets the value of the analyticsEngineControlToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalyticsEngineControlToken(String value) {
        this.analyticsEngineControlToken = value;
    }

}
