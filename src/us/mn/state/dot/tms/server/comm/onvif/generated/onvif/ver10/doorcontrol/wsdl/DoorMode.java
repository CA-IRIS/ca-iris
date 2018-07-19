
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.doorcontrol.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DoorMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DoorMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Unknown"/&gt;
 *     &lt;enumeration value="Locked"/&gt;
 *     &lt;enumeration value="Unlocked"/&gt;
 *     &lt;enumeration value="Accessed"/&gt;
 *     &lt;enumeration value="Blocked"/&gt;
 *     &lt;enumeration value="LockedDown"/&gt;
 *     &lt;enumeration value="LockedOpen"/&gt;
 *     &lt;enumeration value="DoubleLocked"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DoorMode")
@XmlEnum
public enum DoorMode {


    /**
     * The mode of operation is unknown.
     * 
     */
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),

    /**
     * 
     * 								The intention is to set the door to a physical locked state. 
     * 								In this mode the device shall provide momentary access using the AccessDoor 
     * 								method if supported by the door instance.
     * 							
     * 
     */
    @XmlEnumValue("Locked")
    LOCKED("Locked"),

    /**
     * 
     * 								The intention is to set the door to a physical unlocked state. 
     * 								Alarms related to door timing operations such as open too long 
     * 								or forced open are masked in this mode.
     * 							
     * 
     */
    @XmlEnumValue("Unlocked")
    UNLOCKED("Unlocked"),

    /**
     * 
     * 								The intention is to momentary set the door to a physical unlocked state. 
     * 								After a predefined time the device shall revert the door to its previous mode. 
     * 								Alarms related to timing operations such as door forced open are masked in this mode.
     * 							
     * 
     */
    @XmlEnumValue("Accessed")
    ACCESSED("Accessed"),

    /**
     * 
     * 								The intention is to set the door to a physical locked state and the 
     * 								device shall not allow AccessDoor requests, i.e. it is not possible 
     * 								for the door to go to the accessed mode. 
     * 								All other requests to change the door mode are allowed.
     * 							
     * 
     */
    @XmlEnumValue("Blocked")
    BLOCKED("Blocked"),

    /**
     * 
     * 								The intention is to set the door to a physical locked state and the device 
     * 								shall only allow the LockDownReleaseDoor request. 
     * 								All other requests to change the door mode are not allowed.
     * 							
     * 
     */
    @XmlEnumValue("LockedDown")
    LOCKED_DOWN("LockedDown"),

    /**
     * 
     * 								The intention is to set the door to a physical unlocked state and the 
     * 								device shall only allow the LockOpenReleaseDoor request. 
     * 								All other requests to change the door mode are not allowed.
     * 							
     * 
     */
    @XmlEnumValue("LockedOpen")
    LOCKED_OPEN("LockedOpen"),

    /**
     * 
     * 								The intention is to set the door with multiple locks to a physical double locked state. 
     * 								If the door does not support double locking the devices shall 
     * 								treat this as a normal locked mode. 
     * 								When changing to an unlocked mode from the double locked mode, the physical state 
     * 								of the door may first go to locked state before unlocking.
     * 							
     * 
     */
    @XmlEnumValue("DoubleLocked")
    DOUBLE_LOCKED("DoubleLocked");
    private final String value;

    DoorMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DoorMode fromValue(String v) {
        for (DoorMode c: DoorMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
