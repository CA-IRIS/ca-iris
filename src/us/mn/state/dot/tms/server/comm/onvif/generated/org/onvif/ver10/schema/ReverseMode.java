
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for ReverseMode.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="ReverseMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OFF"/&gt;
 *     &lt;enumeration value="ON"/&gt;
 *     &lt;enumeration value="AUTO"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ReverseMode")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum ReverseMode {

    OFF("OFF"),
    ON("ON"),
    AUTO("AUTO"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    ReverseMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReverseMode fromValue(String v) {
        for (ReverseMode c: ReverseMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
