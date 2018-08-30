package us.mn.state.dot.tms.server.comm.onvif.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Random;

/**
 * The logic for WSUsernameToken session authentication.
 * The intended flow of control: 1. instantiate this 2. getUsername() 3.
 * getPasswordDigest() 4. getEncodedNonce() 5. getUTCTime() // requires previous
 * corresponding gePasswordDigest()
 * http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext
 * -1.0.xsd http://docs.oasis-open
 * .org/wss/2004/01/oasis-200401-wss-wssecurity-utility
 * -1.0.xsd
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class WSUsernameToken {

	private String username;
	private String password;
	/**
	 * the session token (static for all sequential session transactions)
	 */
	private String nonce;
	/** the date for the last call to passwordDigest() */
	private String date;
	/** positive value means device is ahead by clockOffset milliseconds */
	private long clockOffset = 0;

	String getUsername() {
		return username;
	}

	/**
	 * @param username may not be null
	 * @param password may not be null
	 */
	public WSUsernameToken(
		String username, String password)
	{
		if (username == null || username.isEmpty()
			|| password == null || password.isEmpty())
			throw new IllegalArgumentException(
				"Username and password may not be null. ");
		this.username = username;
		this.password = password;
	}

	public void setClockOffset(
		ZonedDateTime ourTime, ZonedDateTime deviceTime)
	{
		clockOffset = Duration.between(ourTime, deviceTime).toMillis();
	}

	/**
	 * Note: This will reset the date (timestamp) for this class, hence it
	 * should be called before using getUTCTime.
	 *
	 * @return B64Encode(SHA1 ( B64Decode ( Nonce) + Date + Password ) )
	 */
	String getPasswordDigest() throws NoSuchAlgorithmException {
		resetTime();
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.err.println(
				"ONVIF-required SHA-1 digest algorithm is " +
					"not implemented or not detected. ");
			throw e;
		}
		messageDigest.update((getNonce() + getUTCTime() + password)
			.getBytes());

		return Base64.getEncoder()
			.encodeToString(messageDigest.digest());
	}

	/**
	 * @return the base 64 encoded nonce
	 */
	String getEncodedNonce() {
		if (nonce == null) {
			nonce = createNonce();
		}
		return Base64.getEncoder().encodeToString(nonce.getBytes());
	}

	/**
	 * Note: Should be called after getPasswordDigest()
	 *
	 * @return the UTC date for the most recent call to getPasswordDigest()
	 */
	String getUTCTime() {
		if (date == null) {
			date =
				Instant.now().plusMillis(clockOffset)
					.toString();
		}
		return date;
	}

	private void resetTime() {
		date = null;
	}

	/**
	 * @return a new random int
	 */
	private String createNonce() {
		Random generator = new Random();
		return "" + generator.nextInt();
	}

	/**
	 * @return returns the current nonce else new nonce
	 */
	private String getNonce() {
		if (nonce == null) {
			nonce = createNonce();
		}
		return nonce;
	}
}
