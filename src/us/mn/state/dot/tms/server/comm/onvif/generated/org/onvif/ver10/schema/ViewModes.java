
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ViewModes.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ViewModes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="tt:Fisheye"/&gt;
 *     &lt;enumeration value="tt:360Panorama"/&gt;
 *     &lt;enumeration value="tt:180Panorama"/&gt;
 *     &lt;enumeration value="tt:Quad"/&gt;
 *     &lt;enumeration value="tt:Original"/&gt;
 *     &lt;enumeration value="tt:LeftHalf"/&gt;
 *     &lt;enumeration value="tt:RightHalf"/&gt;
 *     &lt;enumeration value="tt:Dewarp"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ViewModes")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum ViewModes {


    /**
     * Undewarped viewmode from device supporting fisheye lens.
     * 
     */
    @XmlEnumValue("tt:Fisheye")
    TT_FISHEYE("tt:Fisheye"),

    /**
     *  360 degree panoramic view.
     * 
     */
    @XmlEnumValue("tt:360Panorama")
    TT_360_PANORAMA("tt:360Panorama"),

    /**
     *  180 degree panoramic view.
     * 
     */
    @XmlEnumValue("tt:180Panorama")
    TT_180_PANORAMA("tt:180Panorama"),

    /**
     * View mode combining four streams in single Quad, eg., applicable for devices supporting four heads.
     * 
     */
    @XmlEnumValue("tt:Quad")
    TT_QUAD("tt:Quad"),

    /**
     * Unaltered view from the sensor.
     * 
     */
    @XmlEnumValue("tt:Original")
    TT_ORIGINAL("tt:Original"),

    /**
     * Viewmode combining the left side sensors, applicable for devices supporting multiple sensors.
     * 
     */
    @XmlEnumValue("tt:LeftHalf")
    TT_LEFT_HALF("tt:LeftHalf"),

    /**
     * Viewmode combining the right side sensors, applicable for devices supporting multiple sensors.
     * 
     */
    @XmlEnumValue("tt:RightHalf")
    TT_RIGHT_HALF("tt:RightHalf"),

    /**
     * Dewarped view mode for device supporting fisheye lens.
     * 
     */
    @XmlEnumValue("tt:Dewarp")
    TT_DEWARP("tt:Dewarp");
    private final String value;

    ViewModes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ViewModes fromValue(String v) {
        for (ViewModes c: ViewModes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
