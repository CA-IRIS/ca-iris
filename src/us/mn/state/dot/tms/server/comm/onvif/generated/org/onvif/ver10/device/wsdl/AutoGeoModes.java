
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for AutoGeoModes.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="AutoGeoModes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Location"/&gt;
 *     &lt;enumeration value="Heading"/&gt;
 *     &lt;enumeration value="Leveling"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AutoGeoModes")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:46:16-05:00", comments = "JAXB RI v2.2.11")
public enum AutoGeoModes {


    /**
     * Automatic adjustment of the device location.
     * 
     */
    @XmlEnumValue("Location")
    LOCATION("Location"),

    /**
     * Automatic adjustment of the device orientation relative to the compass also called yaw.
     * 
     */
    @XmlEnumValue("Heading")
    HEADING("Heading"),

    /**
     * Automatic adjustment of the deviation from the horizon also called pitch and roll.
     * 
     */
    @XmlEnumValue("Leveling")
    LEVELING("Leveling");
    private final String value;

    AutoGeoModes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AutoGeoModes fromValue(String v) {
        for (AutoGeoModes c: AutoGeoModes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
