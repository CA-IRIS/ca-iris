package uk.me.jstott.sun;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * jSunTimes unit test suite.
 * </p>
 * 
 * <p>
 * (c) 2011 Jonathan Stott
 * </p>
 *
 * <p>
 * Created on 11-Jun-2011
 * </p>
 *
 * @author Jonathan Stott
 * @version 1.0
 * @since 1.0
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for uk.me.jstott.jcoord");
		// $JUnit-BEGIN$
		suite.addTestSuite(TimeTest.class);
		// $JUnit-END$
		return suite;
	}

}
