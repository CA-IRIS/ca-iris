
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.provisioning.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ZoomDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ZoomDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Wide"/&gt;
 *     &lt;enumeration value="Telephoto"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ZoomDirection")
@XmlEnum
public enum ZoomDirection {


    /**
     * Move video source lens toward a wider field of view.
     * 
     */
    @XmlEnumValue("Wide")
    WIDE("Wide"),

    /**
     * Move video source lens toward a narrower field of view.
     * 
     */
    @XmlEnumValue("Telephoto")
    TELEPHOTO("Telephoto");
    private final String value;

    ZoomDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ZoomDirection fromValue(String v) {
        for (ZoomDirection c: ZoomDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
