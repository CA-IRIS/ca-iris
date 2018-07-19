
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorPaletteType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ColorPaletteType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Custom"/&gt;
 *     &lt;enumeration value="Grayscale"/&gt;
 *     &lt;enumeration value="BlackHot"/&gt;
 *     &lt;enumeration value="WhiteHot"/&gt;
 *     &lt;enumeration value="Sepia"/&gt;
 *     &lt;enumeration value="Red"/&gt;
 *     &lt;enumeration value="Iron"/&gt;
 *     &lt;enumeration value="Rain"/&gt;
 *     &lt;enumeration value="Rainbow"/&gt;
 *     &lt;enumeration value="Isotherm"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ColorPaletteType")
@XmlEnum
public enum ColorPaletteType {

    @XmlEnumValue("Custom")
    CUSTOM("Custom"),
    @XmlEnumValue("Grayscale")
    GRAYSCALE("Grayscale"),
    @XmlEnumValue("BlackHot")
    BLACK_HOT("BlackHot"),
    @XmlEnumValue("WhiteHot")
    WHITE_HOT("WhiteHot"),
    @XmlEnumValue("Sepia")
    SEPIA("Sepia"),
    @XmlEnumValue("Red")
    RED("Red"),
    @XmlEnumValue("Iron")
    IRON("Iron"),
    @XmlEnumValue("Rain")
    RAIN("Rain"),
    @XmlEnumValue("Rainbow")
    RAINBOW("Rainbow"),
    @XmlEnumValue("Isotherm")
    ISOTHERM("Isotherm");
    private final String value;

    ColorPaletteType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColorPaletteType fromValue(String v) {
        for (ColorPaletteType c: ColorPaletteType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
