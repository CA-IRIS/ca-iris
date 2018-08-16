
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for Dot11StationMode.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="Dot11StationMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Ad-hoc"/&gt;
 *     &lt;enumeration value="Infrastructure"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Dot11StationMode")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum Dot11StationMode {

    @XmlEnumValue("Ad-hoc")
    AD_HOC("Ad-hoc"),
    @XmlEnumValue("Infrastructure")
    INFRASTRUCTURE("Infrastructure"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    Dot11StationMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Dot11StationMode fromValue(String v) {
        for (Dot11StationMode c: Dot11StationMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
