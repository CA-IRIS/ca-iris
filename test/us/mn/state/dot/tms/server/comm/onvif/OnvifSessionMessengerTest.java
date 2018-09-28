package us.mn.state.dot.tms.server.comm.onvif;

import junit.framework.TestCase;
import us.mn.state.dot.tms.server.comm.ControllerException;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

public class OnvifSessionMessengerTest extends TestCase {

	public void testSelectService() throws IOException {
		String uri = "http://0.0.0.0:80";
		OnvifSessionMessenger m = new OnvifSessionMessenger(uri);
		try {
			m.selectService(OnvifService.PTZ);
			fail();
		} catch (ControllerException e) {
			// successfully caught
		}
		m.selectService(OnvifService.DEVICE);
	}

	public void testUri() throws IOException {
		String goodProtocol = "http";
		String goodHost = "0.0.0.0";
		String goodPort = "80";
		String goodPath = "";
		try {
			new OnvifSessionMessenger("");
			fail();
		} catch (IOException e) {
			// successfully caught
		}
		try {
			new OnvifSessionMessenger(buildUri("tcp", goodHost, goodPort, goodPath));
			fail();
		} catch (IOException e) {
			// successfully caught
		}
		try {
			new OnvifSessionMessenger(buildUri(goodProtocol, goodHost, goodPort, "/path"));
			fail();
		} catch (IOException e) {
			// successfully caught
		}
		try {
			new OnvifSessionMessenger(buildUri(goodProtocol, "", goodPort, goodProtocol));
			fail();
		} catch (IOException e) {
			// successfully caught
		}
		new OnvifSessionMessenger(buildUri(goodProtocol, goodHost, goodPort, goodPath));
	}

	private String buildUri(String protocol, String host, String port, String path) {
		return protocol + "://" + host + ":" + port + path;
	}
}