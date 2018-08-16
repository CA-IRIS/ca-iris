
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * Describe the option of the color supported. Either list each color or define the range of color value. The following values are acceptable for Colourspace attribute.
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ul xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope" xmlns:tt="http://www.onvif.org/ver10/schema" xmlns:wsnt="http://docs.oasis-open.org/wsn/b-2" xmlns:xmime="http://www.w3.org/2005/05/xmlmime" xmlns:xop="http://www.w3.org/2004/08/xop/include" xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;&lt;li&gt;http://www.onvif.org/ver10/colorspace/YCbCr - YCbCr colourspace&lt;/li&gt;&lt;li&gt;http://www.onvif.org/ver10/colorspace/CIELUV - CIE LUV&lt;/li&gt;&lt;li&gt;http://www.onvif.org/ver10/colorspace/CIELAB - CIE 1976 (L*a*b*)&lt;/li&gt;&lt;li&gt;http://www.onvif.org/ver10/colorspace/HSV - HSV colourspace&lt;/li&gt;&lt;/ul&gt;
 * </pre>
 * 
 * 
 * <prop>Java class for ColorOptions complex type.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="ColorList" type="{http://www.onvif.org/ver10/schema}Color" maxOccurs="unbounded"/&gt;
 *         &lt;element name="ColorspaceRange" type="{http://www.onvif.org/ver10/schema}ColorspaceRange" maxOccurs="unbounded"/&gt;
 *       &lt;/choice&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorOptions", propOrder = {
    "colorList",
    "colorspaceRange"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public class ColorOptions {

    @XmlElement(name = "ColorList")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<Color> colorList;
    @XmlElement(name = "ColorspaceRange")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    protected List<ColorspaceRange> colorspaceRange;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the colorList property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorList property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorList().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link Color }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<Color> getColorList() {
        if (colorList == null) {
            colorList = new ArrayList<Color>();
        }
        return this.colorList;
    }

    /**
     * Gets the value of the colorspaceRange property.
     * 
     * <prop>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorspaceRange property.
     * 
     * <prop>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorspaceRange().add(newItem);
     * </pre>
     * 
     * 
     * <prop>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorspaceRange }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
    public List<ColorspaceRange> getColorspaceRange() {
        if (colorspaceRange == null) {
            colorspaceRange = new ArrayList<ColorspaceRange>();
        }
        return this.colorspaceRange;
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
