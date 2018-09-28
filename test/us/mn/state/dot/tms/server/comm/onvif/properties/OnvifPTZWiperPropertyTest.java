package us.mn.state.dot.tms.server.comm.onvif.properties;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class OnvifPTZWiperPropertyTest extends TestCase {
	private static String match = "matchingString";
	private static String notMatch = "anotherString";

	public void testMatchAnyNull() {
		// test two null empty inputs
		List<String> ref = new ArrayList<>();
		String[] find = {};
		String s = OnvifPTZWiperProperty.matchAny(ref, find);
		assertNull(s);

		// test with empty find only
		ref.add(match);
		s = OnvifPTZWiperProperty.matchAny(ref, find);
		assertNull(s);

		// test with empty ref only
		ref.clear();
		String[] find2 = {match};
		s = OnvifPTZWiperProperty.matchAny(ref, find2);
		assertNull(s);

		ref = null;
		s = OnvifPTZWiperProperty.matchAny(ref, find2);
		assertNull(s);
	}

	public void testMatchAnyFalse() {
		// test with single item arrays
		List<String> ref = new ArrayList<>();
		String[] find = {match};
		ref.add(notMatch);
		String s = OnvifPTZWiperProperty.matchAny(ref, find);
		assertNull(s);

		// test with arrays with size greater than 1
		ref.clear();
		ref.add(match);
		String[] find2 = {notMatch};
		s = OnvifPTZWiperProperty.matchAny(ref, find2);
		assertNull(s);
	}

	public void testMatchAnyTrue() {
		// test with arrays of size equal to 1
		List<String> ref = new ArrayList<>();
		String[] find = {match};
		ref.add(match);
		String s = OnvifPTZWiperProperty.matchAny(ref, find);
		assertNotNull(s);
		assertEquals(match, s);

		// test with arrays of size reater than 1
		String[] find2 = {notMatch, match};
		s = OnvifPTZWiperProperty.matchAny(ref, find2);
		assertNotNull(s);
		assertEquals(match, s);

		ref.add(notMatch);
		s = OnvifPTZWiperProperty.matchAny(ref, find);
		assertNotNull(s);
		assertEquals(match, s);
	}
}