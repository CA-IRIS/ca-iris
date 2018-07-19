
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RollDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RollDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Clockwise"/&gt;
 *     &lt;enumeration value="Counterclockwise"/&gt;
 *     &lt;enumeration value="Auto"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RollDirection")
@XmlEnum
public enum RollDirection {


    /**
     * Move clockwise in relation to the video source image.
     * 
     */
    @XmlEnumValue("Clockwise")
    CLOCKWISE("Clockwise"),

    /**
     * Move counterclockwise in relation to the video source image.
     * 
     */
    @XmlEnumValue("Counterclockwise")
    COUNTERCLOCKWISE("Counterclockwise"),

    /**
     * Automatically level the device in relation to the video source image.
     * 
     */
    @XmlEnumValue("Auto")
    AUTO("Auto");
    private final String value;

    RollDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RollDirection fromValue(String v) {
        for (RollDirection c: RollDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
