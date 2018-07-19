
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SceneOrientationMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SceneOrientationMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MANUAL"/&gt;
 *     &lt;enumeration value="AUTO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SceneOrientationMode")
@XmlEnum
public enum SceneOrientationMode {

    MANUAL,
    AUTO;

    public String value() {
        return name();
    }

    public static SceneOrientationMode fromValue(String v) {
        return valueOf(v);
    }

}
