
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for PTZPresetTourOperation.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="PTZPresetTourOperation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Start"/&gt;
 *     &lt;enumeration value="Stop"/&gt;
 *     &lt;enumeration value="Pause"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PTZPresetTourOperation")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum PTZPresetTourOperation {

    @XmlEnumValue("Start")
    START("Start"),
    @XmlEnumValue("Stop")
    STOP("Stop"),
    @XmlEnumValue("Pause")
    PAUSE("Pause"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    PTZPresetTourOperation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PTZPresetTourOperation fromValue(String v) {
        for (PTZPresetTourOperation c: PTZPresetTourOperation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
