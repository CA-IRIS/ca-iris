
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="ActionTrigger" type="{http://www.onvif.org/ver10/actionengine/wsdl}ActionTriggerConfiguration" maxOccurs="unbounded"/&gt;
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
    "actionTrigger"
})
@XmlRootElement(name = "CreateActionTriggers")
public class CreateActionTriggers {

    @XmlElement(name = "ActionTrigger", required = true)
    protected List<ActionTriggerConfiguration> actionTrigger;

    /**
     * Gets the value of the actionTrigger property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionTrigger property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionTrigger().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActionTriggerConfiguration }
     * 
     * 
     */
    public List<ActionTriggerConfiguration> getActionTrigger() {
        if (actionTrigger == null) {
            actionTrigger = new ArrayList<ActionTriggerConfiguration>();
        }
        return this.actionTrigger;
    }

}
