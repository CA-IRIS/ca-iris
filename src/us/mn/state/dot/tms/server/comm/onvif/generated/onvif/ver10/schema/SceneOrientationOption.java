
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SceneOrientationOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SceneOrientationOption"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Below"/&gt;
 *     &lt;enumeration value="Horizon"/&gt;
 *     &lt;enumeration value="Above"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SceneOrientationOption")
@XmlEnum
public enum SceneOrientationOption {

    @XmlEnumValue("Below")
    BELOW("Below"),
    @XmlEnumValue("Horizon")
    HORIZON("Horizon"),
    @XmlEnumValue("Above")
    ABOVE("Above");
    private final String value;

    SceneOrientationOption(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SceneOrientationOption fromValue(String v) {
        for (SceneOrientationOption c: SceneOrientationOption.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
