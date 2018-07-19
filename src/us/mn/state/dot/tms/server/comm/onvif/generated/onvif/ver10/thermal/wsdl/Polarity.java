
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Polarity.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Polarity"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="WhiteHot"/&gt;
 *     &lt;enumeration value="BlackHot"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Polarity")
@XmlEnum
public enum Polarity {

    @XmlEnumValue("WhiteHot")
    WHITE_HOT("WhiteHot"),
    @XmlEnumValue("BlackHot")
    BLACK_HOT("BlackHot");
    private final String value;

    Polarity(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Polarity fromValue(String v) {
        for (Polarity c: Polarity.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
