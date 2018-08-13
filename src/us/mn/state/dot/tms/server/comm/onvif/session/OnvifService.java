package us.mn.state.dot.tms.server.comm.onvif.session;

/**
 * These are the implemented Onvif Services in IRIS.
 * @author Wesley Skillern (Southwest Research Institute)
 */
public enum OnvifService {
	DEVICE("Device"),
	MEDIA("Media"),
	PTZ("PTZ"),
	IMAGING("Imaging");

	private String s;

	OnvifService(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}
}
