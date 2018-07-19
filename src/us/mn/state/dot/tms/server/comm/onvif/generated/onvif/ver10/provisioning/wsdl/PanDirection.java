
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PanDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PanDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Left"/&gt;
 *     &lt;enumeration value="Right"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PanDirection")
@XmlEnum
public enum PanDirection {


    /**
     * Move left in relation to the video source image.
     * 
     */
    @XmlEnumValue("Left")
    LEFT("Left"),

    /**
     * Move right in relation to the video source image.
     * 
     */
    @XmlEnumValue("Right")
    RIGHT("Right");
    private final String value;

    PanDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PanDirection fromValue(String v) {
        for (PanDirection c: PanDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
