
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FileSuffixType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileSuffixType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="sequence"/&gt;
 *     &lt;enumeration value="dateTime"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FileSuffixType")
@XmlEnum
public enum FileSuffixType {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("sequence")
    SEQUENCE("sequence"),
    @XmlEnumValue("dateTime")
    DATE_TIME("dateTime"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    FileSuffixType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileSuffixType fromValue(String v) {
        for (FileSuffixType c: FileSuffixType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
