package us.mn.state.dot.tms.server.comm.ttip.serializers.dmsStatus;

import us.mn.state.dot.tms.server.comm.ttip.serializers.common.InformationResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Exists purely as a wrapper for generic base class.
 *
 * @author Dan Rossiter
 */
@XmlRootElement(name = "informationResponse")
public class DmsInformationResponse extends InformationResponse<LocalResponseGroup> {
}
