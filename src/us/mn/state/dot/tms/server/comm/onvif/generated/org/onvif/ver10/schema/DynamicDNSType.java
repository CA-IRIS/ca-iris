
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for DynamicDNSType.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="DynamicDNSType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NoUpdate"/&gt;
 *     &lt;enumeration value="ClientUpdates"/&gt;
 *     &lt;enumeration value="ServerUpdates"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DynamicDNSType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum DynamicDNSType {

    @XmlEnumValue("NoUpdate")
    NO_UPDATE("NoUpdate"),
    @XmlEnumValue("ClientUpdates")
    CLIENT_UPDATES("ClientUpdates"),
    @XmlEnumValue("ServerUpdates")
    SERVER_UPDATES("ServerUpdates");
    private final String value;

    DynamicDNSType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DynamicDNSType fromValue(String v) {
        for (DynamicDNSType c: DynamicDNSType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
