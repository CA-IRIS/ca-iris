
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TiltDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TiltDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Up"/&gt;
 *     &lt;enumeration value="Down"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TiltDirection")
@XmlEnum
public enum TiltDirection {


    /**
     * Move up in relation to the video source image.
     * 
     */
    @XmlEnumValue("Up")
    UP("Up"),

    /**
     * Move down in relation to the video source image.
     * 
     */
    @XmlEnumValue("Down")
    DOWN("Down");
    private final String value;

    TiltDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TiltDirection fromValue(String v) {
        for (TiltDirection c: TiltDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
