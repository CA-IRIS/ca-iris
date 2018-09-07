package us.mn.state.dot.tms.server.comm.onvif.session;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;


/**
 * Example from ONVIF Programmer's guide:
 * https://www.onvif.org/wp-content/uploads/2016/12/ONVIF_WG-APG-Application_Programmers_Guide-1.pdf
 *
 * B64Encode(SHA1 ( B64Decode ( Nonce ) + Date + Password ) )
 *
 * For example:
 *
 * Nonce
 * LKqI6G/AikKCQrN0zqZFlg==
 *
 * Date
 * 2010-09-16T07:50:45Z
 *
 * Password
 * userpassword
 *
 * Resulting Digest
 * tuOSpGlFlIXsozq4HFNeeGeFLEI=
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class WSUsernameTokenTest extends TestCase {
	public void testDigest()
		throws NoSuchAlgorithmException, IOException
	{
		WSUsernameToken auth = new WSUsernameToken(
			"unused", "userpassword");
		auth.setDate("2010-09-16T07:50:45Z");
		auth.setNonce("LKqI6G/AikKCQrN0zqZFlg==");
		String expected = "tuOSpGlFlIXsozq4HFNeeGeFLEI=";
		String actual = auth._getPasswordDigest();
		assertEquals(expected, actual);
	}
}