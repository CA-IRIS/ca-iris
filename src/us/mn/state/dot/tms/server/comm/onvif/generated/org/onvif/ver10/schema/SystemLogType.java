
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for SystemLogType.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="SystemLogType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="System"/&gt;
 *     &lt;enumeration value="Access"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SystemLogType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum SystemLogType {


    /**
     * Indicates that a system log is requested.
     * 
     */
    @XmlEnumValue("System")
    SYSTEM("System"),

    /**
     * Indicates that a access log is requested.
     * 
     */
    @XmlEnumValue("Access")
    ACCESS("Access");
    private final String value;

    SystemLogType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SystemLogType fromValue(String v) {
        for (SystemLogType c: SystemLogType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
