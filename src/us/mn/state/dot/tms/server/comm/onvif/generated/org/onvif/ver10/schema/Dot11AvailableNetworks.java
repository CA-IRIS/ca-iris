
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <prop>Java class for Dot11AvailableNetworks complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Dot11AvailableNetworks"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SSID" type="{http://www.onvif.org/ver10/schema}Dot11SSIDType"/&gt;
 *         &lt;element name="BSSID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AuthAndMangementSuite" type="{http://www.onvif.org/ver10/schema}Dot11AuthAndMangementSuite" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PairCipher" type="{http://www.onvif.org/ver10/schema}Dot11Cipher" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="GroupCipher" type="{http://www.onvif.org/ver10/schema}Dot11Cipher" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SignalStrength" type="{http://www.onvif.org/ver10/schema}Dot11SignalStrength" minOccurs="0"/&gt;
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}Dot11AvailableNetworksExtension" minOccurs="0"/&gt;
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
@XmlType(name = "Dot11AvailableNetworks", propOrder = {
    "ssid",
    "bssid",
    "authAndMangementSuite",
    "pairCipher",
    "groupCipher",
    "signalStrength",
    "extension"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class Dot11AvailableNetworks {

    @XmlElement(name = "SSID", required = true, type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected byte[] ssid;
    @XmlElement(name = "BSSID")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected String bssid;
    @XmlElement(name = "AuthAndMangementSuite")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Dot11AuthAndMangementSuite> authAndMangementSuite;
    @XmlElement(name = "PairCipher")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Dot11Cipher> pairCipher;
    @XmlElement(name = "GroupCipher")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Dot11Cipher> groupCipher;
    @XmlElement(name = "SignalStrength")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Dot11SignalStrength signalStrength;
    @XmlElement(name = "Extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected Dot11AvailableNetworksExtension extension;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the ssid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public byte[] getSSID() {
        return ssid;
    }

    /**
     * Sets the value of the ssid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setSSID(byte[] value) {
        this.ssid = value;
    }

    /**
     * Gets the value of the bssid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public String getBSSID() {
        return bssid;
    }

    /**
     * Sets the value of the bssid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setBSSID(String value) {
        this.bssid = value;
    }

    /**
     * Gets the value of the authAndMangementSuite property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authAndMangementSuite property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthAndMangementSuite().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Dot11AuthAndMangementSuite }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Dot11AuthAndMangementSuite> getAuthAndMangementSuite() {
        if (authAndMangementSuite == null) {
            authAndMangementSuite = new ArrayList<Dot11AuthAndMangementSuite>();
        }
        return this.authAndMangementSuite;
    }

    /**
     * Gets the value of the pairCipher property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pairCipher property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPairCipher().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Dot11Cipher }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Dot11Cipher> getPairCipher() {
        if (pairCipher == null) {
            pairCipher = new ArrayList<Dot11Cipher>();
        }
        return this.pairCipher;
    }

    /**
     * Gets the value of the groupCipher property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupCipher property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupCipher().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Dot11Cipher }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Dot11Cipher> getGroupCipher() {
        if (groupCipher == null) {
            groupCipher = new ArrayList<Dot11Cipher>();
        }
        return this.groupCipher;
    }

    /**
     * Gets the value of the signalStrength property.
     * 
     * @return
     *     possible object is
     *     {@link Dot11SignalStrength }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Dot11SignalStrength getSignalStrength() {
        return signalStrength;
    }

    /**
     * Sets the value of the signalStrength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dot11SignalStrength }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setSignalStrength(Dot11SignalStrength value) {
        this.signalStrength = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link Dot11AvailableNetworksExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Dot11AvailableNetworksExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dot11AvailableNetworksExtension }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public void setExtension(Dot11AvailableNetworksExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <prop>
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
