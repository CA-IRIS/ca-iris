
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for CapabilityCategory.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="CapabilityCategory"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="All"/&gt;
 *     &lt;enumeration value="Analytics"/&gt;
 *     &lt;enumeration value="Device"/&gt;
 *     &lt;enumeration value="Events"/&gt;
 *     &lt;enumeration value="Imaging"/&gt;
 *     &lt;enumeration value="Media"/&gt;
 *     &lt;enumeration value="PTZ"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CapabilityCategory")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum CapabilityCategory {

    @XmlEnumValue("All")
    ALL("All"),
    @XmlEnumValue("Analytics")
    ANALYTICS("Analytics"),
    @XmlEnumValue("Device")
    DEVICE("Device"),
    @XmlEnumValue("Events")
    EVENTS("Events"),
    @XmlEnumValue("Imaging")
    IMAGING("Imaging"),
    @XmlEnumValue("Media")
    MEDIA("Media"),
    PTZ("PTZ");
    private final String value;

    CapabilityCategory(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CapabilityCategory fromValue(String v) {
        for (CapabilityCategory c: CapabilityCategory.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
