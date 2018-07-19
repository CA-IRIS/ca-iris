
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FocusDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FocusDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Near"/&gt;
 *     &lt;enumeration value="Far"/&gt;
 *     &lt;enumeration value="Auto"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FocusDirection")
@XmlEnum
public enum FocusDirection {


    /**
     * Move to focus on close objects.
     * 
     */
    @XmlEnumValue("Near")
    NEAR("Near"),

    /**
     * Move to focus on distant objects.
     * 
     */
    @XmlEnumValue("Far")
    FAR("Far"),

    /**
     * Automatically focus for the sharpest video source image.
     * 
     */
    @XmlEnumValue("Auto")
    AUTO("Auto");
    private final String value;

    FocusDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FocusDirection fromValue(String v) {
        for (FocusDirection c: FocusDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
