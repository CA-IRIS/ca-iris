
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for ScopeDefinition.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="ScopeDefinition"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Fixed"/&gt;
 *     &lt;enumeration value="Configurable"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ScopeDefinition")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum ScopeDefinition {

    @XmlEnumValue("Fixed")
    FIXED("Fixed"),
    @XmlEnumValue("Configurable")
    CONFIGURABLE("Configurable");
    private final String value;

    ScopeDefinition(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScopeDefinition fromValue(String v) {
        for (ScopeDefinition c: ScopeDefinition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
