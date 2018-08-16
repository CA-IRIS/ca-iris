
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.imaging.wsdl;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for ImagingPresetType.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="ImagingPresetType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Custom"/&gt;
 *     &lt;enumeration value="ClearWeather"/&gt;
 *     &lt;enumeration value="Cloudy"/&gt;
 *     &lt;enumeration value="Fog"/&gt;
 *     &lt;enumeration value="Rain"/&gt;
 *     &lt;enumeration value="Snowing"/&gt;
 *     &lt;enumeration value="Snow"/&gt;
 *     &lt;enumeration value="WDR"/&gt;
 *     &lt;enumeration value="Shade"/&gt;
 *     &lt;enumeration value="Night"/&gt;
 *     &lt;enumeration value="Indoor"/&gt;
 *     &lt;enumeration value="Fluorescent"/&gt;
 *     &lt;enumeration value="Incandescent"/&gt;
 *     &lt;enumeration value="Sodium(Natrium)"/&gt;
 *     &lt;enumeration value="Sunrise(Horizon)"/&gt;
 *     &lt;enumeration value="Sunset(Rear)"/&gt;
 *     &lt;enumeration value="ExtremeHot"/&gt;
 *     &lt;enumeration value="ExtremeCold"/&gt;
 *     &lt;enumeration value="Underwater"/&gt;
 *     &lt;enumeration value="CloseUp"/&gt;
 *     &lt;enumeration value="Motion"/&gt;
 *     &lt;enumeration value="FlickerFree50"/&gt;
 *     &lt;enumeration value="FlickerFree60"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ImagingPresetType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:47:22-05:00", comments = "JAXB RI v2.2.11")
public enum ImagingPresetType {

    @XmlEnumValue("Custom")
    CUSTOM("Custom"),
    @XmlEnumValue("ClearWeather")
    CLEAR_WEATHER("ClearWeather"),
    @XmlEnumValue("Cloudy")
    CLOUDY("Cloudy"),
    @XmlEnumValue("Fog")
    FOG("Fog"),
    @XmlEnumValue("Rain")
    RAIN("Rain"),
    @XmlEnumValue("Snowing")
    SNOWING("Snowing"),
    @XmlEnumValue("Snow")
    SNOW("Snow"),
    WDR("WDR"),
    @XmlEnumValue("Shade")
    SHADE("Shade"),
    @XmlEnumValue("Night")
    NIGHT("Night"),
    @XmlEnumValue("Indoor")
    INDOOR("Indoor"),
    @XmlEnumValue("Fluorescent")
    FLUORESCENT("Fluorescent"),
    @XmlEnumValue("Incandescent")
    INCANDESCENT("Incandescent"),
    @XmlEnumValue("Sodium(Natrium)")
    SODIUM_NATRIUM("Sodium(Natrium)"),
    @XmlEnumValue("Sunrise(Horizon)")
    SUNRISE_HORIZON("Sunrise(Horizon)"),
    @XmlEnumValue("Sunset(Rear)")
    SUNSET_REAR("Sunset(Rear)"),
    @XmlEnumValue("ExtremeHot")
    EXTREME_HOT("ExtremeHot"),
    @XmlEnumValue("ExtremeCold")
    EXTREME_COLD("ExtremeCold"),
    @XmlEnumValue("Underwater")
    UNDERWATER("Underwater"),
    @XmlEnumValue("CloseUp")
    CLOSE_UP("CloseUp"),
    @XmlEnumValue("Motion")
    MOTION("Motion"),
    @XmlEnumValue("FlickerFree50")
    FLICKER_FREE_50("FlickerFree50"),
    @XmlEnumValue("FlickerFree60")
    FLICKER_FREE_60("FlickerFree60");
    private final String value;

    ImagingPresetType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImagingPresetType fromValue(String v) {
        for (ImagingPresetType c: ImagingPresetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
