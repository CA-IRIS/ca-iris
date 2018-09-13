package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetNodes;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.GetNodesResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZNodesProperty extends OnvifProperty {
	public OnvifPTZNodesProperty(
		OnvifSessionMessenger session)
	{
		super(session);
		isQuery = true;
	}

	public void encodeQuery(ControllerImpl c, OutputStream os)
		throws IOException
	{
		response = session.makeRequest(new GetNodes(), GetNodesResponse.class);
	}

	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (!(response instanceof GetNodesResponse))
			throw new OperationFailedException("GetNodesResponse");
		session.setNodes(((GetNodesResponse) response).getPTZNode());

	}
}
