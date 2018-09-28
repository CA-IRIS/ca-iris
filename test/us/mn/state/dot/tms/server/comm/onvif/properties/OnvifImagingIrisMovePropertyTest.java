package us.mn.state.dot.tms.server.comm.onvif.properties;

import junit.framework.TestCase;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.schema.FloatRange;

public class OnvifImagingIrisMovePropertyTest extends TestCase {

	public void testGetNewVal() {
		FloatRange range = new FloatRange();
		range.setMin(-10);
		range.setMax(0);
		assertEquals(-4f, setup(DeviceRequest.CAMERA_IRIS_OPEN)
				.getNewVal(range, -5));
		assertEquals(-6f, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.getNewVal(range, -5));
		range.setMin(0);
		range.setMax(10);
		assertEquals(4f, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.getNewVal(range, 5));
		assertEquals(6f, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.getNewVal(range, 5));
	}

	public void testGetNewValLimits() {
		FloatRange range = new FloatRange();
		range.setMin(-10);
		range.setMax(0);
		assertEquals(0f, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.getNewVal(range, -0.5f));
		assertEquals(-10f, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.getNewVal(range, -9.5f));
		range.setMin(0);
		range.setMax(10);
		assertEquals(0f, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.getNewVal(range, 0.5f));
		assertEquals(10f, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.getNewVal(range, 9.5f));
	}

	public void testNegativeAttenuation() {
		float min = -10, max = 0, oldVal = -5, incr = 1;
		float expected = oldVal + incr;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.negativeAttenuation(oldVal, incr, min, max));
		expected = oldVal - incr;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.negativeAttenuation(oldVal, incr, min, max));
	}

	public void testNegativeLimits() {
		float min = -10, max = 0, oldVal = -0.5f, incr = 1;
		float expected = 0;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.negativeAttenuation(oldVal, incr, min, max));
		oldVal = -9.5f;
		expected = -10;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.negativeAttenuation(oldVal, incr, min, max));
	}

	public void testAbsValAttenuation() {
		float min = 0, max = 10, oldVal = 5, incr = 1;
		float expected = oldVal - incr;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.absValAttenuation(oldVal, incr, min, max));
		expected = oldVal + incr;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.absValAttenuation(oldVal, incr, min, max));
	}

	public void testAbsValLimits() {
		float min = 0, max = 10, oldVal = 0.5f, incr = 1;
		float expected = 0;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_OPEN)
			.absValAttenuation(oldVal, incr, min, max));
		oldVal = 9.5f;
		expected = 10;
		assertEquals(expected, setup(DeviceRequest.CAMERA_IRIS_CLOSE)
			.absValAttenuation(oldVal, incr, min, max));
	}

	private OnvifImagingIrisMoveProperty setup(DeviceRequest req) {
		return new OnvifImagingIrisMoveProperty(
			null, req, null, null);
	}
}
