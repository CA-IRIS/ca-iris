
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for IPType.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="IPType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IPv4"/&gt;
 *     &lt;enumeration value="IPv6"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "IPType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum IPType {

    @XmlEnumValue("IPv4")
    I_PV_4("IPv4"),
    @XmlEnumValue("IPv6")
    I_PV_6("IPv6");
    private final String value;

    IPType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IPType fromValue(String v) {
        for (IPType c: IPType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
