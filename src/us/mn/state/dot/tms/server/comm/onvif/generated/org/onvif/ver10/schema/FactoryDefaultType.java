
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FactoryDefaultType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FactoryDefaultType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Hard"/&gt;
 *     &lt;enumeration value="Soft"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FactoryDefaultType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum FactoryDefaultType {


    /**
     * Indicates that a hard factory default is requested.
     * 
     */
    @XmlEnumValue("Hard")
    HARD("Hard"),

    /**
     * Indicates that a soft factory default is requested.
     * 
     */
    @XmlEnumValue("Soft")
    SOFT("Soft");
    private final String value;

    FactoryDefaultType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FactoryDefaultType fromValue(String v) {
        for (FactoryDefaultType c: FactoryDefaultType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
