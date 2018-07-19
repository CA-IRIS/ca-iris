
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.QueryExpressionType;
import us.mn.state.dot.tms.server.comm.onvif.generated.oasis_open.docs.wsn.b_2.TopicExpressionType;


/**
 * Action Trigger configuration data type contains mandatory Topic Expression (Section Topic Filter in [Core Specification]), optional Message content expression (Section Message Content Filter in [Core Specification]), and set of actions to be triggered.
 * 
 * <p>Java class for ActionTriggerConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActionTriggerConfiguration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TopicExpression" type="{http://docs.oasis-open.org/wsn/b-2}TopicExpressionType"/&gt;
 *         &lt;element name="ContentExpression" type="{http://docs.oasis-open.org/wsn/b-2}QueryExpressionType" minOccurs="0"/&gt;
 *         &lt;element name="ActionToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/actionengine/wsdl}ActionTriggerConfigurationExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionTriggerConfiguration", propOrder = {
    "topicExpression",
    "contentExpression",
    "actionToken",
    "extension"
})
public class ActionTriggerConfiguration {

    @XmlElement(name = "TopicExpression", required = true)
    protected TopicExpressionType topicExpression;
    @XmlElement(name = "ContentExpression")
    protected QueryExpressionType contentExpression;
    @XmlElement(name = "ActionToken")
    protected List<String> actionToken;
    @XmlElement(name = "Extension")
    protected ActionTriggerConfigurationExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the topicExpression property.
     * 
     * @return
     *     possible object is
     *     {@link TopicExpressionType }
     *     
     */
    public TopicExpressionType getTopicExpression() {
        return topicExpression;
    }

    /**
     * Sets the value of the topicExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link TopicExpressionType }
     *     
     */
    public void setTopicExpression(TopicExpressionType value) {
        this.topicExpression = value;
    }

    /**
     * Gets the value of the contentExpression property.
     * 
     * @return
     *     possible object is
     *     {@link QueryExpressionType }
     *     
     */
    public QueryExpressionType getContentExpression() {
        return contentExpression;
    }

    /**
     * Sets the value of the contentExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryExpressionType }
     *     
     */
    public void setContentExpression(QueryExpressionType value) {
        this.contentExpression = value;
    }

    /**
     * Gets the value of the actionToken property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionToken property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionToken().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getActionToken() {
        if (actionToken == null) {
            actionToken = new ArrayList<String>();
        }
        return this.actionToken;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ActionTriggerConfigurationExtension }
     *     
     */
    public ActionTriggerConfigurationExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionTriggerConfigurationExtension }
     *     
     */
    public void setExtension(ActionTriggerConfigurationExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
