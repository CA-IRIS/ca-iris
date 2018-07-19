
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver20.media.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MaskType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MaskType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Color"/&gt;
 *     &lt;enumeration value="Pixelated"/&gt;
 *     &lt;enumeration value="Blurred"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "MaskType")
@XmlEnum
public enum MaskType {

    @XmlEnumValue("Color")
    COLOR("Color"),
    @XmlEnumValue("Pixelated")
    PIXELATED("Pixelated"),
    @XmlEnumValue("Blurred")
    BLURRED("Blurred");
    private final String value;

    MaskType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MaskType fromValue(String v) {
        for (MaskType c: MaskType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
