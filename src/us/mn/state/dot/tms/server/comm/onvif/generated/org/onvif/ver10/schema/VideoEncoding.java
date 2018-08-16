
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for VideoEncoding.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="VideoEncoding"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="JPEG"/&gt;
 *     &lt;enumeration value="MPEG4"/&gt;
 *     &lt;enumeration value="H264"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "VideoEncoding")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum VideoEncoding {

    JPEG("JPEG"),
    @XmlEnumValue("MPEG4")
    MPEG_4("MPEG4"),
    @XmlEnumValue("H264")
    H_264("H264");
    private final String value;

    VideoEncoding(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VideoEncoding fromValue(String v) {
        for (VideoEncoding c: VideoEncoding.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
