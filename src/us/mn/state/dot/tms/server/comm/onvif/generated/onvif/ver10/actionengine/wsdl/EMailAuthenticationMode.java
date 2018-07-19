
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EMailAuthenticationMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EMailAuthenticationMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="SMTP"/&gt;
 *     &lt;enumeration value="POPSMTP"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EMailAuthenticationMode")
@XmlEnum
public enum EMailAuthenticationMode {

    @XmlEnumValue("none")
    NONE("none"),
    SMTP("SMTP"),
    POPSMTP("POPSMTP"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    EMailAuthenticationMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EMailAuthenticationMode fromValue(String v) {
        for (EMailAuthenticationMode c: EMailAuthenticationMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
