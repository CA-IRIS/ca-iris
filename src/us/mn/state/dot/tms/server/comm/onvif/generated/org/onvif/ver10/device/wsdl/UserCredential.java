
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <prop>Java class for UserCredential complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserCredential"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Extension" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserCredential", propOrder = {
    "userName",
    "password",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
public class UserCredential {

    @XmlElement(name = "UserName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected String userName;
    @XmlElement(name = "Password")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected String password;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    protected UserCredential.Extension extension;

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link UserCredential.Extension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public UserCredential.Extension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserCredential.Extension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(UserCredential.Extension value) {
        this.extension = value;
    }


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
     *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "any"
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
    public static class Extension {

        @XmlAnyElement(lax = true)
        @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         * 
         * <prop>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         * 
         * <prop>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         * 
         * 
         * <prop>
         * Objects of the following type(s) are allowed in the list
         * {@link Element }
         * {@link Object }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

    }

}
