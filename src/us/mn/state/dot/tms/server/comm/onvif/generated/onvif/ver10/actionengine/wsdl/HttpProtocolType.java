
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.actionengine.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HttpProtocolType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HttpProtocolType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="http"/&gt;
 *     &lt;enumeration value="https"/&gt;
 *     &lt;enumeration value="Extended"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "HttpProtocolType")
@XmlEnum
public enum HttpProtocolType {

    @XmlEnumValue("http")
    HTTP("http"),
    @XmlEnumValue("https")
    HTTPS("https"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    HttpProtocolType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HttpProtocolType fromValue(String v) {
        for (HttpProtocolType c: HttpProtocolType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
