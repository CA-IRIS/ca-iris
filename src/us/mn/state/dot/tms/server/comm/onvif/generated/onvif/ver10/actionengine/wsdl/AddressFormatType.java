
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressFormatType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AddressFormatType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="hostname"/&gt;
 *     &lt;enumeration value="ipv4"/&gt;
 *     &lt;enumeration value="ipv6"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AddressFormatType")
@XmlEnum
public enum AddressFormatType {

    @XmlEnumValue("hostname")
    HOSTNAME("hostname"),
    @XmlEnumValue("ipv4")
    IPV_4("ipv4"),
    @XmlEnumValue("ipv6")
    IPV_6("ipv6"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    AddressFormatType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AddressFormatType fromValue(String v) {
        for (AddressFormatType c: AddressFormatType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
