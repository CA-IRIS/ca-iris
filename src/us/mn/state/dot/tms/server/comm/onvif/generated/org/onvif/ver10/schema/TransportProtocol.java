
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransportProtocol.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransportProtocol"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="UDP"/&gt;
 *     &lt;enumeration value="TCP"/&gt;
 *     &lt;enumeration value="RTSP"/&gt;
 *     &lt;enumeration value="HTTP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TransportProtocol")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum TransportProtocol {

    UDP,

    /**
     * This value is deprecated.
     * 
     */
    TCP,
    RTSP,
    HTTP;

    public String value() {
        return name();
    }

    public static TransportProtocol fromValue(String v) {
        return valueOf(v);
    }

}
