package us.mn.state.dot.tms.server.comm.onvif;

import junit.framework.TestCase;

public class OnvifPropertyTest extends TestCase {

	private static float SCALER_MAX = 10;

	public void testResizeSimple() {
		assertEquals(4f, OnvifProperty.resize(2, 1, 3, 2, 6));
	}

	public void testResizePositive() {
		float val = 4, oldMin = 1, oldMax = 10, newMin, newMax, expected;
		for (float scaler = 1; scaler <= SCALER_MAX; scaler += 0.5) {
			newMin = oldMin * scaler;
			newMax = oldMax * scaler;
			expected = val * scaler;
			float actual = OnvifProperty.resize(val, oldMin, oldMax, newMin, newMax);
			assertEquals(expected, actual);
		}
	}

	public void testResizeNegative() {
		assertEquals(-2f, OnvifProperty.resize(2f, 1, 2, -4, -2));
		assertEquals(-50f, OnvifProperty.resize(-5f, -10, -1, -100, -10));
	}

	public void testResizeZero() {
		assertEquals(0f, OnvifProperty.resize(5, 1, 10, -4, 5));
		assertEquals(0f, OnvifProperty.resize(0, 0, 10, 0, 100));
		assertEquals(100f, OnvifProperty.resize(9, 0, 9, 0, 100));
	}

	public void testResizeErrors() {
		try {
			OnvifProperty.resize(0, 1, 2, 2, 4);
			fail("resize did not detect val outside old range");
		} catch (AssertionError e) {
			// successfully caught bad input
		}
		try {
			OnvifProperty.resize(0, 0, 0, 2, 4);
			fail("resize did not detect bad old range");
		} catch (AssertionError e) {
			// successfully caught bad input
		}
		try {
			OnvifProperty.resize(0, 0, 1, 4, 0);
			fail("resize did not detect bad new range");
		} catch (AssertionError e) {
			// successfully caught bad input
		}
	}
}