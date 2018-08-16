
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for BacklightCompensationMode.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="BacklightCompensationMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OFF"/&gt;
 *     &lt;enumeration value="ON"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "BacklightCompensationMode")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum BacklightCompensationMode {


    /**
     * Backlight compensation is disabled.
     * 
     */
    OFF,

    /**
     * Backlight compensation is enabled.
     * 
     */
    ON;

    public String value() {
        return name();
    }

    public static BacklightCompensationMode fromValue(String v) {
        return valueOf(v);
    }

}
