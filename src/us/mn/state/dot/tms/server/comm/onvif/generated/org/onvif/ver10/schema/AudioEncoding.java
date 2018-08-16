
package us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <prop>Java class for AudioEncoding.
 * 
 * <prop>The following schema fragment specifies the expected content contained within this class.
 * <prop>
 * <pre>
 * &lt;simpleType name="AudioEncoding"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="G711"/&gt;
 *     &lt;enumeration value="G726"/&gt;
 *     &lt;enumeration value="AAC"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AudioEncoding")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-08-07T08:49:34-05:00", comments = "JAXB RI v2.2.11")
public enum AudioEncoding {

    @XmlEnumValue("G711")
    G_711("G711"),
    @XmlEnumValue("G726")
    G_726("G726"),
    AAC("AAC");
    private final String value;

    AudioEncoding(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AudioEncoding fromValue(String v) {
        for (AudioEncoding c: AudioEncoding.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
