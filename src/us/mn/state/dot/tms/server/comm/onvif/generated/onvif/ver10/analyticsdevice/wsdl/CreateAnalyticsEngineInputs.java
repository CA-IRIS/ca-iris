
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.analyticsdevice.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema.AnalyticsEngineInput;


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
 *         &lt;element name="Configuration" type="{http://www.onvif.org/ver10/schema}AnalyticsEngineInput" maxOccurs="unbounded"/&gt;
 *         &lt;element name="ForcePersistence" type="{http://www.w3.org/2001/XMLSchema}boolean" maxOccurs="unbounded"/&gt;
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
@XmlRootElement(name = "CreateAnalyticsEngineInputs")
public class CreateAnalyticsEngineInputs {

    @XmlElement(name = "Configuration", required = true)
    protected List<AnalyticsEngineInput> configuration;
    @XmlElement(name = "ForcePersistence", type = Boolean.class)
    protected List<Boolean> forcePersistence;

    /**
     * Gets the value of the configuration property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configuration property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfiguration().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnalyticsEngineInput }
     * 
     * 
     */
    public List<AnalyticsEngineInput> getConfiguration() {
        if (configuration == null) {
            configuration = new ArrayList<AnalyticsEngineInput>();
        }
        return this.configuration;
    }

    /**
     * Gets the value of the forcePersistence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the forcePersistence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForcePersistence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     * 
     * 
     */
    public List<Boolean> getForcePersistence() {
        if (forcePersistence == null) {
            forcePersistence = new ArrayList<Boolean>();
        }
        return this.forcePersistence;
    }

}
